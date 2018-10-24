package feta.operations;

import feta.network.Network;

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


}
