package feta.operations;

import feta.Methods;
import feta.network.Network;
import feta.network.UndirectedNetwork;
import feta.objectmodels.MixedModel;

import java.util.ArrayList;

public class Star2 extends Operation2{

    private int centreNode_;
    private int[] leafNodes_;
    private String centreNodeName_;
    private String[] leafNodeNames_;
    private boolean internal_;
    private int noExisting_;
    private int noLeaves_;

    public Star2(int noLeaves, boolean internal) {
        internal_=internal;
        noLeaves_=noLeaves;
    }

    public void bufferLinks(Network net) {
        for (String leaf: leafNodeNames_) {
            net.addNewLink(centreNodeName_,leaf,getTime());
        }
    }

    public void chooseNodes(Network net, MixedModel obm) {

    }

    public void setNodeChoices(boolean orderedData) {
        nodeChoices_= new ArrayList<int[]>();
        nodeChoices_.add(new int[] {centreNode_});
        if (orderedData) {
            for (int node: leafNodes_) {
                nodeChoices_.add(new int[] {node});
            }
        } else {
            nodeChoices_.add(leafNodes_);
        }
    }

    public void pickCentreNode_(Network net, MixedModel obm) {
        if (internal_) {
            centreNode_ = obm.nodeDrawWithoutReplacement(net, new int[0]);
            centreNodeName_=net.nodeNoToName(centreNode_);
        }
        else {
            centreNodeName_ = net.generateNodeName();
            centreNode_=net.noNodes_;
        }
    }

    public void pickLeafNodes_(Network net, MixedModel obm) {
        int[] internalLeaves;
        if (internal_ && net.getClass()==UndirectedNetwork.class) {
            int [] chosen_ = new int[1+((UndirectedNetwork) net).degrees_[centreNode_]];
            chosen_[0]=centreNode_;
            for (int n = 0; n < ((UndirectedNetwork) net).neighbours_.get(centreNode_).size(); n++) {
                chosen_[n+1] = ((UndirectedNetwork) net).neighbours_.get(centreNode_).get(n);
            }
            internalLeaves=obm.drawMultipleNodesWithoutReplacement(net, noExisting_, chosen_);
        } else {
            int [] chosen_ = new int[1];
            chosen_[0]=centreNode_;
            internalLeaves=obm.drawMultipleNodesWithoutReplacement(net, noExisting_, chosen_);
        }
        // Add new nodes
        int noNew_ = leafNodes_.length - noExisting_;
        int[] newLeaves= new int[noNew_];
        for (int i = 0; i < noNew_; i++) {
            String newName = net.generateNodeName();
            net.addNodeToList(newName);
            newLeaves[i]=net.nodeNameToNo(newName);
        }
        leafNodes_= Methods.concatenate(internalLeaves,newLeaves);
    }
}
