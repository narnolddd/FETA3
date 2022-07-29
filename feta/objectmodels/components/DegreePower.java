package feta.objectmodels.components;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;
import org.json.simple.JSONObject;

import java.util.HashSet;

public class DegreePower extends ObjectModelComponent {

    public double power_=1.0;
    public boolean useInDegree_=true;

    @Override
    public void calcNormalisation(UndirectedNetwork net, int sourceNode, HashSet<Integer> availableNodes) {
        double degSum = 0.0;
        for (int node: availableNodes) {
            degSum += Math.pow(net.getDegree(node), power_);
        }

        if (degSum == 0.0) {
            random_=true;
            normalisationConstant_=availableNodes.size();
        }
        else {
            normalisationConstant_ = degSum;
        }
        tempConstant_ = normalisationConstant_;
    }

    @Override
    public void calcNormalisation(DirectedNetwork net, int sourceNode, HashSet<Integer> availableNodes) {
        double degSum = 0.0;
        for (int node: availableNodes) {
            if (useInDegree_) {
                degSum += Math.pow(net.getInDegree(node), power_);
            } else {
                degSum += Math.pow(net.getOutDegree(node), power_);
            }
        }

        if (degSum == 0.0) {
            random_=true;
            normalisationConstant_=availableNodes.size();
        }
        else {
            normalisationConstant_ = degSum;
        }
        tempConstant_ = normalisationConstant_;
    }

    @Override
    public void updateNormalisation(UndirectedNetwork net, HashSet<Integer> availableNodes, int chosenNode) {
        if (!random_) {
            tempConstant_-= Math.pow(net.getDegree(chosenNode), power_);
        }
        if (random_ || tempConstant_==0) {
            random_=true;
            tempConstant_=availableNodes.size();
        }
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
