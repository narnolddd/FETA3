package feta.objectmodels.components;

import feta.network.DirectedNetwork;
import feta.network.Network;
import feta.network.UndirectedNetwork;
import org.json.simple.JSONObject;

import java.util.HashSet;

public abstract class ObjectModelComponent {

    /** Normalisation constant (changing) to avoid calculating every time probability of node is needed */
    double normalisationConstant_;
    double tempConstant_;

    protected boolean random_ = false;

    /** Methods relating to Object Model */

//    public double calcProbability(Network net, int node) {
//        if (net.getClass() == UndirectedNetwork.class) {
//            return calcProbability((UndirectedNetwork) net, node);
//        } else return calcProbability((DirectedNetwork) net, node);
//    }

    public void calcNormalisation(Network network, HashSet<Integer> availableNodes) {
        if (network.getClass() == UndirectedNetwork.class) {
            calcNormalisation((UndirectedNetwork) network, -1, availableNodes);
        } else calcNormalisation((DirectedNetwork) network, -1, availableNodes);
        tempConstant_=normalisationConstant_;
    }

    public abstract void calcNormalisation(UndirectedNetwork net, int sourceNode, HashSet<Integer> availableNodes);
    public abstract void calcNormalisation(DirectedNetwork net, int sourceNode, HashSet<Integer> availableNodes);

    public void updateNormalisation(UndirectedNetwork net, HashSet<Integer> availableNodes, int chosenNode) {
        calcNormalisation(net,chosenNode,availableNodes);
    }
    public void updateNormalisation(DirectedNetwork net, HashSet<Integer> availableNodes, int chosenNode) {
        calcNormalisation(net,chosenNode,availableNodes);
    }

    public abstract double calcProbability(UndirectedNetwork net, int node);
    public abstract double calcProbability(DirectedNetwork net, int node);

    public void reset() {
        normalisationConstant_=0.0;
        tempConstant_=0.0;
        random_=false;
    }

    public final void updateNormalisation(Network net, HashSet<Integer> availableNodes, int chosenNode) {
        random_=false;
        if (net.getClass() == UndirectedNetwork.class) {
            updateNormalisation((UndirectedNetwork) net, availableNodes, chosenNode);
        }
        else {
            updateNormalisation((DirectedNetwork) net, availableNodes, chosenNode);
        }
    }

    /** Parse JSON for parameters when necessary */
    public void parseJSON(JSONObject params){}
}