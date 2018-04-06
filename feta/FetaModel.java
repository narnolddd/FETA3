package feta;

import java.io.IOException;
import java.util.ArrayList;

public class FetaModel {

    private FetaOptions options_;
    private Network network_;

    public FetaModel() { options_= new FetaOptions(); }

    public void readConfig(String cfile) {
        options_.readConfig(cfile);
    }

    public void executeAction() throws IOException{
        if (options_.actionType_ == FetaOptions.ACTION_MEASURE) {
            measure();
        } else if (options_.actionType_ == FetaOptions.ACTION_GROW) {
            grow();
        }
    }

    public void measure() {
        network_= new Network(options_);
        try {
            ArrayList<Link> links = network_.readNet(options_.graphFileInput_, options_.fileFormatRead_);
            links = network_.buildNetwork(links, options_.actionStopTime_);

        } catch (IOException e) {
            System.err.println("Unable to read network.");
        }
    }

    public void grow() throws IOException{
        network_ = new Network(options_);
        if (options_.graphFileInput_ == null) {
            System.err.println("I can't grow a network without a starting seed!");
            System.exit(-1);
        }
        if (options_.graphFileOutput_ == null) {
            System.err.println("No output file for grown graph");
        }
        try {
            ArrayList<Link> links = network_.readNet(options_.graphFileInput_, options_.fileFormatRead_);
            links = network_.buildNetwork(links, options_.actionStartTime_);
            if(network_.noNodes_ == 0) {
                System.err.println("Input file returned empty network. Try increase start time?");
            }
        } catch (IOException e) {
            System.err.println("Unable to read starting network");
        }
        network_.writeNetwork(options_.graphFileOutput_, options_.fileFormatWrite_);
    }

}