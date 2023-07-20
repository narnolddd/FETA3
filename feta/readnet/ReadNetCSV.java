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

    /** Full constructor for runner classes */
    public ReadNetCSV(String fileName, String sep, boolean directed, int srcCol, int dstCol, int timeCol, int srcTypeCol, int dstTypeCol) {
        networkInput_ = fileName;
        sep_=sep;
        sourceColumn_=srcCol;
        dstColumn_=dstCol;
        timeColumn_=timeCol;
        sourceTypeColumn_= srcTypeCol;
        dstTypeColumn_= dstTypeCol;
        lb_= new LinkBuilder();
    }

    /** Basic default for untyped networks */
    public ReadNetCSV(String fileName, String sep, boolean directed, int srcCol, int dstCol, int timeCol) {
        this(fileName,sep,directed,srcCol,dstCol,timeCol,-1,-1);
    }

    /** Ultimate lazy option for basic csv untyped files */
    public ReadNetCSV(String fileName, String sep, boolean directed) {
        this(fileName,sep,directed,0,1,2,-1,-1);
    }

    @Override
    public Link parseLine(String line, long linkno) {
        String[] parts = line.split(sep_);
        if (parts.length < 3) {
            System.err.println("Failed to parse network line "+line);
            System.exit(-1);
        }
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
