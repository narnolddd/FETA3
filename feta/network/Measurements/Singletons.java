package feta.network.Measurements;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;

public class Singletons extends Measurement{

    private int singles_;
    private int doubles_;

    private int singlesIn_;
    private int singlesOut_;
    private int doublesIn_;
    private int doublesOut_;

    public Singletons() {
        nameDirected_="SingletonsIn SingletonsOut DoubletonsIn DoubletonsOut";
        nameUndirected_="Singletons Doubletons";
    }

    @Override
    public void update (DirectedNetwork net) {
        int[] inDegDist = net.getInDegreeDist();
        int[] outDegDist = net.getOutDegreeDist();
        singlesIn_=inDegDist[1];
        singlesOut_=outDegDist[1];
        doublesIn_=inDegDist[2];
        doublesOut_=outDegDist[2];
    }

    @Override
    public void update (UndirectedNetwork net) {
        int[] degDist = net.getDegreeDist();
        singles_=degDist[1];
        doubles_=degDist[2];
    }

    @Override
    public String toStringDirected () {
        return String.format("%1$d %2$d %3$d %4$d",singlesIn_, singlesOut_, doublesIn_, doublesOut_);
    }

    @Override
    public String toStringUndirected () {
        return String.format("%1$d %2$d",singles_, doubles_);
    }
}
