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
    private int latestNodeNo_;
    public boolean duplicatesPresent_;

    private HashMap <String, Integer> nodeNumbers_ = null;
    private HashMap <Integer, String> nodeNames_ = null;
    private ArrayList <Integer> nodes_= null;
    public ArrayList <Link> linksToBuild_;

    /** Initialise an empty network */
    public Network() {
        noNodes_= 0;
        noLinks_= 0;
        nodeNumbers_= new HashMap <String, Integer> ();
        nodeNames_= new HashMap <Integer, String>();
        latestNodeNo_=0;
    }

    /** Build network from list of links */

    public void buildUpTo(long time) {
        for (Link link: linksToBuild_) {
            if (link.getTime() > time)
                break;
            String src = link.getSourceNode();
            String dst = link.getDestNode();
            if (newNode(src)) {
                addNodeToList(src);
            }
            if (newNode(dst)) {
                addNodeToList(dst);
            }
            addLink(src,dst);
            noLinks_++;
        }
    }

    /** Is a given node new? */
    public boolean newNode(String node) {
        if (nodeNames_.containsValue(node)) {
            return false;
        }
        return true;
    }


    /** Add a new node to all data structures */

    public void addNodeToList(String nodeName) {
        nodes_.add(latestNodeNo_);
        int nodeNo= latestNodeNo_;
        latestNodeNo_++;
        noNodes_++;
        nodeNumbers_.put(nodeName,nodeNo);
        nodeNames_.put(nodeNo,nodeName);
        addNode(nodeNo);
    }

    /** Add node (implemented by relevant network class */
    public abstract void addNode(int nodeno);

    /** Abstract method for adding link between two nodes */
    public abstract void addLink(int src, int dst);

    /** Abstract method for adding link between two nodes */
    public abstract void addLink(String src, String dst);

    /** Is there a link between node a and node b? */
    public abstract boolean isLink(int a, int b);

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

    /** Get measurements */
    public abstract void calcMeasurements();

    /** Prints measurements to line */
    public abstract String measureToString();

}