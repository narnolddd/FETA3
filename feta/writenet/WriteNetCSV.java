package feta.writenet;

import feta.FetaOptions;
import feta.network.Link;
import feta.network.Network;
import feta.network.NodeTypes;

import java.util.ArrayList;
import java.util.HashMap;

public class WriteNetCSV extends WriteNet {

    private NodeTypes nt;
    private boolean includeTime_=true;
    private boolean includeTypes_=false;

//    public WriteNetCSV(ArrayList<Link> links, FetaOptions options) {
//        super(links, options);
//        includeTime = options.isIncludeOutTimestamps();
//    }
//
//    public WriteNetCSV(NodeTypes nt, HashMap<String,Integer> nameToNo, ArrayList<Link> links, FetaOptions options) {
//        this(links,options);
//        this.nt = nt;
//    }

    public WriteNetCSV(Network net, String separator, String outputFile, boolean includeTime) {
        sep_=separator;
        network_=net;
        includeTime_=includeTime;
        networkOutput_=outputFile;
        if (network_.getNodeTypes() != null) {
            includeTypes_=true;
            nt = network_.getNodeTypes();
        }
    }

    public WriteNetCSV(Network net, String separator, String outputFile) {
        this(net,separator,outputFile,true);
    }

    @Override
    public String linkToString(Link link) {
        String line = link.sourceNode_+sep_+link.destNode_;
        if (includeTime_) {
            line+=(sep_+link.time_);
        }
        if (includeTypes_) {
            line+=(sep_+nt.getNodeType(network_.nodeNameToNo(link.sourceNode_))+sep_+nt.getNodeType(network_.nodeNameToNo(link.destNode_)));
        }
        return line;
    }
}
