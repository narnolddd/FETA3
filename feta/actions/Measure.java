package feta.actions;

import feta.actions.stoppingconditions.MaxTimeExceeded;
import feta.actions.stoppingconditions.StoppingCondition;
import org.json.simple.JSONObject;

public class Measure extends SimpleAction {

    private long startTime_=10;
    private long interval_=10;
    private StoppingCondition stop_;

    private boolean measureDegDist_=false;

    public Measure() {
    }

    public void execute(){
        long time = startTime_;
        while (!stoppingConditionsExceeded_(network_)) {
            network_.buildUpTo(time);
            network_.calcMeasurements();
            System.out.println(network_.measureToString());
            time += interval_;
        }
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

        Long stopTime = (Long) obj.get("StoppingTime");
        if (stopTime != null) {
            StoppingCondition sc = new MaxTimeExceeded(stopTime);
            stoppingConditions_.add(sc);
        }
    }

}
