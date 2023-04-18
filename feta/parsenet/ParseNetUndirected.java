package feta.parsenet;

import feta.Methods;
import feta.network.Link;
import feta.network.Network;
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
        processedNodes_= new HashSet<>();
        net_=network;
        initialiseProcessedNodeSet();
    }

    public ArrayList<Operation> parseNewLinks(ArrayList <Link> links, Network net) {
        ArrayList<Operation> newOps = new ArrayList<Operation>();
        if (links.size()==1) {
            newOps.add(processAsLink(links.get(0),net));
        } else {
            Link l = links.get(0);
            if (l.sourceNodeType_ != null) {
				System.out.println("Typed nodes with undirected networks not yet implemented");
				System.exit(-1);
			}
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
            if (newNode(sourceNode)) {
                op = new Star(leaves.size(), null,null,false);
            } else {
                op= new Star(leaves.size(), null,null, true);
            }

            processedNodes_.add(sourceNode);
            int noExisting = 0;
            for (String leaf: leaves) {
                if (!newNode(leaf)) {
                    noExisting++;
                }
                processedNodes_.add(leaf);
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


    public Operation processAsLink(Link l, Network net) {
        Star op;
        if (newNode(l.sourceNode_)) {
            op = new Star(1,l.sourceNodeType_,l.destNodeType_,false);
            op.setCentreNode(l.sourceNode_);
            op.setLeaves(new String[] {l.destNode_});
//            net.addNode(l.sourceNode_);
        } else if (newNode(l.destNode_)) {
			op = new Star(1,l.destNodeType_,l.sourceNodeType_,false);
            op.setCentreNode(l.destNode_);
            op.setLeaves(new String[] {l.sourceNode_});
//            net.addNode(l.destNode_);
        } else {
            op = new Star(1,l.sourceNodeType_,l.destNodeType_,true);
            op.setCentreNode(l.sourceNode_);
            op.setLeaves(new String[] {l.destNode_});
        }
        if (!newNode(l.destNode_)) {
            op.setNoExisting(op.getNoExisting()+1);
        }
        processedNodes_.add(l.sourceNode_);
        processedNodes_.add(l.destNode_);
        op.setTime(l.time_);
        op.namesToNodes(net);
        System.out.println(op);
        return op;
    }
}
