package feta.operations;
import java.util.ArrayList;
import org.json.simple.*;
import java.util.Random;

public class MixedOperations extends OperationModel{
    
    private ArrayList <Operation>ops_=null;
    private ArrayList <Operation>specialOps_= null;
    
    public Operation nextOperation() {
        //System.err.println("No Recipients "+noRecipients_);
        if (ops_.size() != 0) {
            int j= getGenerator().nextInt(ops_.size());
            return ops_.remove(j);
        }
        if (specialOps_.size() != 0) {
            int j= getGenerator().nextInt(specialOps_.size());
            return specialOps_.remove(j);
        }
        return null;
    }
    
    /** What happens if an operation fails*/
    public void failedOperation(Operation op) 
    {
        op.setTime(0);
        ops_.add(op);
    }


    @Override
    public void parseJSON(JSONObject params) {
        ops_= new ArrayList<Operation>();
        specialOps_= new ArrayList<Operation>();
        
        JSONArray arr=null;
        try {
            arr=  (JSONArray)params.get("Stars");
        } catch (Exception e) {
            System.err.println("Need to define Stars array in MixedOperations");
            System.exit(0);
        }
        for (int i = 0, size = arr.size(); i < size; i++){
            JSONObject o= (JSONObject)arr.get(i);
            boolean internal=false;
            long intStar=0;
            long extStar=0;
            long occur=0;
            String centreType= null;
            String leafType= null;
            try {
                internal = (Boolean)o.get("Internal");
            } catch (Exception e) {
                System.err.println(e);
                System.err.println("Mixed Operations stars must define Internal as boolean");
            }
            try {
                intStar= (Long)o.get("NoInternal");
            } catch (Exception e) {
                System.err.println(e);
                System.err.println("Mixed Operations stars must define NoInternal as int");
                System.exit(0);               
            }
            try {
                extStar= (Long)o.get("NoExternal");
            } catch (Exception e) {
                System.err.println("Mixed Operations stars must define NoExternal as int");
                System.exit(0);               
            }
            try {
                occur= (Long)o.get("Occurrence");
            } catch (Exception e) {
                System.err.println("Mixed Operations stars must define Occurrence as int");
                System.exit(0);               
            }
            try {
                centreType= (String)o.get("CentreType");
            } catch (Exception e) {
                System.err.println("Mixed Operations stars must define CentreType as String");
                System.exit(0);               
            }
            try {
                leafType= (String)o.get("LeafType");
            } catch (Exception e) {
                System.err.println("Mixed Operations stars must define LeafType as String");
                System.exit(0);               
            }
            //System.out.println("Defined "+intStar+" "+extStar+" "+occur+" "+centreType+" "+leafType);
            if (occur == 0) {
                specialOps_.add(new Star((int)intStar,(int)extStar,centreType,leafType,internal));
            } else {
                for (int j= 0; j < occur; j++) {
                    ops_.add(new Star((int)intStar,(int)extStar,centreType,leafType,internal));
                }
            }
        }
        
    }
}
