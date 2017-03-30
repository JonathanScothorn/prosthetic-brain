package assignment3;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Classifier {
	
	private DepTreeNode root;
	
	public Classifier(){
		
	}
	
	public void setRoot(DepTreeNode root){
		this.root = root;
	}
	
	// The input child array lists the number of children of each node, used to construct the tree
	// The input probabilites array lists the probability of each node having a value of 0, starting with the head node
	public ArrayList<DepTreeNode> generateTree(int[] children, double[] probabilities){
		
		if(children.length != probabilities.length){
			System.out.println("Illegal input size to generate tree.  Must have one probability per node and one child value per node except the head.");
			return null;
		}
		
		ArrayList<DepTreeNode> nodes = new ArrayList<DepTreeNode>();
		
		root = new DepTreeNode(null, probabilities[0]);
		nodes.add(root);
		
		for(int i = 0; i < children.length; i++){
			
			// iterate down the tree, in order
			
			
			for(int j = 0; j < children[i]; j++){
				// create new children nodes
				DepTreeNode node = new DepTreeNode(nodes.get(i), probabilities[j+i+1]);
				nodes.get(i).addChild(node);
				nodes.add(node);
			}
			
		}
		
		return nodes;
		
	}

	public static void main(String[] args) {
		
		int[] treeGeneration = {2, 1, 3, 0, 0, 0, 1, 2, 0, 0};
		double[] treeProbabilities = {0.6, 0.2, 0.7, 0.9, 0.5, 0.4, 0.2, 0.1, 0.5, 0.9};

		//int[] treeGeneration = {2,2,0,0,0};
		//double[] treeProbabilities = {0.1,0.2,0.3,0.4,0.5};
		
		Classifier c = new Classifier();
		
		ArrayList<DepTreeNode> root = c.generateTree(treeGeneration, treeProbabilities);
		
		System.out.println(root.get(0).toString());
	}

}
