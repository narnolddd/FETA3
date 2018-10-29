package feta.objectmodels;

import feta.network.Network;

public class RandomAttachment extends ObjectModelComponent {


    public void calcNormalisation(Network net, int[] removed) {
        if (removed.length >= net.noNodes_) {
            throw new ArithmeticException("No nodes in network to calculate probability");
        }
        else normalisationConstant_= (double) net.noNodes_-removed.length;
    }

    double calcProbability(Network net, int node) {
        return 1.0/normalisationConstant_;
    }
}
