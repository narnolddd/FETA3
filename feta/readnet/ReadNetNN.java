package feta.readnet;

import feta.network.Link;
import feta.readnet.ReadNet;

import java.io.*;
import java.util.ArrayList;

/** Class for reading network files in the NODE-NODE form */

public class ReadNetNN extends ReadNet {

    @Override
    public Link parseLine(String line, long linkno) {
        String[] parts = line.split(sep_);
        if (parts.length != 2) {
            System.err.println("Expected 2 entries per line but got "+line);
            System.exit(-1);
        }
        else {
            String node1 = parts[0];
            String node2 = parts[2];
            long time = linkno;
            return lb_.build(node1,node2,linkno);
        }
        return null;
    }
}
