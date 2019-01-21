package feta;

import feta.actions.*;
import feta.network.DirectedNetwork;
import feta.network.Network;
import feta.network.UndirectedNetwork;
import feta.readnet.*;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Set;

/** Class controlling what FETA does */

public class FetaModel {

    private FetaOptions options_;
    private Network network_;
    private ArrayList<SimpleAction> actionsToDo_;

    public FetaModel() {
        options_= new FetaOptions();
        actionsToDo_= new ArrayList<SimpleAction>();
    }

    public void readConfigs(String configFile) {
        options_.readConfig(configFile);
    }

    public void goForIt() {
        parseActionList(options_.actionOps_);
        initialiseNetwork();
        network_.setNetworkReader(newReader());
        network_.getLinksFromFile();
        for (SimpleAction act: actionsToDo_) {
            act.setNetwork(network_);
            act.execute();
        }
    }
    public void parseActionList(JSONObject actionList) {
        Set<String> actionNames_ = actionList.keySet();
        for (String singleAction: actionNames_) {
            SimpleAction action = newAction(singleAction);
            action.parseActionOptions((JSONObject) actionList.get(singleAction));
            action.parseStoppingConditions((JSONObject) actionList.get(singleAction));
            actionsToDo_.add(action);
        }
    }

    public void initialiseNetwork() {
        if (options_.directedInput_) {
            network_= new DirectedNetwork();
        } else network_= new UndirectedNetwork();
    }

    /** Reads from a string the relevant action type */
    private SimpleAction newAction(String name) {
        if (name.equals("Measure")) {
            return new Measure();
        } else if (name.equals("Grow")) {
            return new Grow(options_);
        } else if (name.equals("Translate")) {
            return new Translate(options_);
        } else if (name.equals("ParseTest") & !options_.directedInput_) {
            return new ParseTest(options_.directedInput_);
        } else if (name.equals("Likelihood")) {
            return new Likelihood(options_);
        } else if (name.equals("NormalisedLikelihood")) {
            return new NormalisedLikelihood(options_);
        } else if (name.equals("FitMixedModel")) {
            return new FitMixedModel(options_);
        }
        else {
            throw new IllegalArgumentException("Unrecognised or missing action name "+name);
        }
    }

    /** Sets which network file reader to do the job */
    private ReadNet newReader() {
        ReadNet reader;
        if (options_.inputType_.equals("NNT")) {
            reader = new ReadNetNNT(options_);
        } else if (options_.inputType_.equals("NN")){
            reader = new ReadNetNN(options_);
        }
        else reader = null;
        return reader;
    }

}