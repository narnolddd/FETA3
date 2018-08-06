package feta.readnet;

import feta.network.Link;

public interface LinkBuilder {

    Link build(String n1, String n2, long time);

}
