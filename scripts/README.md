# About

Package contains JSON configuration files with instructions for the FETA software to parse.

## Structure

Each JSON file has three sections: DataOptions, Action and Model.

### Datafile 

The datafile contains the information about any input or output network files to be read/written by FETA. It is comprised as follows:

```JSON
{
"Data": {
    "GraphInputFile": "someFileName.dat",
    "InputType": "someInputType",
    "Separator": "\\s+",
    "Directed": false,
    "OutputType": "someOutputType",
    "GraphOutputFile": "someOtherFileName.dat"
    }
}
```

* The fields `GraphInputfile` and `GraphOutputFile` refer to the filenames of network edgelists to be inputted and outputted respectively, if applicable.
The default values for these are `seed_graphs/clique-5.dat` and `output/yyyyMMddHHmmss.dat` (i.e. a timestamped filename in the output folder).

* `Directed` specifies whether the the network should be treated as directed or not - default is `false`.

* `Separator` specifies the separator to use for reading edgelists - default is `\\s+` (i.e. tab). 

* Input and output types tell the network reader what type of edgelists to expect. The two options are `NN` (Node1 Node2) or 
`NNT` (Node1 Node2 Timestamp) where nodes are parsed as strings and timestamps as longs. Default option is `NNT` since FETA is 
geared up for temporal networks. If `NN` is chosen, each edge will be treated as arriving in the order it appears in the list, in
order to treat it as temporal.

### Action

The action section tells FETA what to do with the network file it has read in. Options are `Measure`, `Grow`, `Likelihood` and `FETAFile`.

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