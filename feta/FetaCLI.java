package feta;

import feta.readnet.ReadNet;
import feta.readnet.ReadNetNNT;
import feta.readnet.UndirectedLinkBuilder;

import java.io.*;

public class FetaCLI {

    /** Main entry point for user interface. Takes as argument a JSON file. */

    public FetaCLI() {

    }

    public static void main( String[] args) {

//        if (args.length != 1) {
//            System.err.println("Command line must specify a JSON file to read and nothing else");
//            System.exit(-1);
//        }

        FetaModel fm = new FetaModel();

        ReadNet rn = new ReadNetNNT();
        rn.setLinkBuilder(new UndirectedLinkBuilder());
        rn.setFileInput("data/BAgrown.dat");
        rn.readNetwork();

    }

}