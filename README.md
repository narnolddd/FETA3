# FETA3 (WIP, not yet up and running)

## About
FETA stands for "Framework for Evolving Topology Analysis", and is a multi-purpose java-based software for the analysis of evolving networks and models for their topology.

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

The single argument `scripts/[some-script-name].json` is a JSON config file telling FETA what action to perform. For more information on 
writing the JSON with some examples, have a look [here](scripts/README.md)