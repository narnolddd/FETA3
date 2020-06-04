package feta.objectmodels;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class TriangleClosure2 extends ObjectModelComponent{

    private ArrayList<Integer> occurrences_;
    private int [] justChosen_;
    private boolean random_;


    @Override
    public void calcNormalisation (UndirectedNetwork net, int[] removed) {
        occurrences_ = new ArrayList<>();
        justChosen_=removed;
        if (justChosen_.length==0) {
            normalisationConstant_=net.noNodes_;
            random_=true;
        }
        else {
            int node = justChosen_[justChosen_.length - 1];
            for (int n1 : net.neighbours_.get(node)) {
                for (int n2 : net.neighbours_.get(n1)) {
                    if (n2 == node)
                        continue;
                    if (net.isLink(node, n2))
                        continue;
                    occurrences_.add(n2);
                }
            }

            if (occurrences_.size() == 0) {
                random_ = true;
                normalisationConstant_ = net.noNodes_ - removed.length;
            } else {
                normalisationConstant_ = occurrences_.size();
            }
        }
        tempConstant_=normalisationConstant_;
    }

    @Override
    public void calcNormalisation (DirectedNetwork net, int[] removed) {
        ArrayList<Integer> occurrences = new ArrayList<>();
        justChosen_=removed;
        if (justChosen_.length==0) {
            normalisationConstant_=net.noNodes_;
            random_=true;
        }
        else {
            int node = justChosen_[justChosen_.length-1];
            for (int n1 : net.outLinks_.get(node)) {
                for (int n2: net.outLinks_.get(n1)) {
                    if(n2 == node)
                        continue;
                    if(net.isLink(node,n2))
                        continue;
                    occurrences.add(n2);
                }
            }
            if (occurrences_.size()==0) {
                random_=true;
                normalisationConstant_=net.noNodes_ - removed.length;
            }
            else {
                normalisationConstant_ = occurrences_.size();
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
            random_=false;
            return 1.0/tempConstant_;
        }
        double numerator = Collections.frequency(occurrences_,node);
        return numerator/tempConstant_;
    }

    @Override
    public double calcProbability (DirectedNetwork net, int node) {
        if (tempConstant_==0) {
            return 0.0;
        }
        if (random_) {
            random_=false;
            return 1.0/tempConstant_;
        }
        double numerator = Collections.frequency(occurrences_,node);
        return numerator/tempConstant_;
    }

    public String toString() {
        return "TriangleClosure2";
    }
}
