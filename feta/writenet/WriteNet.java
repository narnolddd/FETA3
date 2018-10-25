package feta.writenet;

import feta.network.Link;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public abstract class WriteNet {

    public String sep_= "\\+s";

    public ArrayList<Link> linksToWrite_;

    public WriteNet(ArrayList<Link> links){

        linksToWrite_=links;

    }

    public void write(String fname, long startTime, long endTime) throws IOException{
        BufferedWriter bw = null;
        FileWriter fw = null;

        fw = new FileWriter(fname);
        bw = new BufferedWriter(fw);

        for (Link link:linksToWrite_) {
            if (link.time_<startTime){
                continue;
            }
            if (link.time_>endTime){
                break;
            }
            bw.write(linkToString(link));
        }
    }

    public abstract String linkToString(Link link);


}