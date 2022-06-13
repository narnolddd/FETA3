package feta.network;

import feta.Methods;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

public class DirectedNetwork extends Network {

    /** Hashmaps containing info about node inlinks and outlinks */
    private TreeMap<Integer, ArrayList<Integer>> inLinks_;
    public TreeMap<Integer, ArrayList<Integer>> outLinks_;

    private int[] inDegreeDist_;
    private int[] outDegreeDist_;
    private int inDegArraySize_ = 1000;
    private int outDegArraySize_ = 1000;
    private int maxNodeNo_=1000;

    /** Doubles relating to measurements */
    public int maxInDeg_;
    public int maxOutDeg_;
    private int transitiveTri_=0;
    private int cyclicTri_=0;
    private int[] transitiveTriCount_;
    private int[] cyclicTriCount_;
    private BufferedWriter brIn_=null;
    private BufferedWriter brOut_=null;

    public DirectedNetwork() {
        inLinks_ = new TreeMap<Integer, ArrayList<Integer>>();
        outLinks_ = new TreeMap<Integer, ArrayList<Integer>>();

        inDegreeDist_= new int[inDegArraySize_];
        outDegreeDist_= new int[outDegArraySize_];
        transitiveTriCount_= new int[maxNodeNo_];
        cyclicTriCount_= new int[maxNodeNo_];

    }

    public void addNode(int nodeno) {
        if (nodeno >= maxNodeNo_) {
            int[] newTransTriCount = new int[2*maxNodeNo_];
            int[] newCyclicTriCount = new int[2*maxNodeNo_];
            System.arraycopy(transitiveTriCount_,0,newTransTriCount,0,maxNodeNo_);
            System.arraycopy(cyclicTriCount_, 0, newCyclicTriCount, 0, maxNodeNo_);
            transitiveTriCount_= newTransTriCount;
            cyclicTriCount_= newCyclicTriCount;
            maxNodeNo_ *= 2;
        }

        inLinks_.put(nodeno, new ArrayList<Integer>());
        outLinks_.put(nodeno, new ArrayList<Integer>());
        incrementInDegDist(0);
        incrementOutDegDist(0);
    }

    public void addLink(int src, int dst){
        if (isLink(src,dst) && !allowDuplicates_)
            return;
        inLinks_.get(dst).add(src);
        outLinks_.get(src).add(dst);
        incrementInDegDist(getInDegree(dst));
        reduceInDegDist(getInDegree(dst)-1);
        incrementOutDegDist(getOutDegree(src));
        reduceOutDegDist(getOutDegree(src)-1);
        if(trackCluster_) {
            closeTriangle(src, dst);
        }
    }

    public void closeTriangle(int src, int dst) {
        int newTransitiveTri=0;
        int newCyclicTri=0;

        for (int n1: outLinks_.get(src)) {
            if (n1 == dst){
                continue;
            }
            for (int n2: inLinks_.get(dst)) {
                if (n1==n2) {
                    transitiveTriCount_[src]++;
                    newTransitiveTri++;
                }
            }
        }

        for (int n1: inLinks_.get(src)) {
            if (n1 == dst) {
                continue;
            }
            for (int n2: outLinks_.get(dst)) {
                if (n2 == src) {
                    continue;
                }
                if (n1 == n2) {
                    cyclicTriCount_[src]++;
                    cyclicTriCount_[dst]++;
                    cyclicTriCount_[n1]++;
                    newCyclicTri++;
                }
            }
        }
        transitiveTri_+=newTransitiveTri;
        cyclicTri_+=newCyclicTri;
    }

    public double localTransitivity(int node) {
        int possibleTri = getOutDegree(node) * (getOutDegree(node) - 1);
        if (possibleTri == 0) {
            return 0.0;
        }
        return ((double) transitiveTriCount_[node])/possibleTri;
    }

    public boolean isLink(int a, int b) {
        return outLinks_.get(a).contains(b);
    }

    public void setUpDegDistWriters(String fname) {
        int dot = fname.lastIndexOf('.');
        String inDegFile = fname.substring(0,dot)+"InDeg"+fname.substring(dot);
        String outDegFile = fname.substring(0,dot)+"OutDeg"+fname.substring(dot);

        try {
            FileWriter fwIn = new FileWriter(inDegFile);
            brIn_ = new BufferedWriter(fwIn);
            FileWriter fwOut = new FileWriter(outDegFile);
            brOut_= new BufferedWriter(fwOut);
        } catch (IOException ioe) {
            System.err.println("Could not set up degree distribution writer.");
            ioe.printStackTrace();
        }
    }

    public void writeDegDist() {
        StringBuilder inString = new StringBuilder();
        StringBuilder outString = new StringBuilder();
        for (int i = 0; i < inDegArraySize_; i++) {
            inString.append(inDegreeDist_[i]).append(" ");
        }
        for (int j = 0; j < outDegArraySize_; j++) {
            outString.append(outDegreeDist_[j]).append(" ");
        }
        try {
            brIn_.write(inString+"\n");
            brOut_.write(outString+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[] getOutLinks(int node) {
        return Methods.toIntArray(outLinks_.get(node));
    }

    public void closeWriters() throws IOException {
        brIn_.close();
        brOut_.close();
    }

    public int getInDegree(int node) {
        return inLinks_.get(node).size();
    }

    public int getOutDegree(int node) {
        return outLinks_.get(node).size();
    }

    public int getTransitiveTri() {
        return transitiveTri_;
    }

    public int getCyclicTri() {
        return cyclicTri_;
    }

    public int getTotDegree(int node) {
        return getInDegree(node) + getOutDegree(node);
    }

    /** Increments degree distribution at appropriate index corresponding to new link */
    public void incrementInDegDist(int degree) {
        if (degree > maxInDeg_)
            maxInDeg_=degree;
        if (degree < inDegArraySize_) {
            inDegreeDist_[degree]++;
        } else {
            int[] newDegDist = new int[inDegArraySize_*2];
            System.arraycopy(inDegreeDist_, 0, newDegDist, 0, inDegArraySize_);
            Arrays.fill(newDegDist, inDegArraySize_, inDegArraySize_*2, 0);
            inDegreeDist_=newDegDist;
            inDegArraySize_*=2;
            incrementInDegDist(degree);
        }
    }

    /** Increments degree distribution at appropriate index corresponding to new link */
    public void incrementOutDegDist(int degree) {
        if (degree > maxOutDeg_)
            maxOutDeg_=degree;
        if (degree < outDegArraySize_) {
            outDegreeDist_[degree]++;
        } else {
            int[] newDegDist = new int[outDegArraySize_*2];
            System.arraycopy(outDegreeDist_, 0, newDegDist, 0, outDegArraySize_);
            Arrays.fill(newDegDist, outDegArraySize_, outDegArraySize_*2, 0);
            outDegreeDist_=newDegDist;
            outDegArraySize_*=2;
            incrementOutDegDist(degree);
        }
    }

    public void reduceInDegDist(int degree) {
        inDegreeDist_[degree]--;
    }

    public void reduceOutDegDist(int degree) {
        outDegreeDist_[degree]--;
    }

    public void removeLinks(String nodename) {
        // Removes all links from or to a node. WHY WOULD YOU DO THIS???
    }

    public int[] getInDegreeDist() {return inDegreeDist_;}
    public int[] getOutDegreeDist() {return outDegreeDist_;}

    public String degreeVectorToString() {
        StringBuilder degs = new StringBuilder();
        for (int i = 0; i < noNodes_; i++) {
            degs.append(getInDegree(i)).append(" ");
        }
        return degs+"\n";
    }

    public void addNewLink(String src, String dst, long time) {
        linksToBuild_.add(new DirectedLink(src, dst, time));
    }

}
