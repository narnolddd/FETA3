package feta.writenet;

import feta.FetaOptions;
import feta.network.Link;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public abstract class WriteNet {

    public String sep_;
    public String networkOutput_;
    public ArrayList<Link> linksToWrite_;

    public WriteNet(ArrayList<Link> links, FetaOptions options){
        sep_= options.outSep_;
        networkOutput_=options.netOutputFile_;
        linksToWrite_=links;
    }

    public void write(long startTime, long endTime) {
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(networkOutput_);
            bw = new BufferedWriter(fw);

            for (int i = 0; i < linksToWrite_.size(); i++) {
                Link link = linksToWrite_.get(i);
                if (link.time_ < startTime) {
                    continue;
                }
                if (link.time_ > endTime) {
                    break;
                }
                bw.write(linkToString(link));
            }
            bw.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public abstract String linkToString(Link link);


}