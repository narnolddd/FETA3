package feta;

import com.sun.jdi.event.ExceptionEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Network {

    /** Network properties */
    public int noNodes_ = 0;
    public int noLinks_ = 0;
    public ArrayList<Node> theNodes_;
    public ArrayList<Link> theLinks_;
    public int[] inDegreeDistrib_;
    public int[] outDegreeDistrib_;
    // Size of degree distribution array
    public int degArraySize_ = 1000;
    private String growName_="Artificial-Node-";
    public int growNumber_=0;
    public int time_=0;

    /** Network statistics */

    // Triangle count
    public boolean trackTri_= false;
    public ArrayList<Integer> triCount_;
    public int totTri_=0;

    // Singleton/Doubleton in count
    private int singletonInCount_;
    private int doubletonInCount_;
    // Singleton/Doubleton out count
    private int singletonOutCount_;
    private int doubletonOutCount_;
    // Maximum degrees
    private int maxInDegree_;
    private int maxOutDegree_;
    // Mean degrees squared
    private double meanInDegSq_;
    private double meanOutDegSq_;
    // Clustering coefficient/transitivity
    private double clusterCoeff_;
    // Assortativity
    private double assortIn_;
    private double assortOut_;
    // Degree distribution
    private boolean measureDegDist_;
    private boolean trackDegreeDistrib_;

    // List of in & out links
    public ArrayList<int[]> inLinks_;
    public ArrayList<int[]> outLinks_;
    private ArrayList<long []> inLinksTime_= null;
    private ArrayList<long []> outLinksTime_= null;

    // Mapping node name to number & vice versa
    public HashMap<String, Integer> nameToNumber_;
    public ArrayList<String> numberToName_;

    /** Network options */
    // Multiple links a->b allowed
    public boolean complexNetwork_;
    // Is network directed?
    public boolean directedNetwork_;
    // Get rid of duplicated links?
    public boolean ignoreDuplicates_;
    // Ignore tadpoles?
    public boolean ignoreSelfLinks_;
    // Interval between measurements
    private int interval_;

    public Network(FetaOptions opt) {
        init();
        theNodes_ = new ArrayList<Node>();
        theLinks_ = new ArrayList<Link>();
        complexNetwork_= opt.complexNetwork_;
        directedNetwork_= opt.directedNetwork_;
        ignoreDuplicates_= opt.ignoreDuplicates_;
        ignoreSelfLinks_= opt.ignoreSelfLinks_;
        measureDegDist_= opt.measureDegDist_;
    }

    // Initialiser for network
    private void init() {
        time_= 0;
        noNodes_= 0;
        noLinks_= 0;
        trackDegreeDistrib_= false;
        degArraySize_= 1000;
        maxInDegree_=0;
        maxOutDegree_=0;
        meanInDegSq_=0.0;
        meanOutDegSq_=0.0;
        assortIn_=0.0;
        assortOut_=0.0;
        singletonInCount_=0;
        singletonOutCount_=0;
        doubletonInCount_=0;
        doubletonOutCount_=0;
    }

    private void calcStats()
    {
        maxInDegree_=0;
        maxOutDegree_=0;
        meanInDegSq_=0.0;
        meanOutDegSq_=0.0;
        assortIn_=0.0;
        assortOut_=0.0;
        singletonInCount_=0;
        singletonOutCount_=0;
        doubletonInCount_=0;
        doubletonOutCount_=0;

        for (int i = 0; i < noNodes_; i++) {
            ArrayList<Node> inLinks = theNodes_.get(i).inLinks_;
            ArrayList<Node> outLinks = theNodes_.get(i).outLinks_;
            int inDeg = inLinks.size();
            int outDeg = outLinks.size();
            if (inDeg == 1) {
                singletonInCount_++;
            }
            if (outDeg == 1) {
                singletonOutCount_++;
            }
            if (inDeg == 2) {
                doubletonInCount_++;
            }
            if (outDeg == 2) {
                doubletonOutCount_++;
            }
            meanInDegSq_+= inDeg*inDeg;
            meanOutDegSq_+= outDeg*outDeg;
            if (inDeg > maxInDegree_) {
                maxInDegree_ = inDeg;
            }
            if (outDeg > maxOutDegree_) {
                maxOutDegree_ = outDeg;
            }
        }

        meanInDegSq_/= noNodes_;
        meanOutDegSq_/= noNodes_;

    }

    // Makes bare edgelist from file in node-node format
    public ArrayList<Link> readNN(String file) throws IOException {
        ArrayList<Link> linkz = new ArrayList<Link>();
        int time = 1;
        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String Line;
            while ((Line = br.readLine()) != null) {
                Line = Line.trim();
                if (Line.length() == 0)
                    continue;
                String[] parts = Line.split("\\s+");
                if (parts.length == 0) {
                    continue;
                }
                if (parts.length != 2) {
                    throw new IOException("Expected two entries per line but read: " + Line);
                }
                Node node1 = findNode(parts[0]);
                Node node2 = findNode(parts[1]);
                linkz.add(new Link(node1, node2, time));
                time++;
            }
            in.close();
            }
            catch (Exception e){
            System.err.println(e);
        }
        return linkz;
    }

    //public ArrayList<Link> buildNetwork(ArrayList<Link> linkList, int stopTime)
    {

    }

    public Node findNode(String nName) {
        for (int i = 0; i < noNodes_; i++) {
            Node node = theNodes_.get(i);
            if (node.name_.equals(nName))
            { return node;}
        }
        Node newNode = new Node(null);
        theNodes_.add(newNode);
        time_++;
        return newNode;
    }

}