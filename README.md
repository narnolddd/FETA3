![logo](FETAlogo.png)

# About
FETA stands for "Framework for Evolving Topology Analysis", and is a multi-purpose java-based software for the analysis of evolving networks and models for their topology,
based on [FETA2](https://github.com/richardclegg/FETA2)

## What can I use FETA for?

* Obtaining time series of measurements on growing directed and undirected networks
* Growing networks from a range of models and combinations of these
* Fitting models to timestamped network data.
* (Soon) investigating changes in network growth over time

## What can't I use FETA for?

* Networks with disappearing nodes or edges
* Weighted networks/multigraphs

# Building and running

## Requirements

This software runs using Java, so if you haven't already, install the latest version [here](https://www.java.com/en/download/). 

The jar file is built using Apache Ant, which can be found [here](https://ant.apache.org/).

Optionally, I have some scripts for gnuplot in the tutorial for plotting - you can find gnuplot [here](http://www.gnuplot.info/)

## Installation

First clone the repository, either manually or using the command: 

```bash
git clone https://github.com/narnolddd/FETA3.git
```

For everything else, let's now jump into the `FETA3` folder if you haven't already:

```bash
cd FETA3
```

## Building

Then run the command:

```bash
ant jar
```

to make the jar file.

## Running

Once you have the jar file, FETA is executed from the command line as:

```bash
java -jar feta3-1.0.0.jar scripts/[some-script-name].json
```

The single argument `scripts/[some-script-name].json` is a JSON config file telling FETA what action to perform. The next section describes how this works.

# Examples

## Obtaining time series of network measurements

Let's do an example of obtaining a time series of measurements from a timestamped network dataset. In the `data` folder there's a file 'cit-HepPh-ordered.txt'
which is a timestamped citation network dataset from ArXiV high energy physics, available from [SNAP](https://snap.stanford.edu/data/). We will 
use the script `MeasureCitations.json` in the `tutorial` folder, which looks like:

```JSON
{
  "Data": {
    "GraphInputFile": "data/cit-HepPh-ordered.txt",
    "GraphInputType": "NNT",
    "Directed": false
  },
  "Action": {
    "Measure": {
      "Start": 10,
      "Interval":100,
      "MaxNodes": 10000
    }
  }
}
```

The `Data` tag tells FETA what type of input to expect:

* The `GraphInputFile` tag should be self-explanatory. 
* `GraphInputType` allows either "NNT" or"NN" - this refers to whether or not the edgelist is timestamped. "NNT" is for edges 
which look like `NODE-1 NODE-2 TIMESTAMP`, "NN" for `NODE-1 NODE-2`. In the latter case, edges will be treated as arriving sequentially. 
* `Directed` refers to whether the edges should be treated as directed or not.

The `Action` tag tells FETA what to do with the inputted file; in this case we want to take measurements of it. We can specify:

* `Start` - time at which FETA will start taking measurements.

* `Interval` - time between measurements

* Stopping condition. This can either be a latest time `MaxTime`, maximum number of nodes `MaxNodes` or links `MaxLinks`. This specifies
how long the measurements should take place for - in this example the measurements will finish when the network size has reached 10000 nodes.

In the terminal, run the command 

```bash
java -jar feta3-1.0.0.jar tutorial/MeasureCitations.json > tutorial/CitationsTS.dat
```

It may take a minute or so to run, as the network file is quite big.

You should now have the file `CitationsTS.dat` in the `tutorial` folder. The columns are ordered as: timestamp, number of nodes,
number of links, average degree, density (number of links/number of possible links), maximum degree, average clustering coeff,
mean squared degree, degree assortativity.

To see what these time series look like, you can use your favourite plotting device. Mine happens to be [gnuplot](http://www.gnuplot.info/)
which is reasonably quick to download and usable from the terminal. If you have gnuplot installed, I have written a script for plotting these 
measurements you've just calculated - run the command:

```bash
gnuplot tutorial/CitationsTS.gnu
```

and there should now be some .eps plots in your `tutorial/plots` folder which should contain some looking something like this:

![maxdeg](README_pics/cit_maxdeg.jpg) 
![meandegsq](README_pics/cit_meandegsq.jpg)

![cluster](README_pics/cit_cluster.jpg)
![assort](README_pics/cit_assort.jpg)


Whilst these plots are informative, note that we have treated the citation network edgelist as undirected, whereas since citations are always 
*from* one paper to another, it may more naturally be considered a directed network. What happens if we do the same process but treat the 
network as directed? 

We're going to run the same command as before, but with the file `tutorial/MeasureCitationsDirected.json` which looks like:

```JSON
{
  "Data": {
    "GraphInputFile": "data/cit-HepPh-ordered.txt",
    "GraphInputType": "NNT",
    "Directed": true
  },
  "Action": {
    "Measure": {
      "Start": 10,
      "Interval":100,
      "MaxNodes": 10000
    }
  }
}
```

This is identical to the previous file `tutorial/MeasureCitations.json` apart from the `Directed` tag being changed from
`false` to `true`. 

Let's run the command 

```$xslt
java -jar feta3-1.0.0.jar tutorial/MeasureCitationsDirected.json > tutorial/CitationsTSDirected.dat
```

Now, notice that some of the measurements which made sense for undirected networks may not make sense or at the very least need some
adaptation for directed networks. For instance, 'average degree' will be split into two measurements of 'average in-degree' and 'average
out-degree'. Also, the clustering coefficient is now ill-defined as the concept of a triangle of nodes in directed networks doesn't make 
much sense now. With this in mind, the columns are ordered as: timestamp, number of nodes, number of links, average in-degree, average 
out-degree (both identical), maximum in-degree, maximum out-degree, mean squared in-degree, mean squared out-degree, and four measures of degree assortativity
(in-in, in-out, out-in, out-out).

Again, if you have gnuplot, run:

```$xslt
gnuplot tutorial/CitationsTSDirected.gnu
```

which will generate some .eps files in the plots folder. Compare with the plots generated when we treated the network as undirected.

![maxindeg](README_pics/cit_maxdeg_in.jpg) 
![maxoutdeg](README_pics/cit_maxdeg_out.jpg)

![meanindegsq](README_pics/cit_meandegsq_in.jpg)
![meanoutdegsq](README_pics/cit_meandegsq_out.jpg)

![assortii](README_pics/cit_assort_inin.jpg)
![assortio](README_pics/cit_assort_inout.jpg)

![assortoi](README_pics/cit_assort_outin.jpg)
![assortoo](README_pics/cit_assort_outout.jpg)



## Working with evolving network models

Whilst taking network measurements of timestamped is certainly one helpful feature of FETA, the main focus of the software is on 
a versatile modelling and model validation framework for evolving networks. For a mathematically detailed description of the framework, see the paper
[Likelihood-based assessment of dynamic networks](https://eprints.soton.ac.uk/397485/1/feta_comnet_2015.pdf) by Richard Clegg, Ben 
Parker and Miguel Rio. For the purpose of using the code, I'll give a basic overview.

Network growth models are specified in two parts. The first is the *Operation Model*, which determines the type of graph transformation
that occurs at each iteration. For example, adding a new node and connecting to 3 existing nodes, such as in the traditional Barabasi-Albert
model. The second part is the *Object Model*. This model takes a network topology (possibly with some metadata) and assigns a probability to
each of the nodes in the network to be chosen. For example, in the Barabasi-Albert model, the probability of a node being chosen is proportional
to its degree. 

### Operation Model

For the operation model, we consider just two very simple types of growth operations which we'll call *external* and *internal stars*.

Either a new node joins the network and connects to a number of (existing or new) nodes as below, which we term an *external star*:

![start](README_pics/startinggraph.png)  ![newnode](README_pics/newnode.png)

Alternatively, an existing node connects to a number of other existing nodes, which we call an *internal star*:

![start2](README_pics/startinggraph.png) ![oldnode](README_pics/oldnode.png)

The full operation model specifies a sequence of these operations for the network evolution. 

The current choices of Operation Model for the user are:
* `Email` - named as such to mimic email networks, with growth comprising a mixture of internal and external stars. User can specify
`NoRecipients` which is the number of links for each star, and `PropInternal` which specifies the probability that the star stems from
an internal node.
* `PreferentialAttachment` - named after the original Barabasi-Albert paper, with networks growing by addition of a single node connecting 
to a fixed number of existing nodes. User can specify `InitDegree`, the number of nodes each new node should connect to.

### Object Model

The Object Model assigns, at each timestep, attachment probabilities to each of the nodes. The different models currently loaded in
the software are:

* `RandomAttachment` - all nodes equally likely.
* `DegreeModelComponent` ([Barabasi-Albert](http://science.sciencemag.org/content/286/5439/509)) - nodes of higher degree are more attractive
* `DegreeWithAgeing` ([Dorogovtsev-Mendes](https://arxiv.org/pdf/cond-mat/0001419.pdf)) - like the above but older nodes become less 
attractive
* `RankPreferentialAttachment` ([Fortunato, Flammini, Menczer](https://arxiv.org/abs/cond-mat/0602081)) - highest ranked nodes are the most attractive

In addition, the modelling framework allows for flexibility in two ways:

#### Model Mixtures

Firstly, mixtures of object models may be used, for example a model that is 'half BA and half random' to capture multiple factors driving
attachment. 

#### Time varying models

Secondly, models are allowed to vary throughout the time of a network's evolution, for example, growing for the first 1000 timesteps
according to pure random attachment, from 1000-2000 as half-random half-BA, and 2000-3000 pure BA.

### Growing a network from a specified model

One of the capabilities of FETA is to generate networks from the modelling framework described. Let's look at an example growing script 
called `GrowExample1.json`.
For the sake of exhaustiveness, I've made it a bit more complicated than is probably necessary. 

```JSON

{
  "Data": {
    "GraphInputFile": "seed_graphs/clique-5.dat",
    "GraphOutputFile": "tutorial/GrowExample1.dat",
    "GraphInputType": "NNT",
    "GraphOutputType": "NNT",
    "Directed": false
  },
  "Action": {
    "Grow": {
      "Start": 1,
      "Interval": 10,
      "MaxTime": 3000
    }
  },
  "ObjectModel": [
    {
      "Start": 1,
      "End": 1000,
      "Components": [
        {
          "ComponentName": "feta.objectmodels.DegreeWithAgeing",
          "Weight": 1.0,
          "AgeingExponent": 0.8
        }
      ]
    },
    {
     "Start": 1000,
      "End": 2000,
      "Components": [
        {
          "ComponentName": "feta.objectmodels.RandomAttachment",
          "Weight": 0.5
        },
        {
          "ComponentName": "feta.objectmodels.DegreeModelComponent",
          "Weight": 0.5
        }
      ]
    },
    {
      "Start": 2000,
      "End": 3000,
      "Components": [
        {
          "ComponentName": "feta.objectmodels.RankPreferentialAttachment",
          "Weight": 1.0,
          "Alpha": 0.6
        }
      ]
    }
  ],
  "OperationModel":
  {
    "Name": "feta.operations.Email",
    "NoRecipients": 3,
    "PropInternal":0.2
  }
}
```

Run the command 
```bash
java -jar feta3-1.0.0.jar tutorial/GrowExample1.json
```

Let's get some measurements on the network we've grown. 

Run 

```bash
java -jar feta3-1.0.0.jar tutorial/MeasureArtificial.json > tutorial/ArtificialTS.dat
```

and finally get some measurements using:

```bash
gnuplot tutorial/ArtificialTS.gnu
```

(You should get something like the below in the `tutorial/plots` folder)

![maxdeg](README_pics/artificial_maxdeg.jpg)
![meandegsq](README_pics/artificial_meandegsq.jpg)

![cluster](README_pics/artificial_cluster.jpg)
![assort](README_pics/artificial_assort.jpg)


#### Subtleties

The object model must be fully specified for the whole time period that the network is growing. That is, the value for `End` tag on
the last `ObjectModel` must be at least the value for `MaxTime` in `Action`. Similarly, the last value of each subsequent time interval must
be equal to the start of the next (here 1-1000, 1000-2000, 2000-3000).

### Model likelihood

The main feature of the FETA software, along with generating networks, is to calculate likelihoods of network *object models* for a 
timestamped network dataset. Please refer to the [paper](https://eprints.soton.ac.uk/397485/1/feta_comnet_2015.pdf) for an in depth
description, but informally, a null hypothesis H0 of random (uniform) attachment is used, and the user may specify an alternative model H1.
The value c0 is given, which is a rescaling of the log-likelihood ratio of H1 to H0. One should interpret c0>1 as a model being more
likely than random attachment and vice versa for c0<1.

