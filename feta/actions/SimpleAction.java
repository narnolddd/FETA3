package feta.actions;

import feta.actions.stoppingconditions.StoppingCondition;

import java.util.ArrayList;

public abstract class SimpleAction {

    public ArrayList<StoppingCondition> stoppingConditions_;

    public abstract void execute();

}
