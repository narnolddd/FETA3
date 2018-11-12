package feta.parsenet;

import feta.network.Link;
import feta.network.Network;
import feta.network.UndirectedLink;
import feta.network.UndirectedNetwork;
import feta.operations.Operation;
import feta.operations.Star;

import java.util.ArrayList;

public class ParseNetUndirected extends ParseNet {

    public ParseNetUndirected(UndirectedNetwork network) {
        operations_= new ArrayList<Operation>();
        net_=network;
    }

    public void parseNetwork(UndirectedNetwork net, long start) {
        ArrayList<Link> links = net.linksToBuild_;
        net.buildUpTo(start);
    }

    public void parseNewLinks(ArrayList <Link> links, Network net) {
        UndirectedNetwork newNet = new UndirectedNetwork();
        for (Link l: links){
            newNet.linksToBuild_.add(l);
        }
        newNet.buildUpTo(Long.MAX_VALUE);
        boolean done = false;
        while (!done) {

            if (isStar(newNet)) {
                Star op= new Star(newNet.noNodes_-1, false);
                int count = 0;
                for (int node = 0; node < newNet.noNodes_; node++){
                    // Get centre of the star
                    if (newNet.getDegree(node) == newNet.maxDeg_ && op.centreNodeName_==null) {
                        if (!net.newNode(newNet.nodeNoToName(node))) {
                          op.internal_=true;
                        }
                        op.centreNodeName_=newNet.nodeNoToName(node);
                        continue;
                    }
                    op.leafNodeNames_[count]=newNet.nodeNoToName(node);
                    count++;
                }
                op.time_=newNet.latestTime_;
                System.out.println(op);
                operations_.add(op);
                done=true;
            } else {
                for (Link link : links) {
                    String src = link.sourceNode_;
                    String dst = link.destNode_;
                    if (!net.newNode(src) && !net.newNode(dst)) {
                        Star st = new Star(1, true);
                        st.time_=link.time_;
                        st.centreNodeName_=src;
                        st.leafNodeNames_[0]=dst;
                        operations_.add(st);
                    } else if (net.newNode(src)) {
                        Star st = new Star(1, false);
                        st.time_=link.time_;
                        st.centreNodeName_=src;
                        st.leafNodeNames_[0]=dst;
                        operations_.add(st);
                    } else {
                        Star st = new Star(1, false);
                        st.time_=link.time_;
                        st.centreNodeName_=dst;
                        st.leafNodeNames_[0]=src;
                        operations_.add(st);
                    }
                }
                done=true;
            }


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
