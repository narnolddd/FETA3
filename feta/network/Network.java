package feta.network;

import com.sun.jdi.event.ExceptionEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/** Class representing state of a network - abstract to cover both directed and undirected graphs. */

public abstract class Network {

    /** Properties and methods performed by all networks */

    public int noNodes_;
    public int noLinks_;
    private int latestNodeNo_=0;

    private HashMap <String, Integer> nodeNumbers_ = null;
    private HashMap <Integer, String> nodeNames_ = null;
    private ArrayList <Integer> nodes_= null;

    /** Initialise an empty network */
    public Network() {
        noNodes_= 0;
        noLinks_= 0;
        nodeNumbers_= new HashMap <String, Integer> ();
        nodeNames_= new HashMap <Integer, String>();
    }


    /** Add a new node to all data structures */

    public void addNodeToList(String nodeName) {
        nodes_.add(latestNodeNo_);
        int nodeNo= latestNodeNo_;
        latestNodeNo_++;
        nodeNumbers_.put(nodeName,nodeNo);
        nodeNames_.put(nodeNo,nodeName);
        addNode(nodeNo);
    }

    /** Add node (implemented by relevant network class */
    public abstract void addNode(int nodeno);

    /** Abstract method for adding link between two nodes */
    public abstract void addLink(int src, int dst);

    /** Remove hanging edges for safe deletion of a node from data structures. IMPLEMENT AT YOUR PERIL */

    public abstract void removeLinks(String nodeName);

    /** For quick switching between node names and numbers */

    public int nodeNameToNo(String name)
    {
        return nodeNumbers_.get(name);
    }

    public String nodeNoToName(int node)
    {
        return nodeNames_.get(node);
    }

    /** Get number of links */
    public int getNoLinks() {
        return noLinks_;
    }

    /** Get number of nodes */
    public int getNoNodes() {
        return noNodes_;
    }


}