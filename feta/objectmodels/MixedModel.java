package feta.objectmodels;

import feta.Methods;
import feta.network.Network;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

public class MixedModel {

    public ArrayList<ObjectModelComponent> components_;
    private double[] weights_;
    private boolean checkWeights_;

    public MixedModel() {components_=new ArrayList<ObjectModelComponent>();}

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
        if (Math.abs(1.0 - sum) > 0.0005) {
            throw new IllegalArgumentException("Model weights should add to 1.0, current sum "+sum);
        }
    }

    /** Functions for probability calculation */

    /** For calculating the normalisation constant for the first time */
    public void calcNormalisation(Network net, int [] alreadySampled) {
        for (ObjectModelComponent omc : components_) {
            omc.calcNormalisation(net, alreadySampled);
        }
    }

    public void calcNormalisation(Network net) {
        calcNormalisation(net, new int[0]);
    }

    /** For updating the normalisation constant after sampling nodes in removed */
    public void updateNormalisation(Network net, int [] removed) {
        for (ObjectModelComponent omc: components_) {
            omc.updateNormalisation(net, removed);
        }
    }

    /** Having calculated normalisation, get node probability */
    public double calcProbability(Network net, int node) {
        double probability_=0.0;
        for (int i = 0; i < components_.size(); i++) {
            probability_+= weights_[i]*components_.get(i).calcProbability(net, node);
        }
        return probability_;
    }

    /** Functions for node sampling */

    /** Draw a single node without replacement */
    public final int nodeDrawWithoutReplacement(Network net, int[] alreadyChosenNodes) {
        ArrayList<Integer> nodeList = new ArrayList<Integer>();
        int [] chosen = Methods.removeNegativeNumbers(alreadyChosenNodes);
        int node;
        for (int j = 0; j < net.noNodes_; j++) {
            nodeList.add(j);
        }

        // Removes already chosen nodes from the sample space
        for (int k = 0; k < chosen.length; k++) {
            nodeList.remove((Integer) chosen[k]);
        }

        if (nodeList.isEmpty()) {
            node = -1;
        }
        else {
            // This part does the sampling.
            updateNormalisation(net, chosen);
            double r = Math.random();
            double weightSoFar = 0.0;
            int l;
            for (l = 0; l < nodeList.size(); l++) {
                weightSoFar += calcProbability(net, nodeList.get(l));
                if (weightSoFar > r)
                    break;
            }
            if (l == nodeList.size())
                l--;
            node = nodeList.get(l);
        }
        return node;
    }

    public int nodeDrawWithReplacement(Network net) {
        return nodeDrawWithoutReplacement(net, new int[0]);
    }

    public int[] drawMultipleNodesWithoutReplacement(Network net, int sampleSize, int[] alreadyChosen) {
        int[] chosenNodes = new int[sampleSize];
        for (int j = 0; j<sampleSize; j++) {
            chosenNodes[j] = -1;
        }
        int [] removedFromSample= Methods.concatenate(alreadyChosen,chosenNodes);
        calcNormalisation(net, Methods.removeNegativeNumbers(removedFromSample));
        for (int i = 0; i < sampleSize; i++) {
            int chosenNode = nodeDrawWithoutReplacement(net, removedFromSample);
            chosenNodes[i]=chosenNode;
            removedFromSample[alreadyChosen.length + i]=chosenNode;
        }
        return Methods.removeNegativeNumbers(chosenNodes);
    }

    public int[] drawMultipleNodesWithReplacement(Network net, int sampleSize, int[] alreadyChosen) {
        int[] chosenNodes = new int[sampleSize];
        for (int j = 0; j<sampleSize; j++) {
            chosenNodes[j] = -1;
        }
        for (int i = 0; i < sampleSize; i++) {
            chosenNodes[i] = nodeDrawWithoutReplacement(net,alreadyChosen);
        }
        return Methods.removeNegativeNumbers(chosenNodes);
    }

    /** Performs check that normalisation is correct */
    public void checkNorm(Network net) {
        double sum = 0.0;
        calcNormalisation(net);
        for (int node = 0; node < net.noNodes_; node++) {
            sum += calcProbability(net, node);
        }
        if (Math.abs(sum - 1.0) > 0.0005) {
            System.err.println("Object model calculated not correct. Currently probabilities add to "+sum);
            System.exit(-1);
        }
    }

    /** Read components and weights from JSON */
    public void readObjectModelOptions(JSONArray componentList) {
        weights_= new double[componentList.size()];
        for (int i = 0; i< componentList.size(); i++) {
            JSONObject comp = (JSONObject) componentList.get(i);

            /** Gets object model element class from string. Bit of a mouthful */
            ObjectModelComponent omc = null;
            String omcClass = "feta.objectmodels."+comp.get("ComponentName");
            Class <?extends ObjectModelComponent> component = null;

            try {
                component= Class.forName(omcClass).asSubclass(ObjectModelComponent.class);
                Constructor<?> c = component.getConstructor();
                omc = (ObjectModelComponent)c.newInstance();
            } catch (ClassNotFoundException e){
                System.err.println("Object Model Component "+omcClass+" not found.");
                System.exit(-1);
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

            Double weight = (Double) comp.get("Weight");
            if (weight!=null) {
                weights_[i]=weight;
            }

            omc.parseJSON(comp);
            components_.add(omc);
        }
    }

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MixedModel obm = (MixedModel) o;
        for (int i = 0; i < components_.size(); i++) {
            if (weights_[i]!=obm.weights_[i] ) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return weights_.hashCode();
    }

    public String toString() {
        String str="";
        for (int i = 0; i < components_.size(); i++) {
            str+=weights_[i]+" "+components_.get(i)+"\n";
        }
        return str;
    }

    public MixedModel copy() {
        MixedModel obm = new MixedModel();
        obm.components_=components_;
        return obm;
    }

    /** For assigning weights at runtime */
    public void setWeights(double[] weights) {
        weights_=weights;
    }

}
