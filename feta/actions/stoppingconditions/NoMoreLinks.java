package feta.actions.stoppingconditions;

import feta.network.Network;

public class NoMoreLinks implements StoppingCondition {

    @Override
    public boolean hasBeenReached(Network net) {
        return net.linksToBuild_.isEmpty();
    }
}
