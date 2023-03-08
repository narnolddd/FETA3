package feta.objectmodels.components;

import feta.network.DirectedNetwork;
import feta.network.Network;
import feta.network.UndirectedNetwork;
import org.json.simple.JSONObject;

import java.util.HashSet;

public class RankPreferentialAttachment extends ObjectModelComponent {

    public double alpha_= 0.5;

    @Override
    public void calcNormalisation(UndirectedNetwork net, int sourceNode, HashSet<Integer> availableNodes) {
        double rankSum = 0.0;
        for (int node: availableNodes) {
            rankSum+= Math.pow(node+1, - alpha_);
        }
        normalisationConstant_=rankSum;
        tempConstant_=normalisationConstant_;
    }

    @Override
    public void calcNormalisation(DirectedNetwork net, int sourceNode, HashSet<Integer> availableNodes) {
        double rankSum = 0.0;
        for (int node: availableNodes) {
            rankSum+= Math.pow(node+1, - alpha_);
        }
        normalisationConstant_=rankSum;
        tempConstant_=normalisationConstant_;
    }

    @Override
    public void updateNormalisation(UndirectedNetwork net, HashSet<Integer> availableNodes, int chosenNode) {
        if (!random_) {
            tempConstant_-=Math.pow(chosenNode+1,-alpha_);
        }
        if (random_ || tempConstant_==0) {
            random_=true;
            tempConstant_=availableNodes.size();
        }
    }

    public void calcNormalisation(UndirectedNetwork net, int[] removed){}
    public void calcNormalisation(DirectedNetwork net, int[] removed){}

    public double calcProbability(UndirectedNetwork net, int node) {
        return Math.pow(node + 1, - alpha_)/tempConstant_;
    }

    public double calcProbability(DirectedNetwork net, int node) {
        return Math.pow(node + 1, - alpha_)/tempConstant_;
    }

    public void parseJSON(JSONObject params) {
        Double alpha = (Double) params.get("Alpha");
        if (alpha != null) {
            alpha_=alpha;
        }
    }

    @Override
    public String toString() {
        return "RankPreference "+alpha_;
    }
}
