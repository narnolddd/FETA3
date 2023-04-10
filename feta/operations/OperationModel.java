package feta.operations;

import org.json.simple.JSONObject;
import java.util.Random;
public abstract class OperationModel {

    private Random generator_;
    public OperationModel() { generator_=new Random();}

    public Random getGenerator() {return generator_;}
    public void setGenerator(Random r) {generator_=r;}

    /** Select next growth operation */
    public abstract Operation nextOperation();

    public abstract void parseJSON(JSONObject params);

}
