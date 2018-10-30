package feta.operations;

import feta.network.Network;
import feta.objectmodels.ObjectModel;

import java.util.ArrayList;

/** Class represents growth of network by a star. NB a single link may be considered a star with one leaf */

public class Star extends Operation {

    // Node at the centre of the star
    public String centreNodeName_;
    public int centreNode_;

    // Leaf nodes
    public String[] leafNodes_;

    public ArrayList<String> choices_;
    public boolean internal_;

    public Star(int noLeaves){
        leafNodes_= new String[noLeaves];
    }

    public void build(Network net) {

        if (net.newNode(centreNodeName_)) {
            net.addNodeToList(centreNodeName_);
            internal_=false;
        }

        for(String leaf: leafNodes_) {
            if(net.newNode(leaf)) {
                net.addNodeToList(leaf);
            }
            net.addLink(centreNodeName_,leaf);
        }
    }

    public void setCentreNode_(Network net, ObjectModel om) {
        if (internal_) {
            centreNode_ = om.nodeSampleWithoutReplacement(net, new int[0]);
            centreNodeName_=net.nodeNoToName(centreNode_);
        }
        else {
            centreNodeName_ = net.generateNodeName();
        }
    }

}
