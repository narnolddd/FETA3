package feta.actions.stoppingconditions;

import feta.network.Network;

/** Various conditions for when an action should stop */

public interface StoppingCondition {

    boolean hasBeenReached(Network net);
}
