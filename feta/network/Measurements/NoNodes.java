package feta.network.Measurements;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;

public class NoNodes extends Measurement {

    int noNodes_;

    public NoNodes() {
        nameDirected_=nameUndirected_="NoNodes";
    }

    @Override
    public void update(DirectedNetwork net) {
        noNodes_=net.noNodes_;
    }

    @Override
    public void update(UndirectedNetwork net) {
        noNodes_=net.noNodes_;
    }

    @Override
    public String toStringDirected() {
        return String.format("%d",noNodes_);
    }

    @Override
    public String toStringUndirected() {
        return String.format("%d",noNodes_);
    }
}
