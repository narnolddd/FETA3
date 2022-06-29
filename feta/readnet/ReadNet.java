package feta.readnet;

import feta.FetaOptions;
import feta.network.Link;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public abstract class ReadNet {

    public ArrayList<Link> links_;
    LinkBuilder lb_;
    public String sep_;
    String networkInput_;
    boolean removeDuplicates_=true;
    boolean sampled_;
    double samplingProp_=0.0;

    public ReadNet(FetaOptions options){
        links_= new ArrayList<Link>();
        sep_=options.getInSep();
        networkInput_=options.getNetInputFile();

        sampled_ = options.netInputSampled_;
        samplingProp_= options.sampleProp_;

        if (options.isDirectedInput()) {
            lb_= new DirectedLinkBuilder();
        } else lb_= new UndirectedLinkBuilder();
    }

    public final ArrayList<Link>  readNetwork(){
        System.out.println("Reading network file "+networkInput_);
        int linkno = 1;
        links_= new ArrayList<Link>();
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
                // remove self-loops
                if (link.sourceNode_.equals(link.destNode_)) {
                    System.out.println("Self Loop at time "+link.time_+"!");
                    continue;
                }
//               if (removeDuplicates_ && links_.contains(link))
//                    continue;
                if (sampled_) {
                    Random rg = new Random();
                    if (rg.nextDouble() > samplingProp_)
                        continue;
                }
                links_.add(link);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Cannot read network. File "+networkInput_+" not found.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return links_;
    }

    public abstract Link parseLine(String line, long linkno);

}
