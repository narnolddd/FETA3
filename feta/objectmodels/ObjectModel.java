package feta.objectmodels;

import feta.network.Network;

import java.util.ArrayList;


public class ObjectModel {

    private ArrayList<ObjectModelComponent> components_;
    private double[] weights_;

    public ObjectModel(){}

    /** Checks object model prescribed is valid */
    public void checkValid(){
        if (weights_.length == 0 || components_.size()==0) {
            throw new IllegalArgumentException("Object model components or weights unspecified");
        }
        if (weights_.length != components_.size()) {
            throw new IllegalArgumentException("Weights do not correspond to number of components");
        }

        double sum = 0.0;
        for (int i = 0; i< weights_.length; i++) {
            if (weights_[i]<0) {
                throw new IllegalArgumentException("Cannot have negative weights");
            }
            sum+= weights_[i];
        }

        if (sum == 0.0) {
            throw new IllegalArgumentException("No weights specified");
        }

        // Normalise weights if not done already
        for (int i = 0; i< weights_.length; i++) {
            weights_[i]/=sum;
        }
    }

    public double calcProbability(Network net, int node) {
        double probability_=0.0;
        for (int i = 0; i < components_.size(); i++) {
            probability_+= weights_[i]*components_.get(i).calcProbability(net, node);
        }
        return probability_;
    }

}
