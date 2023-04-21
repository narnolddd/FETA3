package feta.runners;

import feta.actions.Measure;
import feta.actions.SimpleAction;
import feta.network.Network;
import feta.network.UndirectedNetwork;
import feta.network.measurements.*;
import feta.readnet.ReadNet;
import feta.readnet.ReadNetCSV;

import java.util.ArrayList;

public class MeasurementRunner {

    public static void main( String[] args) {
        String netInFile = args[0];
        String measurementOutFile = args[1];
        ReadNet reader = new ReadNetCSV(netInFile," ",false,0,1,2, 3, 4);
        Network net = new UndirectedNetwork(reader, true);
        ArrayList<Measurement> stats = new ArrayList<Measurement>() {
            {
                add(new NoNodes());
                add(new NoLinks());
                add(new NoNodesByType());
                add(new MaxDegree());
                add(new Singletons());
                add(new MeanSquaredDegree());
                add(new Assortativity());
                add(new TriangleCount());
                add(new Clustering());
            }
        };
        SimpleAction action = new Measure(net, stats, measurementOutFile,0,1);
        action.execute();
    }
}
