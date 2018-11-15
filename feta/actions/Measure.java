package feta.actions;

import feta.actions.stoppingconditions.StoppingCondition;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Measure extends SimpleAction {

    private long startTime_=10;
    private long interval_=10;
    public String fname = "output/DMAge1000degvector.dat";
    public BufferedWriter bw = null;
    public FileWriter fw = null;

    // Need to think how this will work alternating between directed and undirected networks.
    private boolean measureDegDist_=false;

    public Measure() {
        stoppingConditions_= new ArrayList<StoppingCondition>();
    }

    public void execute() {
        long time = startTime_;
        network_.buildUpTo(time);
//        setUpBR();
        while (!stoppingConditionsExceeded_(network_) && network_.linksToBuild_.size() > 0) {
            network_.buildUpTo(time);
            network_.calcMeasurements();
            System.out.println(network_.measureToString());
//            try {
//                bw.write(network_.degreeVectorToString());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            time += interval_;
        }
        try {bw.close();} catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUpBR() {
        try {
            fw = new FileWriter(fname);
            bw = new BufferedWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
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
    }
}
