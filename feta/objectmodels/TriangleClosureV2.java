package feta.objectmodels;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;

import java.util.HashSet;
import java.util.Set;

public class TriangleClosureV2 extends ObjectModelComponent{


    public Set<Integer> neighbourhood_;

    public void calcNormalisation(UndirectedNetwork network, int [] removed) {
        neighbourhood_= new HashSet<>();
        if (removed.length==0) {
            for (int node = 0; node < network.noNodes_; node++) {
                neighbourhood_.add(node);
            }
            normalisationConstant_=network.noNodes_;
            return;
        }
        for (int node: removed) {
            neighbourhood_.remove(node);
        }

//        String neighbourString="";
//        for (int node: neighbourhood_){
//            neighbourString+=node+" ";
//        }
//        System.out.println(neighbourString);

        normalisationConstant_=neighbourhood_.size();
    }

    public void calcNormalisation(DirectedNetwork net, int[] removed) {
        Set<Integer> recents = new HashSet<Integer>(net.recentlyPickedNodes_);
        neighbourhood_= new HashSet<>();
        for (Integer r: recents) {
            neighbourhood_.add(r);
            for (int n: net.outLinks_.get(r)) {
                neighbourhood_.add(n);
            }
        }
        for (int node: removed) {
            neighbourhood_.remove(node);
        }
        normalisationConstant_=neighbourhood_.size();
    }

    public double calcProbability(DirectedNetwork net, int node) {
        //System.out.println(normalisationConstant_);
        if (normalisationConstant_==0.0) {
            return 1.0/net.noNodes_;
        } else {
            if (neighbourhood_.contains(node)) {
                return 1.0/normalisationConstant_;
            } else return 0.0;
        }
    }

    public double calcProbability(UndirectedNetwork net, int node) {
        //System.out.println(normalisationConstant_);
        if (normalisationConstant_==0.0) {
            return 1.0/net.noNodes_;
        } else {
            if (neighbourhood_.contains(node)) {
                return 1.0/normalisationConstant_;
            } else return 0.0;
        }
    }

    @Override
    public String toString() {
        return "TriangleClosure";
    }

}
