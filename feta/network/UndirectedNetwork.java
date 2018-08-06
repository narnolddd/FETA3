package feta.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;


public class UndirectedNetwork extends Network {

    /** Hashmap containing list of node's neighbours */
    private TreeMap<Integer, ArrayList<Integer>> neighbours_;
    private int[] degreeDist_;
    private int degArraySize_ = 1000;

    public UndirectedNetwork() {
        neighbours_= new TreeMap<Integer, ArrayList<Integer>>();

        degreeDist_= new int[degArraySize_];
        for (int i = 0; i < degArraySize_; i++) {
            degreeDist_[i] = 0;
        }
    }

    /** Adds undirected link to data structures */
    public void addLink(int src, int dst) {
        neighbours_.get(src).add(dst);
        neighbours_.get(dst).add(src);

        incrementDegDist(getDegree(src));
        incrementDegDist(getDegree(dst));
    }

    public void addLink(String src, String dst) {
        int n1 = nodeNameToNo(src);
        int n2 = nodeNameToNo(dst);
        addLink(n1, n2);
    }

    public boolean isLink(int a, int b) {
        if (neighbours_.get(a).contains(b)) {
            return true;
        }
        return false;
    }

    /** Increments the degree distribution array in the correct place */
    public void incrementDegDist(int degree) {
        // May need to increase degree distribution array size.
        if (degree < degArraySize_) {
            degreeDist_[degree]++;
        } else {
            int[] newDegDist = new int[degArraySize_*2];
            System.arraycopy(degreeDist_, 0, newDegDist, 0, degArraySize_);
            Arrays.fill(newDegDist, degArraySize_, degArraySize_*2, 0);
            degArraySize_*=2;
            incrementDegDist(degree);
        }
    }

    public void addNode(int nodeno) {
        neighbours_.put(nodeno, new ArrayList<Integer>());
    }

    public void removeLinks(String nodename) {
        // Why on earth would you want to do this, please don't!
    }

    /** Returns degree of a node */
    public int getDegree(int nodeno) {
        return neighbours_.get(nodeno).size();
    }

    /** Gets average degree of network */
    public double getAverageDegree(){
        double avgDeg = 2.0 * noLinks_/noNodes_;
        return avgDeg;
    }

    /** Returns mean squared degree of network */
    public double getSecondMoment() {
        double sum = 0.0;
        for (int i = 0; i < noNodes_; i++) {
            double deg = getDegree(i);
            sum += deg*deg;
        }
        if (noNodes_ > 0) {
            return sum/noNodes_;
        }
        return 0.0;
    }

    /** Returns number of pairs of neighbours of node that are themselves neighbours */
    public int triangleCount(int node) {
        if(duplicatesPresent_) {
            System.out.println("Warning: Triangle count is badly defined in non-simple networks.");
        }
        int count = 0;
        for (int i = 0; i < getDegree(node); i++) {
            for (int j = 0; j <i; j++) {
                int n1 = neighbours_.get(node).get(i);
                int n2 = neighbours_.get(node).get(j);
                if (isLink(n1, n2)) {
                    count++;
                }
            }
        }
        return count;
    }

    /** Calculates local clustering of node */
    public double localCluster(int node) {
        if (getDegree(node) == 0 || getDegree(node) == 1) {
            return 0.0;
        }
        double actualTriangles = triangleCount(node);
        double possibleTriangles = 0.5 * getDegree(node) * (getDegree(node) - 1);
        return actualTriangles/possibleTriangles;
    }

    /** Average clustering across network */
    public double getAverageCluster() {
        double sum = 0.0;
        for (int i = 0; i < noNodes_; i++) {
            sum+= localCluster(i);
        }
        if (sum == 0.0) {return 0.0;}
        return sum/noNodes_;
    }



}
