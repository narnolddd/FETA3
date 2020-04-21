package feta.operations;

import org.json.simple.JSONObject;


public class Email extends OperationModel {

    /** Network grows by internal and external stars */

    public double propInternal_=0.2;
    public int noRecipients_=3;

    public Operation nextOperation() {
        double r = Math.random();
        //System.err.println("No Recipients "+noRecipients_);
        if (r<propInternal_) {
            Star intStar = new Star(noRecipients_,true);
            intStar.setNoExisting(noRecipients_);
            return intStar;
        } else {
            Star extStar = new Star(noRecipients_,false);
            extStar.setNoExisting(noRecipients_);
            return extStar;
        }
    }

    @Override
    public void parseJSON(JSONObject params) {
        Double p = (Double) params.get("PropInternal");
        if (p != null) {
            if (p < 0 || p > 1) {
                throw new IllegalArgumentException("Proportion must be a probability.");
            }
            propInternal_=p;
        }
        Long noRecip = (Long)params.get("NoRecipients");
        if (noRecip!= null) {
            if (noRecip <= 0) {
                throw new IllegalArgumentException("Number of recipients must be strictly positive");
            }
            noRecipients_ = noRecip.intValue();
        }
    }
}
