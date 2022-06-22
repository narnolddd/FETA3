package feta.network.measurements;

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
        maxInDeg_=net.maxInDeg_;
        maxOutDeg_=net.maxOutDeg_;
    }

    @Override
    public void update(UndirectedNetwork net) {
        maxDeg_=net.maxDeg_;
    }

    @Override
    public String toStringDirected() {
        return String.format("%1$d %2$d", maxInDeg_, maxOutDeg_);
    }

    @Override
    public String toStringUndirected() {
        return String.format("%d", maxDeg_);
    }
}
