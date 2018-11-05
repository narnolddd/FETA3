package feta.operations;

public class Email extends OperationModel {

    /** Network grows by internal and external stars */

    public double propInternal_=0.2;
    public int noRecipients_=3;

    public Operation nextOperation() {
        double r = Math.random();
        if (r<propInternal_) {
            return new Star(noRecipients_,true);
        } else return new Star(noRecipients_,false);
    }
}
