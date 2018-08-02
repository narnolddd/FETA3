package feta.network;

import java.util.ArrayList;
import java.util.TreeMap;

public class DirectedNetwork extends Network {

    /** Hashmaps containing info about node inlinks and outlinks */
    private TreeMap<Integer, ArrayList<Integer>> inLinks_;
    private TreeMap<Integer, ArrayList<Integer>> outLinks_;

    public DirectedNetwork() {
        inLinks_ = new TreeMap<Integer, ArrayList<Integer>>();
        outLinks_ = new TreeMap<Integer, ArrayList<Integer>>();
    }

    public void addNode(int nodeno) {
        inLinks_.put(nodeno, new ArrayList<Integer>());
        outLinks_.put(nodeno, new ArrayList<Integer>());
    }

    public void addLink(int src, int dst){
        inLinks_.get(dst).add(src);
        outLinks_.get(src).add(dst);
    }

    public void removeLinks(String nodename) {
        // Work in progress
    }

    public double getAverageInDegree(){
        double avgInDeg = (double)noLinks_/noNodes_;
        return avgInDeg;
    }

    public double getAverageOutDegree(){
        return getAverageInDegree();
    }

}
