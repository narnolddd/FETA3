package feta.operations;

import feta.Methods;
import feta.network.Network;
import feta.network.UndirectedNetwork;
import feta.objectmodels.MixedModel;

import java.util.ArrayList;

public class Star extends Operation{

    private int centreNode_;
    private int[] leafNodes_;
    private String centreNodeName_;
    private String[] leafNodeNames_;
    private boolean internal_;
    private int internalId_;
    private int noExisting_;
    private int noLeaves_;

    public Star(int noLeaves, boolean internal) {
        internal_=internal;
        internalId_=internal_ ? 1 : 0;
        noLeaves_=noLeaves;
    }

    public void bufferLinks(Network net) {
        for (String leaf: leafNodeNames_) {
            net.addNewLink(centreNodeName_,leaf,getTime());
        }
    }

    public void chooseNodes(Network net, MixedModel obm) {
        pickCentreNode(net,obm);
        pickLeafNodes(net,obm);
    }

    public void setNodeChoices(boolean orderedData) {
        nodeChoices_= new ArrayList<int[]>();
        if (internal_) {
            nodeChoices_.add(new int[] {centreNode_});
        }
        if (orderedData) {
            for (int node: leafNodes_) {
                nodeChoices_.add(new int[] {node});
            }
        } else {
            nodeChoices_.add(leafNodes_);
        }
        filterNodeChoices();
    }

    public void setCentreNode(String centre) {
        centreNodeName_=centre;
    }

    public void setLeaves(String [] leaves) {
        leafNodeNames_=leaves;
    }

    public int getNoExisting() {
        return noExisting_;
    }

    public void setNoExisting(int noExisting) {
        noExisting_=noExisting;
    }

    public void pickCentreNode(Network net, MixedModel obm) {
        if (internal_) {
            centreNode_ = obm.nodeDrawWithoutReplacement(net, new int[0]);
            centreNodeName_=net.nodeNoToName(centreNode_);
        }
        else {
            centreNodeName_ = net.generateNodeName();
            centreNode_=net.nodeNameToNo(centreNodeName_);
        }
    }

    public void pickLeafNodes(Network net, MixedModel obm) {
        int[] internalLeaves;
        if (internal_) {
            int [] chosen_ = new int[1+net.getOutLinks(centreNode_).length];
            chosen_[0] = centreNode_;
            for (int n = 0; n < net.getOutLinks(centreNode_).length; n++) {
                chosen_[n+1] = net.getOutLinks(centreNode_)[n];
            }
            internalLeaves=obm.drawMultipleNodesWithoutReplacement(net, noExisting_, chosen_);
        } else {
            int [] chosen_ = new int[1];
            chosen_[0]=centreNode_;
            internalLeaves=obm.drawMultipleNodesWithoutReplacement(net, noExisting_, chosen_);
        }
        // Add new nodes
        int noNew = noLeaves_ - noExisting_;
        int[] newLeaves= new int[noNew];
        for (int i = 0; i < noNew; i++) {
            String newName = net.generateNodeName();
            newLeaves[i]=net.nodeNameToNo(newName);
        }
        leafNodes_= Methods.concatenate(internalLeaves,newLeaves);
        nodesToNames(net);
    }

    public void nodesToNames(Network net) {
        leafNodeNames_= new String[leafNodes_.length];
        for (int i = 0; i < leafNodes_.length; i++) {
            leafNodeNames_[i] = net.nodeNoToName(leafNodes_[i]);
        }
    }

    public void namesToNodes(Network net) {
        if (!net.newNode(centreNodeName_)) {
            centreNode_=net.nodeNameToNo(centreNodeName_);
        } else {
            centreNode_=-1;
        }
        leafNodes_= new int[noLeaves_];
        for (int i = 0; i < noLeaves_; i++) {
            String node = leafNodeNames_[i];
            if (net.newNode(node)) {
                leafNodes_[i] = -1;
            }
            else {
                leafNodes_[i] = net.nodeNameToNo(leafNodeNames_[i]);
            }
        }
    }

    public String toString() {
        String str = getTime()+" STAR "+centreNodeName_+" LEAVES ";
        for (String leaf: leafNodeNames_) {
            str+=leaf+" ";
        }
        if (internal_){
            str += "INTERNAL";
        } else {
            str += "EXTERNAL";
        }
        str += " " + noExisting_;
        return str;
    }
}
