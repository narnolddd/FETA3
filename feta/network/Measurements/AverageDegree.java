package feta.network.Measurements;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;

public class AverageDegree extends Measurement {

    /** NB average out degree = average in degree */
    private double avgDeg_;
    private double avgInDeg_;

    public AverageDegree() {
        nameDirected_= "AverageInDegree";
        nameUndirected_= "AverageDegree";
    }
    @Override
    public void update(DirectedNetwork net) {
        if (net.noNodes_>0) {
            avgInDeg_= 1.0*net.noLinks_/net.noNodes_;
        } else {
            avgInDeg_= 0.0;
        }
    }

    @Override
    public void update(UndirectedNetwork net) {
        if (net.noNodes_>0) {
            avgDeg_= 2.0*net.noLinks_/net.noNodes_;
        } else {
            avgDeg_= 0.0;
        }
    }

    @Override
    public String toStringDirected() {
        return String.format("%f",avgInDeg_);
    }

    @Override
    public String toStringUndirected() {
        return String.format("%f",avgDeg_);
    }
}
