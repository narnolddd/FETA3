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

    public Clone() {
        operations_= new ArrayList<Operation>();
    }

    public void readFile() {
        File file = new File(fname_);
        try {
            br_ = new BufferedReader(new FileReader(file));
            long time = 0;
            String lastLine="";
            while (time <= startTime_) {
                lastLine = br_.readLine();
                time = Long.parseLong(lastLine.split(" ")[0].trim());
            }
            while (lastLine != null) {
                lastLine = lastLine.trim();
                parseLine(lastLine);
                lastLine = br_.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parseLine(String line) {
        String [] parts = line.split(" ");
        long time = Long.parseLong(parts[0]);
        String type_ = parts[1];
        if (type_.equals("STAR")) {
            Star op_;
            boolean typed= false;
            int offset=6;
            String centreType=null;
            String leafType=null;
            if (parts[3].equals("TYPES")) {
				offset= 9;
				typed= true;
				centreType= parts[4];
				leafType=parts[5];
			}
            int noLeaves_ = parts.length - offset;
            int noExisting = Integer.parseInt(parts[parts.length - 1]);
            String internal = parts[parts.length-2];
            if (internal.equals("INTERNAL")) {
                op_= new Star(noLeaves_,centreType, leafType, true);
				
            } else {
                op_= new Star(noLeaves_, centreType, leafType, false);
            }
            op_.setTime(time);
            op_.setNoExisting(noExisting);
            operations_.add(op_);
            
        } else {
			System.err.println("Unrecognised type in Clone.parseLine");
			System.exit(-1);
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
