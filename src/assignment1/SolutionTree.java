package assignment1;

import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeSet;

public class SolutionTree {
	
	private int dataStructure;//0 for stack, 1 for queue, 2 for map
	private Stack<BridgeNode> stack;
	private LinkedList<BridgeNode> queue;
	private TreeSet<BridgeNode> tree;
	
	public SolutionTree(int dataStructure){
		
		this.dataStructure = dataStructure;
		
		if(dataStructure == 0){
			stack = new Stack<BridgeNode>();
			queue = null;
			tree = null;
		} else if(dataStructure == 1){
			queue = new LinkedList<BridgeNode>();
			stack = null;
			tree = null;
		} else if(dataStructure == 2){
			tree = new TreeSet<BridgeNode>(new BridgeNodeComparator());
			stack = null;
			queue = null;
		} else {
			System.out.println("Invalid data structure selector passed to BridgeTree constructor.");
		}
	}
	
	public void addNode(BridgeNode n){
		if(dataStructure==0){
			stack.push(n);
		} else if(dataStructure==1){
			queue.add(n);
		} else {
			tree.add(n);
		}
	}
	
	
	
	public BridgeNode getNode(){
		if(dataStructure==0){
			return stack.pop();
		} else if(dataStructure==1){
			return queue.removeFirst();
		} else {
			return tree.pollFirst();
		}
	}
	
	public BridgeNode showNode(){
		if(dataStructure==0){
			return stack.peek();
		} else if(dataStructure==1){
			return queue.peek();
		} else {
			return tree.first();
		}
	}
	
	public boolean isEmpty(){
		if(dataStructure==0){
			return stack.empty();
		} else if(dataStructure==1){
			if(queue.size()==0){
				return true;
			} else {
				return false;
			}
		} else {
			if(tree.size()==0){
				return true;
			} else {
				return false;
			}
		}
	}
	
	@Override
	public String toString(){
		String output = "";
		if(dataStructure==0){
			Stack<BridgeNode> temp = (Stack<BridgeNode>)stack.clone();
			while(!temp.empty()){
				output+=temp.pop().toString();
				output+="-";
			}
		} else if(dataStructure==1){
			LinkedList<BridgeNode> temp = (LinkedList<BridgeNode>)queue.clone();
			while(temp.size()!=0){
				output+=temp.removeFirst().toString();
				output+="-";
			}
		} else {
			for(BridgeNode n: tree){
				output+=n.toString();
				output+="-";
			}
		}
		return output;
	}

}
