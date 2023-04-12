package feta.network.measurements;

import feta.Methods;
import feta.network.DirectedNetwork;
import feta.network.NodeTypes;
import feta.network.UndirectedNetwork;
import org.json.simple.JSONObject;

import java.util.HashMap;

public class NoNodesByType extends Measurement {
    HashMap<String,Integer> sizes;

    public NoNodesByType() {
        sizes = new HashMap<>();
        nameDirected_="NoNodesByType";
        nameUndirected_="NoNodesByType";
    }

    @Override
    public void update(DirectedNetwork net) {
        NodeTypes nt = net.getNodeTypes();
        for (String type : nt.getTypes()) {
            sizes.put(type, nt.getNodesOfType(type).size());
        }
    }

    @Override
    public void update(UndirectedNetwork net) {
        NodeTypes nt = net.getNodeTypes();
        for (String type : nt.getTypes()) {
            sizes.put(type, nt.getNodesOfType(type).size());
        }
    }

    @Override
    public String toStringDirected() {
        return Methods.mapToString(sizes);
    }

    @Override
    public String toStringUndirected() {
        return Methods.mapToString(sizes);
    }
}
