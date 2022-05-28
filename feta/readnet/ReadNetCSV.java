package feta.readnet;

import feta.FetaOptions;
import feta.network.Link;

public class ReadNetCSV extends ReadNet {

    private int sourceColumn_;
    private int dstColumn_;
    private int timeColumn_;
    private int sourceTypeColumn_;
    private int dstTypeColumn_;

    public ReadNetCSV(FetaOptions options) {
        super(options);
        sourceColumn_=options.getSourceColumn();
        dstColumn_=options.getDstColumn();
        timeColumn_=options.getTimeColumn();
    }

    @Override
    public Link parseLine(String line, long linkno) {
        String[] parts = line.split(sep_);
        String node1 = parts[sourceColumn_];
        String node2 = parts[dstColumn_];
        Long time;
        if (timeColumn_>=0) {
            time = Long.parseLong(parts[timeColumn_]);
        } else {
            time = linkno;
        }
        return lb_.build(node1, node2, time);
    }
}
