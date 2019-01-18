package feta.actions;

import feta.FetaOptions;
import feta.actions.stoppingconditions.StoppingCondition;
import feta.objectmodels.FullObjectModel;
import feta.objectmodels.ObjectModel;

import java.util.ArrayList;
import java.util.List;

/** Finds best model mixture - hopefully will be better than calculating likelihood many times */

public class FitMixedModel {

    public FetaOptions options_;
    public FullObjectModel objectModel_;
    public ArrayList<StoppingCondition> stoppingConditions_;

    public FitMixedModel(FetaOptions options){
        stoppingConditions_= new ArrayList<StoppingCondition>();
        options_=options;
        objectModel_= new FullObjectModel(options_.fullObjectModel_);
    }

    public List<int[]> generatePartitions(int n, int k) {
        List<int[]> parts = new ArrayList<>();
        if (k == 2) {
            for (int i=0; i<=n; i++) {
                parts.add(new int[] {i, n-i});
            }
            return parts;
        }
        for (int l = 0; l < n; l++) {

        }
    }
}
