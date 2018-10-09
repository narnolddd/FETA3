package feta.objectmodels;

import feta.network.Network;

public abstract class ObjectModelComponent {

    private double weight = 1.0;

    /** Methods relating to Object Model */

    abstract double calcProbability(Network net, int node);
    abstract double calcNormalisation(Network net);
}
