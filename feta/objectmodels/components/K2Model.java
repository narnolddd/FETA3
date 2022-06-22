package feta.objectmodels.components;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;

public class K2Model extends ObjectModelComponent{
    @Override
    public void calcNormalisation (UndirectedNetwork net, int[] removed) {
    }

    @Override
    public void calcNormalisation (DirectedNetwork net, int[] removed) {

    }

    @Override
    public double calcProbability (UndirectedNetwork net, int node) {
        return 0;
    }

    @Override
    public double calcProbability (DirectedNetwork net, int node) {
        return 0;
    }
}
