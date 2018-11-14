package feta.parsenet;

import feta.network.DirectedNetwork;
import feta.network.Link;
import feta.network.Network;
import feta.operations.Operation;

import java.util.ArrayList;

public class ParseNetDirected extends ParseNet{

    @Override
    public ArrayList<Operation> parseNewLinks(ArrayList<Link> links, Network net) {
        return new ArrayList<Operation>();
    }

    public ParseNetDirected(DirectedNetwork net){
        System.err.println("You haven't written this part yet!!!");
    }
}
