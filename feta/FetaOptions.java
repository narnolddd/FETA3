package feta;

import java.io.*;
import java.util.*;

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
    public int fetaAction_ = ACTION_MEASURE;
    public int actionInterval_ = 1;
    public int actionStartTime_ = 0;
    public long actionStopTime_ = Long.MAX_VALUE;
    public boolean measureDegDist_ = false;
    public String degDistFile_ = null;
    public int maxLinks_ = Integer.MAX_VALUE;
    public int maxNodes_ = Integer.MAX_VALUE;
    public int epochSize_ = 20;

    // Operation Model options

    //


}