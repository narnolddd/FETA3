package feta.objectmodels;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;

import java.util.HashSet;
import java.util.Set;

public class TriangleClosureDegree extends ObjectModelComponent{

    public Set<Integer> neighbourhood_;

    public void calcNormalisation(UndirectedNetwork network, int [] removed) {
        normalisationConstant_=0.0;
        Set<Integer> recents = new HashSet<Integer>(network.recentlyPickedNodes_);
        neighbourhood_= new HashSet<>();
        for (Integer r: recents) {
            neighbourhood_.add(r);
            neighbourhood_.addAll(network.neighbours_.get(r));
        }
        for (int nd: neighbourhood_) {
            normalisationConstant_ += network.degrees_[nd];
        }
        for (int node: removed) {
            if (neighbourhood_.contains(node)){
                normalisationConstant_-= network.degrees_[node];
            }
            neighbourhood_.remove(node);
        }
    }

    public void calcNormalisation(DirectedNetwork net, int[] removed) {
        normalisationConstant_=0.0;
        Set<Integer> recents = new HashSet<Integer>(net.recentlyPickedNodes_);
        neighbourhood_= new HashSet<>();
        for (Integer r: recents) {
            neighbourhood_.add(r);
            neighbourhood_.addAll(net.outLinks_.get(r));
        }
        for (int nd: neighbourhood_) {
            normalisationConstant_+=net.getInDegree(nd);
        }
        for (int node: removed) {
            if (neighbourhood_.contains(node)) {
                normalisationConstant_-= net.getInDegree(node);
            }
            neighbourhood_.remove(node);
        }
    }

    public double calcProbability(DirectedNetwork net, int node) {
        //System.out.println(normalisationConstant_);
        if (normalisationConstant_==0.0) {
            return 1.0/net.noNodes_;
        } else {
            if (neighbourhood_.contains(node)) {
                return net.getInDegree(node)/normalisationConstant_;
            } else return 0.0;
        }
    }

    public double calcProbability(UndirectedNetwork net, int node) {
        //System.out.println(normalisationConstant_);
        if (normalisationConstant_==0.0) {
            return 1.0/net.noNodes_;
        } else {
            if (neighbourhood_.contains(node)) {
                return net.degrees_[node]/normalisationConstant_;
            } else return 0.0;
        }
    }

    @Override
    public String toString() {
        return "TriangleClosureDegree";
    }
}
