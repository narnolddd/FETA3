package feta.writenet;

import feta.FetaOptions;
import feta.network.Link;
import feta.network.Network;

import java.util.ArrayList;

public class WriteNetNNT extends WriteNet {

    public WriteNetNNT(Network net, FetaOptions options){
        super(net, options);
    }

    public String linkToString(Link link) {
        return link.sourceNode_+sep_+link.destNode_+sep_+link.time_;
    }
}
