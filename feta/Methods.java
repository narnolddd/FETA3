package feta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Methods {

    public static int[] concatenate(int[] first, int[] second) {
        int[] both = new int[first.length + second.length];
        System.arraycopy(first,0,both,0,first.length);
        System.arraycopy(second, 0, both, first.length, second.length);
        return both;
    }

    public static int[] removeNegativeNumbers(int[] num) {
        int[] output = new int[num.length];
        int k = 0;
        for (int j : num) {
            if (j >= 0) {
                output[k++] = j;
            }
        }
        return Arrays.copyOfRange(output, 0, k);
    }

    /** Generates all permutations of an array and returns them in an arraylist */
    public static ArrayList<int[]> generatePerms(int start, int[] input, ArrayList<int[]> permList) {
        if (start == input.length) {
            permList.add(input.clone());
            return permList;
        }
        for (int i = start; i < input.length; i++) {
            int temp = input[i];
            input[i] = input[start];
            input[start] = temp;

            generatePerms(start+1,input, permList);

            int temp2 = input[i];
            input[i] = input[start];
            input[start] = temp2;
        }
        return permList;
    }

    /** Samples from different possible permutations of an array */
    public static ArrayList<int[]> generateRandomShuffles(int [] arr, int sampleSize, Random random) {
        int n = arr.length;
        ArrayList <int []>shuffles = new ArrayList<int []>();
        for (int i = 0; i < sampleSize; i++) {
            int[] initial_array=arr.clone();
            // Perform Knuth Shuffles on choices to sample permutations
            for (int ind1 = 0; ind1 < n-2; ind1++) {
                int ind2 = ind1 + random.nextInt(n-ind1);
                int val1 = initial_array[ind1];
                int val2 = initial_array[ind2];
                initial_array[ind1] = val2;
                initial_array[ind2] = val1;
            }
            shuffles.add(initial_array);
        }
        return shuffles;
    }

    public static int[] toIntArray(ArrayList<Integer> set) {
        int[] intArray = new int[set.size()];
        int ind = 0;
        for (Integer i:set) {
            intArray[ind]= i;
            ind++;
        }
        return intArray;
    }

    public static String[] toStringArray(Set<String> set) {
        String[] strArray = new String[set.size()];
        int ind = 0;
        for (String str: set) {
            strArray[ind]=str;
            ind++;
        }
        return strArray;
    }

    public static void printArr(int [] arr) {
        StringBuilder str = new StringBuilder();
        for (int i: arr) {
            str.append(i).append(" ");
        }
        System.out.println(str);
    }
}
