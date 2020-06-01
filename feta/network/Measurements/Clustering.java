package feta.network.Measurements;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;

public class Clustering extends Measurement {

    public double averageClustering_;
    public double averageTransitivity_;

    public Clustering() {
        nameDirected_="AverageTransitivity";
        nameUndirected_= "AverageClustering";
    }

    @Override
    public void update(DirectedNetwork net) {
        int noNodes_=net.noNodes_;
        double sum = 0.0;
        for (int i = 0; i < noNodes_; i++) {
            sum+= net.localTransitivity(i);
        }
        if (sum == 0.0) {
            averageTransitivity_= 0.0;}
        else {
            averageTransitivity_= sum/noNodes_;
        }
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
        return String.format("%f", averageTransitivity_);
    }

    @Override
    public String toStringUndirected() {
        return String.format("%f", averageClustering_);
    }
}
