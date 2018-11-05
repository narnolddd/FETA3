package feta.objectmodels;

import feta.network.DirectedNetwork;
import feta.network.Network;
import feta.network.UndirectedNetwork;

public class RankPreferentialAttachment extends ObjectModelComponent {

    public double alpha_= 0.5;

    @Override
    public void calcNormalisation(Network net, int[] removed) {
        double rankSum = 0.0;
        for (int i = 0; i < net.noNodes_; i++) {
            rankSum+= Math.pow(i+1, - alpha_);
        }
        for (int j = 0 ; j < removed.length; j++) {
            if (removed[j]>=0) {
                rankSum-= Math.pow(removed[j]+1, - alpha_);
            }
        }
    }

    public void calcNormalisation(UndirectedNetwork net, int[] removed){}
    public void calcNormalisation(DirectedNetwork net, int[] removed){}

    public double calcProbability(UndirectedNetwork net, int node) {
        return Math.pow(node + 1, - alpha_)/normalisationConstant_;
    }

    public double calcProbability(DirectedNetwork net, int node) {
        return Math.pow(node + 1, - alpha_)/normalisationConstant_;
    }
}
