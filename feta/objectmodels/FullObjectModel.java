package feta.objectmodels;

import feta.network.Network;

import java.util.ArrayList;

/** Class describing the fully specified object model with times each is active */
public class FullObjectModel {

    public ArrayList<ObjectModel> objectModels_;

    /** Maps a node and network object to a probability of choosing node */
    public double calcProbability(Network net, int node){
        return 0.0;
    }

}
