package feta.actions;

import feta.actions.stoppingconditions.MaxTimeExceeded;
import feta.actions.stoppingconditions.NoMoreLinks;
import feta.actions.stoppingconditions.StoppingCondition;
import feta.network.DirectedNetwork;
import feta.network.Network;
import feta.network.measurements.*;
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
    public String outputFile_ = "output/measurements.dat";
    public BufferedWriter bw_ = null;
    // Need to think how this will work alternating between directed and undirected networks.
    private boolean printDegVector_;
    private boolean printHeader_;
    private boolean directed_;

    private final ArrayList<Measurement> statistics_;

    public Measure() {
        stoppingConditions_= new ArrayList<StoppingCondition>();
        statistics_= new ArrayList<Measurement>();
    }

    public Measure(Network net, ArrayList<Measurement> statistics, String outputFile, long startTime, long endTime, long interval) {
        setNetwork(net);
        statistics_=statistics;
        stoppingConditions_= new ArrayList<StoppingCondition>() { {
            add(new MaxTimeExceeded(endTime));
        }};
        outputFile_=outputFile;
        startTime_=startTime;
        interval_=interval;
    }

    public Measure(Network net, ArrayList<Measurement> statistics, String outputFile, long startTime, long interval) {
        setNetwork(net);
        statistics_=statistics;
        stoppingConditions_= new ArrayList<StoppingCondition>() { {
            add(new NoMoreLinks());
        }};
        outputFile_=outputFile;
        startTime_=startTime;
        interval_=interval;
    }

    public void execute() {
        setUpMeasureWriter(outputFile_);
        network_.setUpDegDistWriters(outputFile_);
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
            while (withinStoppingConditions(network_) && network_.linksToBuild_.size() > 0) {
                network_.buildUpTo(time);
                if (network_.changed_) {
                    update();
                }
                String measurements = time+" "+getLine();
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
            System.out.println("Problem writing measurements to: "+ outputFile_);
            e.printStackTrace();
        }
    }

    public void setUpMeasureWriter(String fname) {
        File file= new File(fname);
        FileWriter fw;
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
            outputFile_ =measureFName;
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
        for (Object statistic : statistics) {
            Measurement measure=null;
            Class<? extends Measurement> cl;
            try {
                cl = findStatisticClass(statistic.toString());
                Constructor<?> c= cl.getConstructor();
                measure= (Measurement) c.newInstance();
            } catch (ClassNotFoundException e) {
                System.err.println("No measurement class found with name "+statistic);
            } catch (NoSuchMethodException e) {
                System.err.println("No constructor found for class with name "+statistic);
                e.printStackTrace();
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                System.err.println("Trouble instantiating class "+statistic);
                e.printStackTrace();
            }
            statistics_.add(measure);
        }
    }

    public Class<?extends Measurement> findStatisticClass(String rawname) throws ClassNotFoundException {
        Class <? extends Measurement> stat;
        String sname = rawname;
        try {
            stat=Class.forName(sname).asSubclass(Measurement.class);
            return stat;
        } catch (ClassNotFoundException e) {
        }
        sname="feta.network.measurements."+rawname;
        try {
            stat=Class.forName(sname).asSubclass(Measurement.class);
            return stat;
        } catch (ClassNotFoundException e) {
            throw e;
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
        StringBuilder header = new StringBuilder("Timestamp ");
        for (Measurement m: statistics_) {
            header.append(m.nameToString()).append(" ");
        }
        return header.toString() +'\n';
    }

    public String getLine() {
        StringBuilder line = new StringBuilder();
        for (Measurement m: statistics_) {
            line.append(m).append(" ");
        }
        return line.toString() +'\n';
    }

    public String toString() { return "Measure"; }
}
