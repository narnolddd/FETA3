package feta.objectmodels;

import feta.TimeInterval;
import feta.network.Network;

import java.util.ArrayList;
import java.util.HashMap;

/** Class describing the fully specified object model with times each is active */
public class FullObjectModel {

    public ArrayList<ObjectModel> objectModels_;
    public ArrayList<TimeInterval> times_;
    public HashMap<TimeInterval, ObjectModel> timeToOM_;

    public FullObjectModel(){}

    /** Maps a node and network object to a probability of choosing node */
    public double calcProbability(Network net, int node, long time){
        ObjectModel om = objectModelAtTime(time);

        return 0.0;
    }

    /** Checks object models given are valid */
    public void checkValid() {
        if (times_.size() == 0 || objectModels_.size() == 0) {
            throw new IllegalArgumentException("Temporal object model unspecified");
        }
        if (times_.size() != objectModels_.size()) {
            throw new IllegalArgumentException("Number of time intervals must equal number of object models");
        }
        for (int i = 0; i < times_.size() - 1; i++) {
            if (times_.get(i).overlapsWith(times_.get(i + 1))) {
                throw new IllegalArgumentException("Time intervals should be non overlapping, have " + times_.get(i) + " and " + times_.get(i + 1));
            }
        }
        for (int j = 0; j<objectModels_.size(); j++) {
            ObjectModel om = objectModels_.get(j);
            om.checkValid();
            timeToOM_.put(times_.get(j), om);
        }
    }

    /** Returns object model active at given time */
    public ObjectModel objectModelAtTime(long time) {
        for (TimeInterval ti: times_) {
            if (ti.contains(time)) {
                return timeToOM_.get(ti);
            }
        }
        throw new IllegalArgumentException("No object model specified for this time");
    }


}