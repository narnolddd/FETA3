package feta.operations;

import org.json.simple.JSONObject;

public class PreferentialAttachment extends OperationModel {

    public int initDegree_= 3;

    public Operation nextOperation() {
        Operation op = new Star(initDegree_,false);
        op.noExisting_=initDegree_;
        return op;
    }

    public void parseJSON(JSONObject params) {
        Long deg = (Long) params.get("InitialDegree");
        if (deg != null) {
            initDegree_= Math.toIntExact(deg);
        }
    }

}