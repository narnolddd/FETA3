package feta.operations;

public class PreferentialAttachment extends OperationModel {

    public int initDegree_;

    public Operation nextOperation() {
        return new Star(initDegree_,false);
    }

}