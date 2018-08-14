package feta;

import feta.actions.SimpleAction
import feta.actions.ComplexAction;
import feta.actions.Measure;
import feta.network.Network;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.util.Iterator;

/** Class controlling what FETA does */

public class FetaModel {

    private FetaOptions options_;
    private Network network_;
    private ComplexAction action_;

    public FetaModel() { options_= new FetaOptions();}

    public void readConfigs(String configFile) {
        options_.readConfig(configFile);
    }

    public void parseActions(JSONObject JSONaction) {

    }

    private void parseSingleAction(JSONObject action) {

        }
    }

}