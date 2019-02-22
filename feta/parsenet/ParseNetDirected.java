package feta.parsenet;

import feta.network.DirectedNetwork;
import feta.network.Link;
import feta.network.Network;
import feta.operations.Operation;
import feta.operations.Star;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ParseNetDirected extends ParseNet{


    public ParseNetDirected(DirectedNetwork net){
        operations_= new ArrayList<Operation>();
        net_=net;
    }

    public ArrayList<Operation> parseNewLinks(ArrayList <Link> links, Network net) {
        ArrayList<Operation> newOps = new ArrayList<Operation>();
        if (links.size()==1) {
            newOps.add(processAsLink(links.get(0),net));
        } else {
            Link l = links.get(0);
            Set<String> intersect_ = new HashSet<String>();
            Set<String> leaves_= new HashSet<String>();
            intersect_.add(l.sourceNode_);
            leaves_.add(l.destNode_);
            for (int j=1; j < links.size(); j++) {
                Set<String> src = new HashSet<String>();
                src.add(links.get(j).sourceNode_);;
                intersect_.retainAll(src);
                leaves_.add(links.get(j).destNode_);
            }
            if (intersect_.size()==0){
                System.out.println("Processing events as links for this time "+links.get(0).time_);
                for (Link l1: links) {
                    newOps.add(processAsLink(l1,net));
                }
                return newOps;
            }
            leaves_.removeAll(intersect_);
            String sourceNode= intersect_.iterator().next();
            Star op_;
            if (net.newNode(sourceNode)) {
                op_ = new Star(leaves_.size(), false);
            } else {
                op_= new Star(leaves_.size(), true);
            }

            int ct = 0;
            for (String leaf: leaves_) {
                if (!net.newNode(leaf)) {
                    op_.noExisting_++;
                }
                op_.leafNodeNames_[ct] = leaf;
                ct++;
            }
            op_.centreNodeName_=sourceNode;
            op_.time_=links.get(0).time_;
            newOps.add(op_);
            operations_.add(op_);
        }
        return newOps;
    }

    public Operation processAsLink(Link l, Network net) {
        if (net.newNode(l.sourceNode_)) {
            Star op = new Star(1,false);
            op.centreNodeName_=l.sourceNode_;
            op.leafNodeNames_[0]=l.destNode_;
            op.time_=l.time_;
            return op;
        } else if (net.newNode(l.destNode_)) {
            Star op = new Star(1,false);
            op.centreNodeName_=l.destNode_;
            op.leafNodeNames_[0]=l.sourceNode_;
            op.time_=l.time_;
            return op;
        } else {
            Star op = new Star(1, true);
            op.centreNodeName_=l.sourceNode_;
            op.leafNodeNames_[0]=l.destNode_;
            op.time_=l.time_;
            return op;
        }
    }

}
