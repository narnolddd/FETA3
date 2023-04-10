package feta.network;

import java.lang.String;
import java.io.*;
import java.util.*;

public class NodeTypes {
	private static NodeTypes nt_ =new NodeTypes();
	private HashMap <Integer,String> nodeTypes_;
	// Map returns the type (as string) of given node (integer)
	private HashMap <String, HashSet<Integer>> nodesByType_;
	// Map which turns a type name into a list of integers of that type

	public NodeTypes() {
		nodeTypes_ = new HashMap <Integer,String>();
		nodesByType_ = new HashMap <String, HashSet<Integer>>();
	}

	/** set the type of a given node to be a string */
	public static void setNodeType(int nodeNo, String nodeType) 
	{
		/* Check if this node is added*/
		String nt= nt_.nodeTypes_.get(nodeNo);
		if (nt != null) {
			if (nt.equals(nodeType)) 
				return;
			System.err.println("Node "+nodeNo+" is set to type "+nodeType+" and "+nt);
			System.exit(-1);
		}

		/* If this is a new type add it */
		nt_.nodesByType_.computeIfAbsent(nodeType, k -> new HashSet<>());
		nt_.nodeTypes_.put(nodeNo,nodeType);
		nt_.nodesByType_.get(nodeType).add(nodeNo);
	}

	/** Return numbers of all nodes with a given type*/
	public static HashSet<Integer> getNodesOfType (String type)
	{
        HashSet<Integer> nodes= nt_.nodesByType_.get(type);
        if (nodes == null) {
            //System.out.println("Trying to find "+type);
            //for (String s: nt_.nodesByType_.keySet()){
             //   System.out.println("Type "+s+" exists");
            //}
            return null;
		}
        return new HashSet<Integer>(nodes);
	}

	/** Get current list of types */
	public static Set<String> getTypes() {
		return nt_.nodesByType_.keySet();
	}
	
	/**Get the type of a given node*/
	public static String getNodeType(int nodeNo) {
		return nt_.nodeTypes_.get(nodeNo);
	}
	
	public static boolean sameTypes(Link l, String sourceType, String destType)
	/** Check link has the same types as two types*/
	{
		return sameTypes(l.sourceNodeType_,l.destNodeType_, sourceType, destType);
	}
	
	public static boolean SameTypes(Link l1, Link l2)
	/** Check two links have same type*/
	{
		return sameTypes(l1.sourceNodeType_,l1.destNodeType_, 
			l2.sourceNodeType_,l2.destNodeType_);
	}
	
	public static boolean sameTypes(String sourceType1, String destType1, 
		String sourceType2, String destType2)
	/** Check two sets of source/dest types are the same*/
	{
		if (sourceType1 == null) {
			if (sourceType2 != null) 
				return false;
		} else {
			if (!sourceType1.equals(sourceType2))
				return false;
		}
		if (destType1 == null) {
			if (destType2 != null) 
				return false;
		} else {
			if (!destType1.equals(destType2))
				return false;
		}
		return true;
	}
}
