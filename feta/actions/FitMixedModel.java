package feta.actions;

/** Feta packages */
import feta.FetaOptions;
import feta.actions.stoppingconditions.StoppingCondition;
import feta.network.DirectedNetwork;
import feta.network.Link;
import feta.network.Network;
import feta.network.UndirectedNetwork;
import feta.objectmodels.FullObjectModel;
import feta.objectmodels.MixedModel;
import feta.objectmodels.ObjectModel;
import feta.operations.Operation;
import feta.operations.Star;
import feta.parsenet.ParseNet;
import feta.parsenet.ParseNetDirected;
import feta.parsenet.ParseNetUndirected;

/** Utils */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.json.simple.JSONObject;

/** Finds best model mixture - hopefully will be better than calculating likelihood many times */

public class FitMixedModel extends SimpleAction {

    public FetaOptions options_;
    public FullObjectModel objectModel_;
    public int granularity_;
    public List<int[]> configs_;
    public HashMap<MixedModel,Double> likelihoods_;
    private HashMap<int[], double[]> configToWeight_;
    public ParseNet parser_;
    public int noComponents_;
    public long startTime_=10;
    public ArrayList<int[]> permList;
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

    private void generateModels(MixedModel obm) {
        for (int[] config: configs_) {

            double[] weights = new double[config.length];
            for (int i=0; i < config.length; i++) {
                weights[i] = (double)config[i]/granularity_;
            }
            MixedModel mm = obm.copy();
            configToWeight_.put(config,weights);
            mm.setWeights(weights);
            likelihoods_.put(mm,0.0);
        }
    }

    public void execute(){
        if (!options_.directedInput_) {
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
        likelihoods_= new HashMap<MixedModel,Double>();
        configToWeight_=new HashMap<int[], double[]>();

        generateModels(obm);

        network_.buildUpTo(start);
        int noChoices = 0;
        HashMap<MixedModel, Double> c0Values_ = new HashMap<>();
        while (network_.linksToBuild_.size()>0 && !stoppingConditionsExceeded_(network_)) {
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
        MixedModel bestConfig_=null;
        for (MixedModel mm: likelihoods_.keySet()) {
            double like= likelihoods_.get(mm);
            double c0 = Math.exp(like/noChoices);
            c0Values_.put(mm,c0);
            if (c0> maxLike) {
                maxLike=c0;
                bestRaw= like;
                bestConfig_=mm;
            }
        }

        System.out.println("Max c0 : "+maxLike+" max like "+bestRaw);
        System.out.println(bestConfig_);

    }

    public void updateLikelihoods(Operation op, MixedModel obm) {
        op.setNodeChoices(orderedData_);
        for (int[] c: configs_) {
            double[] weights = configToWeight_.get(c);
            obm.setWeights(weights);
            double oldLike = likelihoods_.get(obm);
            likelihoods_.put(obm,oldLike+op.calcLogLike(obm,network_,orderedData_));
        }
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
