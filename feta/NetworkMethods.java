package feta;

/** Class with methods for computing various network statistics */
public class NetworkMethods {
    public NetworkMethods(){}

    public double getSecondMoment(int[] degrees, int noNodes){
        double sum = 0.0;
        for (int i = 0; i<degrees.length; i++){
            sum+= degrees[i]*degrees[i];
        }
        if (noNodes == 0){
            return 0.0;
        } else {
            return sum/noNodes;
        }
    }


}
