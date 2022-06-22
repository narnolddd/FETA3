package feta.objectmodels.components;

import feta.network.DirectedNetwork;
import feta.network.Network;
import feta.network.UndirectedNetwork;
import org.json.simple.JSONObject;

public class RankPreferentialAttachment extends ObjectModelComponent {

    public double alpha_= 0.5;

    @Override
    public void calcNormalisation(Network net, int[] removed) {
        double rankSum = 0.0;
        for (int i = 0; i < net.noNodes_; i++) {
            rankSum+= Math.pow(i+1, - alpha_);
        }
        for (int i : removed) {
            if (i >= 0) {
                rankSum -= Math.pow(i + 1, -alpha_);
            }
        }
        normalisationConstant_=rankSum;
        tempConstant_=normalisationConstant_;
    }

    public void calcNormalisation(UndirectedNetwork net, int[] removed){}
    public void calcNormalisation(DirectedNetwork net, int[] removed){}

    public double calcProbability(UndirectedNetwork net, int node) {
        return Math.pow(node + 1, - alpha_)/tempConstant_;
    }

    public double calcProbability(DirectedNetwork net, int node) {
        return Math.pow(node + 1, - alpha_)/tempConstant_;
    }

    @Override
    public void updateNormalisation(UndirectedNetwork net, int[] removed) {
        if (removed.length==0) {
            tempConstant_=normalisationConstant_;
            return;
        }
        int node = removed[removed.length-1];
        tempConstant_-= Math.pow(node + 1, - alpha_);
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
