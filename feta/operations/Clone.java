package feta.operations;

import org.json.simple.JSONObject;
import java.io.*;
import java.util.ArrayList;

/** Class represents operation model obtained by copying that of a real network */

public class Clone extends OperationModel {

    public long startTime_=10;
    public String fname_;
    public ArrayList<Operation> operations_;
    public BufferedReader br_;
    public long token_ = 0;

    public Clone() {
        operations_= new ArrayList<Operation>();
    }

    public void readFile() {
        File file = new File(fname_);
        try {
            br_ = new BufferedReader(new FileReader(file));
            String line;
            long time = 0;
            while (time < startTime_) {
                String l = br_.readLine();
                time = Long.parseLong(l.split(" ")[0].trim());
            }
            while ((line = br_.readLine()) != null) {
                line = line.trim();
                parseLine(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parseLine(String line) {
        Operation op_;
        String [] parts = line.split(" ");
        Long time = Long.parseLong(parts[0]);
        String type_ = parts[1];
        if (type_.equals("STAR")) {
            int noLeaves_ = parts.length - 6;
            int noExisting_ = Integer.parseInt(parts[parts.length - 1]);
            String internal = parts[parts.length-2];
            if (internal.equals("INTERNAL")) {
                op_= new Star(noLeaves_,true);
            } else {
                op_= new Star(noLeaves_, false);
            }
            op_.time_=time;
            op_.noExisting_=noExisting_;
            operations_.add(op_);
        }
    }

    public Operation nextOperation(){
        if (operations_.size()>0) {
            Operation op_=operations_.get(0);
            operations_.remove(0);
            return op_;
        } else return new Star(3, false);
    }

    public void parseJSON(JSONObject params) {
        Long start_ = (Long) params.get("Start");
        if (start_!=null) {
            startTime_=start_;
        }
        String file = (String) params.get("FileName");
        if (file == null) {
            System.err.println("No network file specified to clone");
        } else {
            fname_=file;
        }
        readFile();
    }
}
