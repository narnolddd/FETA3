package feta.operationmodels;

import feta.operations.Operation;

import java.util.ArrayList;

public abstract class OperationModel {

    public ArrayList<Operation> operations_;

    public OperationModel() {

    }

    abstract Operation nextOperation();
}