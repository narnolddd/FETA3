package feta.network;

import com.sun.jdi.event.ExceptionEvent;
import feta.readnet.ReadNet;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/** Class representing state of a network - abstract to cover both directed and undirected graphs. */

public abstract class Network {

    /** Properties and methods performed by all networks */

    public int noNodes_;
    public int noLinks_;

    /** For tracking time in network building */
    private int latestNodeNo_; // Number of nodes built
    public long latestTime_; // Time at which the most recent link was added

    /** Options for reading network */
    public boolean duplicatesPresent_=false;
    public ReadNet networkReader_;

    /** Data structures mapping node names to their index and vice versa */
    private HashMap <String, Integer> nodeNumbers_;
    private HashMap <Integer, String> nodeNames_;
    private ArrayList <Integer> nodes_;

    /** List of links read from the network edgelist file */
    public ArrayList <Link> linksToBuild_;

    /** Name for new nodes */
    public static final String artificialNodeName= "NODE--NUMBER--";

    /** Initialise an empty network */
    public Network() {
        noNodes_= 0;
        noLinks_= 0;
        nodes_= new ArrayList<Integer>();
        linksToBuild_= new ArrayList<Link>();
        nodeNumbers_= new HashMap <String, Integer> ();
        nodeNames_= new HashMap <Integer, String>();
        latestNodeNo_=0;
        latestTime_=0;
    }

    /** Build network from list of links */

    public void buildUpTo(long time) {
        ArrayList<Link> remaining_ = linksToBuild_;
        int i = 0;
        while (true) {
            Link link = linksToBuild_.get(i);
            if (link.time_ > time)
                break;
            String src = link.sourceNode_;
            String dst = link.destNode_;
            if (newNode(src)) {
                addNodeToList(src);
            }
            if (newNode(dst)) {
                addNodeToList(dst);
            }
            addLink(src,dst);
            remaining_.remove(link);
            latestTime_= link.time_;
            noLinks_++; i++;
        }
        linksToBuild_=remaining_;
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

    /** Calls the addLink method on the integers corresponding to the string nodenames */
    public void addLink(String src, String dst) {
        int n1 = nodeNameToNo(src);
        int n2 = nodeNameToNo(dst);
        addLink(n1, n2);
    }

    /** Read links from network file */
    public void getLinksFromFile() {
        networkReader_.readNetwork();
        linksToBuild_=networkReader_.links_;
    }



    /** Set which reader to use */
    public void setNetworkReader(ReadNet rn) {
        networkReader_=rn;
    }

    /** Add node (implemented by relevant network class */
    public abstract void addNode(int nodeno);

    /** Abstract method for adding link between two nodes */
    public abstract void addLink(int src, int dst);

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

    /** Generates a node name for a node generated from a network model */
    public String generateNodeName() {
        return artificialNodeName+noNodes_;
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