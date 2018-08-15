package feta.actions.stoppingconditions;

import feta.network.Network;

public class MaxTimeExceeded implements StoppingCondition {

    private long maxTime_;

    public MaxTimeExceeded(long maxTime){
        maxTime_=maxTime;
    }

    // Hmm this should work.
    public boolean hasBeenReached(Network net) {
        if (net.latestTime_ >= maxTime_)
            return true;
        return false;
    }

}
