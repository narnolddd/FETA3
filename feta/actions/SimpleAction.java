package feta.actions;

import feta.actions.stoppingconditions.MaxLinksExceeded;
import feta.actions.stoppingconditions.MaxNodeExceeded;
import feta.actions.stoppingconditions.MaxTimeExceeded;
import feta.actions.stoppingconditions.StoppingCondition;
import feta.network.Network;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Math.toIntExact;

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

    public void setNetwork(Network net) {
        network_=net;
    }

    /** Parses the json for any stopping conditions */
    public void parseStoppingConditions(JSONObject obj) {

        Long stopTime = (Long) obj.get("StoppingTime");
        if (stopTime != null) {
            StoppingCondition sc = new MaxTimeExceeded(stopTime);
            stoppingConditions_.add(sc);
        }

        // These don't work at the moment bc of java type issues

        Long mn = (Long) obj.get("MaxNodes");
        if (mn != null) {
            int maxNodes = toIntExact(mn);
            StoppingCondition sc = new MaxNodeExceeded(maxNodes);
            stoppingConditions_.add(sc);
        }

        Long ml = (Long) obj.get("MaxLinks");
        if (ml != null) {
            int maxLinks = toIntExact(ml);
            StoppingCondition sc = new MaxLinksExceeded(maxLinks);
            stoppingConditions_.add(sc);
        }
    }

}
