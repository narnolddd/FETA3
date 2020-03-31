package feta.objectmodels;

import feta.network.DirectedNetwork;
import feta.network.Network;
import feta.network.UndirectedNetwork;

public class RandomAttachment extends ObjectModelComponent {


    @Override
    public void calcNormalisation(Network net, int[] removed) {
        if (removed.length >= net.noNodes_) {
            throw new ArithmeticException("No nodes in network to calculate probability");
        }
        else normalisationConstant_= (double) net.noNodes_-removed.length;
        tempConstant_=normalisationConstant_;
    }

    public void calcNormalisation(UndirectedNetwork net, int[] removed){}
    public void calcNormalisation(DirectedNetwork net, int[] removed){}

    @Override
    public double calcProbability(UndirectedNetwork net, int node) {
        return 1.0/tempConstant_;
    }

    public double calcProbability(DirectedNetwork net, int node) {
        return 1.0/tempConstant_;
    }

    @Override
    public void updateNormalisation(UndirectedNetwork net, int[] removed) {
        if (removed.length==0) {
            tempConstant_=normalisationConstant_;
            return;
        }
        tempConstant_-= 1;
    }

    @Override
    public String toString() {
        return "Random";
    }
}
