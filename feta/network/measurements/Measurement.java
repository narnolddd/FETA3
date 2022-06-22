package feta.network.measurements;

import feta.network.DirectedNetwork;
import feta.network.Network;
import feta.network.UndirectedNetwork;

public abstract class Measurement{

    public Network network_;
    public boolean directed_;
    public String nameDirected_;
    public String nameUndirected_;

    public final void setNetwork(Network net) {
        network_=net;
    }

    public final void setDirected(boolean directed) {
        directed_=directed;
    }

    public final void update() {
        if (directed_) {
            update((DirectedNetwork) network_);
        } else {
            update((UndirectedNetwork) network_);
        }
    }

    public abstract void update (DirectedNetwork net);
    public abstract void update (UndirectedNetwork net);

    public final String toString() {
        String str;
        if (directed_) {
            str = toStringDirected();
        }
        else {
            str = toStringUndirected();
        }
        return str;
    }
    public abstract String toStringDirected();
    public abstract String toStringUndirected();

    public String nameToString() {
        String name;
        if (directed_) {
            name = nameDirected_;
        } else {
            name = nameUndirected_;
        }
        return name;
    }
}
