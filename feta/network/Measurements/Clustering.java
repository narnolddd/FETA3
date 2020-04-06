package feta.network.Measurements;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;

public class Clustering extends Measurement {

    public double averageClustering_;

    public Clustering() {
        nameDirected_="";
        nameUndirected_= "AverageClustering";
    }

    @Override
    public void update(DirectedNetwork net) {
        averageClustering_=0.0;
    }

    @Override
    public void update(UndirectedNetwork net) {
        int noNodes_=net.noNodes_;
        double sum = 0.0;
        for (int i = 0; i < noNodes_; i++) {
            sum+= net.localCluster(i);
        }
        if (sum == 0.0) {
            averageClustering_= 0.0;}
        else {
            averageClustering_= sum/noNodes_;
        }
    }

    @Override
    public String toStringDirected() {
        return "";
    }

    @Override
    public String toStringUndirected() {
        return String.format("%f", averageClustering_);
    }
}
