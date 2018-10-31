package feta.operations;

public abstract class OperationModel {

    public OperationModel() { }

    /** Select next growth operation */
    public abstract Operation nextOperation();

}