package feta.actions;

import feta.actions.stoppingconditions.MaxLinksExceeded;
import feta.actions.stoppingconditions.MaxNodeExceeded;
import feta.actions.stoppingconditions.MaxTimeExceeded;
import feta.actions.stoppingconditions.StoppingCondition;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import static java.lang.Math.toIntExact;

public class Measure extends SimpleAction {

    private long startTime_=10;
    private long interval_=10;
    private StoppingCondition stop_;

    // Need to think how this will work alternating between directed and undirected networks.
    private boolean measureDegDist_=false;

    public Measure() {
        stoppingConditions_= new ArrayList<StoppingCondition>();
    }

    public void execute(){
        long time = startTime_;
        network_.buildUpTo(time);
        while (!stoppingConditionsExceeded_(network_)) {
            network_.buildUpTo(time);
            network_.calcMeasurements();
            System.out.println(network_.measureToString());
            time += interval_;
        }
    }

    /** Parses the json for any stopping conditions */
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

        Long stopTime = (Long) obj.get("StoppingTime");
        if (stopTime != null) {
            StoppingCondition sc = new MaxTimeExceeded(stopTime);
            stoppingConditions_.add(sc);
        }

        // These don't work at the moment bc of java type issues

        Long mn = (Long) obj.get("MaxNodes");
        if (mn != null) {
            int maxNodes = toIntExact(mn);
            StoppingCondition sc = new MaxNodeExceeded(maxNodes);
            stoppingConditions_.add(sc);
        }

        Long ml = (Long) obj.get("MaxLinks");
        if (ml != null) {
            int maxLinks = toIntExact(ml);
            StoppingCondition sc = new MaxLinksExceeded(maxLinks);
            stoppingConditions_.add(sc);
        }
    }

}
