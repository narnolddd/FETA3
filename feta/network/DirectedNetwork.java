package feta.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

public class DirectedNetwork extends Network {

    /** Hashmaps containing info about node inlinks and outlinks */
    private TreeMap<Integer, ArrayList<Integer>> inLinks_;
    private TreeMap<Integer, ArrayList<Integer>> outLinks_;

    private int[] inDegreeDist_;
    private int[] outDegreeDist_;
    private int inDegArraySize_ = 1000;
    private int outDegArraySize_ = 1000;

    public DirectedNetwork() {
        inLinks_ = new TreeMap<Integer, ArrayList<Integer>>();
        outLinks_ = new TreeMap<Integer, ArrayList<Integer>>();

        inDegreeDist_= new int[inDegArraySize_];
        outDegreeDist_= new int[outDegArraySize_];
    }

    public void addNode(int nodeno) {
        inLinks_.put(nodeno, new ArrayList<Integer>());
        outLinks_.put(nodeno, new ArrayList<Integer>());
    }

    public void addLink(int src, int dst){
        inLinks_.get(dst).add(src);
        outLinks_.get(src).add(dst);
        incrementInDegDist(getInDegree(dst));
        incrementOutDegDist(getOutDegree(src));
    }

    public void addLink(String src, String dst) {
        int n1 = nodeNameToNo(src);
        int n2 = nodeNameToNo(dst);
        addLink(n1, n2);
    }

    public boolean isLink(int a, int b) {
        if (outLinks_.get(a).contains(b)) {
            return true;
        }
        return false;
    }

    public int getInDegree(int node) {
        return inLinks_.get(node).size();
    }

    public int getOutDegree(int node) {
        return outLinks_.get(node).size();
    }

    public int getTotDegree(int node) {
        return getInDegree(node) + getOutDegree(node);
    }

    public void incrementInDegDist(int degree) {
        if (degree < inDegArraySize_) {
            inDegreeDist_[degree]++;
        } else {
            int[] newDegDist = new int[inDegArraySize_*2];
            System.arraycopy(inDegreeDist_, 0, newDegDist, 0, inDegArraySize_);
            Arrays.fill(newDegDist, inDegArraySize_, inDegArraySize_*2, 0);
            inDegArraySize_*=2;
            incrementInDegDist(degree);
        }
    }

    public void incrementOutDegDist(int degree) {
        if (degree < outDegArraySize_) {
            outDegreeDist_[degree]++;
        } else {
            int[] newDegDist = new int[outDegArraySize_*2];
            System.arraycopy(outDegreeDist_, 0, newDegDist, 0, outDegArraySize_);
            Arrays.fill(newDegDist, outDegArraySize_, outDegArraySize_*2, 0);
            outDegArraySize_*=2;
            incrementOutDegDist(degree);
        }
    }



    public void removeLinks(String nodename) {
        // Work in progress. Probably a terrible idea to implement.
    }

    public double getAverageInDegree(){
        double avgInDeg = (double)noLinks_/noNodes_;
        return avgInDeg;
    }

    public double getAverageOutDegree(){
        return getAverageInDegree();
    }


}
