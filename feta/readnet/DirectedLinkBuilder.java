package feta.readnet;

import feta.network.Link;
import feta.network.DirectedLink;

public class DirectedLinkBuilder implements LinkBuilder {
    @Override
    public Link build(String n1, String n2, long time, String n1Type, String n2Type) {
        return null;
    }

    public Link build(String n1, String n2, long time) {
        return new DirectedLink(n1, n2, time);
    }

}
