package feta.operations;

import feta.network.Network;
import feta.objectmodels.ObjectModel;

import java.util.ArrayList;

public class Clique extends Operation {

    String[] members_;

    public Clique(int cliqueSize_) {
        members_= new String[cliqueSize_];
    }

    public void build(Network net) {
        System.err.println("You haven't written this part yet!");
    }

    public void fill(Network net, ObjectModel om) {};

    public double calcLogLike(Network net, ObjectModel om){
        return 0.0;
    }

    public ArrayList<double[]> getComponentProbabilities(Network net, ObjectModel obm) {
        return new ArrayList<double[]>();
    }

    public void printMeanLike(double meanLike, ObjectModel om, Network network){}
}
