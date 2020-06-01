package feta.actions;

import feta.actions.stoppingconditions.StoppingCondition;
import feta.network.DirectedNetwork;
import feta.network.Measurements.Measurement;
import feta.network.UndirectedNetwork;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Measure extends SimpleAction {

    private long startTime_=10;
    private long interval_=10;
    public String measureName_ = "output/measurements.dat";
    public BufferedWriter bw_ = null;
    // Need to think how this will work alternating between directed and undirected networks.
    private boolean measureDegDist_;
    private boolean printDegVector_;
    private boolean printHeader_;
    private boolean directed_;

    private ArrayList<Measurement> statistics_;

    public Measure() {
        stoppingConditions_= new ArrayList<StoppingCondition>();
        statistics_= new ArrayList<Measurement>();
    }

    public void execute() {
        setUpMeasureWriter(measureName_);
        network_.setUpDegDistWriters(measureName_);
        network_.trackCluster_=true;
        long time = startTime_;
        network_.buildUpTo(time);
        initialise();
        if (printHeader_) {
            try {
                bw_.write(getHeader());
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
        try {
            while (!stoppingConditionsExceeded_(network_) && network_.linksToBuild_.size() > 0) {
                network_.buildUpTo(time);
                if (network_.changed_) {
                    update();
                }
                String measurements = time+" "+getLine();
                //network_.calcMeasurements();
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
        Boolean printHeader = (Boolean) obj.get("PrintHeader");
        if (printHeader != null) {
            printHeader_= printHeader;
        }
        Boolean degVector = (Boolean) obj.get("PrintDegVector");
        if (degVector != null) {
            printDegVector_=degVector;
        }
        JSONArray statistics = (JSONArray) obj.get("Statistics");
        parseStatistics(statistics);
    }

    public void parseStatistics(JSONArray statistics) {
        for (int i = 0; i < statistics.size(); i++) {
            Measurement stat = null;
            String statClass = (String) "feta.network.Measurements."+statistics.get(i);
            Class <?extends Measurement> measurement;

            try {
                measurement = Class.forName(statClass).asSubclass(Measurement.class);
                Constructor<?> c = measurement.getConstructor();
                stat = (Measurement) c.newInstance();
            } catch (ClassNotFoundException e){
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            statistics_.add(stat);
        }
    }

    public void initialise() {
        if (network_.getClass() == DirectedNetwork.class) {
            directed_=true;
        }
        for (Measurement m: statistics_) {
            m.setNetwork(network_);
            m.setDirected(directed_);
        }
    }

    public void update() {
        for (Measurement m: statistics_) {
            m.update();
        }
    }

    public String getHeader() {
        String header = "Timestamp ";
        for (Measurement m: statistics_) {
            header += m.nameToString()+" ";
        }
        return header+'\n';
    }

    public String getLine() {
        String line = "";
        for (Measurement m: statistics_) {
            line += m+" ";
        }
        return line+'\n';
    }
}
