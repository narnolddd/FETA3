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

public class FitAndReturnOperations {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Please provide the following as args: input file name, operations file name and object model file name");
            System.exit(-1);
        }

        String netFile = args[0];
        String opFile = args[1];
        String omFile = args[2];

        // Read in network to be fitted
        ReadNet reader = new ReadNetCSV(netFile," ",true,0,1,2,3,4);
        double[] degreePowerParms = new double[] {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2};

        double bestLikelihood = 0.0;
        FitMixedModel bestFit = null;
        for (double d: degreePowerParms) {
            Network net = new DirectedNetwork(reader, true);

            // Specify object model to be tested
            ArrayList<ObjectModelComponent> components = new ArrayList<>() {
                {
                    add(new RandomAttachment());
                    add(new DegreePower(d, DegreePower.Direction.IN));
                    add(new TriangleClosure());
                }
            };

            MixedModel model = new MixedModel(components);

            // Fit mixed model
            FitMixedModel fit = new FitMixedModel(net, model, 100, 1, false);
            fit.execute();

            double currentLikelihood = fit.getBestLikelihood();
            if (currentLikelihood > bestLikelihood) {
                bestLikelihood = currentLikelihood;
                bestFit = fit;
            }
        }
        bestFit.writeObjectModelToFile(omFile);
        bestFit.writeOperationsToFile(opFile, true);
//        // Grow network from this operation and object model and write to csv
//        Network grownNet = new DirectedNetwork(reader, true);
//        Grow grow = new Grow(grownNet, bestObm, om, 10L, 100L);
//        grow.execute();
//        WriteNet writer = new WriteNetCSV(grownNet, " ", "testOutput.csv");
//        writer.write();
    }
}
