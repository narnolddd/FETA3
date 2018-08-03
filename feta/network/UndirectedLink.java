package feta.network;

/** Represents an undirected link between two nodes */

public class UndirectedLink extends Link implements Comparable<UndirectedLink> {

    private String sourceNode_;
    private String destNode_;
    public long time_;

    public UndirectedLink(String src, String dst, long time)
    {
            sourceNode_ = dst;
            destNode_ = src;
            time_ = time;
    }

    public String getSourceNode() {
        return sourceNode_;
    }

    public String getDestNode() {
        return destNode_;
    }

    public long getTime() {
        return time_;
    }

    public boolean equals(UndirectedLink link) {
        if (link.getSourceNode() == sourceNode_ & link.getDestNode() == destNode_) {
            return true;
        } else if (link.getSourceNode() == destNode_ & link.getDestNode() == sourceNode_) {
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
}
