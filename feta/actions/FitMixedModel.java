package feta.actions;

/** Feta packages */
import feta.FetaOptions;
import feta.actions.stoppingconditions.StoppingCondition;
import feta.network.DirectedNetwork;
import feta.network.Link;
import feta.network.Network;
import feta.network.UndirectedNetwork;
import feta.objectmodels.FullObjectModel;
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
    public HashMap<int[],Double> likelihoods_;
    public HashMap<int[], double[]> partitionToWeight_;
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
    public static List<int[]> generatePartitions(int n, int k) {
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
        noComponents_ = objectModel_.objectModelAtTime(start).components_.size();
        configs_=generatePartitions(granularity_,noComponents_);
        partitionToWeight_=new HashMap<int[], double[]>();
        likelihoods_= new HashMap<>();

        // Get partition to weights vector ready & likelihood
        for (int[] config: configs_) {
            likelihoods_.put(config,0.0);
            double[] weights = new double[config.length];
            for (int i=0; i < config.length; i++) {
                weights[i] = (double)config[i]/granularity_;
            }
            partitionToWeight_.put(config,weights);
        }

        network_.buildUpTo(start);
        int noChoices = 0;
        HashMap<int[], Double> c0Values_ = new HashMap<>();
        while (network_.linksToBuild_.size()>0 && !stoppingConditionsExceeded_(network_)) {
            if (network_.latestTime_ > end)
                break;
            ArrayList<Link> links = network_.linksToBuild_;
            ArrayList<Link> lset = parser_.getNextLinkSet(links);
            ArrayList<Operation> newOps = parser_.parseNewLinks(lset, network_);
            for (Operation op: newOps) {
                objectModel_.objectModelAtTime(op.time_).normaliseAll(network_);
                //ArrayList<double[]> componentProbabilities = op.getComponentProbabilities(network_,objectModel_.objectModelAtTime(op.time_));
                likelihoods_ = op.updateLikelihoods(likelihoods_,partitionToWeight_,network_,objectModel_.objectModelAtTime(op.time_));
                noChoices+=op.noChoices_;
                op.build(network_);
            }
            ArrayList<Link> newLinks = new ArrayList<Link>();
            for (int i = lset.size(); i < links.size(); i++){
                newLinks.add(links.get(i));
            }
            network_.linksToBuild_=newLinks;
        }

        // Turn everything into a C0 value
        double maxLike=0.0;
        double[] bestConfig_ = new double[noComponents_];
        for (int[] partition: likelihoods_.keySet()) {
            double c0 = Math.exp(likelihoods_.get(partition)/noChoices);
            c0Values_.put(partition,c0);
            if (c0> maxLike) {
                maxLike=c0;
                bestConfig_=partitionToWeight_.get(partition);
            }
        }

        System.out.println("Max likelihood : "+maxLike);
        for (int l=0; l < bestConfig_.length; l++){
            System.out.println(bestConfig_[l]+" "+objectModel_.objectModelAtTime(start).components_.get(l));
            //System.out.println(noChoices);
        }

    }

    public void updateLikelihoodsOrdered(ArrayList<double[]> nodeCompProbs) {
        for (int[] partition: likelihoods_.keySet()) {
            double[] weights = partitionToWeight_.get(partition);

            double logSum = 0.0;
            double logRand = 0.0;
            double probUsed = 0.0;
            double randUsed = 0.0;
            double like;

            for (double[] node : nodeCompProbs) {

                // calc prob of choosing node with this weighting
                double nodeprob = 0.0;
                for (int i = 0; i < node.length; i++) {
                    nodeprob+=node[i]*weights[i];
                }
                if (nodeprob <= 0) {
                    //System.out.println("Node returned zero probability");
                    logSum = 0.0;
                    logRand = 0.0;
                    break;
                }
                logSum+= Math.log(nodeprob) - Math.log(1 - probUsed);
                logRand+=Math.log(1.0/network_.noNodes_) - Math.log(1 - randUsed);
                randUsed+= 1.0/network_.noNodes_;
                probUsed+= nodeprob;

            }
            like = logSum - logRand;

            likelihoods_.put(partition, likelihoods_.get(partition) + like);
        }
    }

    /** Helper method for generating all permutations of an integer array */
    public void generatePerms(int start, int[] input) {
        if (start == input.length) {
            permList.add(input.clone());
            return;
        }
        for (int i = start; i < input.length; i++) {
            int temp = input[i];
            input[i] = input[start];
            input[start] = temp;

            generatePerms(start+1,input);

            int temp2 = input[i];
            input[i] = input[start];
            input[start] = temp2;
        }
    }

    public static ArrayList <int[]> generateRandomShuffles(int n, int number) {
        ArrayList shuffles = new ArrayList();
        for (int i = 0; i < number; i++) {
            int [] initial_array = new int[n];
            for (int j = 0; j < n; j++) {
                initial_array[j]=j;
            }
            // Perform Knuth Shuffles on choices to sample permutations
            for (int ind1 = 0; ind1 < n-2; ind1++) {
                int ind2 = ThreadLocalRandom.current().nextInt(ind1, n);
                int val1 = initial_array[ind1];
                int val2 = initial_array[ind2];
                initial_array[ind1] = val2;
                initial_array[ind2] = val1;
            }
            shuffles.add(initial_array);
        }
        return shuffles;
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
