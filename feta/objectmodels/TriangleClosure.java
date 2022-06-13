package feta.objectmodels;

import feta.network.DirectedNetwork;
import feta.network.Measurements.MaxDegree;
import feta.network.Network;
import feta.network.UndirectedNetwork;
import org.json.simple.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class TriangleClosure extends ObjectModelComponent {

    private Set<Integer> neighbourhood_;
    private int [] justChosen_;
    private int depth_=1;

    public void calcNormalisation(UndirectedNetwork net, int [] removed) {
        justChosen_=removed;
        neighbourhood_= new HashSet<>();
        int depth = Math.min(depth_,justChosen_.length);
        for (int i=0; i < depth; i++) {
            int node = justChosen_[justChosen_.length-1-i];
            neighbourhood_.addAll(net.neighbours_.get(node));
        }
        for (int node: removed) {
            neighbourhood_.remove(node);
        }

        normalisationConstant_=neighbourhood_.size();
        tempConstant_=normalisationConstant_;
    }

    public void calcNormalisation(DirectedNetwork net, int[] removed) {
        justChosen_=removed;
        neighbourhood_= new HashSet<>();
        int depth = Math.min(depth_,justChosen_.length);
        for (int i=0; i < depth; i++) {
            int node = justChosen_[justChosen_.length - 1 -i];
            neighbourhood_.addAll(net.outLinks_.get(node));
        }
        for (int node: removed) {
            neighbourhood_.remove(node);
        }

        normalisationConstant_=neighbourhood_.size();
        tempConstant_=normalisationConstant_;
    }

    public double calcProbability(DirectedNetwork net, int node) {
        //System.out.println(normalisationConstant_);
        if (normalisationConstant_==0.0) {
            return 1.0/net.noNodes_;
        } else {
            if (neighbourhood_.contains(node)) {
                return 1.0/tempConstant_;
            } else return 0.0;
        }
    }

    public double calcProbability(UndirectedNetwork net, int node) {
        if (tempConstant_==0.0) {
            return 1.0/net.noNodes_;
        } else {
            if (neighbourhood_.contains(node)) {
                return 1.0/tempConstant_;
            } else return 0.0;
        }
    }

    public void parseJSON(JSONObject params) {
        Long depth = (Long) params.get("Depth");
        if (depth!= null) {
            depth_=depth.intValue();
        }
    }

    @Override
    public String toString() {
        return "TriangleClosure "+depth_;
    }
}
