package feta.writenet;

import feta.FetaOptions;
import feta.network.Link;
import feta.network.Network;

import java.util.ArrayList;

public class WriteNetNN extends WriteNet{

    public WriteNetNN(Network net, FetaOptions options){
        super(net, options);
    }

    public String linkToString(Link link) {
        return link.sourceNode_+sep_+link.destNode_;
    }
}
