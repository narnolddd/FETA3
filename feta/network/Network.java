package feta.network;

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
    public boolean allowDuplicates_=true;

    /** Data structures mapping node names to their index and vice versa */
    private HashMap <String, Integer> nodeNumbers_;
    private HashMap <Integer, String> nodeNames_;
    private ArrayList <Integer> nodes_;

    /** This bit is for aiming to get good triangle closure */
    public ArrayList<Integer> recentlyPickedNodes_;
    public int numRecents_=1;

    /** List of links read from the network edgelist file */
    public ArrayList <Link> linksToBuild_;
    public ArrayList <Link> linksBuilt_;

    /** Name for new nodes */
    public static final String artificialNodeName= "NODE--NUMBER--";

    /** Initialise an empty network */
    public Network() {
        noNodes_= 0;
        noLinks_= 0;
        nodes_= new ArrayList<Integer>();
        linksToBuild_= new ArrayList<Link>();
        linksBuilt_=new ArrayList<Link>();
        nodeNumbers_= new HashMap <String, Integer> ();
        nodeNames_= new HashMap <Integer, String>();
        latestNodeNo_=0;
        latestTime_=0;
        recentlyPickedNodes_= new ArrayList<>();
    }

    /** Build network from list of links */

    public void buildUpTo(long time) {
        ArrayList<Link> remaining_ = new ArrayList<Link>();
        int i;
        for (i=0; i < linksToBuild_.size(); i++) {
            Link link = linksToBuild_.get(i);
            if (link.time_ > time){
                remaining_=linksToBuild_;
                for (int j=0; j < i; j++) {
                    remaining_.remove(0);
                }
                break;
            }
            String src = link.sourceNode_;
            String dst = link.destNode_;
            if (newNode(src)) {
                addNodeToList(src);
            }
            if (newNode(dst)) {
                addNodeToList(dst);
            }
            addLink(src,dst);

            recentlyPickedNodes_.add(nodeNameToNo(dst));
            if (recentlyPickedNodes_.size()>numRecents_){
                recentlyPickedNodes_.remove(0);
            }

            noLinks_++;
            latestTime_=link.time_;
            linksBuilt_.add(link);
        }
        linksToBuild_=remaining_;
    }

    /** Is a given node new? */
    public boolean newNode(String node) {
        if (nodeNumbers_.get(node) == null) {
            return true;
        }
        return false;
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

    /** Makes buffered writers for writing degree distribution */
    public abstract void setUpDegDistWriters(String fname);
    public abstract void writeDegDist();
    public abstract void closeWriters() throws IOException;

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

    public boolean isLink(String a, String b) {
        if (!nodeNames_.containsValue(a) || !nodeNames_.containsValue(b)) {
            return false;
        }
        return isLink(nodeNumbers_.get(a), nodeNumbers_.get(b));
    }

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

    public int age(int node) {
        return noNodes_-node;
    }

    /** Generates a node name for a node generated from a network model */
    public String generateNodeName() {
        return artificialNodeName+noNodes_;
    }

    /** Get measurements */
    public abstract void calcMeasurements();
    public abstract String degreeVectorToString();

    /** Prints measurements to line */
    public abstract String measureToString();

    /** Growth */
    public abstract void addNewLink(String src, String dst, long time);

}