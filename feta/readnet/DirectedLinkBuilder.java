package feta.readnet;

import feta.network.Link;
import feta.network.DirectedLink;

public class DirectedLinkBuilder implements LinkBuilder {

    public Link build(String n1, String n2, long time) {
        return new DirectedLink(n1, n2, time);
    }

}
