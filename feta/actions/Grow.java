package feta.actions;

import feta.FetaOptions;
import feta.network.Link;
import feta.objectmodels.FullObjectModel;
import feta.operations.OperationModel;
import feta.writenet.WriteNet;
import feta.writenet.WriteNetNN;
import feta.writenet.WriteNetNNT;
import jdk.dynalink.Operation;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Grow extends SimpleAction {

    long startTime=10;
    long interval=1;

    public OperationModel operationModel_;
    public FullObjectModel objectModel_;
    public FetaOptions options_;
    public JSONObject operationModelJSON_;

    public Grow(FetaOptions options) {
        JSONArray fullObjectModel = options_.fullObjectModel_;
        operationModelJSON_ = options_.operationModel_;

        options_=options;
        objectModel_= new FullObjectModel(fullObjectModel);
        parseOperationModel();
    }

    public void execute() {
        // Build network up to starting time from seed
        network_.buildUpTo(startTime);

        // Clear any other links not built in seed network
        network_.linksToBuild_= new ArrayList<Link>();
        network_.latestTime_+=interval;
        while (!stoppingConditionsExceeded_(network_)) {
            feta.operations.Operation op = operationModel_.nextOperation();
            op.time_=network_.latestTime_;
            op.fill(network_,objectModel_.objectModelAtTime(network_.latestTime_));
            network_.latestTime_+=interval;
        }

        WriteNet writer;
        if (options_.outputType_== "NNT") {
            writer = new WriteNetNNT(network_.linksBuilt_, options_);
        } else if (options_.outputType_== "NN") {
            writer = new WriteNetNN(network_.linksBuilt_, options_);
        } else throw new IllegalArgumentException("Unrecognised output type "+options_.outputType_);
        writer.write(1,Long.MAX_VALUE);
    }

    public void parseActionOptions(JSONObject obj){

    }

    public void parseOperationModel() {
        /** Gets object model element class from string. Bit of a mouthful */
        OperationModel om = null;
        String omcClass = (String) operationModelJSON_.get("Name");
        Class <?extends OperationModel> component = null;

        try {
            component= Class.forName(omcClass).asSubclass(OperationModel.class);
            Constructor<?> c = component.getConstructor();
            om = (OperationModel) c.newInstance();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        operationModel_=om;
    }

}
