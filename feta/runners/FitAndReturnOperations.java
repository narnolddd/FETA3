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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
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
        String modelString = null;
        FitMixedModel fit = null;

        for (double d: degreePowerParms) {
            Network net = new DirectedNetwork(reader, true);

            // Specify object model to be tested
            ArrayList<ObjectModelComponent> components = new ArrayList<>() {
                {
                    add(new RandomAttachment());
                    add(new DegreePower(d, DegreePower.Direction.BOTH));
                    add(new TriangleClosure());
                }
            };

            MixedModel model = new MixedModel(components);

            // Fit mixed model
            fit = new FitMixedModel(net, model, 100, 0, false);
            fit.execute();

            double currentLikelihood = fit.getBestLikelihood();
            if (currentLikelihood >= bestLikelihood) {
                bestLikelihood = currentLikelihood;
                modelString = fit.getObjectModelString();
            }
        }

        // Write out operation model
        fit.writeOperationsToFile(opFile, true);

        // Write out object model
        try (PrintStream out = new PrintStream(new FileOutputStream(omFile))) {
            out.print(modelString);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
