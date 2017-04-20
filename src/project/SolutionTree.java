package project;

import java.util.TreeSet;

public class SolutionTree {

	private TreeSet<Model> tree;
	
	public SolutionTree(){
		tree = new TreeSet<Model>(new ModelComparator());
	}
	
	public void addNode(Model m){
		tree.add(m);
	}
	
	public Model getNode(){
		return tree.pollFirst();
	}
	
	public Model peekNode(){
		return tree.first();
	}
	
	public boolean isEmpty(){
		if(tree.size() == 0){
			return true;
		}
		return false;
	}
	
	public boolean containsNode(Model model){
		for(Model m: tree){
			if(m.equals(model)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString(){
		String output = "";
		for(Model m: tree){
			output+=m.toString()+"\n";
		}
		return output;
	}
}
