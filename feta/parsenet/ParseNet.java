package feta.parsenet;

import feta.network.Link;
import feta.network.Network;
import feta.operations.Operation;

import java.util.ArrayList;

public abstract class ParseNet {

    Network net_;

    public ArrayList<Operation> operations_;

    public void parseNetwork(long start, long end) {
        net_.buildUpTo(start);
        while (true) {
            ArrayList<Link> links = net_.linksToBuild_;
            if (links.size() == 0)
                break;
            ArrayList<Link> linkSet = getNextLinkSet(links);
            long time = linkSet.get(0).time_;
            if (time>end)
                break;
            parseNewLinks(linkSet,net_);
            net_.buildUpTo(time);
        }
    }


    public ArrayList<Link> getNextLinkSet(ArrayList<Link> links) {
        ArrayList<Link> linkSet = new ArrayList<Link>();
        Link l1 = links.get(0);
        linkSet.add(l1);
        // Get links added at same time as l1
        for (int i = 1; i < links.size(); i++) {
            Link l2 = links.get(i);
            if (l2.time_>l1.time_)
                break;
            linkSet.add(l2);
        }
        return linkSet;
    }


    /** To be processed by directed/undirected parser - not much difference between the two */
    public abstract ArrayList<Operation> parseNewLinks(ArrayList<Link> links, Network net);

}
