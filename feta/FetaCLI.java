package feta;

import java.io.*;

public class FetaCLI {

    /** Main entry point for user interface. Takes as argument a JSON file. */

    public FetaCLI() {

    }

    public static void main( String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Exactly one argument expected as JSON file");
            System.exit(-1);
        }

        System.out.println("Doing the thing");

        FetaModel fm = new FetaModel();
        fm.readConfig(args[0]);
        fm.executeAction();

    }

}