package feta.parsenet;

import feta.actions.Likelihood;
import feta.network.Link;
import feta.network.Network;
import feta.network.UndirectedLink;
import feta.network.UndirectedNetwork;
import feta.operations.Operation;
import feta.operations.Star;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ParseNetUndirected extends ParseNet {

    public ParseNetUndirected(UndirectedNetwork network) {
        operations_= new ArrayList<Operation>();
        net_=network;
    }

    public void parseNetwork(UndirectedNetwork net, long start) {
        ArrayList<Link> links = net.linksToBuild_;
        net.buildUpTo(start);
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
            leaves_.add(l.sourceNode_);
            intersect_.add(l.destNode_);
            for (int j=1; j < links.size(); j++) {
                Set<String> newLink = new HashSet<String>();
                newLink.add(links.get(j).sourceNode_);
                newLink.add(links.get(j).destNode_);
                intersect_.retainAll(newLink);
                leaves_.addAll(newLink);
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

    /** Is network a star? */
    public boolean isStar(UndirectedNetwork net) { //Assume no links
        if (net.degreeDist_[1] == net.noNodes_-1 && net.degreeDist_[net.noNodes_-1]==1 || net.noLinks_==1)
            return true;
        System.out.println("Ambiguous growth operation at time "+net.latestTime_+". Processing as set of links.");
        return false;
    }
}
