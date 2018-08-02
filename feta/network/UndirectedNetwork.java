package feta.network;

import java.util.ArrayList;
import java.util.TreeMap;


public class UndirectedNetwork extends Network {

    /** Hashmap containing list of node's neighbours */
    private TreeMap<Integer, ArrayList<Integer>> neighbours_;
    private ArrayList<Integer> degrees_;

    public UndirectedNetwork() {
        neighbours_= new TreeMap<Integer, ArrayList<Integer>>();
    }

    /** Adds undirected link to data structures */
    public void addLink(int src, int dst) {
        neighbours_.get(src).add(dst);

    }

    public void addNode(int nodeno) {
        neighbours_.put(nodeno, new ArrayList<Integer>());
    }

    public void removeLinks(String nodename) {
        // Work in progress
    }

    public int getDegree(int nodeno) {
        return neighbours_.get(nodeno).size();
    }

    public double getAverageDegree(){
        double avgDeg = 2.0 * noLinks_/noNodes_;
        return avgDeg;
    }

}
