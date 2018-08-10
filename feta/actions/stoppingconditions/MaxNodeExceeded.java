package feta.actions.stoppingconditions;

import feta.network.Network;

/** Action must stop when network size reaches a given number of nodes */

public class MaxNodeExceeded implements StoppingCondition {

    private int maxNodes_;

    public MaxNodeExceeded(int noNodes) {
        maxNodes_ = noNodes;
    }

    public boolean hasBeenReached(Network net) {
        if (net.noNodes_ >= maxNodes_)
            return true;
        return false;
    }
}
