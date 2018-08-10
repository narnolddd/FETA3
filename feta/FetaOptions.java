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
    String inputType_;
    String outputType_;



    public void readConfig(String file) {

        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(file));
            JSONObject jsonObject = (JSONObject) obj;

            // Get datafile
            JSONObject dataFileOps = (JSONObject) jsonObject.get("Data");

        } catch (FileNotFoundException e) {
            System.err.println("JSON Options file "+file+" not found.");
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void parseDataTag(JSONObject df) {
        netInputFile_ = (String) df.get("InputFile");
        netOutputFile_= (String) df.get("OutputFile");
        inputType_= (String) df.get("")
    }

}