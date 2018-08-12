package feta;

import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/** Class which parses JSON file for user specified options */

public class FetaOptions {

    /** Options related to the data input and output files */
    String netInputFile_;
    String netOutputFile_;
    String inputType_ = "NNT";
    String outputType_;
    String sep_ = "\\s+";



    public void readConfig(String file) {

        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(file));
            JSONObject jsonObject = (JSONObject) obj;

            // Get datafile
            JSONObject dataFileOps = (JSONObject) jsonObject.get("Data");

            parseDataTag(dataFileOps);

            JSONObject actionOps = (JSONObject) jsonObject.get("Action");



        } catch (FileNotFoundException e) {
            System.err.println("JSON Options file "+file+" not found.");
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void parseDataTag(JSONObject df) {
        netInputFile_ = (String) df.get("GraphInputFile");

        String in = (String) df.get("GraphInputType");
        if (in != null) {
            inputType_ = in;
        }

        String sep = (String) df.get("LineSeparator");
        if (sep != null) {
            sep_= sep;
        }
    }

}