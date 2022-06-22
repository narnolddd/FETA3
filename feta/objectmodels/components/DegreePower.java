package feta.objectmodels.components;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;
import org.json.simple.JSONObject;

public class DegreePower extends ObjectModelComponent {

    public double power_=1.0;
    public boolean useInDegree_=true;

    public void calcNormalisation(UndirectedNetwork network, int [] removed) {
        double degSum = 0.0;
        for (int i = 0; i < network.noNodes_; i++) {
            degSum += Math.pow(network.getDegree(i), power_);
        }

        for (int j = 0; j < removed.length; j++) {
            if (removed[j]>=0) {
                degSum -= Math.pow(network.getDegree(removed[j]),power_);
            }
        }
        normalisationConstant_=degSum;
    }

    public void calcNormalisation(DirectedNetwork network, int [] removed) {
        double degSum = 0.0;
        for (int i = 0; i < network.noNodes_; i++) {
            if (useInDegree_) {
                degSum+= Math.pow(network.getInDegree(i)+1,power_);
            } else {
                degSum+= Math.pow(network.getOutDegree(i),power_);
            }
        }

        for (int j = 0; j < removed.length; j++) {
            if (removed[j]>=0 && useInDegree_) {
                degSum -= Math.pow(network.getInDegree(removed[j])+1,power_);
            }
            if (removed[j]>=0 && !useInDegree_) {
                degSum-= Math.pow(network.getOutDegree(removed[j])+1, power_);
            }
        }
        normalisationConstant_=degSum;
    }

    public double calcProbability(UndirectedNetwork net, int node) {
        if (tempConstant_==0.0)
            return 0.0;
        return Math.pow(net.getDegree(node), power_)/tempConstant_;
    }

    public double calcProbability(DirectedNetwork net, int node) {
        if (tempConstant_==0.0)
            return 0.0;
        if (useInDegree_)
            return Math.pow(net.getInDegree(node)+1,power_)/tempConstant_;
        return Math.pow(net.getOutDegree(node)+1,power_)/tempConstant_;
    }

    @Override
    public void updateNormalisation(UndirectedNetwork net, int[] removed) {
        if (removed.length==0) {
            tempConstant_=normalisationConstant_;
            return;
        }
        int node = removed[removed.length-1];
        tempConstant_-= Math.pow(net.getDegree(node), power_);
    }

    public void parseJSON(JSONObject params) {
        Boolean useInDeg = (Boolean) params.get("UseInDegree");
        if (useInDeg!= null) {
            useInDegree_=useInDeg;
        }
        Double power = (Double) params.get("Power");
        if (power!=null) {
            power_=power;
        }
    }

    @Override
    public String toString() {
        return "DegreePower "+power_;
    }
}
