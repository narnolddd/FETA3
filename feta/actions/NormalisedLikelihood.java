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

public class NormalisedLikelihood extends SimpleAction {

    public FetaOptions options_;
    public FullObjectModel objectModel_;
    public long startTime_;
    public ParseNet parser_;

    public NormalisedLikelihood(FetaOptions options) {
        stoppingConditions_ = new ArrayList<StoppingCondition>();
        options_ = options;
        objectModel_ = new FullObjectModel(options_.fullObjectModel_);
    }

    public void execute() {
        if (!options_.isDirectedInput()) {
            parser_ = new ParseNetUndirected((UndirectedNetwork) network_);
        } else parser_= new ParseNetDirected((DirectedNetwork) network_);
        getLike(startTime_);
    }

    public void parseActionOptions(JSONObject obj) {
        Long start = (Long) obj.get("Start");
        if (start != null)
            startTime_=start;
    }

    public void getLike(long start) {
        network_.buildUpTo(start);
        long time;
        while (network_.linksToBuild_.size()>0 && withinStoppingConditions(network_)) {
            ArrayList<Link> links = network_.linksToBuild_;
            ArrayList<Link> lset = parser_.getNextLinkSet(links);
            ArrayList<Operation> newOps = parser_.parseNewLinks(lset, network_);
            time = newOps.get(0).getTime();
            objectModel_.objectModelAtTime(time).calcNormalisation(network_, network_.getNodeListCopy());
            //double[] meanSD_ = objectModel_.objectModelAtTime(time).calcMeanSDLike(network_);
            //System.out.println(meanSD_[0]);
            for (Operation ignored : newOps) {
                //op.printMeanLike(meanSD_[0], objectModel_.objectModelAtTime(time),network_);
                //op.build(network_);
            }
            ArrayList<Link> newLinks = new ArrayList<Link>();
            for (int i = lset.size(); i < links.size(); i++){
                newLinks.add(links.get(i));
            }
            network_.linksToBuild_=newLinks;
        }
    }
}
