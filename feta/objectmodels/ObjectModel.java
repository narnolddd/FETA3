package feta.objectmodels;

import feta.Methods;
import feta.network.Network;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

public class ObjectModel {

    private ArrayList<ObjectModelComponent> components_;
    private double[] weights_;

    public ObjectModel(){
        components_= new ArrayList<ObjectModelComponent>();
    }

    /** Checks object model prescribed is valid */
    public void checkValid(){
        if (weights_.length == 0 || components_.size()==0) {
            throw new IllegalArgumentException("Object model components or weights unspecified");
        }
        if (weights_.length != components_.size()) {
            throw new IllegalArgumentException("Weights do not correspond to number of components");
        }

        double sum = 0.0;
        for (int i = 0; i< weights_.length; i++) {
            if (weights_[i]<0) {
                throw new IllegalArgumentException("Cannot have negative weights");
            }
            sum+= weights_[i];
        }

        if (sum == 0.0) {
            throw new IllegalArgumentException("No object model weights specified");
        }

        // Normalise weights if not done already
        for (int i = 0; i< weights_.length; i++) {
            weights_[i]/=sum;
        }
    }

    public double calcProbability(Network net, int node) {
        double probability_=0.0;
        for (int i = 0; i < components_.size(); i++) {
            probability_+= weights_[i]*components_.get(i).calcProbability(net, node);
        }
        return probability_;
    }


    public void normaliseAll(Network net, int [] alreadySampled) {
        for (ObjectModelComponent omc : components_) {
            omc.calcNormalisation(net, alreadySampled);
        }
    }

    public void normaliseAll(Network net) {
        normaliseAll(net, new int[0]);
    }

    /** Performs check that normalisation is correct */
    public void checkNorm(Network net) {
        double sum = 0.0;
        normaliseAll(net);
        for (int node = 0; node < net.noNodes_; node++) {
            sum += calcProbability(net, node);
        }
        if (Math.abs(sum - 1.0) > 0.0005) {
            System.err.println("Object model calculated not correct. Currently probabilities add to "+sum);
        }
    }


    /** This part of the code exists for growing networks - monte carlo sampling of nodes from prob distributions given by object model */

    public int[] getNodesWithoutReplacement(Network net, int sampleSize, int[] alreadyChosen) {
        int[] chosenNodes = new int[sampleSize];
        for (int j = 0; j<sampleSize; j++) {
            chosenNodes[j] = -1;
        }
        int [] removedFromSample= concatenate(chosenNodes,alreadyChosen);
        for (int i = 0; i < sampleSize; i++) {
            int chosenNode = nodeSampleWithoutReplacement(net, removedFromSample);
            chosenNodes[i]=chosenNode;
            removedFromSample[i]=chosenNode;
        }
        return chosenNodes;
    }

    public int[] getNodesWithReplacement(Network net, int sampleSize) {
        int[] chosenNodes = new int[sampleSize];
        for (int i = 0; i<sampleSize; i++) {
            int chosenNode = nodeSampleWithReplacement(net);
            chosenNodes[i]=chosenNode;
        }
        return chosenNodes;
    }

    public int nodeSampleWithoutReplacement(Network net, int[] alreadyChosenNodes) {
        ArrayList<Integer> nodeList = new ArrayList<Integer>();
        for (int j = 0; j < net.noNodes_; j++) {
            nodeList.add(j);
        }

        // This bit of code deals with the "without replacement" aspect of the sampling, by removing the already chosen nodes from the sample space.
        Arrays.sort(alreadyChosenNodes);
        int numAlreadyChosen = 0;
        for (int k = alreadyChosenNodes.length - 1; k>=0; k--) {
            if (alreadyChosenNodes[k]>=0) {
                nodeList.remove(alreadyChosenNodes[k]);
                numAlreadyChosen++;
            }
        }

        // This part does the sampling.

        normaliseAll(net, alreadyChosenNodes);
        double r = Math.random();
        double weightSoFar = 0.0;
        int l;
        for (l = 0; l < nodeList.size(); l++) {
            weightSoFar+= calcProbability(net, nodeList.get(l));
            if (weightSoFar > r)
                break;
        }
        return nodeList.get(l);
    }

    public int nodeSampleWithReplacement(Network net) {
        return nodeSampleWithoutReplacement(net, new int[0]);
    }

    /** Handy method for concatenating arrays */
    public static int[] concatenate(int[] a1, int[] a2) {
        int len1 = a1.length;
        int len2 = a2.length;

        int[] a3 = new int[len1+len2];
        System.arraycopy(a1,0,a3,0,len1);
        System.arraycopy(a2,0,a3,len1,len2);
        return a3;
    }

    /** Read components and weights from JSON */
    public void readObjectModelOptions(JSONArray componentList) {
        weights_= new double[componentList.size()];
        for (int i = 0; i< componentList.size(); i++) {
            JSONObject comp = (JSONObject) componentList.get(i);

            /** Gets object model element class from string. Bit of a mouthful */
            ObjectModelComponent omc = null;
            String omcClass = (String) comp.get("ComponentName");
            Class <?extends ObjectModelComponent> component = null;

            try {
                component= Class.forName(omcClass).asSubclass(ObjectModelComponent.class);
                Constructor<?> c = component.getConstructor();
                omc = (ObjectModelComponent)c.newInstance();
            } catch (ClassNotFoundException e){
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            // Heck...

            double weight = (double) comp.get("Weight");

            omc.parseJSON(comp);
            components_.add(omc);
            weights_[i]=weight;
        }
    }

}
