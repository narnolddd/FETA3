package feta.network;

import java.util.ArrayList;

/** Interface for selection of available nodes */
public interface NodeSelector {

    /** Pick nodes from the network */
    public ArrayList<Integer> getAvailableNodes(Network net, ArrayList<Integer> selectedNodes);


}

class UntypedNodeSelector implements NodeSelector {

    @Override
    public ArrayList<Integer> getAvailableNodes(Network net, ArrayList<Integer> selectedNodes) {
        ArrayList<Integer> fullList = net.getNodeListCopy();
        fullList.removeAll(selectedNodes);
        return fullList;
    }
}

class TypedNodeSelector extends UntypedNodeSelector {
    public ArrayList<Integer> getAvailableNodes(Network net, ArrayList<Integer> selectedNodes, String nodeType) {
        ArrayList<Integer> fullList = NodeTypes.getNodesOfType(nodeType);
        fullList.removeAll(selectedNodes);
        return fullList;
    }
}