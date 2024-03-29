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
        double degSum = 0.0;
        for (int node: availableNodes) {
            degSum += ageingFunction(node,net);
        }

        if (degSum == 0.0) {
            random_=true;
            normalisationConstant_=availableNodes.size();
        }
        else {
            normalisationConstant_ = degSum;
        }
        tempConstant_ = normalisationConstant_;
    }

    @Override
    public void calcNormalisation(DirectedNetwork net, int sourceNode, HashSet<Integer> availableNodes) {
        double degSum = 0.0;
        for (int node: availableNodes) {
            degSum += ageingFunction(node,net);
        }

        if (degSum == 0.0) {
            random_=true;
            normalisationConstant_=availableNodes.size();
        }
        else {
            normalisationConstant_ = degSum;
        }
        tempConstant_ = normalisationConstant_;
    }

    @Override
    public void updateNormalisation(UndirectedNetwork net, HashSet<Integer> availableNodes, int chosenNode) {
        if (!random_) {
            tempConstant_-= ageingFunction(chosenNode,net);
        }
        if (random_ || tempConstant_==0) {
            random_=true;
            tempConstant_=availableNodes.size();
        }
    }

    public double calcProbability(UndirectedNetwork net, int node) {
        return ageingFunction(node,net)/tempConstant_;
    }

    public double calcProbability(DirectedNetwork net, int node) {
        return ageingFunction(node,net)/tempConstant_;
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
