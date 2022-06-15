package feta.objectmodels;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;
import org.json.simple.JSONObject;

public class PFP extends ObjectModelComponent{

    public double delta_=0.048;
    public boolean useInDegree_=true;

    public void calcNormalisation(UndirectedNetwork network, int [] removed) {
        double degSum = 0.0;
        for (int i = 0; i < network.noNodes_; i++) {
            degSum += Math.pow(network.getDegree(i), 1 + delta_ * Math.log10(network.getDegree(i)));
        }

        for (int i : removed) {
            if (i >= 0) {
                degSum -= Math.pow(network.getDegree(i), 1 + delta_ * Math.log10(network.getDegree(i)));
            }
        }
        normalisationConstant_=degSum;
    }

    public void calcNormalisation(DirectedNetwork network, int [] removed) {
        double degSum = 0.0;
        for (int i = 0; i < network.noNodes_; i++) {
            if (useInDegree_) {
                degSum+= Math.pow(network.getInDegree(i)+1, 1 + delta_*Math.log10(network.getInDegree(i)+1));
            } else {
                degSum+= Math.pow(network.getOutDegree(i), 1 + delta_*Math.log10(network.getOutDegree(i)+1));
            }
        }

        for (int i : removed) {
            if (i >= 0 && useInDegree_) {
                degSum -= Math.pow(network.getInDegree(i) + 1, 1 + delta_ * Math.log10(network.getInDegree(i) + 1));
            }
            if (i >= 0 && !useInDegree_) {
                degSum -= Math.pow(network.getOutDegree(i) + 1, 1 + delta_ * Math.log10(network.getOutDegree(i) + 1));
            }
        }
        normalisationConstant_=degSum;
    }

    public double calcProbability(UndirectedNetwork net, int node) {
        if (normalisationConstant_==0.0)
            return 0.0;
        return Math.pow(net.getDegree(node), 1 + delta_*Math.log10(net.getDegree(node)))/normalisationConstant_;
    }

    public double calcProbability(DirectedNetwork net, int node) {
        if (normalisationConstant_==0.0)
            return 0.0;
        if (useInDegree_)
            return Math.pow(net.getInDegree(node)+1, 1 + delta_*Math.log10(net.getInDegree(node)+1))/normalisationConstant_;
        return Math.pow(net.getOutDegree(node)+1, 1 + delta_*Math.log10(net.getInDegree(node)+1))/normalisationConstant_;
    }

    @Override
    public void updateNormalisation(UndirectedNetwork net, int [] removed) {
        int node = removed[removed.length-1];
        normalisationConstant_ -= Math.pow(net.getDegree(node), 1 + delta_ * Math.log10(net.getDegree(node)));
    }

    public void parseJSON(JSONObject params) {
        Boolean useInDeg = (Boolean) params.get("UseInDegree");
        if (useInDeg!= null) {
            useInDegree_=useInDeg;
        }
        Double delta = (Double) params.get("Delta");
        if (delta!=null) {
            delta_=delta;
        }
    }

    @Override
    public String toString() {
        return "PFP "+delta_;
    }
}
