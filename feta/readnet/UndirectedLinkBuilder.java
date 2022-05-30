package feta.readnet;

import feta.network.Link;
import feta.network.UndirectedLink;

public class UndirectedLinkBuilder implements LinkBuilder {

    public Link build(String n1, String n2, long time) {
        return new UndirectedLink(n1, n2, time);
    }

    @Override
    public Link build(String n1, String n2, long time, String n1Type, String n2Type) {
        return null;
    }

}
