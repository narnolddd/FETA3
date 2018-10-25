package feta.actions;

import feta.FetaOptions;
import feta.writenet.WriteNet;
import feta.writenet.WriteNetNN;
import feta.writenet.WriteNetNNT;
import org.json.simple.JSONObject;

import java.io.IOException;

public class Translate extends SimpleAction {

    public FetaOptions options_;
    public long startTime_=10;


    public Translate(FetaOptions options){
        options_=options;
    }

    public void parseActionOptions(JSONObject obj){
        Long start = (Long) obj.get("Start");
        if (start != null)
            startTime_=start;
    }

    public void execute() {
        WriteNet writer;
        if (options_.outputType_== "NNT") {
            writer = new WriteNetNNT(network_.linksToBuild_, options_);
        } else if (options_.outputType_== "NN") {
            writer = new WriteNetNN(network_.linksToBuild_, options_);
        } else throw new IllegalArgumentException("Unrecognised output type "+options_.outputType_);
        writer.write(startTime_,Long.MAX_VALUE);
    }
}
