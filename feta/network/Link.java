package feta.network;

public abstract class Link {

    public String sourceNode_;
    public String destNode_;
    public long time_;

    public abstract Link reverse();

}
