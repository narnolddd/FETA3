package feta.objectmodels;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class TriangleClosure2 extends ObjectModelComponent{

    private int[] occurrences_;
    private boolean random_;


    public void calcNormalisation (UndirectedNetwork net, int[] removed) {
        random_=false;
        if (removed.length==0) {
            normalisationConstant_=net.noNodes_;
            random_=true;
        }
        else {
            int total=0;
            occurrences_ = new int[net.noNodes_+2];
            int node = removed[0];
            for (int n1 : net.neighbours_.get(node)) {
                for (int n2 : net.neighbours_.get(n1)) {
                    if (n2 == node)
                        continue;
                    if (net.isLink(node, n2))
                        continue;
                    occurrences_[n2]++;
                    total++;
                }
            }

            for (int n: removed) {
                total-= occurrences_[n];
                occurrences_[n]=0;
            }

            if (total == 0) {
                random_ = true;
                normalisationConstant_ = net.noNodes_ - removed.length;
            } else {
                normalisationConstant_ = total;
            }
        }
        tempConstant_=normalisationConstant_;
    }

    @Override
    public void calcNormalisation (DirectedNetwork net, int[] removed) {
        if (removed.length==0) {
            normalisationConstant_=net.noNodes_;
            random_=true;
        }
        else {
            int total=0;
            occurrences_ = new int[net.noNodes_+1];
            int node = removed[removed.length - 1];
            for (int n1 : net.outLinks_.get(node)) {
                for (int n2 : net.outLinks_.get(n1)) {
                    if (n2 == node)
                        continue;
                    if (net.isLink(node, n2))
                        continue;
                    occurrences_[n2]++;
                    total++;
                }
            }

            if (total == 0) {
                random_ = true;
                normalisationConstant_ = net.noNodes_ - removed.length;
            } else {
                normalisationConstant_ = total;
            }
        }
        tempConstant_=normalisationConstant_;
    }

    @Override
    public double calcProbability (UndirectedNetwork net, int node) {
        if (tempConstant_==0) {
            return 0.0;
        }
        if (random_) {
            return 1.0/tempConstant_;
        }
        double numerator = occurrences_[node];
        return numerator/tempConstant_;
    }

    @Override
    public double calcProbability (DirectedNetwork net, int node) {
        if (tempConstant_==0) {
            return 0.0;
        }
        if (random_) {
            return 1.0/tempConstant_;
        }
        double numerator = occurrences_[node];
        return numerator/tempConstant_;
    }

    public String toString() {
        return "TriangleClosure2";
    }
}
