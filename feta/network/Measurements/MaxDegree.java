package feta.network.Measurements;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;

public class MaxDegree extends Measurement {

    int maxDeg_;
    int maxInDeg_;
    int maxOutDeg_;

    public MaxDegree() {
        nameDirected_= "MaxInDeg MaxOutDeg";
        nameUndirected_= "MaxDeg";
    }
    @Override
    public void update(DirectedNetwork net) {
        maxDeg_=net.maxInDeg_;
    }

    @Override
    public void update(UndirectedNetwork net) {

    }

    @Override
    public String toStringDirected() {
        return null;
    }

    @Override
    public String toStringUndirected() {
        return null;
    }
}
