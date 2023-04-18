package feta.runners;

import feta.actions.Measure;
import feta.actions.SimpleAction;
import feta.network.Network;
import feta.network.UndirectedNetwork;
import feta.network.measurements.Measurement;
import feta.network.measurements.NoLinks;
import feta.network.measurements.NoNodes;
import feta.network.measurements.NoNodesByType;
import feta.readnet.ReadNet;
import feta.readnet.ReadNetCSV;

import java.util.ArrayList;

public class MeasurementRunner {

    public static void main( String[] args) {
        ReadNet reader = new ReadNetCSV("data/cit-HepPh-new.txt"," ",false,0,1,2);
        Network net = new UndirectedNetwork(reader, true);
        ArrayList<Measurement> stats = new ArrayList<Measurement>() {
            {
                add(new NoNodes());
                add(new NoLinks());
                add(new NoNodesByType());
            }
        };
        SimpleAction action = new Measure(net, stats, "test/test_measure_runner.csv",698889600,1015956000,2592000);
        action.execute();
    }
}
