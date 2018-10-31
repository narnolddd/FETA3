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

    /** Doubles relating to measurements */
    private double avgInDeg_;
    private double avgOutDeg_;
    private double meanInDegSq_;
    private double meanOutDegSq_;
    private double inAssort_;
    private double outAssort_;
    private int maxInDeg_;
    private int maxOutDeg_;

    public DirectedNetwork() {
        inLinks_ = new TreeMap<Integer, ArrayList<Integer>>();
        outLinks_ = new TreeMap<Integer, ArrayList<Integer>>();

        inDegreeDist_= new int[inDegArraySize_];
        outDegreeDist_= new int[outDegArraySize_];

        avgInDeg_= 0.0;
        avgOutDeg_= 0.0;
        meanInDegSq_= 0.0;
        meanOutDegSq_= 0.0;
        inAssort_= 0.0;
        outAssort_= 0.0;
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

    public double getMeanInDegSq() {
        double sum = 0.0;
        for (int i = 0; i < noNodes_; i++) {
            double inDeg = getInDegree(i);
            sum += inDeg*inDeg;
        }
        if (noNodes_> 0) {
            return sum/noNodes_;
        }
        return 0.0;
    }

    public double getMeanOutDegSq() {
        double sum = 0.0;
        for (int i = 0; i < noNodes_; i++) {
            double outDeg = getOutDegree(i);
            sum += outDeg*outDeg;
        }
        if (noNodes_> 0) {
            return sum/noNodes_;
        }
        return 0.0;
    }

    public double getInAssort() {
        double assSum = 0.0;
        double assProd = 0.0;
        double assSq = 0.0;

        for (int i = 0; i < noNodes_; i++) {
            ArrayList<Integer> links = inLinks_.get(i);
            for (int j = 0; j < links.size(); j++) {
                int l = links.get(j);
                if (l < i)
                    continue;
                int srcDeg = getInDegree(i);
                int dstDeg = getInDegree(l);
                assSum += 0.5 * (1.0/noLinks_) * (srcDeg + dstDeg);
                assProd += srcDeg * dstDeg;
                assSq += 0.5 * (1.0/noLinks_) * (srcDeg*srcDeg + dstDeg*dstDeg);
            }
        }
        double assNum = (1.0/noLinks_) * assProd - assSum * assSum;
        double assDom = assSq - assSum * assSum;
        return assNum/assDom;
    }

    public double getOutAssort_() {
        double assSum = 0.0;
        double assProd = 0.0;
        double assSq = 0.0;

        for (int i = 0; i < noNodes_; i++) {
            ArrayList<Integer> links = outLinks_.get(i);
            for (int j = 0; j < links.size(); j++) {
                int l = links.get(j);
                if (l < i)
                    continue;
                int srcDeg = getOutDegree(i);
                int dstDeg = getOutDegree(l);
                assSum += 0.5 * (1.0/noLinks_) * (srcDeg + dstDeg);
                assProd += srcDeg * dstDeg;
                assSq += 0.5 * (1.0/noLinks_) * (srcDeg*srcDeg + dstDeg*dstDeg);
            }
        }
        double assNum = (1.0/noLinks_) * assProd - assSum * assSum;
        double assDom = assSq - assSum * assSum;
        return assNum/assDom;
    }

    public void calcMeasurements() {
        avgInDeg_= getAverageInDegree();
        avgOutDeg_= getAverageOutDegree();
        meanInDegSq_= getMeanInDegSq();
        meanOutDegSq_= getMeanOutDegSq();
        inAssort_= getInAssort();
        outAssort_= getOutAssort_();
    }

    public String measureToString() {
        return noNodes_+" "+noLinks_+" "+avgInDeg_+" "+avgOutDeg_+" "+maxInDeg_+" "+maxOutDeg_+" "+meanInDegSq_+" "+
                meanOutDegSq_+" "+inAssort_+" "+outAssort_;
    }

    public void addNewLink(String src, String dst, long time) {
        linksToBuild_.add(new DirectedLink(src, dst, time));
    }

}
