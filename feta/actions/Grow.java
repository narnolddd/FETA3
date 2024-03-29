package feta.actions;

import feta.FetaOptions;
import feta.actions.stoppingconditions.MaxTimeExceeded;
import feta.actions.stoppingconditions.StoppingCondition;
import feta.network.Link;
import feta.network.Network;
import feta.objectmodels.FullObjectModel;
import feta.objectmodels.MixedModel;
import feta.operations.Operation;
import feta.operations.OperationModel;
import feta.writenet.WriteNet;
import feta.writenet.WriteNetCSV;
import feta.writenet.WriteNetNN;
import feta.writenet.WriteNetNNT;
import org.json.simple.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Grow extends SimpleAction {

    long startTime_=10;
    long interval_=1;

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

    public Grow(Network net, FullObjectModel obm, OperationModel om, Long startTime, Long endTime) {
        setNetwork(net);
        stoppingConditions_= new ArrayList<StoppingCondition>() { {
            add(new MaxTimeExceeded(endTime));
        }};
        operationModel_=om;
        objectModel_=obm;
        startTime_=startTime;
    }

    public void execute() {
        // Build network up to starting time from seed
        network_.buildUpTo(startTime_);

        // Clear any other links not built in seed network
        network_.linksToBuild_= new ArrayList<Link>();
        long time = startTime_;
        boolean checkModel = true;
        while (withinStoppingConditions(network_) && time < objectModel_.lastTime_) {
            if (time > 50) {
                checkModel=false;
            }
            Operation op = operationModel_.nextOperation();
            if (op == null) {
                break;
            }
            if (op.getTime() == 0) {
                op.setTime(time);
            }

            MixedModel obm = objectModel_.objectModelAtTime(op.getTime());
            if (checkModel) {
                obm.checkNorm(network_);
            }
            try {
                op.chooseNodes(network_,obm);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                operationModel_.failedOperation(op);
                continue;
            }
            op.bufferLinks(network_);
            time = op.getTime();
            time+=interval_;
            network_.buildUpTo(Long.MAX_VALUE);
        }
    }

    public void parseActionOptions(JSONObject obj){
        Long start = (Long) obj.get("Start");
        if (start != null)
            startTime_=start;

        Long interval = (Long) obj.get("Interval");
        if (interval != null) {
            if (interval >= 0) {
                interval_= interval;
            } else {
                System.err.println("Invalid interval");
            }
        }
    }
    /** Gets object model element class from string. Bit of a mouthful */
    public void parseOperationModel() {
        OperationModel om=null;
        String omcClass = "feta.operations." +operationModelJSON_.get("Name");
        Class <?extends OperationModel> component;

        try {
            component= Class.forName(omcClass).asSubclass(OperationModel.class);
            Constructor<?> c = component.getConstructor();
            om = (OperationModel) c.newInstance();
            om.parseJSON(operationModelJSON_);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        operationModel_=om;

        // check valid output file
        if (options_.netOutputFile_==null) {
            System.err.println("No output file supplied for Grow.");
            System.exit(-1);
        }
    }

    public String toString() {return "Grow";}
}
