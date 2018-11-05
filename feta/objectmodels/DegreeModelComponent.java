package feta.objectmodels;

import feta.network.DirectedNetwork;
import feta.network.Network;
import feta.network.UndirectedNetwork;

public class DegreeModelComponent extends ObjectModelComponent{

    public boolean inDegree_=true;

    public void calcNormalisation(UndirectedNetwork network, int [] removed) {
        int degSum = 0;
        for (int i = 0; i < removed.length; i++) {
            if (removed[i]>=0) {
                degSum += network.getDegree(removed[i]);
            }
        }
        normalisationConstant_ = 2*network.noLinks_ - degSum;
    }

    public void calcNormalisation(DirectedNetwork network, int [] removed) {
        int degSum = 0;
        for (int i = 0; i < removed.length; i++) {
            if (inDegree_ && removed[i]>=0) {
                degSum+= network.getInDegree(removed[i]);
            }
            if (!inDegree_ && removed[i]>=0) {
                degSum+= network.getOutDegree(removed[i]);
            }
        }
        normalisationConstant_= network.noLinks_-degSum;
    }

    public double calcProbability(UndirectedNetwork net, int node) {
        if (normalisationConstant_==0.0){
            return 0.0;
        }
        return net.getDegree(node)/normalisationConstant_;
    }

    public double calcProbability(DirectedNetwork net, int node) {
        if (normalisationConstant_==0.0){
            return 0.0;
        }
        if (inDegree_) {
            return net.getInDegree(node)/normalisationConstant_;
        } else return net.getOutDegree(node)/normalisationConstant_;
    }

}
