package feta.objectmodels;

import feta.TimeInterval;
import feta.network.Network;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/** Class describing the fully specified object model with times each is active */
public class FullObjectModel {

    public ArrayList<ObjectModel> objectModels_;
    public ArrayList<TimeInterval> times_;
    public HashMap<TimeInterval, ObjectModel> timeToOM_;
    public long lastTime_;

    public FullObjectModel(JSONArray model){
        objectModels_=new ArrayList<ObjectModel>();
        times_= new ArrayList<TimeInterval>();
        timeToOM_= new HashMap<TimeInterval, ObjectModel>();
        parseObjectModels(model);
        lastTime_=times_.get(times_.size()-1).end_;
    }

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
        throw new IllegalArgumentException("No object model specified for this time "+time);
    }

    public void parseObjectModels(JSONArray model) {
        int number = model.size();

        objectModels_= new ArrayList<ObjectModel>(number);
        times_= new ArrayList<TimeInterval>(number);

        for (int i = 0; i< number; i++) {
            JSONObject om = (JSONObject) model.get(i);
            JSONArray components = (JSONArray) om.get("Components");
            ObjectModel obm = new ObjectModel();
            obm.readObjectModelOptions(components);

            long start = (Long) om.get("Start");
            long end = (Long) om.get("End");
            TimeInterval ti = new TimeInterval(start,end);
            times_.add(ti);
            objectModels_.add(obm);
            timeToOM_.put(ti, obm);
        }
        checkValid();
    }
}
