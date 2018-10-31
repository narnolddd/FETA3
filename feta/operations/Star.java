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

    public ArrayList<String> choices_;
    public boolean internal_;

    public Star(int noLeaves, boolean internal_){
        this.internal_=internal_;
        leafNodeNames_= new String[noLeaves];
        leafNodes_= new int[noLeaves];
    }

    public void build(Network net) {

        if (net.newNode(centreNodeName_)) {
            net.addNodeToList(centreNodeName_);
        }

        for(String leaf: leafNodeNames_) {
            if(net.newNode(leaf)) {
                net.addNodeToList(leaf);
            }
            net.addNewLink(centreNodeName_, leaf, time_);
        }
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
        }
    }

    public void fill(Network net, ObjectModel om) {
        pickCentreNode_(net, om);
        pickLeafNodes_(net, om);

        for(String leaf: leafNodeNames_) {
            net.addNewLink(centreNodeName_, leaf, time_);
        }
    }

}
