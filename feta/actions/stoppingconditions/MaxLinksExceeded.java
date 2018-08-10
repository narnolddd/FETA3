package feta.actions.stoppingconditions;

import feta.network.Network;

public class MaxLinksExceeded implements StoppingCondition {

    private int maxLinks_;

    public MaxLinksExceeded(int noLinks) {
        maxLinks_= noLinks;
    }

    public boolean hasBeenReached(Network net) {
        if (net.noLinks_>= maxLinks_)
            return true;
        return false;
    }
}
