package feta.network;

import java.lang.String;
import java.io.*;
import java.util.*;

public class NodeTypes {
	private static NodeTypes nt_ =new NodeTypes();
	private ArrayList <Integer> nodeTypes_;
	private ArrayList <String> typeNames_;
	private HashMap <String, Integer> typeNumbers_;
	
	/** set the type of a given node to be a string */
	public static void setNodeType(int nodeNo, String nodeType) 
	{
		Integer typeNo= nt_.typeNumbers_.get(nodeType);
		/** If this is a new type add it*/
		if (typeNo == null) {
			typeNo= addNewType(nodeType);
		}
		nt_.nodeTypes_.set(nodeNo,typeNo);
	} 
	
	/** Add a new type of node called typeName and return the
	 * integer representing it*/
	public static Integer addNewType (String typeName) {
		nt_.typeNames_.add(typeName);
		return nt_.typeNames_.size()-1;
	}
	
	/**Get the number associated with the type of a given node*/
	public static int getNodeType(int nodeNo) {
		return nt_.nodeTypes_.get(nodeNo);
	}
	
}
