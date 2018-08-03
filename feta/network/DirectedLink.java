package feta.network;

public class DirectedLink extends Link implements Comparable<DirectedLink> {

    private String sourceNode_;
    private String destNode_;
    private long time_;

    public DirectedLink(String src, String dst, long time) {

        sourceNode_ = src;
        destNode_= dst;
        time_= time;
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

    public boolean equals(DirectedLink link) {
        if (link.getSourceNode() == sourceNode_ & link.getDestNode() == destNode_) {
            return true;
        }
        return false;
    }

    public int compareTo(DirectedLink link) {
        if (time_ < link.time_)
            return -1;
        if (time_ > link.time_)
            return 1;
        return 0;
    }
}
