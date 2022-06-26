package feta.network;


/** Represents an undirected link between two nodes */

public class UndirectedLink extends Link implements Comparable<UndirectedLink> {

    public UndirectedLink(String src, String dst, long time)
    {
            sourceNode_ = src;
            destNode_ = dst;
            time_ = time;
            sourceNodeType_= null;
            destNodeType_= null;
    }
    
    public UndirectedLink(String src, String dst, long time, String srcType, String dstType)
    {
            sourceNode_ = src;
            destNode_ = dst;
            time_ = time;
            sourceNodeType_= srcType;
            destNodeType_= dstType;
    }

    public boolean equals(UndirectedLink link) {
        if (link.sourceNode_.equals(sourceNode_) & link.destNode_ .equals(destNode_)) {
            return true;
        } else if (link.sourceNode_.equals(destNode_) & link.destNode_.equals(sourceNode_)) {
            return true;
        }
        return false;
    }

    public int compareTo(UndirectedLink link) {
        if (time_ < link.time_)
            return -1;
        if (time_ > link.time_)
            return 1;
        return 0;
    }

    public Link reverse() {
        return new UndirectedLink(destNode_, sourceNode_, time_);
    }

}
