package feta.objectmodels.components;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;

import java.util.HashSet;

public class TriangleClosure extends ObjectModelComponent {

    private int[] occurrences_;
    private boolean random_;

    @Override
    public void calcNormalisation(UndirectedNetwork net, int sourceNode, HashSet<Integer> availableNodes) {
        occurrences_ = new int[net.noNodes_+2];
        random_=false;
        // this is a hack in place for if the node is a new node
        if (sourceNode == -1) {
            random_=true;
            normalisationConstant_ = availableNodes.size();
        } else {
            // count open wedges from the source node that can be closed
            for (int n1: net.neighbours_.get(sourceNode)) {
                for (int n2: net.neighbours_.get(n1)) {
                    occurrences_[n2]++;
                }
            }

            // go through available nodes to add to normalisation const.
            int total = 0;
            for (int node: availableNodes) {
                total+=occurrences_[node];
            }

            // check for edge case of no triangles to be closed
            if (total == 0) {
                random_=true;
                normalisationConstant_ = availableNodes.size();
            } else {
                normalisationConstant_=total;
            }
            tempConstant_=normalisationConstant_;
        }
    }

    @Override
    public void updateNormalisation(UndirectedNetwork net, HashSet<Integer> availableNodes, int chosenNode) {

        for (int n1 : net.neighbours_.get(chosenNode)) {
            if (n1 == chosenNode)
                continue;
            occurrences_[n1]++;

        }

        int total = 0;
        for (int node : availableNodes) {
            total += occurrences_[node];
        }

        if (total == 0) {
            random_ = true;
            tempConstant_ = availableNodes.size();
        } else {
            tempConstant_ = total;
        }

    }

    public void calcNormalisation (UndirectedNetwork net, int[] removed) {
        // make an array logging the number of triangles an edge could close if each node is selected.
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
