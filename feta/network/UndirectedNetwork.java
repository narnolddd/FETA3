package feta.network;

import feta.Methods;
import feta.readnet.ReadNet;
import feta.objectmodels.components.ObjectModelComponent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;


public class UndirectedNetwork extends Network {

    /** Hashmap containing list of node's neighbours */
    public TreeMap<Integer, ArrayList<Integer>> neighbours_;
    public int[] degreeDist_;
    private int degArraySize_ = 1000;
    public int[] degrees_;
    private int maxNodeNumber = 1000;

    /** Variables related to measurements */
    public int maxDeg_;
    private int[] triCount_;
    private int totTri_;
    private BufferedWriter br_;

    public UndirectedNetwork(ReadNet reader, boolean isTyped) {
        super(reader, isTyped);
        neighbours_= new TreeMap<>();
        degreeDist_= new int[degArraySize_];
        degrees_= new int[maxNodeNumber];
        maxDeg_=0;
        triCount_= new int[maxNodeNumber];
        this.getLinksFromFile();
    }

    /** Adds undirected link to data structures */
    public void addLink(int src, int dst) {
        if (!allowDuplicates_){
            if (isLink(src,dst))
                System.out.println("Duplicate Link");
                    return;
        }
        neighbours_.get(src).add(dst);
        neighbours_.get(dst).add(src);

        if (src >= maxNodeNumber || dst >= maxNodeNumber) {
            int[] newDegrees_ = new int[2 * maxNodeNumber];
            System.arraycopy(degrees_, 0, newDegrees_, 0, maxNodeNumber);
            degrees_ = newDegrees_;
            maxNodeNumber *= 2;
        }
        degrees_[src]++;
        degrees_[dst]++;
        incrementDegDist(getDegree(src));
        reduceDegDist(getDegree(src)-1);
        incrementDegDist(getDegree(dst));
        reduceDegDist(getDegree(dst)-1);
        if(trackCluster_) {
            closeTriangle(src, dst);
        }
    }

    /** Updates node triangle counts if a new link closes a triangle */
    public void closeTriangle(int src, int dst) {
        int newTri = 0;

        for (int n1neighbour : neighbours_.get(src)) {
            if (n1neighbour == dst)
                continue;
            for (int n2neighbour: neighbours_.get(dst)) {
                if (n2neighbour == src)
                    continue;
                if (n1neighbour == n2neighbour) {
                    newTri++;
                    triCount_[n1neighbour]++;
                }
            }
        }
        if (newTri>0) {
            triCount_[src]+=newTri;
            triCount_[dst]+=newTri;
        }
        totTri_+=newTri;
    }

    public boolean isLink(int a, int b) {
        return neighbours_.get(a).contains(b);
    }

    @Override
    public int[] getOutLinks(int node) {
        return Methods.toIntArray(neighbours_.get(node));
    }

    public void setUpDegDistWriters(String fname) {
        int dot = fname.lastIndexOf('.');
        String degFile = fname.substring(0,dot)+"Deg"+fname.substring(dot);

        try {
            FileWriter fwIn = new FileWriter(degFile);
            br_ = new BufferedWriter(fwIn);
        } catch (IOException ioe) {
            System.err.println("Could not set up degree distribution writer.");
            ioe.printStackTrace();
        }
    }

    public void writeDegDist() {
        StringBuilder degString = new StringBuilder();
        for (int i = 0; i < degArraySize_; i++) {
            degString.append(degreeDist_[i]).append(" ");
        }
        try {
            br_.write(degString+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeWriters() throws IOException {
        br_.close();
    }

    /** Increments the degree distribution array in the correct place */
    public void incrementDegDist(int degree) {
        // May need to increase degree distribution array size.
        if (degree > maxDeg_)
            maxDeg_= degree;
        if (degree < degArraySize_) {
            degreeDist_[degree]++;
        } else {
            int[] newDegDist = new int[degArraySize_*2];
            System.arraycopy(degreeDist_, 0, newDegDist, 0, degArraySize_);
            Arrays.fill(newDegDist, degArraySize_, degArraySize_*2, 0);
            degreeDist_=newDegDist;
            degArraySize_*=2;
            incrementDegDist(degree);
        }
    }

    public void reduceDegDist(int degree) {
        degreeDist_[degree]--;
    }

    public void addNode(int nodeno) {
        if (nodeno>= maxNodeNumber) {
            int[] newDegrees_= new int[2*maxNodeNumber];
            int[] newTriCount_ = new int[2*maxNodeNumber];
            System.arraycopy(degrees_,0,newDegrees_,0,maxNodeNumber);
            System.arraycopy(triCount_,0,newTriCount_,0,maxNodeNumber);
            degrees_=newDegrees_;
            triCount_=newTriCount_;
            maxNodeNumber*=2;
        }
        neighbours_.put(nodeno, new ArrayList<>());
        incrementDegDist(0);
    }

    public void removeLatestNode() {
        neighbours_.remove(latestNodeNo_);
        degreeDist_[0]--;
    }

    public void removeLinks(String nodename) {
        // Not implemented as not used in any graph model
    }

    /** Returns degree of a node */
    public int getDegree(int nodeno) {
        return degrees_[nodeno];
    }

    public int getTriCount() {return totTri_;}

    public double getDensity() {
        double possibleLinks = 0.5 * noNodes_ * (noNodes_ - 1);
        return noLinks_/possibleLinks;
    }

    public int[] getDegreeDist() {
        return degreeDist_;
    }

    /** Calculates local clustering of node */
    public double localCluster(int node) {
        if (getDegree(node) == 0 || getDegree(node) == 1) {
            return 0.0;
        }
        double actualTriangles = triCount_[node];
        double possibleTriangles = 0.5 * getDegree(node) * (getDegree(node) - 1);
        return actualTriangles/possibleTriangles;
    }

    /** Return degree sequence as a string */
    public String degreeVectorToString() {
        StringBuilder degs = new StringBuilder();
        for (int i = 0; i < noNodes_; i++) {
            degs.append(getDegree(i)).append(" ");
        }
        return degs.toString();
    }

    /** Section related to growing networks */
    public void addNewLink(String src, String dst, String srcType, String dstType, long time) {
        if (srcType!=null) {
            linksToBuild_.add(new UndirectedLink(src,dst,time,srcType,dstType));
        } else {
            linksToBuild_.add(new UndirectedLink(src, dst, time));
        }
    }


    public double calcProbability(ObjectModelComponent omc, int node) {
        return omc.calcProbability(this, node);
    }
}
