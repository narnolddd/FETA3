package feta;

import feta.actions.*;
import feta.network.DirectedNetwork;
import feta.network.Network;
import feta.network.UndirectedNetwork;
import feta.readnet.*;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/** Class controlling what FETA does */

public class FetaModel {

    private final FetaOptions options_;
    private Network network_;
    private final ArrayList<SimpleAction> actionsToDo_;

    public FetaModel() {
        options_= new FetaOptions();
        actionsToDo_= new ArrayList<SimpleAction>();
    }

    public void readConfigs(String configFile) {
        options_.readConfig(configFile);
    }

    public void execute() {
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

        for (Object o: actionList.keySet()) {
            String singleAction= o.toString();
            SimpleAction action = newAction(singleAction);
            action.parseActionOptions((JSONObject) actionList.get(singleAction));
            action.parseStoppingConditions((JSONObject) actionList.get(singleAction));
            actionsToDo_.add(action);
        }
    }

    public void initialiseNetwork() {
        if (options_.isDirectedInput()) {
            network_= new DirectedNetwork();
        } else network_= new UndirectedNetwork();
        network_.numRecents_=options_.getNoRecents();
    }

    /** Reads from a string the relevant action type */
    private SimpleAction newAction(String name) {
        if (name.equals("Measure")) {
            return new Measure();
        } else if (name.equals("Grow")) {
            return new Grow(options_);
        } else if (name.equals("Translate")) {
            return new Translate(options_);
        } else if (name.equals("ParseTest") & !options_.isDirectedInput()) {
            return new ParseTest(options_.isDirectedInput());
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
        String inputType = options_.getInputType();
        reader = switch (inputType) {
            case "NNT" -> new ReadNetNNT(options_);
            case "NN" -> new ReadNetNN(options_);
            case "CSV" -> new ReadNetCSV(options_);
            default -> null;
        };
        return reader;
    }

}
