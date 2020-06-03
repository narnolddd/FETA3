package feta.objectmodels;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;

import java.util.HashMap;
import java.util.Set;

public class TriangleClosure2 extends ObjectModelComponent{

    private Set<Integer> neighbourhood_;
    private int [] justChosen_;


    @Override
    public void calcNormalisation (UndirectedNetwork net, int[] removed) {
        double normalisation=0.0;
        HashMap <Integer,Integer> occurrences = new HashMap<>();
        justChosen_=removed;
        if (justChosen_.length==0)
            return;
        int node = justChosen_[justChosen_.length-1];
        for (int n1 : net.neighbours_.get(node)) {
            for (int n2: net.neighbours_.get(n1)) {
                if(n2 == node)
                    continue;
                if(net.isLink(node,n2))
                    continue;

            }
        }
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
