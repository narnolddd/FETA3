# FETA3 (WIP, only partially running)

## About
FETA stands for "Framework for Evolving Topology Analysis", and is a multi-purpose java-based software for the analysis of evolving networks and models for their topology.

### What can I use FETA for?

* Obtaining time series of measurements on growing directed and undirected networks
* Growing networks from a range of models and combinations of these
* Fitting models to timestamped network data.
* (Soon) investigating changes in network growth over time

### What can't I use FETA for?

* Networks with disappearing nodes or edges
* Weighted networks/multigraphs

## Building and running

### Requirements

This software runs using Java, so if you haven't already, install the latest version [here](https://www.java.com/en/download/). 

The jar file is built using Apache Ant, which can be found [here](https://ant.apache.org/).

### Installation

First clone the repository, either manually or using the command: 

```$xslt
git clone https://github.com/narnolddd/FETA3.git
```

### Building

Then run the command:

```$xslt
ant jar
```

to make the jar file.

### Running

Once you have the jar file, FETA is executed from the command line as:

```$xslt
java -jar feta3-1.0.0.jar scripts/[some-script-name].json
```

The single argument `scripts/[some-script-name].json` is a JSON config file telling FETA what action to perform. The next section describes how this works.

## JSON Structure

Each JSON file has three sections: DataOptions, Action and Model.

### Datafile 

The datafile contains the information about any input or output network files to be read/written by FETA. It is comprised as follows:

```JSON
{
"Data": {
    "GraphInputFile": "someFileName.dat",
    "InputType": "someInputType",
    "InputLineSeparator": "\\s+",
    "Directed": false,
    "OutputType": "someOutputType",
    "GraphOutputFile": "someOtherFileName.dat"
    }
}
```

* The fields `GraphInputfile` and `GraphOutputFile` refer to the filenames of network edgelists to be inputted and outputted respectively, if applicable.
The default values for these are `seed_graphs/clique-5.dat` and `output/yyyyMMddHHmmss.dat` (i.e. a timestamped filename in the output folder).

* `Directed` specifies whether the the network should be treated as directed or not - default is `false`.

* `InputLineSeparator` specifies the separator to use for reading edgelists - default is `\\s+` (i.e. tab). 

* Input and output types tell the network reader what type of edgelists to expect. The two options are `NN` (Node1 Node2) or 
`NNT` (Node1 Node2 Timestamp) where nodes are parsed as strings and timestamps as longs. Default option is `NNT` since FETA is 
geared up for temporal networks. If `NN` is chosen, each edge will be treated as arriving in the order it appears in the list, in
order to treat it as temporal.

### Action

The action section tells FETA what to do with the network file it has read in. Options are `Measure`, `Grow`, `Likelihood` and `Translate`.

#### Measure

`Measure` will return a time series of network metrics in a TSV. For undirected networks, these measures are:

```$xslt
Timestamp #Nodes #Links  Average Degree  Network density  Maximum Degree  Average Clustering   Mean squared degree   Degree assortativity
```

and for directed networks, the same measures with in/out (and minus clustering coefficient) are returned.

Options for measuring networks are:

```JSON

{
  "Action": {
    "Measure": {
      "Start": 10,
      "Interval": 10,
      "MaxLinks": 300,
      "MaxNodes": 100,
      "MaxTime" : 1000
    }
  }
}

```

`Start` refers to when to start taking measurements (given as long value), `Interval` specifies the time granularity of the measurements, and
`MaxLinks`/`MaxNodes`/`MaxTime` altogether specify a stopping condition - the measurements will stop being taken when any one of these conditions are
reached. Not all of these need be specified, but at least one does.