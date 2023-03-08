package feta.objectmodels.components;

import feta.network.DirectedNetwork;
import feta.network.Network;
import feta.network.UndirectedNetwork;

import java.util.HashSet;

public class RandomAttachment extends ObjectModelComponent {


    @Override
    public void calcNormalisation(UndirectedNetwork net, int sourceNode, HashSet<Integer> availableNodes) {
        normalisationConstant_ = availableNodes.size();
        tempConstant_= normalisationConstant_;
    }

    @Override
    public void calcNormalisation(DirectedNetwork net, int sourceNode, HashSet<Integer> availableNodes) {
        normalisationConstant_ = availableNodes.size();
        tempConstant_= normalisationConstant_;
    }

    public void calcNormalisation(UndirectedNetwork net, int[] removed){}
    public void calcNormalisation(DirectedNetwork net, int[] removed){}

    @Override
    public double calcProbability(UndirectedNetwork net, int node) {
        if (tempConstant_==0)
            return 0.0;
        return 1.0/tempConstant_;
    }

    public double calcProbability(DirectedNetwork net, int node) {
        if (tempConstant_==0)
            return 0.0;
        return 1.0/tempConstant_;
    }


    @Override
    public String toString() {
        return "Random";
    }
}
