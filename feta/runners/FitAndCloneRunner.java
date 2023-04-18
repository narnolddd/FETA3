package feta.runners;

import feta.actions.FitMixedModel;
import feta.network.DirectedNetwork;
import feta.network.Network;
import feta.objectmodels.FullObjectModel;
import feta.objectmodels.MixedModel;
import feta.objectmodels.components.*;
import feta.operations.MixedOperations;
import feta.operations.OperationModel;
import feta.readnet.ReadNet;
import feta.readnet.ReadNetCSV;

import java.util.ArrayList;

public class FitAndCloneRunner {

    public static void main(String[] args) {
        // Read in network to be fitted
        ReadNet reader = new ReadNetCSV("test/typeTest.dat"," ",true,0,1,2,3,4);
        double[] degreePowerParms = new double[] {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2};

        double bestLikelihood = 0.0;
        FullObjectModel bestObm = null;
        for (double d: degreePowerParms) {
            Network net = new DirectedNetwork(reader, true);

            // Specify object model to be tested
            ArrayList<ObjectModelComponent> components = new ArrayList<>() {
                {
                    add(new RandomAttachment());
                    add(new DegreePower(d, DegreePower.Direction.IN));
                }
            };

            MixedModel model = new MixedModel(components);

            // Fit mixed model
            FitMixedModel fit = new FitMixedModel(net, model, 100, 10, false);
            fit.execute();

            // Get out parsed operation model and fitted object model
            OperationModel om = new MixedOperations(fit.getParsedOperations());
            FullObjectModel obm = fit.getFittedModel();
            double currentLikelihood = fit.getBestLikelihood();
            if (currentLikelihood > bestLikelihood) {
                bestLikelihood = currentLikelihood;
                bestObm = obm;
            }
        }
        System.out.println(bestObm);
//        // Grow network from this operation and object model and write to csv
//        Network grownNet = new DirectedNetwork(reader, true);
//        Grow grow = new Grow(grownNet, bestObm, om, 10L, 100L);
//        grow.execute();
//        WriteNet writer = new WriteNetCSV(grownNet, " ", "testOutput.csv");
//        writer.write();
    }
}
