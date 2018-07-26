package feta.network;

/** Represents an undirected link between two nodes */

public class UndirectedLink implements Link {

    private int sourceNode_;
    private int destNode_;

    public UndirectedLink(int src, int dst)
    {
        // Ensures consistent definitions of link
        if (src <= dst) {
            sourceNode_ = src;
            destNode_= dst;
        } else {
            sourceNode_ = dst;
            destNode_ = src;
        }
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

    public boolean equals(UndirectedLink link) {
        if (link.getSourceNode() == sourceNode_ & link.getDestNode() == destNode_) {
            return true;
        }
        return false;
    }

}
