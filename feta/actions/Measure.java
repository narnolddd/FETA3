package feta.actions;

import feta.network.Network;
import org.json.simple.JSONObject;

public class Measure extends SimpleAction {

    private long startTime_=10;
    private long interval_=10;
    private boolean measureDegDist_=false;

    public Measure() {
    }

    public void execute(){}

    public void parseJSON(JSONObject obj) {

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



}
