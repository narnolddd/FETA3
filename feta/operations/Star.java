package feta.operations;

import feta.network.Link;
import feta.network.Network;
import feta.objectmodels.ObjectModel;

import java.util.ArrayList;

/** Class represents growth of network by a star. NB a single link may be considered a star with one leaf */

public class Star extends Operation {

    // Node at the centre of the star
    public String centreNodeName_;
    public int centreNode_;

    // Leaf nodes
    public String[] leafNodeNames_;
    public int[] leafNodes_;
    public boolean internal_;

    public Star(int noLeaves, boolean internal_){
        this.internal_=internal_;
        leafNodeNames_= new String[noLeaves];
        leafNodes_= new int[noLeaves];
        noChoices_=noLeaves;
    }

    public void build(Network net) {

        if (net.newNode(centreNodeName_)) {
            net.addNodeToList(centreNodeName_);
        }

        for(String leaf: leafNodeNames_) {
            if(net.newNode(leaf)) {
                net.addNodeToList(leaf);
            }
            net.addLink(centreNodeName_,leaf);
            net.noLinks_++;
        }
        net.latestTime_=time_;
    }

    public void pickCentreNode_(Network net, ObjectModel om) {
        if (internal_) {
            centreNode_ = om.nodeSampleWithoutReplacement(net, new int[0]);
            centreNodeName_=net.nodeNoToName(centreNode_);
        }
        else {
            centreNodeName_ = net.generateNodeName();
        }
    }

    public void pickLeafNodes_(Network net, ObjectModel om) {
        if (internal_) {
            int [] chosen_ = new int[1];
            chosen_[0]=centreNode_;
            leafNodes_=om.getNodesWithoutReplacement(net, leafNodes_.length, chosen_);
        } else {
            leafNodes_=om.getNodesWithoutReplacement(net, leafNodes_.length, new int[0]);
        }
    }

    public void fill(Network net, ObjectModel om) {
        pickCentreNode_(net, om);
        pickLeafNodes_(net, om);

        for(int leaf: leafNodes_) {
            String leafName = net.nodeNoToName(leaf);
            if (net.isLink(centreNodeName_,leafName) && !net.allowDuplicates_)
                continue;
            net.addNewLink(centreNodeName_, leafName, time_);
        }
    }

    public String toString() {
        String str = time_+" STAR "+centreNodeName_+" LEAVES ";
        for (String leaf: leafNodeNames_) {
            str+=leaf+" ";
        }
        if (internal_){
            str+="INTERNAL";
        } else {
            str+="EXTERNAL";
        }

        return str;
    }

    public double calcLogLike(Network net, ObjectModel obm) {
        double logSum = 0.0;
        double logRand = 0.0;
        double probUsed = 0.0;
        double randUsed = 0.0;
        for (String leaf: leafNodeNames_) {
            if (!net.newNode(leaf)) {
                int node = net.nodeNameToNo(leaf);
                double prob = obm.calcProbability(net,node);
                if (prob <= 0) {
                    System.err.println("Node returned zero probability");
                    System.exit(0);
                }
                // Log Likelihood is calculated without replacement
                logSum+= Math.log(prob) - Math.log(1 - probUsed);
                logRand+=Math.log(1.0/net.noNodes_) - Math.log(1 - randUsed);
                randUsed+= 1.0/net.noNodes_;
                probUsed+= prob;
            }
            else continue;
        }
        return logSum - logRand;
    }

    public ArrayList<double[]> getComponentProbabilities(Network net, ObjectModel obm) {
        ArrayList<double[]> probs = new ArrayList<>();
        for (String leaf: leafNodeNames_) {
            if (!net.newNode(leaf)) {
                int node = net.nodeNameToNo(leaf);
                double[] nodeprobs = obm.getComponentProbabilities(net, node);
                probs.add(nodeprobs);
            }
            else continue;
        }
        return probs;
    }


    public void printMeanLike(double meanLike, ObjectModel om, Network network){
        for (String leaf : leafNodeNames_) {
            int node = network.nodeNameToNo(leaf);
            double prob = om.calcProbability(network,node);
            double normLike = prob/meanLike;
            System.out.println(time_+" "+normLike);
        }
    }
}
