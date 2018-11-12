package feta.actions;

import feta.actions.stoppingconditions.StoppingCondition;
import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;
import feta.parsenet.ParseNet;
import feta.parsenet.ParseNetDirected;
import feta.parsenet.ParseNetUndirected;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class ParseTest extends SimpleAction {

    public long startTime_=10;
    public long interval_=10;

    boolean directed_;
    ParseNet parser_;

    public ParseTest(boolean directed) {
        stoppingConditions_= new ArrayList<StoppingCondition>();
        directed_=directed;
    }

    public void parseActionOptions(JSONObject obj) {
        Long start = (Long) obj.get("Start");
        if (start != null)
            startTime_=start;

        Long interval = (Long) obj.get("Interval");
        if (interval != null) {
            if (interval >= 0) {
                interval_= interval;
            } else {
                System.err.println("Invalid interval");
            }
        }
    }

    public void execute() {
        if (!directed_) {
            parser_ = new ParseNetUndirected((UndirectedNetwork) network_);
        } else parser_= new ParseNetDirected((DirectedNetwork) network_);
        long time_=startTime_;
        while (!stoppingConditionsExceeded_(network_)) {
            parser_.parseNetwork(time_,time_+interval_);
            time_+=interval_;
        }
    }
}
