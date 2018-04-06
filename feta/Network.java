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
    public ArrayList<Node> builtNodes_;
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

    // Mean degree (N.B. mean in degree = mean out degree)
    private double meanDeg_;
    // Singleton/Doubleton in count
    private int singletonInCount_;
    private int doubletonInCount_;
    // Singleton/Doubleton out count
    private int singletonOutCount_;
    private int doubletonOutCount_;
    // Maximum degrees
    private int maxInDegree_;
    private int maxOutDegree_;
    private int maxDegree_;
    // Mean degrees squared
    private double meanInDegSq_;
    private double meanOutDegSq_;
    private double meanDegSq_;
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
    // Is the action "measure"?
    private boolean measure;
    // Print the headers
    private boolean firstRunPrint = true;
    // Next measurement time
    private int nextMeasureTime;

    public Network(FetaOptions opt) {
        init();
        theNodes_ = new ArrayList<Node>();
        theLinks_ = new ArrayList<Link>();
        complexNetwork_= opt.complexNetwork_;
        directedNetwork_= opt.directedNetwork_;
        ignoreDuplicates_= opt.ignoreDuplicates_;
        ignoreSelfLinks_= opt.ignoreSelfLinks_;
        measureDegDist_= opt.measureDegDist_;
        measure = (opt.actionType_ == FetaOptions.ACTION_MEASURE);
        interval_=opt.actionInterval_;

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
        totTri_ = 0;
    }

    private void calcStats()
    {
        maxInDegree_=0;
        maxOutDegree_=0;
        maxDegree_=0;
        meanInDegSq_=0.0;
        meanOutDegSq_=0.0;
        assortIn_=0.0;
        assortOut_=0.0;
        singletonInCount_=0;
        singletonOutCount_=0;
        doubletonInCount_=0;
        doubletonOutCount_=0;

        noNodes_ = theNodes_.size();
        noLinks_ = 0;

        for (int i = 0; i < noNodes_; i++) {
            ArrayList<Node> inLinks = theNodes_.get(i).inLinks_;
            ArrayList<Node> outLinks = theNodes_.get(i).outLinks_;
            int inDeg = inLinks.size();
            int outDeg = outLinks.size();
            int degree = inDeg+outDeg;
            int localClust;
            noLinks_+= outDeg;
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
            meanDegSq_+= inDeg*inDeg;
            if (inDeg > maxInDegree_) {
                maxInDegree_ = inDeg;
            }
            if (outDeg > maxOutDegree_) {
                maxOutDegree_ = outDeg;
            }
            if (degree > maxDegree_) {
                maxDegree_ = degree;
            }
        }

        meanDeg_= 2*(double)noLinks_/noNodes_;
        meanInDegSq_/= noNodes_;
        meanOutDegSq_/= noNodes_;
        meanDegSq_/= noNodes_;
        double possTri = 2*noNodes_*(meanDegSq_ - meanDeg_);
        clusterCoeff_ = totTri_/possTri;
        noLinks_/= 2;
    }

    public ArrayList<Link> readNet(String file, int format) throws IOException
    {
        if (format == FetaOptions.NODE_NODE_TIME || format == FetaOptions.INT_INT_TIME) {
            return readNNT(file);
        } else if (format == FetaOptions.NODE_NODE_ || format == FetaOptions.INT_INT) {
            return readNN(file);
        }
        throw new IOException("Unknown file format for network read "+format);
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
            int order = 1;
            while ((Line = br.readLine()) != null) {
                Line = Line.trim();
                if (Line.length() == 0)
                    continue;
                String[] parts = Line.split("\\s+");
                if (parts.length == 0) {
                    continue;
                }
                if (parts.length != 2) {
                    throw new IOException("Expected two entries per line but read: "+Line);
                }
                String node1 = parts[0];
                String node2 = parts[1];
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

    // Makes bare edgelist from file in node-node-time format
    public ArrayList<Link> readNNT(String file) throws IOException {
        ArrayList<Link> linkz = new ArrayList<Link>();
        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String Line;
            int order = 1;
            while ((Line = br.readLine()) != null) {
                Line = Line.trim();
                if (Line.length() == 0)
                    continue;
                String[] parts = Line.split("\\s+");
                if (parts.length == 0) {
                    continue;
                }
                if (parts.length != 3) {
                    throw new IOException("Expected three entries per line but read: "+Line+"\n");
                }
                int time;
                try {
                    time = Integer.parseInt(parts[2]);
                } catch (Exception e) {
                    System.err.println(e);
                    throw new IOException("Could not interpret "+parts[2]+" as an integer time \n");
                }
                String node1 = parts[0];
                String node2 = parts[1];
                linkz.add(new Link(node1, node2, time));
            }
            in.close();
        }
        catch (Exception e){
            System.err.println(e);
        }
        return linkz;
    }

    // Builds the actual network up to stop time and spits out remaining (unbuilt) links
    public ArrayList<Link> buildNetwork(ArrayList<Link> linkList, long stopTime)
    { nextMeasureTime = 0;
        for(int i = 0; i < linkList.size(); i++) {
            Link link = linkList.get(i);
            if( link.time >= stopTime) {
                ArrayList <Link> remaining = new ArrayList<Link>();
                for(int j = i; j < linkList.size(); j++) {
                    remaining.add(linkList.get(j));
                }
                return remaining;
            }
            theLinks_.add(link);

            Node n1 = findNode(link.node1);
            if (n1 == null) {
                n1 = new Node(link.node1);
                noNodes_++;
                theNodes_.add(n1);
            }
            Node n2 = findNode(link.node2);
            if (n2 == null) {
                n2 = new Node(link.node2);
                theNodes_.add(n2);
                noNodes_++;
            }
            if (n1.IsOutLink(n2) && !complexNetwork_) {
                System.err.println("Uh-oh! Link exists twice in non-complex network");
                System.exit(-1);
            }
            n1.addOutLink(n2, link.time);
            n2.addInLink(n1, link.time);
            totTri_+=countTri(n1,n2);
            if(measure && link.time >= nextMeasureTime) {
                printNetworkStatistics();
                nextMeasureTime+=interval_;
            }
        }
        return new ArrayList<Link>();
    }

    public void printNetworkStatistics()
    {
        if (firstRunPrint) {
            System.out.println("time Nodes Links MaxIndegree MaxOutDegree MaxDegree SingIn SingOut DoubIn DoubOut "+
            "InDegSq OutDegSq");

            firstRunPrint = false;
        }

        calcStats();

        System.out.println(nextMeasureTime+" "+noNodes_+" "+noLinks_+" "+maxInDegree_+" "+maxOutDegree_+
        " "+maxDegree_+" "+singletonInCount_+" "+singletonOutCount_+" "+doubletonInCount_+" "+doubletonOutCount_
                +" "+" "+meanInDegSq_+" "+meanOutDegSq_+" "+totTri_+" "+clusterCoeff_);

    }

    public Node findNode(String nName) {
        for (int i = 0; i < noNodes_; i++) {
            Node node = theNodes_.get(i);
            if (node.name_.equals(nName))
            { return node;}
        }
        return null;
    }

    public int countTri(Node n1, Node n2) {
        int t = 0;
        ArrayList<Node> n1Neigh = new ArrayList<Node>();
        n1Neigh.addAll(n1.inLinks_);
        n1Neigh.addAll(n1.outLinks_);

        ArrayList<Node> n2Neigh = new ArrayList<Node>();
        n2Neigh.addAll(n2.inLinks_);
        n2Neigh.addAll(n2.outLinks_);

        for (int i = 0; i < n1Neigh.size(); i++)
        {
            for (int j = 0; j < n2Neigh.size(); j++)
            {
                if ( n1Neigh.get(i) == n2Neigh.get(j) )
                {
                    t++;
                }
            }
        }
        return t;
    }

    public void writeNetwork(String fname, int format) throws IOException {
        OutputStream os;
        if (fname == null) {
            os = System.out;
        } else {
            File f = new File(fname);
            os = new FileOutputStream(f,false);
        }
        if(format == FetaOptions.NODE_NODE_TIME) {
            writeNetworkNNT(os);
        } else if (format == FetaOptions.NODE_NODE_) {
            writeNetworkNN(os);
        } else {
            System.err.println("Unrecognised output graph format");
        }
        if (os != System.out) {
            os.close();
        }
    }

    private void writeNetworkNNT(OutputStream os) {
        PrintStream p = new PrintStream(os, false);
        for (Link l: theLinks_) {
            p.println(l.node1+" "+l.node2+" "+l.time);
        }
    }

    private void writeNetworkNN(OutputStream os) {
        PrintStream p = new PrintStream(os, false);
        for (Link l: theLinks_) {
            p.println(l.node1+" "+l.node2);
        }
    }

}