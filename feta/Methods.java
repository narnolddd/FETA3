package feta;

public class Methods {

    public static int[] concatenate(int[] a1, int[] a2) {
        int len1 = a1.length;
        int len2 = a2.length;

        int[] a3 = new int[len1+len2];
        System.arraycopy(a1,0,a3,0,len1);
        System.arraycopy(a2,0,a3,len1,len2);
        return a3;
    }
}
