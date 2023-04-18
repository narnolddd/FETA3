package feta.objectmodels.components;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;
import org.json.simple.JSONObject;

import java.util.HashSet;

public class DegreeModelComponent extends ObjectModelComponent{

    public enum Direction  {
        IN,
        OUT,
        BOTH
    }

    private Direction dir_;

    public DegreeModelComponent(){
        dir_=Direction.IN;
    }

    public DegreeModelComponent(Direction dir){
        dir_=dir;
    }

    @Override
    public void calcNormalisation(UndirectedNetwork net, int sourceNode, HashSet<Integer> availableNodes) {
        random_=false;
        int total = 0;
        for (int node: availableNodes) {
            total+=net.getDegree(node);
        }

        if (total > 0) {
            normalisationConstant_ = total;
        } else {
            random_=true;
            normalisationConstant_=availableNodes.size();
        }
        tempConstant_=normalisationConstant_;
    }

    @Override
    public void calcNormalisation(DirectedNetwork net, int sourceNode, HashSet<Integer> availableNodes) {
        random_=false;
        int total = 0;
        for (int node: availableNodes) {
            switch (dir_) {
                case IN:
                    total += net.getInDegree(node);
                    break;
                case OUT:
                    total += net.getOutDegree(node);
                    break;
                case BOTH:
                    total += net.getTotalDegree(node);
                    break;
            }
        }

        if (total > 0) {
            normalisationConstant_ = total;
        } else {
            random_=true;
            normalisationConstant_=availableNodes.size();
        }
        tempConstant_=normalisationConstant_;
    }

    @Override
    public void updateNormalisation(UndirectedNetwork net, HashSet<Integer> availableNodes, int chosenNode) {
        if (!random_) {
            tempConstant_-=net.getDegree(chosenNode);
        }
        if (random_ || tempConstant_==0) {
            random_=true;
            tempConstant_=availableNodes.size();
        }
    }

    public void updateNormalisation(DirectedNetwork net, HashSet<Integer> availableNodes, int chosenNode) {
        if (!random_) {
            for (int node: availableNodes) {
                switch (dir_) {
                    case IN:
                        tempConstant_ -= net.getInDegree(node);
                        break;
                    case OUT:
                        tempConstant_ -= net.getOutDegree(node);
                        break;
                    case BOTH:
                        tempConstant_ -= net.getTotalDegree(node);
                        break;
                }
            }
        }
        if (random_ || tempConstant_==0) {
            random_=true;
            tempConstant_=availableNodes.size();
        }
    }

    public double calcProbability(UndirectedNetwork net, int node) {
        if (random_){
            return 1.0/tempConstant_;
        }
        return net.getDegree(node)/tempConstant_;
    }

    public double calcProbability(DirectedNetwork net, int node) {
        if (random_){
            return 1.0/tempConstant_;
        }
        switch (dir_) {
            case IN:
                return net.getInDegree(node)/tempConstant_;
            case OUT:
                return net.getOutDegree(node)/tempConstant_;
            case BOTH:
                return net.getTotalDegree(node)/tempConstant_;
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
                    dir_=Direction.IN;
                    break;
                case "OUT":
                    dir_=Direction.OUT;
                    break;
                case "BOTH":
                    dir_=Direction.BOTH;
                    break;
                default:
                    System.err.println("Invalid direction "+dir+". Direction for DegreeModelComponent should be specified as IN, OUT or BOTH.");
                    break;
            }
        } else {
            dir_=Direction.IN;
        }
    }

    @Override
    public String toString() {
        return "Degree";
    }
}
