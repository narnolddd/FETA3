package feta.actions;

import java.util.ArrayList;

public abstract class ComplexAction extends SimpleAction {

    ArrayList<SimpleAction> actions_;

    public void execute(){
        for (SimpleAction a:actions_) {
            a.execute();
        }
    }

    public void addAction(SimpleAction action) {
        actions_.add(action);
    }

}
