package feta.readnet;

import feta.network.Link;

/** Class for reading network files in the NODE-NODE form */

public class ReadNetNN extends ReadNet {

    @Override
    public Link parseLine(String line, long linkno) {
        String[] parts = line.split(sep_);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Expected 2 entries per line but got "+line);
        }
        else {
            String node1 = parts[0];
            String node2 = parts[2];
            long time = linkno;
            return lb_.build(node1,node2,linkno);
        }
    }
}
