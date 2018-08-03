package feta.readnet;

import feta.network.Link;

import java.io.*;
import java.util.ArrayList;

public abstract class ReadNet {

    ArrayList<Link> links_;

    public void readNetwork(String fname){
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
                Link link = parseLine(line);
                links.add()
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch ()
    }

    public abstract Link parseLine(String line);

}
