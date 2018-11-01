package feta.readnet;

import feta.FetaOptions;
import feta.network.Link;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.ArrayList;

public abstract class ReadNet {

    public ArrayList<Link> links_;
    LinkBuilder lb_;
    public String sep_;
    String networkInput_;
    boolean removeDuplicates_;

    public ReadNet(FetaOptions options){
        links_= new ArrayList<Link>();
        sep_=options.inSep_;
        networkInput_=options.netInputFile_;

        if (options.directedInput_) {
            lb_= new DirectedLinkBuilder();
        } else lb_= new UndirectedLinkBuilder();
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
                System.out.println(link);
//                if (removeDuplicates_ && links_.contains(link))
//                    continue;
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
