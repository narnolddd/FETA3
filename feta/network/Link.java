package feta.network;

public abstract class Link {

    /** Get source node of link */
    public abstract String getSourceNode();

    /** Get destination node of link */
    public abstract String getDestNode();

    /** Get time added -- long since could be UNIX timestamp*/
    public abstract long getTime();

}
