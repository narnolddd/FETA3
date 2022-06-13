package feta.actions;

import feta.FetaOptions;
import feta.actions.stoppingconditions.StoppingCondition;
import feta.network.DirectedNetwork;
import feta.network.Link;
import feta.network.UndirectedNetwork;
import feta.objectmodels.FullObjectModel;
import feta.objectmodels.MixedModel;
import feta.operations.Operation;
import feta.parsenet.ParseNet;
import feta.parsenet.ParseNetDirected;
import feta.parsenet.ParseNetUndirected;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;

/** Finds best model mixture - hopefully will be better than calculating likelihood many times */
public class FitMixedModel extends SimpleAction {

    public FetaOptions options_;
    public FullObjectModel objectModel_;
    public int granularity_;
    public List<int[]> configs_;
    public ParseNet parser_;
    public int noComponents_;
    public long startTime_=10;
    private boolean orderedData_ = false;

    public FitMixedModel(FetaOptions options){
        stoppingConditions_= new ArrayList<StoppingCondition>();
        options_=options;
        objectModel_= new FullObjectModel(options_.fullObjectModel_);
    }

    /** Method that generates all possible configurations of model mixture */
    private static List<int[]> generatePartitions(int n, int k) {
        List<int[]> parts = new ArrayList<>();
        if (k == 1) {
            parts.add(new int[] {n});
            return parts;
        }
        List<int[]> newParts = new ArrayList<>();
        for (int l = 0; l < n; l++) {
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
        if (!options_.isDirectedInput()) {
            parser_ = new ParseNetUndirected((UndirectedNetwork) network_);
        } else parser_= new ParseNetDirected((DirectedNetwork) network_);
        for (int j = 0; j < objectModel_.objectModels_.size(); j++) {
            long start = objectModel_.times_.get(j).start_;
            long end = objectModel_.times_.get(j).end_;
            getLikelihoods(start,end);
        }
    }

    public void getLikelihoods(long start, long end) {
        MixedModel obm = objectModel_.objectModelAtTime(start);
        noComponents_ = obm.components_.size();
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
            ArrayList<Link> lset = parser_.getNextLinkSet(links);
            ArrayList<Operation> newOps = parser_.parseNewLinks(lset, network_);
            for (Operation op: newOps) {
                long time = op.getTime();
                obm.calcNormalisation(network_);
                updateLikelihoods(op, obm);
                noChoices+=op.getNoChoices();
                network_.buildUpTo(time);
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

        System.out.println("Max c0 : "+maxLike+" max like "+bestRaw+" choices "+noChoices);
        for (int i = 0; i < bestConfig.length; i++) {
            System.out.println(bestConfig[i]+" "+obm.components_.get(i));
        }
    }

    public void updateLikelihoods(Operation op, MixedModel obm) {
        op.setNodeChoices(orderedData_);
        ArrayList<int[]> nc = op.getNodeOrders();
        obm.updateLikelihoods(network_,nc);
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
    }
}
