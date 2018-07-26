package feta.network;

public class DirectedLink implements Link {

    private int sourceNode_;
    private int destNode_;

    public DirectedLink(int src, int dst) {

        sourceNode_ = src;
        destNode_= dst;
    }

    public int getSourceNode() {
        return sourceNode_;
    }

    public int getDestNode() {
        return destNode_;
    }

    public String getSourceName() {
        return null;
    }

    public String getDestName() {
        return null;
    }

    public boolean equals(DirectedLink link) {
        if (link.getSourceNode() == sourceNode_ & link.getDestNode() == destNode_) {
            return true;
        }
        return false;
    }
}
