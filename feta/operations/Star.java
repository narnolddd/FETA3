package feta.operations;

import feta.Methods;
import feta.network.Network;
import feta.network.NodeTypes;
import feta.network.UndirectedNetwork;
import feta.objectmodels.MixedModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Star extends Operation{

    private int centreNode_;
    private int[] leafNodes_;
    private String centreNodeName_;
    private String[] leafNodeNames_;
    private final boolean internal_; // Remove this variable ?    
    private int noExisting_;
    private final int noLeaves_;  // Remove this variable ?
    private int noExternalLeaves_;
    private int noInternalLeaves_;
    private String centreType_;
    private String leafType_;

    public Star(boolean sourceInternal, int noInternal, int noExternal, String centreType, String leafType) {
        noInternalLeaves_= noInternal;
        noExternalLeaves_= noExternal;
        internal_ = true; // This is not needed
        noLeaves_ = noInternal+noExternal;
        if (centreType != null && centreType.length() > 0) {
            centreType_= centreType;
        } else {
            centreType_= null;
        }
        if (leafType != null && leafType.length() > 0) {
            leafType_= leafType;
        } else {
            leafType_= null;
        }
        
    }

    public Star(int noLeaves, boolean internal) {
        internal_=internal;
        if (internal) {
            noInternalLeaves_ = noLeaves;
            noExternalLeaves_= 0;
        } else {
            noExternalLeaves_= noLeaves;
            noInternalLeaves_= 0;
        }
        noLeaves_=noLeaves;
        centreType_= null;
        leafType_= null;
    }
    
    public Star (int noLeaves, String centreType, String leafType, boolean internal)
    /** centreType and leafType will be null for untyped networks*/
    {
	    internal_=internal;
        if (internal) {
            noInternalLeaves_ = noLeaves;
            noExternalLeaves_= 0;
        } else {
            noExternalLeaves_= noLeaves;
            noInternalLeaves_= 0;
        }
        noLeaves_=noLeaves;
        centreType_= centreType;
        leafType_= leafType;	
	}

    public void bufferLinks(Network net) {
        for (String leaf: leafNodeNames_) {
            net.addNewLink(centreNodeName_,leaf, centreType_, leafType_, getTime());
        }
    }

    public void chooseNodes(Network net, MixedModel obm) {
        pickCentreNode(net,obm);
        pickLeafNodes(net,obm);
    }

    public void setNodeChoices(boolean orderedData) {
        nodeChoices_= new ArrayList<int[]>();
//        if (internal_) {
//            nodeChoices_.add(new int[] {centreNode_});
//        }
        if (orderedData) {
            for (int node: leafNodes_) {
                nodeChoices_.add(new int[] {node});
            }
        } else {
            nodeChoices_.add(leafNodes_);
        }
        filterNodeChoices();
        nodeOrders_ = generateOrdersFromOperation();
    }

    public void setCentreNode(String centre) {
        centreNodeName_=centre;
    }

    public void setLeaves(String [] leaves) {
        leafNodeNames_=leaves;
    }

    public int getNoExisting() {
        return noExisting_;
    }

    public void setNoExisting(int noExisting) {
        noExisting_=noExisting;
    }

    public void updateLikelihoods(MixedModel obm, Network net) {
        if (!internal_) {
            if (noExisting_==0)
                return;
            HashSet<Integer> availableNodes;
            if (centreType_!=null) {
                availableNodes = NodeTypes.getNodesOfType(leafType_);
            } else {
                availableNodes = net.getNodeListCopy();
            }
            if (availableNodes.isEmpty()) {
                System.err.println("No available target nodes to choose from! Operation "+this);
                System.exit(-1);
            }
            availableNodes.remove(centreNode_);
            obm.updateLikelihoods(net, availableNodes, nodeOrders_);
        } else {
            HashSet<Integer> availableSourceNodes;
            HashSet<Integer> availableTargetNodes;
            if (centreType_!=null) {
                availableSourceNodes = NodeTypes.getNodesOfType(centreType_);
                availableTargetNodes = NodeTypes.getNodesOfType(leafType_);
            } else {
                availableSourceNodes = net.getNodeListCopy();
                availableTargetNodes = net.getNodeListCopy();
            }

            if (availableSourceNodes.isEmpty()) {
                System.err.println("No available source nodes to choose from! Operation "+this);
                System.exit(-1);
            }

            if (availableTargetNodes.isEmpty()) {
                System.err.println("No available target nodes to choose from! Operation "+this);
                System.exit(-1);
            }

            ArrayList<int[]> sourceNodeChoice = new ArrayList<>();
            sourceNodeChoice.add(new int[] {centreNode_});
            obm.updateLikelihoods(net, availableSourceNodes, sourceNodeChoice);

            availableTargetNodes.remove(centreNode_);
            for (int node: net.getOutLinks(centreNode_)) {
                availableTargetNodes.remove(node);
            }
            obm.updateLikelihoods(net,availableTargetNodes,nodeOrders_);
        }
    }

    public void pickCentreNode(Network net, MixedModel obm) {
        if (internal_) {
            HashSet<Integer> availableNodes;
            if (centreType_ != null) {
                availableNodes = NodeTypes.getNodesOfType(centreType_);
            } else {
                availableNodes = net.getNodeListCopy();
            }
            centreNode_ = obm.nodeDrawWithoutReplacement(net, availableNodes, -1);
            centreNodeName_=net.nodeNoToName(centreNode_);
        }
        else {
            centreNodeName_ = net.generateNodeName(centreType_);
            centreNode_=net.nodeNameToNo(centreNodeName_);
        }
    }

    public void pickLeafNodes(Network net, MixedModel obm) {
        HashSet<Integer> availableNodes;
        leafNodes_ = new int[noLeaves_];

        // Add new nodes according to number of external leaves
        int noNew = noLeaves_ - noExisting_;
        for (int i = 0; i < noNew; i++) {
            String newName = net.generateNodeName(leafType_);
            leafNodes_[i]=net.nodeNameToNo(newName);
        }

        // Check which nodes are in the sample space for the existing nodes
        if (leafType_ != null) {
            availableNodes = NodeTypes.getNodesOfType(leafType_);
        } else {
            availableNodes = net.getNodeListCopy();
        }
        availableNodes.remove(centreNode_);

        // Internal or external star.
        int[] internalLeaves;
        if (internal_) {
            for (int node: net.getOutLinks(centreNode_)) {
                availableNodes.remove(node);
            }
            internalLeaves = obm.drawMultipleNodesWithoutReplacement(net,noExisting_,availableNodes);
        } else {
            internalLeaves=obm.drawMultipleNodesWithoutReplacement(net,noExisting_,availableNodes);
        }

        if (noExisting_ >= 0) System.arraycopy(internalLeaves, 0, leafNodes_, noNew, noExisting_);
        nodesToNames(net);
    }

    public void nodesToNames(Network net) {
        leafNodeNames_= new String[leafNodes_.length];
        for (int i = 0; i < leafNodes_.length; i++) {
            leafNodeNames_[i] = net.nodeNoToName(leafNodes_[i]);
        }
    }

    public void namesToNodes(Network net) {
        if (!net.newNode(centreNodeName_)) {
            centreNode_=net.nodeNameToNo(centreNodeName_);
        } else {
            centreNode_=-1;
        }
        leafNodes_= new int[noLeaves_];
        for (int i = 0; i < noLeaves_; i++) {
            String node = leafNodeNames_[i];
            if (net.newNode(node)) {
                leafNodes_[i] = -1;
            }
            else {
                leafNodes_[i] = net.nodeNameToNo(leafNodeNames_[i]);
            }
        }
    }

    public String toString() {
        StringBuilder str = new StringBuilder(getTime() + " STAR ");
        if (isCensored()) {
            str.append("ANON");
        } else {
            str.append(centreNodeName_);
        }
        if (centreType_ != null) {
			str.append(" TYPES ").append(centreType_).append(" ").append(leafType_);
		}
		str.append(" LEAVES ");
		if (leafNodeNames_ == null) {
			System.out.println("leafNodeNames should not be null");
		}
        for (String leaf: leafNodeNames_) {
            if (isCensored()) {
				str.append(" ANON ");
			} else {
				str.append(leaf).append(" ");
			}
        }
        if (internal_){
            str.append("INTERNAL");
        } else {
            str.append("EXTERNAL");
        }
        str.append(" ").append(noExisting_);
        return str.toString();
    }
}
