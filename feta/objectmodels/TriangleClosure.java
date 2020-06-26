package feta.objectmodels;

import feta.network.DirectedNetwork;
import feta.network.Measurements.MaxDegree;
import feta.network.Network;
import feta.network.UndirectedNetwork;
import org.json.simple.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class TriangleClosure extends ObjectModelComponent {

    private int[] occurrences_;
    private boolean random_;

    public void calcNormalisation (UndirectedNetwork net, int[] removed) {
        occurrences_ = new int[net.noNodes_+2];
        random_=false;
        if (removed.length==0) {
            normalisationConstant_=net.noNodes_;
            random_=true;
        }
        else {
            int total=0;

            for (int node: removed) {
                for (int n1 : net.neighbours_.get(node)) {
                    if (n1 == node)
                        continue;
                    occurrences_[n1]++;
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

    public void calcNormalisation (DirectedNetwork net, int[] removed) {
        if (removed.length==0) {
            normalisationConstant_=net.noNodes_;
            random_=true;
        }
        else {
            int total=0;
            occurrences_ = new int[net.noNodes_+1];
            for (int node: removed) {
                for (int n1 : net.outLinks_.get(node)) {
                    if (n1 == node)
                        continue;
                    if (net.isLink(node, n1))
                        continue;
                    occurrences_[n1]++;
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

    public double calcProbability (UndirectedNetwork net, int node) {
        if (tempConstant_==0 || node >= occurrences_.length) {
            return 0.0;
        }
        if (random_) {
            return 1.0/tempConstant_;
        }
        double numerator = occurrences_[node];
        return numerator/tempConstant_;
    }

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

    public void updateNormalisation(UndirectedNetwork net, int [] removed) {
        random_=false;
        if (removed.length==0) {
            tempConstant_=normalisationConstant_;
            return;
        }
        int node = removed[removed.length-1];
        int total = 0;
        for (int n1 : net.neighbours_.get(node)) {
            if (n1 == node)
                continue;
            occurrences_[n1]++;
        }

        for (int n: removed) {
            occurrences_[n]=0;
        }

        for (int val: occurrences_) {
            total+=val;
        }

        if (total==0) {
            random_=true;
            tempConstant_= net.noNodes_-removed.length;
        }
        else {
            tempConstant_= total;
        }
    }

    @Override
    public String toString() {
        return "TriangleClosure";
    }
}
