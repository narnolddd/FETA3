package feta.actions.stoppingconditions;

import feta.network.Network;

public class MaxTimeExceeded implements StoppingCondition {

    public MaxTimeExceeded(){}

    // uhh... I haven't thought this one through have I
    public boolean hasBeenReached(Network net) {
        return false;
    }
}
