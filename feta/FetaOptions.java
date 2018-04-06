package feta;

import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/** Options for FETA use */
public class FetaOptions {

    // Graph file formats understood (all in the form of edgelist)

    // NNT: Node1 Node2 Time
    public static final int NODE_NODE_TIME= 0;
    // NN: Node1 Node2
    public static final int NODE_NODE_= 1;
    // IIT: Int1 Int2 Time
    public static final int INT_INT_TIME= 2;
    // II Int1 Int2
    public static final int INT_INT= 3;

    // Actions which can be taken

    // Measure network statistics
    public static final int ACTION_MEASURE= 1;
    // Calculate likelihood of model
    public static final int ACTION_LIKELIHOOD= 2;
    // Grow a network
    public static final int ACTION_GROW= 3;
    // Translate one graph file format to another
    public static final int ACTION_TRANSLATE= 4;
    // Extract 'FetaNetwork' sequence of operations from graph file
    public static final int ACTION_FETANETWORK= 5;

    // File options
    public String graphFileInput_ = null;
    public String graphFileOutput_ = null;
    public String fetaFileInput_ = null;
    public String fetaFileOutput_ = null;
    public int fileFormatRead_ = NODE_NODE_TIME;
    public int fileFormatWrite_ = NODE_NODE_TIME;
    public boolean directedNetwork_ = false;
    public boolean complexNetwork_ = false;
    public boolean ignoreDuplicates_ = false;
    public boolean ignoreSelfLinks_ = true;

    // Options relating to the action to be taken
    public int actionType_;
    public int fetaAction_ = ACTION_MEASURE;
    public int actionInterval_ = 1;
    public int actionStartTime_ = 0;
    public long actionStopTime_ = Long.MAX_VALUE;
    public boolean measureDegDist_ = false;
    public String degDistFile_ = null;
    public String measureFile_ = null;
    public int maxLinks_ = Integer.MAX_VALUE;
    public int maxNodes_ = Integer.MAX_VALUE;
    public int epochSize_ = 20;

    // Operation Model options

    public void readConfig( String cfile) {
        try {
            FileReader reader = new FileReader(cfile);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

            // Read the subtags by turn

            // System.out.println("retrieving options");

            JSONObject dataOpt = (JSONObject) jsonObject.get("dataoptions");

            JSONObject action = (JSONObject) jsonObject.get("action");

            JSONObject likelihood = (JSONObject) jsonObject.get("likelihood");

            JSONArray operationModel = (JSONArray) jsonObject.get("operationmodel");

            JSONArray objectModels = (JSONArray) jsonObject.get("objectmodels");

            // Continue & parse

            parseDataOptions(dataOpt);
            parseAction(action);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

    }

    public void parseDataOptions(JSONObject obj) {
        try {
            graphFileInput_ = (String) obj.get("graphinput");
            //System.out.println(graphFileInput_);
            graphFileOutput_ = (String) obj.get("graphoutput");
            System.out.println(graphFileOutput_);
            String inFormat = (String) obj.get("fileformatread");
            fileFormatRead_ = getGraphFormat(inFormat);
            String outFormat = (String) obj.get("fileformatwrite");
            fileFormatWrite_ = getGraphFormat(outFormat);
        } catch (Exception e) {
            System.err.println("Some error with datafile tag");
        }
    }

    public int getGraphFormat(String format) {
        switch (format) {
            case "NNT":
                return NODE_NODE_TIME;
            case "NN":
                return NODE_NODE_;
            case "IIT":
                return INT_INT_TIME;
            case "II":
                return INT_INT;
                default:
                    System.out.println("Warning: null or unrecognised file format "+format);
                    System.out.println("Using NNT");
                    return NODE_NODE_TIME;
        }
    }

    public void parseAction(JSONObject obj) {
        String type = (String) obj.get("type");
        if (getActionType(type) < 0) {
            System.err.println("Unrecognised action type: "+type);
            System.exit(-1);
        }
        actionType_ = getActionType(type);
        Long actstart = (Long) obj.get("start");
        actionStartTime_ = Integer.valueOf(actstart.intValue());
        Long actionInterval = (Long) obj.get("interval");
        actionInterval_ = Integer.valueOf(actionInterval.intValue());
        actionStopTime_ = (Long) obj.get("stop");

        if (actionType_ == ACTION_MEASURE) {
            degDistFile_= (String) obj.get("degreedistfile");
            measureFile_= (String) obj.get("measurefile");
        }
    }

    public int getActionType(String action) {
        switch (action) {
            case "measure":
                return ACTION_MEASURE;
            case "likelihood":
                return ACTION_LIKELIHOOD;
            case "grow":
                return ACTION_GROW;
            case "translate":
                return ACTION_TRANSLATE;
            case "fetanetwork":
                return ACTION_FETANETWORK;
                default:
                    return -1;
        }
    }
}