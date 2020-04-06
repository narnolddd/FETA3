package feta.operations;

import feta.network.Network;
import feta.objectmodels.ObjectModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Operation implements Comparable<Operation> {

    public int noChoices_;
    public int[] nodeChoices_;
    public long time_;
    // How many of the leaf nodes are already existing in the network?
    public int noExisting_;
    private ArrayList<int[]> permList;

    public int compareTo(Operation o2) {
        if (time_ < o2.time_) {
            return -1;
        }
        if (time_ > o2.time_) {
            return 1;
        }
        return 0;
    }

    public static ArrayList <int[]> generateRandomShuffles(int n, int number) {
        ArrayList <int []>shuffles = new ArrayList<int []>();
        for (int i = 0; i < number; i++) {
            int [] initial_array = new int[n];
            for (int j = 0; j < n; j++) {
                initial_array[j]=j;
            }
            // Perform Knuth Shuffles on choices to sample permutations
            for (int ind1 = 0; ind1 < n-2; ind1++) {
                int ind2 = ThreadLocalRandom.current().nextInt(ind1, n);
                int val1 = initial_array[ind1];
                int val2 = initial_array[ind2];
                initial_array[ind1] = val2;
                initial_array[ind2] = val1;
            }
            shuffles.add(initial_array);
        }
        return shuffles;
    }


    public abstract void build(Network net);

    public abstract void fill(Network net, ObjectModel om);

    public abstract double calcLogLike(Network net, ObjectModel obm, boolean ordered);

    public abstract ArrayList<double[]> getComponentProbabilities(Network net, ObjectModel obm);

    public abstract void printMeanLike(double meanLike, ObjectModel om, Network network);

    public abstract void updateLikelihoods(HashMap<double[],Double> likelihoods_, Network net, ObjectModel obm);
}

