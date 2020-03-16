#!/usr/bin/env python
import numpy as np
import networkx as nx

probs={}

def unwindSet(edgeset):
    choices= []
    sn= None
    for (n1,n2) in edgeset:
        if sn == None:
            sn == n1
        else:
            assert sn == n1
        choices.append(n2)
    return choices

def dp_probability(node, G):
    degrees_squared = [np.power(G.degree(n),2) for n in G.nodes()]
    normalisation = float(sum(degrees_squared))
    denom = np.power(G.degree(node),2)
    return denom/normalisation

def calc_prob(choices, G):
    #print(choices,G)
    for node in choices:
        probs[node]=dp_probability(node,G)
    no_nodes = G.number_of_nodes()
    # number of nodes chosen
    no_choices = len(choices)
    assert no_choices == 3
    # probabilities with replacement: random then degree power 2.0
    randprob = (1.0/no_nodes) * (1.0/(no_nodes - 1)) * (1.0/(no_nodes - 2))
    dpprob = probs[choices[0]]*(probs[choices[1]]/(1 - probs[choices[0]]))*(probs[choices[2]]/(1 - probs[choices[0]] - probs[choices[1]]))
    ## If the probabilities were with replacement
    ## randprobwr = (1.0/no_nodes) *(1.0/no_nodes) *(1.0/no_nodes)
    ## dpprobwr = probs["NODE-E"]*probs["NODE-B"]*probs["NODE-C"]
    return (np.log(dpprob), np.log(randprob), no_choices)

with open('output/dpTest.dat','r') as f:
    lines = f.readlines()
    f.close()

G = nx.Graph()

currTime= 1
edgeset=[]
totRealProb= 0.0
totRandProb= 0.0
noChoices= 0

print("#log(rand prob),log(real prob),log(cum rand prob),log (cum real prob),c0,number choices")
for line in lines:
    n1, n2, time = line.split()
    time= int(time)
    if time > currTime:
        if time > 60:
            break
        if (time > 2):
            choices= unwindSet(edgeset)
            (realProb, randProb, nc)= calc_prob(choices,G)
            totRandProb+= randProb
            totRealProb+= realProb
            noChoices+= nc
            print(randProb,realProb,totRandProb,totRealProb, np.exp((totRealProb-totRandProb)/noChoices),noChoices)
        for (n1,n2) in edgeset:
            G.add_edge(n1,n2)
            #print("G now",n1,n2,list(G.nodes(data=True)))
        edgeset=[]
        currTime= time
    edgeset.append((n1,n2))

choices= unwindSet(edgeset)
(realProb, randProb, nc)= calc_prob(choices,G)
totRandProb+= randProb
totRealProb+= realProb
noChoices+= nc

print(randProb,realProb,totRandProb,totRealProb, np.exp((totRealProb-totRandProb)/noChoices),noChoices)