package feta.writenet;

import feta.network.Link;
import java.util.ArrayList;

public class WriteNetNN extends WriteNet{

    public WriteNetNN(ArrayList<Link> links){
        super(links);
    }

    public String linkToString(Link link) {
        return link.sourceNode_+sep_+link.destNode_+"\n";
    }
}
