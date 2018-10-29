package feta.objectmodels;

import feta.network.Network;

public abstract class ObjectModelComponent {

    /** Normalisation constant (changing) to avoid calculating every time probability of node is needed */
    double normalisationConstant_;

    /** Methods relating to Object Model */

    abstract double calcProbability(Network net, int node);

    abstract void calcNormalisation(Network net, int[] removed);

    void calcNormalisation(Network net) {
        calcNormalisation(net, new int[0]);
    }
}
