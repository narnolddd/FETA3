package feta.operations;

import org.json.simple.JSONObject;

import java.util.ArrayList;

/** Class represents operation model obtained by copying that of a real network */

public class Clone extends OperationModel {

    public long startTime_=10;
    public String fname_;
    public ArrayList<Operation> operations_;

    public Clone() {
        operations_= new ArrayList<Operation>();
    }

    public Operation nextOperation(){
        return null;
    }

    public void parseJSON(JSONObject params) {
        Long start_ = (Long) params.get("Start");
        if (start_!=null) {
            startTime_=start_;
        }
        String file = (String) params.get("FileName");
        if (file == null) {
            System.err.println("No network file specified to clone");
        }
    }
}
