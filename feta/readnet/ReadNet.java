package feta.readnet;

import feta.network.Link;

import java.io.*;
import java.util.ArrayList;

public abstract class ReadNet {

    ArrayList<Link> links_;
    LinkBuilder lb_;
    public String sep_;

    public void setLinkBuilder(LinkBuilder lb) {
        lb_=lb;
    }

    public void setSep(String sep) {
        sep_ = sep;
    }

    public final void readNetwork(String fname){
        ArrayList<Link> links = new ArrayList<Link>();
        int linkno = 1;
        try {
            FileInputStream fstream = new FileInputStream(fname);
            DataInputStream dstream = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(dstream));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0)
                    continue;
                Link link = parseLine(line, linkno);
                links.add(link);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Cannot read network. File "+fname+" not found.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract Link parseLine(String line, long linkno);

}
