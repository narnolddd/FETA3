package feta.readnet;

import feta.network.Link;

public class LinkBuilder {

    Link build(String n1, String n2, long time) {
        return new Link(n1,n2,time);    }
    Link build(String n1, String n2, long time, String n1Type, String n2Type) {
        return new Link(n1, n2, n1Type, n2Type, time);
    }

}
