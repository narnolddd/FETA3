package feta;

import feta.actions.Grow;
import feta.actions.SimpleAction;
import feta.actions.ComplexAction;
import feta.actions.Measure;
import feta.network.Network;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Set;

/** Class controlling what FETA does */

public class FetaModel {

    private FetaOptions options_;
    private Network network_;
    private ComplexAction action_;

    public FetaModel() { options_= new FetaOptions();}

    public void readConfigs(String configFile) {
        options_.readConfig(configFile);
    }

    public void parseActionList(JSONObject actionList) {
        Set<String> actionNames_ = actionList.keySet();
        for (String singleAction: actionNames_) {

        }
    }

    private void parseSingleAction(JSONObject action) {

    }

    private SimpleAction newAction(String name) {
        if (name == "Measure") {
            return new Measure();
        } else if (name == "Grow") {
            return new Grow();
        } else {
            System.err.println("Invalid action type");
            return null;
        }
    }

}