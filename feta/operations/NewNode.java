package feta.operations;

public class NewNode extends Operation {

    public String toString() {
        return "NEW NODE CONNECTING TO "+noChoices_+" EXISTING NODES";
    }

    public NewNode(int noChoices) {

        noChoices_= noChoices;
        nodeChoices_= new int[noChoices];

    }


}
