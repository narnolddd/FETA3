package feta;

import java.util.ArrayList;
import java.util.List;

public class TestingTesting {

    public List<int[]> generatePartitions(int n, int k) {
        List<int[]> parts = new ArrayList<>();
        if (k == 1) {
            parts.add(new int[] {n});
            return parts;
        }
        List<int[]> newParts = new ArrayList<>();
        for (int l = 0; l < n; l++) {
            List<int[]> oldParts = generatePartitions(n-l,k-1);
            for (int[] partition: oldParts) {
                int[] newPartition = new int[partition.length+1];
                System.arraycopy(partition,0,newPartition,0,partition.length);
                newPartition[partition.length]=l;
                newParts.add(newPartition);
            }
        }
        return newParts;
    }

    public static void main(String[] args) {
        List<int[]> partitions = new TestingTesting().generatePartitions(10,4);
        for (int[] part: partitions) {
            String partition = "";
            for (int i: part) {
                partition+=i+" + ";
            }
            System.out.println(partition);
        }
    }
}
