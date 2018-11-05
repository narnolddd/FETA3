package feta.operations;

import org.json.simple.JSONObject;

public abstract class OperationModel {

    public OperationModel() { }

    /** Select next growth operation */
    public abstract Operation nextOperation();

    public abstract void parseJSON(JSONObject params);

}