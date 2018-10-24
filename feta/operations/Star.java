package feta.operations;

import feta.network.Network;

import java.util.ArrayList;

/** Class represents growth of network by a star. NB a single link may be considered a star with one leaf */

public class Star extends Operation {

    // Node at the centre of the star
    public String centreNode_;

    // Leaf nodes
    public String[] leafNodes_;

    public ArrayList<String> choices_;
    public boolean internal_;

    public Star(int noLeaves){
        leafNodes_= new String[noLeaves];
    }

    public void build(Network net) {

        if (net.newNode(centreNode_)) {
            net.addNodeToList(centreNode_);
            internal_=false;
        }

        for(String leaf: leafNodes_) {
            if(net.newNode(leaf)) {
                net.addNodeToList(leaf);
            }
            net.addLink(centreNode_,leaf);
        }
    }

}
