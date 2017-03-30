package assignment3;

import java.util.ArrayList;

public class DepTreeNode {

	private DepTreeNode parent;
	private int depth;
	private ArrayList<DepTreeNode> children;
	private double probability0Given0; // probability that the dimension will be 0 if the parent is 0.  Must be between 0 and 1
	private double probability0Given1; // probability that the dimension will be 0 if the parent is 1.  Must be between 0 and 1
	private int nodeNumber;
	
	// constructor exclusively for root node
	public DepTreeNode(double probability){
		this(null, probability, 0, 0);
	}
	
	public DepTreeNode(DepTreeNode parent, double probability0Given0, double probability0Given1, int nodeNumber){
		
		this.nodeNumber = nodeNumber;
		this.parent = parent;
		this.probability0Given0 = probability0Given0;
		this.probability0Given1 = probability0Given1;
		children = new ArrayList<DepTreeNode>();
		
		depth = 0;
		DepTreeNode tempParent = parent;
		
		while(tempParent != null){
			depth++;
			tempParent = tempParent.getParent();
		}
		
	}
	
	public void addChild(DepTreeNode child){
		
		children.add(child);
		
	}
	
	public ArrayList<DepTreeNode> getChildren(){
		return children;
	}
	
	public DepTreeNode getParent() {
		return parent;
	}
	
	public double getProbability0Given0(){
		return probability0Given0;
	}
	
	public double getProbability1Given0(){
		return 1-probability0Given0;
	}
	
	public double getProbability0Given1(){
		return probability0Given1;
	}
	
	public double getProbability1Given1(){
		return 1-probability0Given1;
	}
	
	@Override
	public String toString(){
		String output = "";
		
		for(int i=0; i<depth; i++){
			output+=" ";
		}
		
		output += "Node "+nodeNumber+" with depth "+depth+", probability given 0 of "+probability0Given0+", and probability given 1 of "+probability0Given1+"\n";
		
		for(DepTreeNode node: children){
			output += node.toString();
		}
		
		
		
		
		return output;
	}
	
}
