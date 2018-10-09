package feta;

import feta.actions.Grow;
import feta.actions.SimpleAction;
import feta.actions.ComplexAction;
import feta.actions.Measure;
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

    public void goForIt() {
        System.out.println("Doing the thing");
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
            actionsToDo_.add(action);
        }
    }

    public void initialiseNetwork() {
        if (options_.directedInput_) {
            network_= new DirectedNetwork();
        } else network_= new UndirectedNetwork();
    }

    public void readConfigs(String configFile) {
        options_.readConfig(configFile);
    }

    /** Reads from a string the relevant action type */
    private SimpleAction newAction(String name) {
        if (name.equals("Measure")) {
            System.out.println("Measuring");
            return new Measure();
        } else if (name.equals("Grow")) {
            return new Grow();
        } else {
            System.err.println("Invalid action type: "+name);
            return null;
        }
    }

    /** Sets which network file reader to do the job */
    private ReadNet newReader() {
        ReadNet reader;
        if (options_.inputType_ == "NNT") {
            reader = new ReadNetNNT();
        } else {
            reader = new ReadNetNN();
        }
        reader.setSep(options_.sep_);
        if (options_.directedInput_) {
            reader.setLinkBuilder(new DirectedLinkBuilder());
        } else reader.setLinkBuilder(new UndirectedLinkBuilder());
        reader.setFileInput(options_.netInputFile_);
        return reader;
    }

}