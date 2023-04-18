package feta;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/** Class which parses JSON file for user specified options */

public class FetaOptions {

    /** Default options related to the data input and output files */
    public String netInputFile_="seed_graphs/clique-5.dat";
    public String inputType_ = "NNT";
    private String inSep_ = "\\s+";
    public boolean directedInput_;
    public boolean netInputSampled_;
    public double sampleProp_= 1.0;
    private int sourceColumn=0;
    private int dstColumn=1;
    private int timeColumn=2;
    private int noRecents_=10;

    /** Options related to typed networks */
    private boolean typedNetwork_=false;
    private int sourceTypeColumn_=-1;
    private int dstTypeColumn_=-1;

    /** Net output options */
    public String netOutputFile_;
    private String outSep_= " ";
    public String outputType_="NNT";
    private boolean includeOutTimestamps = true;

    /** Type of action. Everything else related to the action will be parsed in the relevant action class */
    JSONObject actionOps_;

    /** Options relating to model, which will be parsed in the respective classes */
    public JSONArray fullObjectModel_;
    public JSONObject operationModel_;

    public void readConfig(String file) {

        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(file));
            JSONObject jsonObject = (JSONObject) obj;

            // Get datafile
            JSONObject dataFileOps = (JSONObject) jsonObject.get("Data");

            parseDataTag(dataFileOps);

            actionOps_ = (JSONObject) jsonObject.get("Action");

            fullObjectModel_= (JSONArray) jsonObject.get("ObjectModel");

            operationModel_= (JSONObject) jsonObject.get("OperationModel");

        } catch (FileNotFoundException e) {
            System.err.println("JSON Options file "+file+" not found.");
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

    }

    public void parseDataTag(JSONObject df) throws ParseException {

        String ifile = (String) df.get("GraphInputFile");
        if (ifile != null) {
            netInputFile_=ifile;
            df.remove("GraphInputFile");
        }

        String ofile = (String) df.get("GraphOutputFile");
        if (ofile != null) {
            netOutputFile_=ofile;
            df.remove("GraphOutputFile");
        }

        String in = (String) df.get("GraphInputType");
        if (in != null) {
            inputType_ = in;
            df.remove("GraphInputType");
        }

        Boolean insample = (Boolean) df.get("SampleIncomingGraph");
        if (insample != null) {
            netInputSampled_= insample;
            df.remove("SampleIncomingGraph");
        }

        Double sampleProp = (Double) df.get("SampleProportion");
        if (sampleProp != null) {
            sampleProp_=sampleProp;
            df.remove("SampleProportion");
        }

        Long srccol = (Long) df.get("Source");
        if (srccol != null) {
            sourceColumn = Math.toIntExact(srccol);
            df.remove("Source");
        }

        Long dstcol = (Long) df.get("Target");
        if (dstcol != null) {
            dstColumn = Math.toIntExact(dstcol);
            df.remove("Target");
        }

        Long timecol = (Long) df.get("Time");
        if (timecol != null) {
            timeColumn = Math.toIntExact(timecol);
            df.remove("Time");
        }

        Boolean typed = (Boolean) df.get("Typed");
        if (typed != null) {
            typedNetwork_ = typed;
            df.remove("Typed");
        }

        Long srctype = (Long) df.get("SourceType");
        if (srctype != null) {
            sourceTypeColumn_ = Math.toIntExact(srctype);
            df.remove("SourceType");
        }

        Long dsttype = (Long) df.get("TargetType");
        if (dsttype != null) {
            dstTypeColumn_ = Math.toIntExact(dsttype);
            df.remove("TargetType");
        }

        String out = (String) df.get("GraphOutputType");
        if (out != null) {
            outputType_= out;
            df.remove("GraphOutputType");
        }

        Boolean includets = (Boolean) df.get("IncludeOutputTimestamps");
        if (includets != null) {
            includeOutTimestamps = includets;
            df.remove("IncludeOutputTimestamps");
        }

        String insep = (String) df.get("InputLineSeparator");
        if (insep != null) {
            inSep_= insep;
            df.remove("InputLineSeparator");
        }

        String outsep = (String) df.get("OutputLineSeparator");
        if (outsep != null) {
            outSep_=outsep;
            df.remove("OutputLineSeparator");
        }

        Boolean dir = (Boolean) df.get("Directed");
        if (dir != null) {
            directedInput_=dir;
            df.remove("Directed");
        }

        Long noRecents = (Long) df.get("NoRecents");
        if (noRecents != null) {
            noRecents_= Math.toIntExact(noRecents);
            df.remove("NoRecents");
        }

        if (df.keySet().size() != 0) {
            throw new ParseException(ParseException.ERROR_UNEXPECTED_TOKEN);
        }
    }

    public String getNetInputFile() {
        return netInputFile_;
    }

    public String getNetOutputFile() {
        return netOutputFile_;
    }

    public String getInputType() {
        return inputType_;
    }

    public boolean isDirectedInput() {
        return directedInput_;
    }

    public String getOutputType() {
        return outputType_;
    }

    public boolean isIncludeOutTimestamps() {
        return includeOutTimestamps;
    }

    public String getInSep() {
        return inSep_;
    }

    public String getOutSep() {
        return outSep_;
    }

    public int getNoRecents() {
        return noRecents_;
    }

    public JSONObject getActionOps() {
        return actionOps_;
    }

    public JSONArray getFullObjectModel() {
        return fullObjectModel_;
    }

    public JSONObject getOperationModel() {
        return operationModel_;
    }

    public int getSourceColumn() {
        return sourceColumn;
    }

    public int getDstColumn() {
        return dstColumn;
    }

    public int getTimeColumn() {
        return timeColumn;
    }

    public boolean isTypedNetwork() {
        return typedNetwork_;
    }

    public int getSourceTypeColumn() {
        return sourceTypeColumn_;
    }

    public int getDstTypeColumn() {
        return dstTypeColumn_;
    }
}
