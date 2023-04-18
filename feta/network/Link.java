package feta.network;

public class Link implements Comparable<Link>{

    public String sourceNode_;
    public String destNode_;
    public String sourceNodeType_;
    public String destNodeType_; 
    public long time_;

    public Link(String src, String dst, String srcType, String dstType, long time)
    /** Creates a directed link with types as well*/
    {

        sourceNode_ = src;
        destNode_= dst;
        sourceNodeType_= srcType;
        destNodeType_= dstType;
        time_= time;
    }

    public Link(String src, String dst, long time) {

        sourceNode_ = src;
        destNode_= dst;
        sourceNodeType_= null;
        destNodeType_= null;
        time_= time;
    }

    public int compareTo(Link link) {
        if (time_ < link.time_)
            return -1;
        if (time_ > link.time_)
            return 1;
        return 0;
    }

}
