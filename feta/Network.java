package feta;

import com.sun.jdi.event.ExceptionEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/** Class representing state of a network - abstract to cover both directed and undirected graphs. */

public abstract class Network {

    /** Properties and methods performed by all networks */

    private int noNodes_;
    private int noLinks_;
    private int latestNodeNo_=0;

    private HashMap <String, Integer> nodeNumbers_ = null;
    private HashMap <Integer, String> nodeNames_ = null;
    private HashMap <Integer, ArrayList <Integer>> outLinks_= null;
    private HashMap <Integer, ArrayList <Integer>> inLinks_= null;
    private ArrayList <Integer> nodes_= null;


    /** Add a new node to all data structures */

    public void addNode(String nodeName) {
        nodes_.add(latestNodeNo_);
        int nodeNo= latestNodeNo_;
        latestNodeNo_++;
        outLinks_.put(nodeNo,new ArrayList<Integer>());
        inLinks_.put(nodeNo,new ArrayList<Integer>());
        nodeNumbers_.put(nodeName,nodeNo);
        nodeNames_.put(nodeNo,nodeName);
    }

    /** Remove hanging edges for safe deletion of a node from data structures. IMPLEMENT AT YOUR PERIL */

    public void removeOutLinks(String nodeName) {
        // Work in progress
        int nodeNo_= nodeNameToNo(nodeName);

    }

    /** For quick switching between node names and numbers */

    public int nodeNameToNo(String name)
    {
        return nodeNumbers_.get(name);
    }

    public String nodeNoToName(int node)
    {
        return nodeNames_.get(node);
    }

    /** Abstract method for adding link between two nodes */
    public void addLink() {}

}