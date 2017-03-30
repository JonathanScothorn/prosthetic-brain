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
	
	public DepTreeNode getRoot(){
		return root;
	}
	
	// The input child array lists the number of children of each node, used to construct the tree
	// The input probabilites array lists the probability of each node having a value of 0, starting with the head node
	public ArrayList<DepTreeNode> generateTree(int[] children, double[] probabilitiesGiven0, double[] probabilitiesGiven1, double initialProbability){
		
		if(children.length != probabilitiesGiven0.length + 1){
			System.out.println("Illegal input size to generate tree.  Must have one probability per node and one child value per node except the head.");
			return null;
		}
		
		ArrayList<DepTreeNode> nodes = new ArrayList<DepTreeNode>();
		
		root = new DepTreeNode(initialProbability);
		nodes.add(root);
		
		for(int i = 0; i < children.length; i++){
			
			// iterate down the tree, in order
			
			
			for(int j = 0; j < children[i]; j++){
				// create new children nodes
				DepTreeNode node = new DepTreeNode(nodes.get(i), probabilitiesGiven0[nodes.size()-1], probabilitiesGiven1[nodes.size()-1], nodes.size());
				nodes.get(i).addChild(node);
				nodes.add(node);
			}
			
		}
		
		return nodes;
		
	}
	
	public Sample generateSample(ArrayList<DepTreeNode> dependenceTree, ArrayList<Boolean> features){
		
		
		
		for(DepTreeNode node: dependenceTree){
			
			double randomNumber = Math.random();
			
			if(randomNumber >= node.getProbability0Given0()){
				features.add(true);
			}
		}
		
		
		
		
		return new Sample(new ArrayList<Boolean>());
	}

	public static void main(String[] args) {
		
		int[] treeGeneration = {2, 1, 3, 0, 0, 0, 1, 2, 0, 0};
		double[] treeProbabilitiesGiven0 = {0.2, 0.7, 0.9, 0.5, 0.4, 0.2, 0.1, 0.5, 0.9};
		double[] treeProbabilitiesGiven1 = {0.8, 0.7, 0.3, 0.5, 0.9, 0.7, 0.2, 0.1, 0.1};
		double initialProbability = 0.6;

		//int[] treeGeneration = {2,2,0,0,0};
		//double[] treeProbabilities = {0.1,0.2,0.3,0.4,0.5};
		
		Classifier c = new Classifier();
		
		ArrayList<DepTreeNode> dependenceTree = c.generateTree(treeGeneration, treeProbabilitiesGiven0, treeProbabilitiesGiven1, initialProbability);
		
		System.out.println("Generated the following dependency tree: \n"+dependenceTree.get(0).toString());
		

		Sample s = c.generateSample(dependenceTree, new ArrayList<Boolean>());
		
		System.out.println(s.toString());
		
	}

}
