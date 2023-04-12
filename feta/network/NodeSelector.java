package feta.network;

import java.util.ArrayList;
import java.util.HashSet;

/** Interface for selection of available nodes */
public interface NodeSelector {

    /** Pick nodes from the network */
    public HashSet<Integer> getAvailableNodes(Network net, ArrayList<Integer> selectedNodes);


}

class UntypedNodeSelector implements NodeSelector {

    @Override
    public HashSet<Integer> getAvailableNodes(Network net, ArrayList<Integer> selectedNodes) {
        HashSet<Integer> fullList = net.getNodeListCopy();
        for (int node: selectedNodes) {
            fullList.remove(node);
        }
        return fullList;
    }
}

class TypedNodeSelector extends UntypedNodeSelector {
    public HashSet<Integer> getAvailableNodes(Network net, ArrayList<Integer> selectedNodes, String nodeType) {
        NodeTypes nt = net.getNodeTypes();
        HashSet<Integer> fullList = nt.getNodesOfType(nodeType);
        fullList.removeAll(selectedNodes);
        return fullList;
    }
}