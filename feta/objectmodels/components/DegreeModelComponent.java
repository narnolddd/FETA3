package feta.objectmodels.components;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;
import org.json.simple.JSONObject;

import java.util.HashSet;

public class DegreeModelComponent extends ObjectModelComponent{

    public boolean useInDegree_=true;

    @Override
    public void calcNormalisation(UndirectedNetwork net, int sourceNode, HashSet<Integer> availableNodes) {
        random_=false;
        int total = 0;
        for (int node: availableNodes) {
            total+=net.getDegree(node);
        }

        if (total > 0) {
            normalisationConstant_ = total;
        } else {
            random_=true;
            normalisationConstant_=availableNodes.size();
        }
        tempConstant_=normalisationConstant_;
    }

    @Override
    public void calcNormalisation(DirectedNetwork net, int sourceNode, HashSet<Integer> availableNodes) {
        random_=false;
        int total = 0;
        for (int node: availableNodes) {
            if (useInDegree_) {
                total += net.getInDegree(node);
            } else {
                total += net.getOutDegree(node);
            }
        }

        if (total > 0) {
            normalisationConstant_ = total;
        } else {
            random_=true;
            normalisationConstant_=availableNodes.size();
        }
        tempConstant_=normalisationConstant_;
    }

    @Override
    public void updateNormalisation(UndirectedNetwork net, HashSet<Integer> availableNodes, int chosenNode) {
        if (!random_) {
            tempConstant_-=net.getDegree(chosenNode);
        }
        if (random_ || tempConstant_==0) {
            random_=true;
            tempConstant_=availableNodes.size();
        }
    }

    public void updateNormalisation(DirectedNetwork net, HashSet<Integer> availableNodes, int chosenNode) {
        if (!random_) {
            if (useInDegree_) {
                tempConstant_ -= net.getInDegree(chosenNode);
            } else {
                tempConstant_ -= net.getOutDegree(chosenNode);
            }
        }
        if (random_ || tempConstant_==0) {
            random_=true;
            tempConstant_=availableNodes.size();
        }
    }

    public double calcProbability(UndirectedNetwork net, int node) {
        if (tempConstant_==0.0){
            return 0.0;
        }
        return net.getDegree(node)/tempConstant_;
    }

    public double calcProbability(DirectedNetwork net, int node) {
        if (normalisationConstant_==0.0){
            return 0.0;
        }
        if (useInDegree_) {
            return (net.getInDegree(node))/tempConstant_;
        } else return (net.getOutDegree(node))/tempConstant_;
    }

    public void parseJSON(JSONObject params) {
        Boolean useInDeg = (Boolean) params.get("UseInDegree");
        if (useInDeg!= null) {
            useInDegree_=useInDeg;
        }
    }

    @Override
    public String toString() {
        return "Degree";
    }
}
