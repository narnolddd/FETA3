package feta.objectmodels;

import feta.Methods;
import feta.network.Network;
import feta.objectmodels.components.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

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

    /** For calculating the normalisation constant for the first time */
    public void calcNormalisation(Network net, HashSet<Integer> availableNodes) {
        for (ObjectModelComponent omc : components_) {
            omc.calcNormalisation(net, availableNodes);
        }
    }

    public void calcNormalisation(Network net) {
        calcNormalisation(net, net.getNodeListCopy());
    }

    public void updateNormalisation(Network net, HashSet<Integer> availableNodes, int node) {
        for (ObjectModelComponent omc: components_) {
            omc.updateNormalisation(net, availableNodes, node);
        }
    }

    public double[] getComponentProbs(Network net, int node) {
        double[] probs = new double[components_.size()];
        for (int i = 0; i < components_.size(); i++) {
            probs[i] = net.calcProbability(components_.get(i), node);
        }
        return probs;
    }

    /** Having calculated normalisation, get node probability */
    public double calcProbability(Network net, int node) {
        double probability=0.0;
        for (int i = 0; i < components_.size(); i++) {
            probability+= weights_[i]*net.calcProbability(components_.get(i), node);
        }
        return probability;
    }

    public final int nodeDrawWithoutReplacement(Network net, HashSet<Integer> availableNodes, int seedNode) {
        if (seedNode == -1) {
            calcNormalisation(net, availableNodes);
        } else {
        updateNormalisation(net, availableNodes, seedNode);}
        double r = Math.random();
        double weightSoFar = 0.0;
        for (int node: availableNodes) {
            weightSoFar += calcProbability(net, node);
            if (weightSoFar > r)
                return node;
        }
        System.err.println("No nodes left to choose from");
        System.exit(-1);
        return -1;
    }

//    public int nodeDrawWithReplacement(Network net) {
//        return nodeDrawWithoutReplacement(net, new int[0]);
//    }

    public int[] drawMultipleNodesWithoutReplacement(Network net, int seedNode, int sampleSize, HashSet<Integer> availableNodes) {
        int[] chosenNodes = new int[sampleSize];
        if (sampleSize == 0)
            return chosenNodes;
        HashSet<Integer> nodesCopy = new HashSet<Integer>(availableNodes);
        if (sampleSize > nodesCopy.size()) {
            System.err.println("Desired sample size ("+sampleSize+") is larger than nodes available ("+nodesCopy.size()+")");
            System.exit(-1);
        }
        calcNormalisation(net, availableNodes);
        for (int i = 0; i<sampleSize; i++) {
            int node = nodeDrawWithoutReplacement(net, nodesCopy, seedNode);
            nodesCopy.remove(node);
            chosenNodes[i] = node;
            seedNode = node;
        }
        return chosenNodes;
    }

//    public int[] drawMultipleNodesWithReplacement(Network net, int sampleSize, int[] alreadyChosen) {
//        int[] chosenNodes = new int[sampleSize];
//        for (int j = 0; j<sampleSize; j++) {
//            chosenNodes[j] = -1;
//        }
//        for (int i = 0; i < sampleSize; i++) {
//            chosenNodes[i] = nodeDrawWithoutReplacement(net,alreadyChosen);
//        }
//        return Methods.removeNegativeNumbers(chosenNodes);
//    }

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

    public void checkUpdatedNorm(Network net, int[] from) {
        double sum = 0.0;
        for (int node = 0; node < net.noNodes_; node++) {
            sum += calcProbability(net, node);
        }
        for (int node : from) {
            sum -= calcProbability(net,node);
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

    public void updateLikelihoods(Network net, HashSet<Integer> availableNodes, ArrayList<int[]> nodeOrders) {
        int noOrders = nodeOrders.size();
        double[] opLikeRatio = new double[likelihoods_.size()];
        Arrays.fill(opLikeRatio, -1 * Double.POSITIVE_INFINITY);

        for (int[] order : nodeOrders) {
            updateIndividualLikelihoods(net, order, availableNodes, opLikeRatio);
        }

        int i=0;
        for (double [] weight: likelihoods_.keySet()) {
            double like = opLikeRatio[i];
            if (noOrders == 0) {
                return;
            }
            double logLike = like - Math.log(noOrders);
            likelihoods_.put(weight, likelihoods_.get(weight) + logLike);
            i++;
        }
    }

    public void updateIndividualLikelihoods(Network net, int[] nodeSet, HashSet<Integer> availableNodes, double[] opLikeRatio) {
        HashSet<Integer> sampleSpace = new HashSet<>(availableNodes);
        double[] likeRatio = new double[likelihoods_.size()];
        for (int i = 0; i < nodeSet.length; i++) {
            int node = nodeSet[i];
            if (i == 0 || node == -1) {
                calcNormalisation(net, sampleSpace);
            } else {
                updateNormalisation(net, sampleSpace, nodeSet[i-1]);
            }
            double[] probs = getComponentProbs(net,node);
            int k = 0;
            for (double[] weight : likelihoods_.keySet()) {
                double prob = 0.0;
                for (int j = 0; j < weight.length; j++) {
                    prob += weight[j] * probs[j];
                }
                prob *= (availableNodes.size());
                likeRatio[k] += Math.log(prob);
                k++;
            }
            sampleSpace.remove(node);
        }
        for (int i = 0; i < likeRatio.length; i++) {
            //System.out.println(opLikeRatio[i]);
            double tmp = Methods.addLogs(opLikeRatio[i],likeRatio[i]);
            opLikeRatio[i] = tmp;
            //System.out.println(likeRatio[i]+" "+opLikeRatio[i]);
        }
    }

    public String toString() {
        StringBuilder str= new StringBuilder();
        for (int i = 0; i < components_.size(); i++) {
            str.append(weights_[i]).append(" ").append(components_.get(i)).append("\n");
        }
        return str.toString();
    }

}
