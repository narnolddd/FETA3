package feta;
import java.io.*;
import java.util.*;

/** Class representing a node with various properties. */
public class Node {
    public String name_;
    public int birthTime_ = -1;
    final static String growName_="Grown-Node-";
    public int order_;
    public int inDegree_;
    public int outDegree_;
    public ArrayList<Node> inLinks_;
    public ArrayList<Integer> inLinksTimes_;
    public ArrayList<Node> outLinks_;
    public ArrayList<Integer> outLinksTimes_;

    public Node(String nodename) {
        if (nodename != null) {
            name_ = nodename;
        } else {
            name_ = growName_+Integer.toString(order_);
        }
        inLinks_ = new ArrayList<Node>();
        outLinks_ = new ArrayList<Node>();
        inLinksTimes_ = new ArrayList<Integer>();
        outLinksTimes_ = new ArrayList<Integer>();
        inDegree_ = 0;
        outDegree_ = 0;
    }

    public void birth(int time)
    {
        birthTime_= time;
    }
    // Add link to various structures
    public void addOutLink(Node node2, int time) {
        outLinks_.add(node2);
        outLinksTimes_.add(time);
        outDegree_++;
    }

    public void addInLink(Node node2, int time) {
        inLinks_.add(node2);
        inLinksTimes_.add(time);
        inDegree_++;
    }

    public boolean IsInLink(Node node) {
        for(int i = 0; i< inLinks_.size(); i++)
        {
            if(inLinks_.get(i) == node)
                return true;
        }
        return false;
    }

    public boolean IsOutLink(Node node) {
        for(int i = 0; i< outLinks_.size(); i++)
        {
            if(outLinks_.get(i) == node)
                return true;
        }
        return false;
    }

}
