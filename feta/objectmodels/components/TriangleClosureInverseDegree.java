package feta.objectmodels.components;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;

import java.util.HashSet;
import java.util.Set;

public class TriangleClosureInverseDegree extends ObjectModelComponent {

    public Set<Integer> neighbourhood_;

    public void calcNormalisation(UndirectedNetwork network, int [] removed) {
        normalisationConstant_=0.0;
        Set<Integer> recents = new HashSet<Integer>(network.recentlyPickedNodes_);
        neighbourhood_= new HashSet<>();
        for (Integer r: recents) {
            neighbourhood_.add(r);
            for (int n: network.neighbours_.get(r)) {
                neighbourhood_.add(n);
            }
        }
        for (int nd: neighbourhood_) {
            normalisationConstant_ += 1.0 / (network.degrees_[nd] + 1);
        }
        for (int node: removed) {
            if (neighbourhood_.contains(node)){
                normalisationConstant_-= 1.0/ (network.degrees_[node] + 1);
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
            for (int n: net.outLinks_.get(r)) {
                neighbourhood_.add(n);
            }
        }
        for (int nd: neighbourhood_) {
            normalisationConstant_+=1.0/ (1 + net.getInDegree(nd));
        }
        for (int node: removed) {
            if (neighbourhood_.contains(node)) {
                normalisationConstant_-= 1.0/ (1 + net.getInDegree(node));
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
                return (1.0 / (1 + net.getInDegree(node)))/normalisationConstant_;
            } else return 0.0;
        }
    }

    public double calcProbability(UndirectedNetwork net, int node) {
        //System.out.println(normalisationConstant_);
        if (normalisationConstant_==0.0) {
            return 1.0/net.noNodes_;
        } else {
            if (neighbourhood_.contains(node)) {
                return (1.0/ (1 + net.degrees_[node]))/normalisationConstant_;
            } else return 0.0;
        }
    }

    @Override
    public String toString() {
        return "TriangleClosureInverseDegree";
    }
}
