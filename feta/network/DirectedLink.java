package feta.network;

public class DirectedLink extends Link implements Comparable<DirectedLink> {

    public DirectedLink(String src, String dst, long time) {

        sourceNode_ = src;
        destNode_= dst;
        time_= time;
    }

    public boolean equals(DirectedLink link) {
        if (link.sourceNode_.equals(sourceNode_) & link.destNode_.equals(destNode_)) {
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
