package feta.operations;

import feta.network.Network;
import feta.objectmodels.ObjectModel2;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Operation2 {

    private int noOldNodes_;
    private int[] oldNodes_;
    private int noNewNodes_;
    private int[] newNodes_;
    private long time_;

    /** updates network with the new nodes and links that occur in this operation
     alternative is for this to happen in the Netwwork interface */
    public abstract void buildOperation(Network net);

    /** Implemented in Operation, this is for selecting old nodes when growing network */
    public void chooseNodes(Network net, ObjectModel2 obm) { }

    /** Returns the likelihood ratio against random model of an ordered set of nodes nodeSet, given that alreadyChosen
     * have been chosen. These can be added up to get likelihoods for unordered sets of nodes */
    public double likelihoodRatioOrderedSet(Network net, ObjectModel2 obm, int[] nodeSet, int[] alreadyChosen){
        return 0.0;
    }


    public void updateLikelihoodGrid(HashMap<double[], Double> likelihoods, ObjectModel2 obm){}

    /** Helper methods */

    /** Generates all permutations of an array and returns them in an arraylist */
    public static ArrayList<int[]> generatePermutations(int [] arr) {
        return new ArrayList<int[]>();
    }

    /** Samples from different possible permutations of an array */
    public static ArrayList<int[]> generateRandomShuffles(int [] arr, int sampleSize) {
        return new ArrayList<int[]>();
    }

}
