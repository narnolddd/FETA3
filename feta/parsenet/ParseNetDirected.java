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
        net_=net;
    }

    public ArrayList<Operation> parseNewLinks(ArrayList <Link> links, Network net) {
        ArrayList<Operation> newOps = new ArrayList<Operation>();
        if (links.size()==1) {
            newOps.add(processAsLink(links.get(0),net));
            //System.out.println("Processing as link");
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
		Set<String> intersect_ = new HashSet<String>();
		Set<String> leaves= new HashSet<String>();
		intersect_.add(l.sourceNode_);
		leaves.add(l.destNode_);
		String sourceType= l.sourceNodeType_;
		String destType= l.destNodeType_;
		for (int j=1; j < links.size(); j++) {
			Set<String> src = new HashSet<String>();
			src.add(links.get(j).sourceNode_);;
			intersect_.retainAll(src);
			leaves.add(links.get(j).destNode_);
		}
		if (intersect_.size()==0){
			//System.out.println("Intersect Processing events as links for this time "+links.get(0).time_);
			for (Link l1: links) {
				opsSoFar.add(processAsLink(l1,net));
				System.out.println("Added op");
			}
			return opsSoFar;
		}
		leaves.removeAll(intersect_);
		String sourceNode= intersect_.iterator().next();
		Star op;
		if (net.newNode(sourceNode)) {
			op = new Star(leaves.size(), sourceType,destType, false);
		} else {
			op= new Star(leaves.size(), sourceType,destType,true);
		}

		int noExisting = 0;
		for (String leaf: leaves) {
			if (!net.newNode(leaf)) {
				noExisting++;
			}
		}
		op.setNoExisting(noExisting);
		op.setCentreNode(sourceNode);
		op.setTime(links.get(0).time_);
		op.setLeaves(Methods.toStringArray(leaves));
		opsSoFar.add(op);
        return opsSoFar;
    }
		
            


}
