package feta.objectmodels.components;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;
import org.json.simple.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class TriangleClosure extends ObjectModelComponent {

    private int[] occurrences_;
    private boolean random_;

    public void calcNormalisation (UndirectedNetwork net, int[] removed) {
        // make an array logging the number of triangles an edge could could close if each node is selected.
        // This model assumes all links are coming from a newly added node
        occurrences_ = new int[net.noNodes_+2];
        random_=false;
        if (removed.length==0) {
            normalisationConstant_=net.noNodes_;
            random_=true;
        }
        else {
            int total=0;

            // Counts the number of neighbourhoods each node is a member of
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
        occurrences_ = new int[net.noNodes_+2];
        random_=false;
        if (removed.length==0) {
            normalisationConstant_=net.noNodes_;
            random_=true;
        }
        else {
            int total=0;
            occurrences_ = new int[net.noNodes_+2];
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

    public double calcProbability (UndirectedNetwork net, int node) {
        if (tempConstant_==0 || node >= occurrences_.length) {
            // System.out.println("Yo");
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
            random_=true;
            tempConstant_=normalisationConstant_;
        } else {
            int node = removed[removed.length - 1];

            for (int n1 : net.neighbours_.get(node)) {
                if (n1 == node)
                    continue;
                occurrences_[n1]++;
            }

            for (int n : removed) {
                occurrences_[n] = 0;
            }

            int total = 0;
            for (int val : occurrences_) {
                total += val;
            }

            if (total == 0) {
                random_ = true;
                tempConstant_ = net.noNodes_ - removed.length;
            } else {
                tempConstant_ = total;
            }
        }
//        double const1 = tempConstant_;
//        calcNormalisation(net, removed);
//        double const2 = tempConstant_;
//
//        if (const1!=const2) {
//            System.out.println("Without speedup: "+const1+", with speedup "+const2);
//        }
    }

    @Override
    public String toString() {
        return "TriangleClosure";
    }
}
