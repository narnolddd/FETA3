package feta.objectmodels.components;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;
import org.json.simple.JSONObject;

import java.util.HashSet;

public class DegreePower extends ObjectModelComponent {

    public enum Direction  {
        IN,
        OUT,
        BOTH
    }

    private Direction dir_;


    public DegreePower(){};
    public DegreePower(double power) {
        power_=power;
    }
    public DegreePower(double power, Direction dir) {
        power_=power;
        dir_=dir;
    }

    public double power_=1.0;

    @Override
    public void calcNormalisation(UndirectedNetwork net, int sourceNode, HashSet<Integer> availableNodes) {
        random_=false;
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
        random_=false;
        double degSum = 0.0;
        for (int node: availableNodes) {
            switch (dir_) {
                case IN:
                    degSum += Math.pow(net.getInDegree(node), power_);
                    break;
                case OUT:
                    degSum += Math.pow(net.getOutDegree(node), power_);
                    break;
                case BOTH:
                    degSum += Math.pow(net.getTotalDegree(node), power_);
                    break;
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
        if (random_)
            return 1.0/tempConstant_; // tempConstant is never zero if random is true
        return Math.pow(net.getDegree(node), power_)/tempConstant_;
    }

    public double calcProbability(DirectedNetwork net, int node) {
        if (random_){
            return 1.0/tempConstant_;
        }
        switch (dir_) {
            case IN:
                return Math.pow(net.getInDegree(node),power_)/tempConstant_;
            case OUT:
                return Math.pow(net.getOutDegree(node),power_)/tempConstant_;
            case BOTH:
                return Math.pow(net.getTotalDegree(node),power_)/tempConstant_;
            default:
            {System.err.println("No direction specified!");
                return 0.0;}
        }
    }

    public void parseJSON(JSONObject params) {
        String dir = (String) params.get("Direction");
        if (dir!= null) {
            switch (dir) {
                case "IN":
                    dir_= Direction.IN;
                    break;
                case "OUT":
                    dir_= Direction.OUT;
                    break;
                case "BOTH":
                    dir_= Direction.BOTH;
                    break;
                default:
                    System.err.println("Invalid direction "+dir+". Direction for DegreeModelComponent should be specified as IN, OUT or BOTH.");
                    break;
            }
        } else {
            dir_= Direction.IN;
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
