package feta.objectmodels;

import feta.network.DirectedNetwork;
import feta.network.Network;
import feta.network.UndirectedNetwork;
import org.json.simple.JSONObject;

public abstract class ObjectModelComponent {

    /** Normalisation constant (changing) to avoid calculating every time probability of node is needed */
    double normalisationConstant_;

    /** Methods relating to Object Model */

    public double calcProbability(Network net, int node) {
        if (net.getClass() == UndirectedNetwork.class) {
            return calcProbability((UndirectedNetwork) net, node);
        } else return calcProbability((DirectedNetwork) net, node);
    }

    public void calcNormalisation(Network network, int [] removed) {
        if (network.getClass() == UndirectedNetwork.class) {
            calcNormalisation((UndirectedNetwork) network, removed);
        } else calcNormalisation((DirectedNetwork) network, removed);
    }

    public abstract void calcNormalisation(UndirectedNetwork net, int [] removed);
    public abstract void calcNormalisation(DirectedNetwork net, int[] removed);

    public abstract double calcProbability(UndirectedNetwork net, int node);
    public abstract double calcProbability(DirectedNetwork net, int node);

    public void calcNormalisation(Network net) {
        calcNormalisation(net, new int[0]);
    }

    /** Parse JSON for parameters when necessary */
    public void parseJSON(JSONObject params){};
}
