package feta.network.measurements;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;

public class MeanSquaredDegree extends Measurement {

    public MeanSquaredDegree(){
        nameDirected_="MeanSquaredInDeg MeanSquaredOutDeg";
        nameUndirected_="MeanSquaredDeg";
    }

    private double meanDegSq_;
    private double meanInDegSq_;
    private double meanOutDegSq_;

    @Override
    public void update(DirectedNetwork net) {
        double inSum = 0.0;
        double outSum = 0.0;
        for (int i = 0; i < net.noNodes_; i++) {
            double inDeg = net.getInDegree(i);
            double outDeg = net.getOutDegree(i);
            inSum += inDeg*inDeg;
            outSum += outDeg*outDeg;
        }
        if (net.noNodes_ > 0) {
            meanInDegSq_ = inSum/net.noNodes_;
            meanOutDegSq_ = outSum/net.noNodes_;
        } else {
            meanInDegSq_ = 0.0;
            meanOutDegSq_= 0.0;
        }
    }

    @Override
    public void update(UndirectedNetwork net) {
        double sum = 0.0;
        for (int i = 0; i < net.noNodes_; i++) {
            double deg = net.getDegree(i);
            sum += deg*deg;
        }
        if (net.noNodes_ > 0) {
            meanDegSq_ = sum/net.noNodes_;
        } else {
            meanDegSq_ = 0.0;
        }
    }

    @Override
    public String toStringDirected() {
        return String.format("%1$f %2$f",meanInDegSq_,meanOutDegSq_);
    }

    @Override
    public String toStringUndirected() {
        return String.format("%f",meanDegSq_);
    }
}
