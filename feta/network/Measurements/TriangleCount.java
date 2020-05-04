package feta.network.Measurements;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;

public class TriangleCount extends Measurement{

    private int totTri_;
    private int transitiveTri_;
    private int cyclicTri_;

    public TriangleCount() {
        nameDirected_="TransitiveTriCount CyclicTriCount";
        nameUndirected_="TriCount";
    }

    @Override
    public void update (DirectedNetwork net) {
        transitiveTri_=0;
        cyclicTri_=0;
    }

    @Override
    public void update (UndirectedNetwork net) {
        totTri_=net.getTriCount();
    }

    @Override
    public String toStringDirected () {
        return String.format("%1$d %2$d", transitiveTri_,cyclicTri_);
    }

    @Override
    public String toStringUndirected () {
        return String.format("%d",totTri_);
    }
}
