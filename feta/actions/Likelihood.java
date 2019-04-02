package feta.actions;

import feta.FetaOptions;
import feta.actions.stoppingconditions.StoppingCondition;
import feta.network.DirectedNetwork;
import feta.network.Link;
import feta.network.UndirectedNetwork;
import feta.objectmodels.FullObjectModel;
import feta.operations.Operation;
import feta.parsenet.ParseNet;
import feta.parsenet.ParseNetDirected;
import feta.parsenet.ParseNetUndirected;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class Likelihood extends SimpleAction {

    public long startTime_=10;
    public long endTime_;
    public FetaOptions options_;
    public FullObjectModel objectModel_;
    public ParseNet parser_;


    public Likelihood(FetaOptions options){
        stoppingConditions_= new ArrayList<StoppingCondition>();
        options_=options;
        objectModel_= new FullObjectModel(options_.fullObjectModel_);
    }

    public void parseActionOptions(JSONObject obj) {
        Long start = (Long) obj.get("Start");
        if (start != null)
            startTime_=start;
    }

    public void execute() {
        if (!options_.directedInput_) {
            parser_ = new ParseNetUndirected((UndirectedNetwork) network_);
        } else parser_= new ParseNetDirected((DirectedNetwork) network_);
        getLogLike(startTime_, Long.MAX_VALUE);
    }

    public void getLogLike(long start, long end) {
        network_.buildUpTo(start);
        double like = 0.0;
        int noChoices = 0;
        while (network_.linksToBuild_.size()>0 && !stoppingConditionsExceeded_(network_)) {
            if (network_.latestTime_ > end)
                break;
            ArrayList<Link> links = network_.linksToBuild_;
            ArrayList<Link> lset = parser_.getNextLinkSet(links);
            ArrayList<Operation> newOps = parser_.parseNewLinks(lset, network_);
            for (Operation op: newOps) {
                //objectModel_.objectModelAtTime(op.time_).normaliseAll(network_);
                like += op.calcLogLike(network_, objectModel_.objectModelAtTime(op.time_));
                noChoices+=op.noChoices_;
                op.build(network_);
            }
            ArrayList<Link> newLinks = new ArrayList<Link>();
            for (int i = lset.size(); i < links.size(); i++){
                newLinks.add(links.get(i));
            }
            network_.linksToBuild_=newLinks;
        }

        double c0 = Math.exp(like/noChoices);
        System.out.println(c0);
        System.out.println(noChoices);
    }
}
