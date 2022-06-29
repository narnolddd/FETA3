package feta.objectmodels.components;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;
import org.json.simple.JSONObject;

import java.util.HashSet;

public class DegreeWithAgeing extends ObjectModelComponent{

    public double ageingExponent_=0.5;
    public boolean useInDegree_=true;

    public double ageingFunction(int node, UndirectedNetwork net) {
        return Math.pow(net.age(node), - ageingExponent_) * net.degrees_[node];
    }

    public double ageingFunction(int node, DirectedNetwork net) {
        if (useInDegree_) {
            return Math.pow(net.age(node), - ageingExponent_) * net.getInDegree(node);
        } else return Math.pow(net.age(node), - ageingExponent_) * net.getOutDegree(node);
    }

    @Override
    public void calcNormalisation(UndirectedNetwork net, int sourceNode, HashSet<Integer> availableNodes) {

    }

    @Override
    public void updateNormalisation(UndirectedNetwork net, HashSet<Integer> availableNodes, int chosenNode) {

    }

    public void calcNormalisation(UndirectedNetwork net, int[] removed) {
        double sum = 0.0;
        for (int i = 0; i < net.noNodes_; i++) {
            sum += ageingFunction(i, net);
        }
        for (int j = 0; j < removed.length; j++) {
            if (removed[j]>=0) {
                sum-= ageingFunction(removed[j], net);
            }
        }
        normalisationConstant_=sum;
    }

    public void calcNormalisation(DirectedNetwork net, int[] removed) {
        double sum = 0.0;
        for (int i = 0; i < net.noNodes_; i++) {
            sum += ageingFunction(i, net);
        }
        for (int j = 0; j < removed.length; j++) {
            if (removed[j]>=0) {
                sum-= ageingFunction(removed[j], net);
            }
        }
        normalisationConstant_=sum;
    }

    public double calcProbability(UndirectedNetwork net, int node) {
        return ageingFunction(node,net)/normalisationConstant_;
    }

    public double calcProbability(DirectedNetwork net, int node) {
        return ageingFunction(node,net)/normalisationConstant_;
    }

    public void parseJSON(JSONObject params) {
        Double ageExp = (Double) params.get("AgeingExponent");
        if (ageExp != null){
            ageingExponent_=ageExp;
        }
    }

    @Override
    public String toString() {
        return "DegreeWithAgeing "+ageingExponent_;
    }
}
