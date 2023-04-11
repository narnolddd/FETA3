package feta.parsenet;

import feta.network.DirectedNetwork;
import feta.network.Link;
import feta.network.Network;
import feta.operations.Operation;
import feta.operations.Star;
import feta.network.NodeTypes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import feta.Methods;

public class ParseNetDirected extends ParseNet{


    public ParseNetDirected(DirectedNetwork net){
		operations_ = new ArrayList<Operation>();
		processedNodes_= new HashSet<>();
        net_=net;
    }

    public ArrayList<Operation> parseNewLinks(ArrayList <Link> links, Network net) {
        ArrayList<Operation> newOps = new ArrayList<Operation>();
        if (links.size()==1) {
            newOps.add(processAsLink(links.get(0),net));
            return newOps;
        } 
        while (links.size() > 0) {
			String sourceNodeType= links.get(0).sourceNodeType_;
			String destNodeType= links.get(0).destNodeType_;
			ArrayList <Link> procLinks= new ArrayList <Link> ();
			for (Link l: links) {
				if (NodeTypes.sameTypes(l,sourceNodeType,destNodeType)) {
					procLinks.add(l);
				}
			}
			//System.out.println("Processing events as links for this time "+procLinks.get(0).time_+" "+procLinks.size());
			newOps.addAll(parseTypedLinks(procLinks,net));
			links.removeAll(procLinks);
			//System.out.println("NewOps len"+newOps.size());
		}
        return newOps;
	}
	
	private ArrayList<Operation> parseTypedLinks(ArrayList <Link> links, Network net) {	
		/** Process a subset of links all of which are of the same type add the links to opsSoFar
		 */
		ArrayList<Operation> opsSoFar= new ArrayList<Operation>();
		Link l = links.get(0);
		Set<String> intersect = new HashSet<String>();
		Set<String> leaves= new HashSet<String>();
		intersect.add(l.sourceNode_);
		leaves.add(l.destNode_);
		String sourceType= l.sourceNodeType_;
		String destType= l.destNodeType_;
		for (int j=1; j < links.size(); j++) {
			Set<String> src = new HashSet<String>();
			src.add(links.get(j).sourceNode_);;
			intersect.retainAll(src);
			leaves.add(links.get(j).destNode_);
		}
		if (intersect.size()==0){
			//System.out.println("Intersect Processing events as links for this time "+links.get(0).time_);
			for (Link l1: links) {
				opsSoFar.add(processAsLink(l1,net));
				//System.out.println("Added op");
			}
			return opsSoFar;
		}
		leaves.removeAll(intersect);
		String sourceNode= intersect.iterator().next();
		Star op;
		if (newNode(sourceNode)) {
			op = new Star(leaves.size(), sourceType,destType, false);
		} else {
			op= new Star(leaves.size(), sourceType,destType,true);
		}

		int noExisting = 0;
		for (String leaf: leaves) {
			if (!newNode(leaf)) {
				noExisting++;
			}
			processedNodes_.add(leaf);
		}
		processedNodes_.add(sourceNode);
		op.setNoExisting(noExisting);
		op.setCentreNode(sourceNode);
		op.setLeaves(Methods.toStringArray(leaves));
		op.namesToNodes(net);
		op.setTime(links.get(0).time_);
		opsSoFar.add(op);
        return opsSoFar;
    }

    public Operation processAsLink(Link l, Network net) {
        Star op;
        if (newNode(l.sourceNode_)) {
            op = new Star(1,l.sourceNodeType_,l.destNodeType_,false);
            op.setCentreNode(l.sourceNode_);
            op.setLeaves(new String[] {l.destNode_});
//            net.addNode(l.sourceNode_);
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
        return op;
    }		
            


}
