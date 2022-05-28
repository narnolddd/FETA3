package feta.actions;

import feta.FetaOptions;
import feta.actions.stoppingconditions.StoppingCondition;
import feta.network.DirectedNetwork;
import feta.network.Link;
import feta.network.UndirectedNetwork;
import feta.objectmodels.FullObjectModel;
import feta.objectmodels.MixedModel;
import feta.operations.Operation;
import feta.parsenet.ParseNet;
import feta.parsenet.ParseNetDirected;
import feta.parsenet.ParseNetUndirected;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Likelihood extends SimpleAction {

    public long startTime_=10;
    public long endTime_;
    public FetaOptions options_;
    public FullObjectModel objectModel_;
    public ParseNet parser_;
    private boolean orderedData_; // default false


    public Likelihood(FetaOptions options){
        stoppingConditions_= new ArrayList<StoppingCondition>();
        options_=options;
        objectModel_= new FullObjectModel(options_.fullObjectModel_);
    }

    public void parseActionOptions(JSONObject obj) {
        Long start = (Long) obj.get("Start");
        if (start != null)
            startTime_=start;
        Boolean orderedData = (Boolean) obj.get("OrderedData");
        if (orderedData != null)
            orderedData_=orderedData;
    }

    public void execute(){
        if (!options_.isDirectedInput()) {
            parser_ = new ParseNetUndirected((UndirectedNetwork) network_);
        } else parser_= new ParseNetDirected((DirectedNetwork) network_);
        for (int j = 0; j < objectModel_.objectModels_.size(); j++) {
            long start = objectModel_.times_.get(j).start_;
            long end = objectModel_.times_.get(j).end_;
            getLikelihoods(start,end);
        }
    }

    public void getLikelihoods(long start, long end) {
        MixedModel obm = objectModel_.objectModelAtTime(start);

        obm.initialiseLikelihoods();

        network_.buildUpTo(start);
        int noChoices = 0;
        HashMap<double[], Double> c0Values = new HashMap<>();
        while (network_.linksToBuild_.size()>0 && !stoppingConditionsExceeded_(network_)) {
            if (network_.latestTime_ > end)
                break;
            ArrayList<Link> links = network_.linksToBuild_;
            ArrayList<Link> lset = parser_.getNextLinkSet(links);
            ArrayList<Operation> newOps = parser_.parseNewLinks(lset, network_);
            for (Operation op: newOps) {
                long time = op.getTime();
                obm.calcNormalisation(network_);
                updateLikelihoods(op, obm);
                noChoices+=op.getNoChoices();
                network_.buildUpTo(time);
            }
        }

        // Turn everything into a C0 value
        double like=0.0;
        double raw=0.0;
        HashMap<double[], Double> likelihoods = obm.getLikelihoods();
        raw = likelihoods.get(obm.getWeights());
        like = Math.exp(raw/noChoices);

        System.out.println("c0 "+like+" raw "+raw+" choices "+noChoices);
    }

    public void updateLikelihoods(Operation op, MixedModel obm) {
        op.setNodeChoices(orderedData_);
        ArrayList<int[]> nc = op.getNodeOrders();
        obm.updateLikelihoods(network_,nc);
    }

//    public void getLogLike(long start, long end) {
//        //System.out.println("StartTime   EndTime    C0");
//        network_.buildUpTo(start);
//        double like = 0.0;
//        double c0;
//        int noChoices = 0;
//        while (network_.linksToBuild_.size()>0 && !stoppingConditionsExceeded_(network_)) {
//            if (network_.latestTime_ > end)
//                break;
//            ArrayList<Link> links = network_.linksToBuild_;
//            ArrayList<Link> lset = parser_.getNextLinkSet(links);
//            ArrayList<Operation> newOps = parser_.parseNewLinks(lset, network_);
//            for (Operation op: newOps) {
//                long time = op.getTime();
//                op.setNodeChoices(orderedData_);
//                MixedModel obm = objectModel_.objectModelAtTime(time);
//                obm.calcNormalisation(network_);
//                like += op.calcLogLike(obm,network_, orderedData_);
//                noChoices += op.getNoChoices();
//                network_.buildUpTo(time);
//            }
//        }
//
//        c0 = Math.exp(like/noChoices);
//        System.out.println("c0 "+c0 + " raw "+ like);
//        //System.out.println(noChoices);
//    }
}
