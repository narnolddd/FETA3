package feta.readnet;

import feta.network.Link;

public class ReadNetNNT extends ReadNet{

    @Override
    public Link parseLine(String line, long linkno) {
        String[] parts = line.split(sep_);
        if (parts.length != 3) {
            throw new IllegalArgumentException("Expected 3 entries per line but got "+line);
        }
        else {
            String node1 = parts[0];
            String node2 = parts[1];
            long time = Long.parseLong(parts[2]);
            return lb_.build(node1,node2,time);
        }
    }
}