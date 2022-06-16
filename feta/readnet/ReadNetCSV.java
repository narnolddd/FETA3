package feta.readnet;

import feta.FetaOptions;
import feta.network.Link;

public class ReadNetCSV extends ReadNet {

    private final int sourceColumn_;
    private final int dstColumn_;
    private final int timeColumn_;
    private final int sourceTypeColumn_;
    private final int dstTypeColumn_;

    public ReadNetCSV(FetaOptions options) {
        super(options);
        sourceColumn_=options.getSourceColumn();
        dstColumn_=options.getDstColumn();
        timeColumn_=options.getTimeColumn();
        sourceTypeColumn_= options.getSourceTypeColumn();
        dstTypeColumn_= options.getDstTypeColumn();
    }

    @Override
    public Link parseLine(String line, long linkno) {
        String[] parts = line.split(sep_);
        String node1 = parts[sourceColumn_];
        String node2 = parts[dstColumn_];

        long time;
        // is there a time column
        if (parts.length == 3 || parts.length == 5) {
            time = Long.parseLong(parts[timeColumn_]);
        } else {
            time = linkno;
        }
        if (sourceTypeColumn_ < 0) {
			return lb_.build(node1, node2, time);
		}
        String node1type = parts[sourceTypeColumn_];
        String node2type = parts[dstTypeColumn_];
        return lb_.build(node1, node2, time,  node1type, node2type); 
    }
}
