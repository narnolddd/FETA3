package feta.operations;

import feta.network.Network;
import feta.objectmodels.ObjectModel;

import java.util.ArrayList;

public abstract class Operation implements Comparable<Operation> {

    public int noChoices_;
    public int[] nodeChoices_;
    public long time_;

    public int compareTo(Operation o2) {
        if (time_ < o2.time_) {
            return -1;
        }
        if (time_ > o2.time_) {
            return 1;
        }
        return 0;
    }

    public abstract void build(Network net);

    public abstract void fill(Network net, ObjectModel om);

    public abstract double calcLogLike(Network net, ObjectModel obm);

    public abstract ArrayList<double[]> getComponentProbabilities(Network net, ObjectModel obm);

    public abstract void printMeanLike(double meanLike, ObjectModel om, Network network);
}
