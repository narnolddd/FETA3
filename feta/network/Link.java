package feta.network;

public interface Link {

    /** Get source node of link */
    public int getSourceNode();

    /** Get destination node of link */
    public int getDestNode();

    /** Get source node name */
    public String getSourceName();

    /** Get destination node name */
    public String getDestName();

}
