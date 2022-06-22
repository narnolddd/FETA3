package feta.objectmodels;

import feta.Methods;
import feta.network.Network;
import feta.objectmodels.components.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MixedModel {

    public ArrayList<ObjectModelComponent> components_;
    private double[] weights_;
    private boolean checkWeights_;
    private HashMap<double[], Double> likelihoods_;

    public MixedModel() {components_=new ArrayList<ObjectModelComponent>();}

    public HashMap<double[], Double> getLikelihoods () {
        return likelihoods_;
    }

    public double[] getWeights() {return weights_;}

    /** Checks object model prescribed is valid */
    public void checkValid(){
        if (weights_.length == 0 || components_.size()==0) {
            throw new IllegalArgumentException("Object model components or weights unspecified");
        }
        if (weights_.length != components_.size()) {
            throw new IllegalArgumentException("Weights do not correspond to number of components");
        }

        double sum = 0.0;
        for (double v : weights_) {
            if (v < 0) {
                throw new IllegalArgumentException("Cannot have negative weights");
            }
            sum += v;
        }
        if (Math.abs(1.0 - sum) > 0.0005) {
            throw new IllegalArgumentException("Model weights should add to 1.0, current sum "+sum);
        }
    }

    /** Functions for probability calculation */

    /** For calculating the normalisation constant for the first time */
    public void calcNormalisation(Network net, int [] alreadySampled) {
        for (ObjectModelComponent omc : components_) {
            omc.calcNormalisation(net, alreadySampled);
        }
    }

    public void calcNormalisation(Network net) {
        calcNormalisation(net, new int[0]);
    }

    /** For updating the normalisation constant after sampling nodes in removed */
    public void updateNormalisation(Network net, int [] removed) {
        for (ObjectModelComponent omc: components_) {
            omc.updateNormalisation(net, removed);
        }
    }

    public double[] getComponentProbs(Network net, int node) {
        double[] probs = new double[components_.size()];
        for (int i = 0; i < components_.size(); i++) {
            probs[i] = components_.get(i).calcProbability(net, node);
        }
        return probs;
    }

    /** Having calculated normalisation, get node probability */
    public double calcProbability(Network net, int node) {
        double probability_=0.0;
        for (int i = 0; i < components_.size(); i++) {
            probability_+= weights_[i]*components_.get(i).calcProbability(net, node);
        }
        return probability_;
    }

    /** Draw a single node without replacement */
    public final int nodeDrawWithoutReplacement(Network net, int[] alreadyChosenNodes) {
        ArrayList<Integer> nodeList = new ArrayList<Integer>();
        int [] chosen = Methods.removeNegativeNumbers(alreadyChosenNodes);
        int node;
        for (int j = 0; j < net.noNodes_; j++) {
            nodeList.add(j);
        }

        // Removes already chosen nodes from the sample space
        for (int i : chosen) {
            nodeList.remove((Integer) i);
        }

        if (nodeList.isEmpty()) {
            node = -1;
        }
        else {
            // This part does the sampling.
            updateNormalisation(net, chosen);
            double r = Math.random();
            double weightSoFar = 0.0;
            int l;
            for (l = 0; l < nodeList.size(); l++) {
                weightSoFar += calcProbability(net, nodeList.get(l));
                if (weightSoFar > r)
                    break;
            }
            if (l == nodeList.size())
                l--;
            node = nodeList.get(l);
        }
        return node;
    }

    public int nodeDrawWithReplacement(Network net) {
        return nodeDrawWithoutReplacement(net, new int[0]);
    }

    public int[] drawMultipleNodesWithoutReplacement(Network net, int sampleSize, int[] alreadyChosen) {
        int[] chosenNodes = new int[sampleSize];
        for (int j = 0; j<sampleSize; j++) {
            chosenNodes[j] = -1;
        }
        int [] removedFromSample= Methods.concatenate(alreadyChosen,chosenNodes);
        calcNormalisation(net, Methods.removeNegativeNumbers(removedFromSample));
        for (int i = 0; i < sampleSize; i++) {
            int chosenNode = nodeDrawWithoutReplacement(net, removedFromSample);
            chosenNodes[i]=chosenNode;
            removedFromSample[alreadyChosen.length + i]=chosenNode;
        }
        return Methods.removeNegativeNumbers(chosenNodes);
    }

    public int[] drawMultipleNodesWithReplacement(Network net, int sampleSize, int[] alreadyChosen) {
        int[] chosenNodes = new int[sampleSize];
        for (int j = 0; j<sampleSize; j++) {
            chosenNodes[j] = -1;
        }
        for (int i = 0; i < sampleSize; i++) {
            chosenNodes[i] = nodeDrawWithoutReplacement(net,alreadyChosen);
        }
        return Methods.removeNegativeNumbers(chosenNodes);
    }

    /** Performs check that normalisation is correct */
    public void checkNorm(Network net) {
        double sum = 0.0;
        calcNormalisation(net);
        for (int node = 0; node < net.noNodes_; node++) {
            sum += calcProbability(net, node);
        }
        if (Math.abs(sum - 1.0) > 0.0005) {
            System.err.println("Object model calculated not correct. Currently probabilities add to "+sum);
            System.exit(-1);
        }
    }

    /** Read components and weights from JSON */
    public void readObjectModelOptions(JSONArray componentList) {
        weights_= new double[componentList.size()];
        for (int i = 0; i< componentList.size(); i++) {
            JSONObject comp = (JSONObject) componentList.get(i);
            ObjectModelComponent omc = null;
            String omcClass = (String) comp.get("ComponentName");

            try {
                omc = instantiateOMC(findOMCClass(omcClass));
            } catch (ClassNotFoundException e) {
                System.err.println("Unable to find class "+omcClass);
                e.printStackTrace();
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                System.err.println("Unable to instantiate class "+omcClass);
                e.printStackTrace();
            }

            Double weight = (Double) comp.get("Weight");
            if (weight!=null) {
                weights_[i]=weight;
            }

            if (omc!=null) {
                omc.parseJSON(comp);
            }
            components_.add(omc);
        }
    }

    private ObjectModelComponent instantiateOMC(Class <? extends ObjectModelComponent> omcClass) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        ObjectModelComponent omc;
        try {
            Constructor <?> c = omcClass.getConstructor();
            omc = (ObjectModelComponent) c.newInstance();
            return omc;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            System.err.println("Unable to instantiate Object Model Component Class");
            throw e;
        }
    }

    private Class <? extends ObjectModelComponent > findOMCClass(String rawname) throws ClassNotFoundException {
        Class <? extends ObjectModelComponent> cl;
        String cname = rawname;
        try {
            cl = Class.forName(cname).asSubclass(ObjectModelComponent.class);
            return cl;
        } catch (ClassNotFoundException ignored) {
        }
        cname = "feta.objectmodels.components."+rawname;
        try {
            cl = Class.forName(cname).asSubclass(ObjectModelComponent.class);
            return cl;
        } catch (ClassNotFoundException e) {
            System.err.println("Unable to find class of name "+rawname+" or "+cname);
            throw e;
        }
    }

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MixedModel obm = (MixedModel) o;
        for (int i = 0; i < components_.size(); i++) {
            if (weights_[i]!=obm.weights_[i] ) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return Arrays.hashCode(weights_);
    }

    public String toString() {
        StringBuilder str= new StringBuilder();
        for (int i = 0; i < components_.size(); i++) {
            str.append(weights_[i]).append(" ").append(components_.get(i)).append("\n");
        }
        return str.toString();
    }

    public MixedModel copy() {
        MixedModel obm = new MixedModel();
        obm.components_=components_;
        return obm;
    }

    /** For assigning weights at runtime */
    public void setWeights(double[] weights) {
        weights_=weights;
    }

    public void initialiseLikelihoods(ArrayList<double[]> weights) {
        likelihoods_=new HashMap<double[], Double>();
        for (double[] weight: weights) {
            likelihoods_.put(weight,0.0);
        }
    }

    public void initialiseLikelihoods() {
        ArrayList<double[]> list = new ArrayList<>();
        list.add(weights_);
        initialiseLikelihoods(list);
    }

    public void updateLikelihoods(Network net, ArrayList<int[]> nodeOrders) {
        int noOrders = nodeOrders.size();
        HashMap<double[], Double> opLikeRatio = new HashMap<>();

        for (double[] weight : likelihoods_.keySet()) {
            opLikeRatio.put(weight, 0.0);
        }

        for (int[] order : nodeOrders) {
            HashMap<double[], Double> updated = updateIndividualLikelihoods(net, order, new int[0]);
            for (double[] weight : likelihoods_.keySet())
                opLikeRatio.put(weight, opLikeRatio.get(weight) + updated.get(weight));
        }

        for (double [] weight: likelihoods_.keySet()) {
            double like = opLikeRatio.get(weight);
            if (like == 0.0 || noOrders == 0) {
                return;
            }
            double logLike = Math.log(like) - Math.log(noOrders);
            likelihoods_.put(weight, likelihoods_.get(weight) + logLike);
        }


    }

    public HashMap<double[], Double> updateIndividualLikelihoods(Network net, int [] nodeSet, int[] alreadyChosen) {
        HashMap<double[], Double> likeRatio = new HashMap<>();
        for (double[] weight : likelihoods_.keySet()) {
            likeRatio.put(weight,1.0);
        }
        for (int node : nodeSet) {
            updateNormalisation(net, alreadyChosen);
            double[] probs = getComponentProbs(net, node);
            for (double[] weight : likelihoods_.keySet()) {
                double prob = 0.0;
                for (int j = 0; j < weight.length; j++) {
                    prob += weight[j] * probs[j];
                }
                prob *= (net.noNodes_ - alreadyChosen.length);
                likeRatio.put(weight, likeRatio.get(weight) * prob);
            }
            alreadyChosen = Methods.concatenate(alreadyChosen, new int[]{node});
        }
        return likeRatio;
    }

}
