package feta.network;

import feta.readnet.ReadNet;

import java.io.*;
import java.util.*;

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
    private final HashMap <String, Integer> nodeNumbers_;
    private final HashMap <Integer, String> nodeNames_;

    /** Typed network datastructures */
    private NodeTypes nodeTypes;

    /** This bit is for aiming to get good triangle closure */
    public ArrayList<Integer> recentlyPickedNodes_;
    public int numRecents_=15;
    public boolean trackCluster_;

    /** For minimising measurement calculation */
    public boolean changed_;

    /** List of links read from the network edgelist file */
    public ArrayList <Link> linksToBuild_;
    public ArrayList <Link> linksBuilt_;

    /** Name for new nodes */
    public static final String artificialNodeName= "NODE--NUMBER--";

    /** Initialise an empty network
     * @param reader reader type for the edgelist file
     * @param isTyped whether or not a typed network*/
    public Network(ReadNet reader, boolean isTyped) {
        noNodes_= 0;
        noLinks_= 0;
        linksToBuild_= new ArrayList<Link>();
        linksBuilt_=new ArrayList<Link>();
        nodeNumbers_= new HashMap <String, Integer> ();
        nodeNames_= new HashMap <Integer, String>();
        latestNodeNo_=0;
        latestTime_=0;
        recentlyPickedNodes_= new ArrayList<>();
        networkReader_=reader;
        if (isTyped) {
            nodeTypes = new NodeTypes();
        }
    }

    /** Build network from list of links */

    public void buildUpTo(long time) {
        ArrayList<Link> remaining_ = new ArrayList<Link>();
        int i;
        for (i=0; i < linksToBuild_.size(); i++) {
            Link link = linksToBuild_.get(i);
            if (link.time_ > time){
                changed_= i > 0;
                remaining_=linksToBuild_;
                if (i > 0) {
                    remaining_.subList(0, i).clear();
                }
                break;
            }
            String src = link.sourceNode_;
            String dst = link.destNode_;
            if (newNode(src)) {
                addNode(src);
            }
            if (newNode(dst)) {
                addNode(dst);
            }

            addLink(src,dst);
            updateRecents(nodeNameToNo(dst));

            noLinks_++;
            latestTime_=link.time_;
            linksBuilt_.add(link);
        }
        linksToBuild_=remaining_;
    }

    /** Is a given node new? */
    public boolean newNode(String node) {
        return nodeNumbers_.get(node) == null;
    }

    /** Add a new node to all data structures */
    public void addNode(String nodeName) {
        int nodeNo= latestNodeNo_;
        latestNodeNo_++;
        noNodes_++;
        nodeNumbers_.put(nodeName,nodeNo);
        nodeNames_.put(nodeNo,nodeName);
        addNode(nodeNo);
    }

    /** Add a typed node to all data structures */
    public void addNode(String nodeName, String nodeType) {
        addNode(nodeName);
        NodeTypes.setNodeType(nodeNumbers_.get(nodeName),nodeType);
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

    /** Update tracker of recently picked nodes */
    public void updateRecents(int node) {
        recentlyPickedNodes_.add(node);
        if (recentlyPickedNodes_.size()>numRecents_){
            recentlyPickedNodes_.remove(0);
        }
    }

    public void updateRecents(int[] nodes) {
        for (int node: nodes) {
            updateRecents(node);
        }
    }

    public int[] getRecentlyPickedNodes() {
        int [] recents = new int[numRecents_];
        for (int i=0; i < numRecents_; i++) {
            recents[i]=recentlyPickedNodes_.get(i);
        }
        return recents;
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

    /** Returns the nodes that a node "points to" */
    public abstract int[] getOutLinks(int node);

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
        String name = artificialNodeName+noNodes_;
        addNode(name);
        return name;
    }

    /** Get measurements */
    public abstract String degreeVectorToString();

    /** Growth */
    public abstract void addNewLink(String src, String dst, long time);

    public HashMap<String, Integer> getNodeNumbers() {
        return nodeNumbers_;
    }

    public NodeTypes getNodeTypes() {
        return nodeTypes;
    }

}