package feta.actions;

import feta.actions.stoppingconditions.StoppingCondition;
import feta.network.Network;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public abstract class SimpleAction {

    public Network network_;

    public ArrayList<StoppingCondition> stoppingConditions_;

    public boolean stoppingConditionsExceeded_(Network net) {
        for (StoppingCondition sc: stoppingConditions_) {
            if (sc.hasBeenReached(net)) {return true;}
        }
        return false;
    }

    public abstract void parseActionOptions(JSONObject obj);

    public abstract void execute();

}
