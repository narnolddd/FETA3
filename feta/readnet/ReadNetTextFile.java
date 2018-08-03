package feta.readnet;

import feta.network.Link;
import feta.readnet.ReadNet;

import java.io.*;
import java.util.ArrayList;

public class ReadNetTextFile extends ReadNet{

    public ReadNet rn;
    FileInputStream fstream = null;
    DataInputStream dstream = null;
    BufferedReader br = null;

    public static final int NODE_NODE = 2;
    public static final int NODE_NODE_TIME = 3;

    private int type_;
    private String sep_;
    private boolean directed_;

    public ArrayList<Link> linkList_;

    public ReadNetTextFile(String type, String sep, boolean directed) {
        setType(type);
        setSep(sep);
        directed_= directed;
    }

    public void setType(String type) {
        if (type == "NNT"){
            type_= NODE_NODE_TIME;
        } else if (type == "NN") {
            type_ = NODE_NODE;
        } else {
            System.err.println("Unknown network file type " + type);
            System.exit(-1);
        }
    }

    public void setSep(String sep) {
        if (sep == "," || sep == "\\s+") {
            sep_= sep;
        } else {
            System.err.println("Unknown separator "+sep);
            System.exit(-1);
        }
    }

    public void readNetwork(String fname) {}

    public void readNetworkNNUn(String fname){
        ArrayList<Link> links = new ArrayList<Link>()
        int linkno = 1;
        try {
            FileInputStream fstream = new FileInputStream(fname);
            DataInputStream dstream = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(dstream));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0)
                    continue;
                String[] parts = line.split(sep_);
                if (parts.length == 0)
                    continue;
                if (parts.length != 2) {
                    System.err.println("Expected "+type_+" entries on line "+linkno+" but got "+parts.length);
                    System.exit(-1);
                }
                if(type_ == NODE_NODE) {
                    String
                }
            }
        }
    }

    public void parseNN

}
