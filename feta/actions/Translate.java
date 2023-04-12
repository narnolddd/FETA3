package feta.actions;

import feta.FetaOptions;
import feta.actions.stoppingconditions.StoppingCondition;
import feta.writenet.WriteNet;
import feta.writenet.WriteNetNN;
import feta.writenet.WriteNetNNT;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class Translate extends SimpleAction {

    public FetaOptions options_;
    public long startTime_=1;
    public long interval_=1;


    public Translate(FetaOptions options){
        options_=options;
        stoppingConditions_=new ArrayList<StoppingCondition>();
    }

    public void parseActionOptions(JSONObject obj){
        Long start = (Long) obj.get("Start");
        if (start != null)
            startTime_=start;
        Long interval = (Long) obj.get("Interval");
        if (interval!= null)
            interval_=interval;
    }

    public void execute() {
        WriteNet writer;
        String outputType = options_.getOutputType();
        if (outputType.equals("NNT")) {
            writer = new WriteNetNNT(network_, options_);
        } else if (outputType.equals("NN")) {
            writer = new WriteNetNN(network_, options_);
        } else throw new IllegalArgumentException("Unrecognised output type "+outputType);
        long time = startTime_;
        while(withinStoppingConditions(network_)) {
            network_.buildUpTo(time);
            time+=interval_;
        }
        long maxTime = network_.linksBuilt_.get(network_.linksBuilt_.size()-1).time_;
        writer.write();
    }
}
