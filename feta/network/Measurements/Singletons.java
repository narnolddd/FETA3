package feta.network.Measurements;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;

public class Singletons extends Measurement{

    private int singles_;
    private int doubles_;

    public Singletons() {
        nameDirected_="Singletons Doubletons";
        nameUndirected_="Singletons Doubletons";
    }

    @Override
    public void update (DirectedNetwork net) {
        int[] inDegDist = net.getInDegreeDist();
        singles_=inDegDist[1];
        doubles_=inDegDist[2];
    }

    @Override
    public void update (UndirectedNetwork net) {
        int[] degDist = net.getDegreeDist();
        singles_=degDist[1];
        doubles_=degDist[2];
    }

    @Override
    public String toStringDirected () {
        return String.format("%1$d %2$d",singles_, doubles_);
    }

    @Override
    public String toStringUndirected () {
        return String.format("%1$d %2$d",singles_, doubles_);
    }
}
