package feta.objectmodels;

import feta.TimeInterval;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/** Class describing the fully specified object model with times each is active */
public class FullObjectModel {

    public ArrayList<MixedModel> objectModels_;
    public ArrayList<TimeInterval> times_;
    public HashMap<TimeInterval, MixedModel> timeToOM_;
    public long lastTime_;

    public FullObjectModel(JSONArray model){
        objectModels_=new ArrayList<MixedModel>();
        times_= new ArrayList<TimeInterval>();
        timeToOM_= new HashMap<TimeInterval, MixedModel>();
        parseObjectModels(model);
        lastTime_=times_.get(times_.size()-1).end_;
    }

    public FullObjectModel(ArrayList<MixedModel> models, long[] times) {
        try {
            times_ = new ArrayList<>();
            objectModels_ = models;
            timeToOM_ = new HashMap<>();
            for( int i = 0; i < models.size(); i++) {
                TimeInterval ti = new TimeInterval(times[i], times[i+1]);
                timeToOM_.put(ti, models.get(i));
                times_.add(ti);
            }
            lastTime_=times[models.size()];
        } catch (Exception e) {
            System.err.println("Something wrong with specified Object Model");
            e.printStackTrace();
        }
        checkValid();
    }

    public FullObjectModel(MixedModel model) {
        this(new ArrayList<>() { {add(model);} },new long[]{Long.MIN_VALUE, Long.MAX_VALUE});
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
            MixedModel om = objectModels_.get(j);
            //om.checkValid();
            timeToOM_.put(times_.get(j), om);
        }
    }

    /** Returns object model active at given time */
    public MixedModel objectModelAtTime(long time) {
        for (TimeInterval ti: times_) {
            if (ti.contains(time)) {
                return timeToOM_.get(ti);
            }
        }
        throw new IllegalArgumentException("No object model specified for this time "+time);
    }

    public void parseObjectModels(JSONArray model) {
        int number = model.size();

        objectModels_= new ArrayList<MixedModel>(number);
        times_= new ArrayList<TimeInterval>(number);

        for (Object o : model) {
            JSONObject om = (JSONObject) o;
            JSONArray components = (JSONArray) om.get("Components");
            MixedModel obm = new MixedModel();
            obm.readObjectModelOptions(components);

            long start = (Long) om.get("Start");
            long end = (Long) om.get("End");
            TimeInterval ti = new TimeInterval(start, end);
            times_.add(ti);
            objectModels_.add(obm);
            timeToOM_.put(ti, obm);
        }
        checkValid();
    }
}