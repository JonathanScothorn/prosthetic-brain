package assignment3;

import java.util.ArrayList;

public class DepTreeNode {

	private DepTreeNode parent;
	private int depth;
	private ArrayList<DepTreeNode> children;
	private double probability0; // probability that the dimension will be 0.  Must be between 0 and 1
	
	public DepTreeNode(DepTreeNode parent, double probability0){
		
		this.parent = parent;
		this.probability0 = probability0;
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
	
	public double probability0(){
		return probability0;
	}
	
	public double probability1(){
		return 1-probability0;
	}
	
	@Override
	public String toString(){
		String output = "Node with depth "+depth+" and probability "+probability0;
		
		if(children.size() > 0){
			output += "children: \n ";
			for(DepTreeNode node: children){
				for(int i=1; i<depth; i++){
					output+=" ";
				}
				output += node.toString()+"\n";
			}
		}
		
		
		
		return output;
	}
	
}
