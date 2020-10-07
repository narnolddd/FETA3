package feta.parsenet;

import feta.Methods;
import feta.actions.Likelihood;
import feta.network.Link;
import feta.network.Network;
import feta.network.UndirectedLink;
import feta.network.UndirectedNetwork;
import feta.operations.Operation;
import feta.operations.Star;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
            Set<String> leaves= new LinkedHashSet<String>();
            intersect_.add(l.sourceNode_);
            leaves.add(l.destNode_);
            leaves.add(l.sourceNode_);
            intersect_.add(l.destNode_);
            for (int j=1; j < links.size(); j++) {
                Set<String> newLink = new HashSet<String>();
                newLink.add(links.get(j).sourceNode_);
                newLink.add(links.get(j).destNode_);
                intersect_.retainAll(newLink);
                leaves.addAll(newLink);
            }
            if (intersect_.size()==0){
                //System.out.println("Processing events as links for this time "+links.get(0).time_);
                for (Link l1: links) {
                    newOps.add(processAsLink(l1,net));
                }
                return newOps;
            }
            leaves.removeAll(intersect_);
            String sourceNode= intersect_.iterator().next();
            Star op;
            if (net.newNode(sourceNode)) {
                op = new Star(leaves.size(), false);
            } else {
                op= new Star(leaves.size(), true);
            }

            int noExisting = 0;
            for (String leaf: leaves) {
                if (!net.newNode(leaf)) {
                    noExisting++;
                }
            }
            op.setNoExisting(noExisting);
            op.setLeaves(Methods.toStringArray(leaves));
            op.setCentreNode(sourceNode);
            op.namesToNodes(net);
            op.setTime(links.get(0).time_);
            newOps.add(op);
        }
        return newOps;
    }

}
