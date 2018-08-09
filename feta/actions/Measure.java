package feta.actions;

public class Measure extends SimpleAction {

    private long startTime_;
    private long interval_;
    private boolean measureDegDist_;

    public Measure() {
        interval_=10;
        measureDegDist_=false;
    }

    public void execute(){}

}
