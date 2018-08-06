package feta.readnet;

import feta.network.Link;

import java.io.*;
import java.util.ArrayList;

public abstract class ReadNet {

    ArrayList<Link> links_;
    LinkBuilder lb_;
    public String sep_ = "\\s+";
    String networkInput_;
    boolean removeDuplicates_;

    public ReadNet(){
        links_= new ArrayList<Link>();
    }

    public void setLinkBuilder(LinkBuilder lb) {
        lb_=lb;
    }

    public void setSep(String sep) {
        sep_ = sep;
    }

    public void setFileInput(String fname) {
        networkInput_= fname;
    }

    public final void readNetwork(){
        int linkno = 1;
        try {
            FileInputStream fstream = new FileInputStream(networkInput_);
            DataInputStream dstream = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(dstream));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0)
                    continue;
                Link link = parseLine(line, linkno);
                if (removeDuplicates_ && links_.contains(link))
                    continue;
                links_.add(link);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Cannot read network. File "+networkInput_+" not found.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract Link parseLine(String line, long linkno);

}
