package feta.actions;

import feta.actions.stoppingconditions.StoppingCondition;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Measure extends SimpleAction {

    private long startTime_=10;
    private long interval_=10;
    public String measureName_ = "output/measurements.dat";
    public BufferedWriter bw_ = null;
    // Need to think how this will work alternating between directed and undirected networks.
    private boolean measureDegDist_=false;
    private boolean printDegVector_= false;

    public Measure() {
        stoppingConditions_= new ArrayList<StoppingCondition>();
    }

    public void execute() {
        setUpMeasureWriter(measureName_);
        network_.setUpDegDistWriters(measureName_);
        network_.trackCluster_=true;
        long time = startTime_;
        network_.buildUpTo(time);
        try {
            while (!stoppingConditionsExceeded_(network_) && network_.linksToBuild_.size() > 0) {
                network_.buildUpTo(time);
                network_.calcMeasurements();
                String measurements = time+" "+network_.measureToString()+"\n";
                //System.out.println(measurements);
                bw_.write(measurements);
                if (printDegVector_) {
                    System.out.println(network_.degreeVectorToString());
                }
                network_.writeDegDist();
                time += interval_;
            }
            bw_.close();
            network_.closeWriters();
        } catch (IOException e) {
            System.out.println("Problem writing measurements to: "+measureName_);
            e.printStackTrace();
        }
    }

    public void setUpMeasureWriter(String fname) {
        File file= new File(fname);
        FileWriter fw = null;
        try{
            fw = new FileWriter(file);
            bw_= new BufferedWriter(fw);
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
        String measureFName = (String) obj.get("FileName");
        if (measureFName != null) {
            measureName_=measureFName;
        }
        Boolean degVector = (Boolean) obj.get("PrintDegVector");
        if (degVector != null) {
            printDegVector_=degVector;
        }
    }
}
