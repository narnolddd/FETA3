package feta.actions;

import feta.FetaOptions;
import feta.actions.stoppingconditions.NoMoreLinks;
import feta.actions.stoppingconditions.StoppingCondition;
import feta.network.DirectedNetwork;
import feta.network.Link;
import feta.network.Network;
import feta.network.UndirectedNetwork;
import feta.objectmodels.FullObjectModel;
import feta.objectmodels.MixedModel;
import feta.operations.Operation;
import feta.parsenet.ParseNet;
import feta.parsenet.ParseNetDirected;
import feta.parsenet.ParseNetUndirected;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.json.simple.JSONObject;

/** Finds best model mixture - hopefully will be better than calculating likelihood many times */
public class FitMixedModel extends SimpleAction {

    public FetaOptions options_;
    public FullObjectModel objectModel_;
    public int granularity_;
    public List<int[]> configs_;

    // Options related to Output
    private double[] c0Intervals_;
    private double[] rawIntervals_;
    private int[] choicesIntervals_;

    private String modelAsString_="";

    private double totalChoices_;
    private double bestLikelihood_;
    private double bestRaw_;


    public long startTime_=10;
    private boolean orderedData_ = false;
    private Random random_;
    private boolean debugMode_=false;
    private ArrayList<Operation> operationsExtracted_;
    ParseNet parser_;

    public FitMixedModel(FetaOptions options){
        stoppingConditions_= new ArrayList<StoppingCondition>();
        options_=options;
        objectModel_= new FullObjectModel(options_.fullObjectModel_);
    }

    public FitMixedModel(Network net, FullObjectModel model, int granularity, long start, boolean orderedData) {
        network_=net;
        objectModel_=model;
        granularity_=granularity;
        startTime_=start;
        orderedData_=orderedData;
        stoppingConditions_= new ArrayList<StoppingCondition>() { {
            add(new NoMoreLinks());
        }};
    }

    public FitMixedModel(Network net, MixedModel model, int granularity, long start, boolean orderedData) {
        this(net, new FullObjectModel(model),granularity,start,orderedData);
    }

    /** Method that generates all possible configurations of model mixture */
    private static List<int[]> generatePartitions(int n, int k) {
        List<int[]> parts = new ArrayList<>();
        if (k == 1) {
            parts.add(new int[] {n});
            return parts;
        }
        List<int[]> newParts = new ArrayList<>();
        for (int l = 0; l <= n; l++) {
            List<int[]> oldParts = generatePartitions(n-l,k-1);
            for (int[] partition: oldParts) {
                int[] newPartition = new int[partition.length+1];
                System.arraycopy(partition,0,newPartition,0,partition.length);
                newPartition[partition.length]=l;
                newParts.add(newPartition);
            }
        }
        return newParts;
    }

    private ArrayList<double[]> generateModels() {
        ArrayList<double[]> weightList = new ArrayList<>();
        for (int[] config: configs_) {
            double[] weights = new double[config.length];
            for (int i=0; i < config.length; i++) {
                weights[i] = (double)config[i]/granularity_;
            }
            weightList.add(weights);
        }
        return weightList;
    }

    public void execute(){
        network_.buildUpTo(startTime_);
        operationsExtracted_= new ArrayList<>();
        if (network_ instanceof UndirectedNetwork) {
            parser_ = new ParseNetUndirected((UndirectedNetwork) network_);
        } else parser_= new ParseNetDirected((DirectedNetwork) network_);
        if (debugMode_) {
            random_= new Random(42);
        } else {
            random_= new Random();
        }

        int noIntervals = objectModel_.objectModels_.size();
        c0Intervals_= new double[noIntervals];
        rawIntervals_= new double[noIntervals];
        choicesIntervals_= new int[noIntervals];

        modelAsString_+="{\"changepoints\": "+(noIntervals-1)+", \"intervals\": \n[";
        int[] totalChoices = new int[1];
        double[] runningLike = new double[1];
        for (int j = 0; j < noIntervals; j++) {
            long start = Math.max(objectModel_.times_.get(j).start_, startTime_);
            long end = objectModel_.times_.get(j).end_;
            String line = getLikelihoods(parser_, start, end, totalChoices, runningLike);
            if (j != objectModel_.objectModels_.size()-1)
                line+=",";
            else
                line+="],";
            modelAsString_+=line;
        }
        double finalC0 = Math.exp(runningLike[0]/totalChoices[0]);
        modelAsString_+="\n\"finalc0\": "+finalC0+", \"finalraw\": "+runningLike[0]+", \"finalchoices\": "+totalChoices[0]+"}";
        System.out.println(modelAsString_);
    }

    public String getLikelihoods(ParseNet parser, long start, long end, int[] totalChoices, double[] runningLike) {
        MixedModel obm = objectModel_.objectModelAtTime(start);
        int noComponents_ = obm.components_.size();
        configs_=generatePartitions(granularity_,noComponents_);

        ArrayList<double[]> weightList = generateModels();
        obm.initialiseLikelihoods(weightList);

        network_.buildUpTo(start);
        int noChoices = 0;
        HashMap<double[], Double> c0Values = new HashMap<>();
        while (network_.linksToBuild_.size()>0 && withinStoppingConditions(network_)) {
            if (network_.latestTime_ > end)
                break;
            ArrayList<Link> links = network_.linksToBuild_;
            ArrayList<Link> lset = parser.getNextLinkSet(links);
            ArrayList<Operation> newOps = parser.parseNewLinks(lset, network_);
            for (Operation op: newOps) {
                long time = op.getTime();
                obm.calcNormalisation(network_);
                updateLikelihoods(op, obm);
                // debug line
                //if (op.getNoChoices() <= 5) {
                    noChoices+=op.getNoChoices();
                //}
                network_.buildUpTo(time);
                operationsExtracted_.add(op);
            }
        }

        // Turn everything into a C0 value
        double maxLike=0.0;
        double bestRaw=0.0;
        double[] bestConfig = new double[noComponents_];
        HashMap<double[], Double> likelihoods = obm.getLikelihoods();
        for (double [] weights : weightList) {
            double like= likelihoods.get(weights);
            double c0 = Math.exp(like/noChoices);
            c0Values.put(weights,c0);
            if (c0> maxLike) {
                maxLike=c0;
                bestRaw= like;
                bestConfig=weights;
            }
        }
        obm.setWeights(bestConfig);
        runningLike[0] += bestRaw;
        totalChoices[0] += noChoices;

        String toPrint = "{\"start\":"+start+", \"c0max\" : "+maxLike+
                ", \"raw\": "+bestRaw+", \"choices\": "+noChoices+", \n \"models\": ";

        bestLikelihood_=maxLike;

        String[] models = new String[bestConfig.length];
        for (int i = 0; i < bestConfig.length; i++) {
            models[i]="{\""+obm.components_.get(i)+"\": "+bestConfig[i]+"}";
        }
        toPrint += "["+String.join(",",models)+"]}";
        return toPrint;
    }

    /** Extract Parsed Operations from fitting process */
    public ArrayList<Operation> getParsedOperations() {
        return operationsExtracted_;
    }

    /** Extract Full Object Model from process */
    public FullObjectModel getFittedModel() {
        objectModel_.reset();
        return objectModel_;
    }

    /** Return highest likelihood model achieved */
    public double getBestLikelihood() {
        return bestLikelihood_;
    }

    /** Write operation model to file using Parser */
    public void writeOperationsToFile(String filename, Boolean censored) {
        BufferedWriter bw;
        try {
            FileWriter fw = new FileWriter(filename);
            bw = new BufferedWriter(fw);
            for (Operation op: operationsExtracted_) {
                if (censored)
                    op.censor();
                bw.write(op+"\n");
                // Print for debugging: System.out.println(op);
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Default method for the above with censored set to false */
    public void writeOperationsToFile(String filename) {
        writeOperationsToFile(filename, false);
    }

    public void writeObjectModelToFile(String filename) {
        BufferedWriter bw;
        try {
            FileWriter fw = new FileWriter(filename);
            bw = new BufferedWriter(fw);
            bw.write(modelAsString_);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLikelihoods(Operation op, MixedModel obm) {
        op.setRandom(random_);
        op.setNodeChoices(orderedData_);

        ArrayList<int[]> nc = op.getNodeOrders();
        op.updateLikelihoods(obm,network_);
    }

    public void parseActionOptions(JSONObject obj) {
        long granuLong = (Long) obj.get("Granularity");
        granularity_=Math.toIntExact(granuLong);
        Long start = (Long) obj.get("Start");
        if (start != null)
            startTime_=start;
        Boolean ordereddata = (Boolean) obj.get("OrderedData");
        if (ordereddata != null)
            orderedData_=ordereddata;
        Boolean debug = (Boolean) obj.get("DebugMode");
        if (debug != null)
            debugMode_=debug;
    }

    public String toString() { return "FitMixedModel"; }
}
