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
    public String netOutputFile_;
    public String inputType_ = "NNT";
    public boolean directedInput_;
    public boolean netInputSampled_;
    public double sampleProp_= 1.0;
    public String outputType_="NNT";
    public String inSep_ = "\\s+";
    public String outSep_= " ";
    public int noRecents_=10;

    /** Type of action. Everything else related to the action will be parsed in the relevant action class */
    JSONObject actionOps_;

    /** Options relating to model, which will be parsed in the respective classes */
    public JSONArray fullObjectModel_;
    public JSONObject operationModel_;


    public FetaOptions(){

        String dateTime = new SimpleDateFormat("yyyyMMddHHmmss'.dat'").format(new Date());
        netOutputFile_="output/"+dateTime;

    }

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
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
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

        String out = (String) df.get("GraphOutputType");
        if (out != null) {
            outputType_= out;
            df.remove("GraphOutputType");
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
}