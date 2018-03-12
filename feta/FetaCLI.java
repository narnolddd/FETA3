package feta;

import java.io*;

public class FetaCLI {

    /** Main entry point for user interface. Takes as argument a JSON file. */

    public FetaCLI() {

        public static void main( String[] args) {

            if(args.length != 1){
                System.err.println("Command line must specify JSON file to read and nothing else");
                System.exit(-1);
            }


        }
    }
}