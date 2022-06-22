package feta.network.measurements;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;

public class NoLinks extends Measurement{

    int noLinks_;

    public NoLinks() {
        nameDirected_=nameUndirected_="NoLinks";
    }

    @Override
    public void update(DirectedNetwork net) {
        noLinks_= net.noLinks_;
    }

    @Override
    public void update(UndirectedNetwork net) {
        noLinks_=net.noLinks_;
    }

    @Override
    public String toStringDirected() {
        return String.format("%d",noLinks_);
    }

    @Override
    public String toStringUndirected() {
        return String.format("%d",noLinks_);
    }
}
