package feta.operations;

import feta.network.Network;
import feta.objectmodels.MixedModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Clique extends Operation {

    String[] members_;

    public Clique(int cliqueSize_) {
        members_= new String[cliqueSize_];
    }

    @Override
    public void bufferLinks(Network net) {

    }

    @Override
    public void chooseNodes(Network net, MixedModel obm) {

    }

    @Override
    public void setNodeChoices(boolean orderedData) {

    }
}
