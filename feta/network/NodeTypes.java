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
	
	/** set the type of a given node to be a string */
	public static void setNodeType(int nodeNo, String nodeType) 
	{
		/** Check if this node is added*/
		String nt= nt_.nodeTypes_.get(nodeNo);
		if (nt != null) {
			if (nt.equals(nodeType)) 
				return;
			System.out.println("Node "+nodeNo+" is set to type "+nodeType+" and "+nt);
			System.exit(0);
		}
		
		/** If this is a new type add it */
		if (nt_.nodesByType_.get(nodeType) == null) {
			nt_.nodesByType_.put(nodeType, new HashSet<>());
		}
		nt_.nodeTypes_.put(nodeNo,nodeType);
		nt_.nodesByType_.get(nodeType).add(nodeNo);
	}
	
	public static HashSet<Integer> getNodesOfType (String type)
	/** Return numbers of all nodes with a given type*/
	{
		return nt_.nodesByType_.get(type);
	}
	
	/**Get the type of a given node*/
	public static String getNodeType(int nodeNo) {
		return nt_.nodeTypes_.get(nodeNo);
	}
	
}
