package feta.writenet;

import feta.FetaOptions;
import feta.network.Link;
import feta.network.NodeTypes;

import java.util.ArrayList;
import java.util.HashMap;

public class WriteNetCSV extends WriteNet {

    private NodeTypes nt;
    private HashMap<String,Integer> nameToNo;
    private final boolean includeTime;

    public WriteNetCSV(ArrayList<Link> links, FetaOptions options) {
        super(links, options);
        includeTime = options.isIncludeOutTimestamps();
    }

    public WriteNetCSV(NodeTypes nt, HashMap<String,Integer> nameToNo, ArrayList<Link> links, FetaOptions options) {
        this(links,options);
        this.nt = nt;
        this.nameToNo = nameToNo;
    }

    @Override
    public String linkToString(Link link) {
        String line = link.sourceNode_+sep_+link.destNode_;
        if (includeTime) {
            line+=(sep_+link.time_);
        }
        if (nt!=null) {
            line+=(sep_+nt.getNodeType(nameToNo.get(link.sourceNode_))+sep_+nt.getNodeType(nameToNo.get(link.destNode_)));
        }
        return line;
    }
}
