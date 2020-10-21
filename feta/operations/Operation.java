package feta.operations;

import feta.Methods;
import feta.network.Network;
import feta.objectmodels.MixedModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public abstract class Operation {

    ArrayList<int[]> nodeChoices_;
    ArrayList <int[]> nodeOrders_;
    private long time_;
    private int noChoices_=0;
    private Random generator_;

    /** updates network with the new nodes and links that occur in this operation
     alternative is for this to happen in the Network interface */
    public abstract void bufferLinks(Network net);

    /** Implemented in Operation, this is for selecting old nodes when growing network */
    public abstract void chooseNodes(Network net, MixedModel obm);

    /** Extracts operation into a node choices arraylist */
    public abstract void setNodeChoices(boolean orderedData);

    /** Get time */
    public long getTime() {return time_;}

    /** Set time */
    public void setTime(long time) {
        time_=time;
    }

    /** Set random generator */
    public void setRandom(Random rg) {generator_=rg;}

    public int getNoChoices() {return noChoices_;}

    public ArrayList<int[]> getNodeOrders() {
        return nodeOrders_;
    }

    /** Helper methods */

    /** Concatenates two int arrays */
    public static int[] concatenate(int[] first, int[] second) {
        int[] both = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, both, first.length, second.length);
        return both;
    }

    /** Generates possible ways of concatenaing items from list1 with items from list2 */
    private static ArrayList<int[]> combineOrders(ArrayList<int[]> list1, ArrayList<int[]> list2) {
        ArrayList<int[]> combined = new ArrayList<int[]>();
        for (int [] l1: list1) {
            for (int [] l2: list2) {
                combined.add(concatenate(l1, l2));
            }
        }
        return combined;
    }

    /** Generates all possible choice sequences of nodes from choice arraylist */
    private static ArrayList<int[]> generatePossibleSequences(ArrayList<ArrayList<int[]>> listOfLists) {
        ArrayList<int[]> finalList = new ArrayList<int[]>();
        finalList.add(new int[0]);
        for (int i = 0; i< listOfLists.size(); i++) {
            finalList = combineOrders(finalList, listOfLists.get(i));
        }
        return finalList;
    }

    ArrayList<int[]> generateOrdersFromOperation() {
        ArrayList<ArrayList<int[]>> listOfLists = new ArrayList<ArrayList<int[]>>();
        for (int[] arr: nodeChoices_) {
            if (arr.length <= 5) {
                listOfLists.add(Methods.generatePerms(0,arr,new ArrayList<int[]>()));
            }
            else {
                listOfLists.add(Methods.generateRandomShuffles(arr, 50, generator_));
            }
        }
        return generatePossibleSequences(listOfLists);
    }

    public void filterNodeChoices() {
        noChoices_=0;
        ArrayList<int[]> newChoices = new ArrayList<int[]>();
        for (int[] nodeSet: nodeChoices_) {
            int[] copy = Methods.removeNegativeNumbers(nodeSet);
            newChoices.add(copy);
            noChoices_+=copy.length;
        }
        nodeChoices_=newChoices;
    }

}
