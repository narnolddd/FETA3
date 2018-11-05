package feta.actions;

import feta.FetaOptions;
import feta.actions.stoppingconditions.StoppingCondition;
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
    public JSONObject operationModelJSON_;
    public FetaOptions options_;

    public Grow(FetaOptions options) {
        stoppingConditions_= new ArrayList<StoppingCondition>();
        options_=options;
        operationModelJSON_ = options.operationModel_;
        objectModel_= new FullObjectModel(options_.fullObjectModel_);
        parseOperationModel();
    }

    public void execute() {
        // Build network up to starting time from seed
        network_.buildUpTo(startTime);

        // Clear any other links not built in seed network
        network_.linksToBuild_= new ArrayList<Link>();
        Long time = startTime;
        boolean checkModel = true;
        while (!stoppingConditionsExceeded_(network_)) {
            time+=interval;
            if (time > 50) {
                checkModel=false;
            }
            feta.operations.Operation op = operationModel_.nextOperation();
            op.time_=time;
            if (checkModel) {
                objectModel_.objectModelAtTime(time).checkNorm(network_);
            }
            op.fill(network_,objectModel_.objectModelAtTime(time));
            network_.buildUpTo(Long.MAX_VALUE);
        }

        WriteNet writer;
        if (options_.outputType_.equals("NNT")) {
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
            om.parseJSON(operationModelJSON_);
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
