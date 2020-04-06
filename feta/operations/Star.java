package feta.operations;

import feta.network.Network;
import feta.network.UndirectedNetwork;
import feta.objectmodels.ObjectModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/** Class represents growth of network by a star. NB a single link may be considered a star with one leaf */

public class Star extends Operation {

    // Node at the centre of the star
    public String centreNodeName_;
    public int centreNode_;

    // Leaf nodes
    public String[] leafNodeNames_;
    public int[] leafNodes_;
    public boolean internal_;
    public int internalId_;
    public ArrayList<Integer> recents_;
    private ArrayList<int[]> permList;

    public Star(int noLeaves, boolean internal_){
        this.internal_=internal_;
        internalId_= internal_ ? 1 : 0;
        leafNodeNames_= new String[noLeaves];
        leafNodes_= new int[noLeaves];
        noChoices_=noLeaves;
    }

    public void build(Network net) {

        if (net.newNode(centreNodeName_)) {
            net.addNodeToList(centreNodeName_);
        }

        for(String leaf: leafNodeNames_) {
            if(net.newNode(leaf)) {
                net.addNodeToList(leaf);
            }
            net.addLink(centreNodeName_,leaf);
            net.noLinks_++;

            net.recentlyPickedNodes_.add(net.nodeNameToNo(leaf));
            if (net.recentlyPickedNodes_.size()>net.numRecents_) {
                net.recentlyPickedNodes_.remove(0);
            }
        }
        net.latestTime_=time_;
    }

    /** Functions related to growing a network */

    public void pickCentreNode_(Network net, ObjectModel om) {
        if (internal_) {
            centreNode_ = om.nodeSampleWithoutReplacement(net, new int[0]);
            centreNodeName_=net.nodeNoToName(centreNode_);
        }
        else {
            centreNodeName_ = net.generateNodeName();
            centreNode_=net.noNodes_;
            net.addNodeToList(centreNodeName_);
        }
    }

    public void pickLeafNodes_(Network net, ObjectModel om) {
        int[] existingLeaves;
        if (internal_ && net.getClass()==UndirectedNetwork.class) {
            int [] chosen_ = new int[1+((UndirectedNetwork) net).degrees_[centreNode_]];
            chosen_[0]=centreNode_;
            for (int n = 0; n < ((UndirectedNetwork) net).neighbours_.get(centreNode_).size(); n++) {
                chosen_[n+1] = ((UndirectedNetwork) net).neighbours_.get(centreNode_).get(n);
            }
            existingLeaves=om.getNodesWithoutReplacement(net, noExisting_, chosen_);
        } else {
            int [] chosen_ = new int[1];
            chosen_[0]=centreNode_;
            existingLeaves=om.getNodesWithoutReplacement(net, noExisting_, chosen_);
        }
        // Add new nodes
        int noNew_ = leafNodes_.length - noExisting_;
        int[] newLeaves_= new int[noNew_];
        for (int i = 0; i < noNew_; i++) {
            String newName = net.generateNodeName();
            net.addNodeToList(newName);
            newLeaves_[i]=net.nodeNameToNo(newName);
        }
        leafNodes_=ObjectModel.concatenate(existingLeaves,newLeaves_);
    }

    public void fill(Network net, ObjectModel om) {
        pickCentreNode_(net, om);
        pickLeafNodes_(net, om);

        for(int leaf: leafNodes_) {
            String leafName = net.nodeNoToName(leaf);
            if (net.isLink(centreNodeName_,leafName) && !net.allowDuplicates_)
                continue;
            net.addNewLink(centreNodeName_, leafName, time_);
        }
    }

    /** Different routines for calculating the loglikelihood based on how big the star is and whether data is ordered or not */

    public int[] getChoices(Network net) {
        noChoices_=0;
        for (String node: leafNodeNames_) {
            if (!net.newNode(node)) {
                noChoices_++;
            }
        }
        int[] choices = new int[noChoices_];
        int ind = 0;
        for (String node: leafNodeNames_) {
            if (!net.newNode(node)) {
                choices[ind]=net.nodeNameToNo(node);
                ind++;
            }
        }
        return choices;
    }

    public double calcLogLike(Network net, ObjectModel obm, boolean ordered) {
        recents_ = net.recentlyPickedNodes_;
        int [] choices = getChoices(net);

        if (ordered) {
            return calcLogLikeOrdered(net,obm);
        }

        // If number of chosen nodes is small enough, we can get the exact expression without sampling
        obm.normaliseAll(net);
        if (noChoices_ < 5) {
            return calcLogLikeSmall(net, obm);
        }

        double probSum = 0.0;
        double oldLogLike = 0.0;
        int noSamples = 10;
        int totalSamples = 10;
        while (true) {
            permList = generateRandomShuffles(noChoices_, noSamples);
            for (int[] p : permList) {
                net.recentlyPickedNodes_=recents_;
                int[] nodes = new int[noChoices_+internalId_];
                if (internal_) {
                    nodes[0]=net.nodeNameToNo(centreNodeName_);
                }
                for (int j = 0; j<noChoices_; j++) {
                    nodes[internalId_+j]=choices[p[j]];
                }
                double probProd = 1.0;
                for (int i = 0; i < noChoices_+internalId_; i++) {
                    int[] removed = new int[i];
                    for (int j = 0; j<i; j++) {
                        removed[j]=nodes[j];
                    }
                    obm.updateAll(net,removed);
                    int node = nodes[i];
                    double prob = obm.calcProbability(net, choices[node]);
                    net.recentlyPickedNodes_.add(node);
                    if (net.recentlyPickedNodes_.size()>net.numRecents_) {
                        net.recentlyPickedNodes_.remove(0);
                    }
                    probProd *= (net.noNodes_ - i);
                    probProd *= prob;
                }
                probSum += probProd;
            }
            double newLogLike = Math.log(probSum) - Math.log(totalSamples);
            if (oldLogLike != 0.0) {
                if (Math.abs(newLogLike - oldLogLike)/Math.abs(oldLogLike) < 0.01 || noSamples > 1000) {
                    System.out.println(totalSamples);
                    noChoices_+=internalId_;
                    return newLogLike;
                }
            }
            oldLogLike = newLogLike;
            noSamples *= 2;
            totalSamples += noSamples;
        }
    }

    public double calcLogLikeSmall(Network net, ObjectModel obm) {
        double probSum = 0.0;
        int[] choices = new int[noChoices_];
        int ind = 0;
        for (String node: leafNodeNames_) {
            if (!net.newNode(node)) {
                choices[ind]=net.nodeNameToNo(node);
                ind++;
            }
        }
        permList = new ArrayList<int[]>();
        generatePerms(0,choices);

        int noPerms = permList.size();
        for (int[] p : permList) {
            net.recentlyPickedNodes_=recents_;

            // Taking into account here the choice of the centre *IF* the star is an internal one.
            int[] perm = new int[noChoices_+internalId_];
            System.arraycopy(p,0,perm,internalId_,noChoices_);
            if (internal_) {
                perm[0]=net.nodeNameToNo(centreNodeName_);
            }

            double probProd = 1.0;
            String str = "";
            for (int i = 0; i < noChoices_+internalId_; i++) {
                int[] removed = new int[i];
                for (int j = 0; j<i; j++) {
                    removed[j]=perm[j];
                }
                obm.updateAll(net,removed);
                int node = perm[i];
                str+=node+" ";
                double prob = obm.calcProbability(net,node);
                probProd *= (net.noNodes_ - i);
                probProd *= prob;
                net.recentlyPickedNodes_.add(node);
                if (net.recentlyPickedNodes_.size()>net.numRecents_) {
                    net.recentlyPickedNodes_.remove(0);
                }
            }
            //System.out.println(str);
            probSum += probProd;
        }
        noChoices_+=internalId_;
        return Math.log(probSum) - Math.log(noPerms);
    }

    public double calcLogLikeOrdered(Network net, ObjectModel obm) {
        noChoices_=0;
        double probProd = 1.0;
        int[] choices = getChoices(net);

        obm.normaliseAll(net,new int[0]);
        int[] nodes = new int[noChoices_+internalId_];
        System.arraycopy(choices,0,nodes,internalId_,noChoices_);
        if (internal_) {
            nodes[0]=net.nodeNameToNo(centreNodeName_);
        }

        for (int i =0; i<noChoices_+internalId_; i++) {
            int[] removed = new int[i];
            for (int j = 0; j < i; j++) {
                removed[j]=nodes[j];
            }
            obm.updateAll(net,removed);
            int node = nodes[i];
            double prob = obm.calcProbability(net, choices[node]);
            probProd *= (net.noNodes_ - i);
            probProd *= prob;
        }
        double logLike = Math.log(probProd);
        noChoices_+=internalId_;
        return logLike;
    }


    public ArrayList<double[]> getComponentProbabilities(Network net, ObjectModel obm) {
        noChoices_=0;
        for (String node: leafNodeNames_) {
            if (!net.newNode(node)) {
                noChoices_++;
            }
        }
        noChoices_+=internalId_;

        int[] choices = new int[noChoices_];
        int ind = 0;
        if (internal_) {
            choices[0]=net.nodeNameToNo(centreNodeName_);
            ind++;
        }
        for (String node: leafNodeNames_) {
            if (!net.newNode(node)) {
                choices[ind]=net.nodeNameToNo(node);
                ind++;
            }
        }

        ArrayList<double[]> probs = new ArrayList<>();
        obm.normaliseAll(net,new int[0]);

        for (int i = 0; i < noChoices_; i++) {
            int[] removed = new int[i];
            for (int j = 0; j<i; j++) {
                removed[j]=choices[j];
            }
            obm.updateAll(net, removed);
            int node = choices[i];
            double [] nodeprobs = obm.getComponentProbabilities(net, node);
            probs.add(nodeprobs);
            net.recentlyPickedNodes_.add(node);
            if (net.recentlyPickedNodes_.size()>net.numRecents_) {
                net.recentlyPickedNodes_.remove(0);
            }
        }
        return probs;
    }

    public void updateLikelihoods(HashMap<double[],Double> likelihoods_,
                                  Network net, ObjectModel obm) {
        noChoices_=0;
        for (String node: leafNodeNames_) {
            if (!net.newNode(node)) {
                noChoices_++;
            }
        }
        if (noChoices_ < 5) {
            permList = new ArrayList<int[]>();
            int[] indices = new int[noChoices_];
            for (int j = 0; j < noChoices_; j++) {
                indices[j] = j;
            }
            generatePerms(0, indices);
        } else {
            permList = generateRandomShuffles(noChoices_, 50);
        }

        recents_ = net.recentlyPickedNodes_;
        int[] choices = new int[noChoices_+internalId_];
        int ind = 0;
        if (internal_) {
            choices[0]=net.nodeNameToNo(centreNodeName_);
            ind++;
        }
        for (String node: leafNodeNames_) {
            if (!net.newNode(node)) {
                choices[ind]=net.nodeNameToNo(node);
                ind++;
            }
        }

        obm.normaliseAll(net);

        for (double [] partition: likelihoods_.keySet()) {
            double probSum = 0.0;
            double like;
            for (int [] p : permList) {
                net.recentlyPickedNodes_=recents_;
                // Taking into account here the choice of the centre *IF* the star is an internal one.
                int[] nodes = new int[noChoices_+internalId_];
                if (internal_) {
                    nodes[0]=net.nodeNameToNo(centreNodeName_);
                }
                for (int j = 0; j<noChoices_; j++) {
                    nodes[internalId_+j]=choices[p[j]];
                }
                double probProd = 1.0;
                for (int i = 0; i < noChoices_+internalId_; i++) {
                    int[] removed = new int[i];
                    for (int j = 0; j<i; j++) {
                        removed[j]=nodes[j];
                    }
                    obm.updateAll(net,removed);
                    int node = nodes[i];
                    double [] nodeprobs = obm.getComponentProbabilities(net, node);
                    double prob = 0.0;
                    for (int j = 0; j < nodeprobs.length; j++){
                        prob+=nodeprobs[j]*partition[j];
                    }
                    net.recentlyPickedNodes_.add(node);
                    if (net.recentlyPickedNodes_.size()>net.numRecents_) {
                        net.recentlyPickedNodes_.remove(0);
                    }
                    probProd *= (net.noNodes_ - i);
                    probProd *= prob;
                }
                probSum += probProd;
            }
            like = Math.log(probSum) - Math.log(permList.size());
            likelihoods_.put(partition,likelihoods_.get(partition) + like);
        }
        noChoices_+=internalId_;
    }


    public void printMeanLike(double meanLike, ObjectModel om, Network network){
        for (String leaf : leafNodeNames_) {
            int node = network.nodeNameToNo(leaf);
            double prob = om.calcProbability(network,node);
            double normLike = prob/meanLike;
            System.out.println(time_+" "+normLike);
        }
    }

    public void generatePerms(int start, int[] input) {
        if (start == input.length) {
            permList.add(input.clone());
            return;
        }
        for (int i = start; i < input.length; i++) {
            int temp = input[i];
            input[i] = input[start];
            input[start] = temp;

            generatePerms(start+1,input);

            int temp2 = input[i];
            input[i] = input[start];
            input[start] = temp2;
        }
    }

    public String toString() {
        String str = time_+" STAR "+centreNodeName_+" LEAVES ";
        for (String leaf: leafNodeNames_) {
            str+=leaf+" ";
        }
        if (internal_){
            str+="INTERNAL";
        } else {
            str+="EXTERNAL";
        }
        str+=" "+noExisting_;
        return str;
    }
}
