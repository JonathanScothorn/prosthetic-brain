package assignment1;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

public class BridgeTree {
	
	private int dataStructure;//0 for stack, 1 for queue, 2 for map
	private Stack<BridgeNode> stack;
	private LinkedList<BridgeNode> queue;
	private HashMap<Integer,BridgeNode> map;
	
	public BridgeTree(int dataStructure){
		
		this.dataStructure = dataStructure;
		
		if(dataStructure == 0){
			stack = new Stack<BridgeNode>();
			queue = null;
			map = null;
		} else if(dataStructure == 1){
			queue = new LinkedList<BridgeNode>();
			stack = null;
			map = null;
		} else if(dataStructure == 2){
			map = new HashMap();
			stack = null;
			queue = null;
		} else {
			System.out.println("Invalid data structure selector passed to BridgeTree constructor.");
		}
	}
	
	public void addNode(BridgeNode n, int heuristic){
		if(dataStructure==0){
			stack.push(n);
		} else if(dataStructure==1){
			queue.add(n);
		} else {
			map.put(new Integer(heuristic), n);
		}
	}
	
	
	
	public BridgeNode getNode(){
		if(dataStructure==0){
			return stack.pop();
		} else if(dataStructure==1){
			return queue.removeFirst();
		} else {
			//sort the keyset as a treeset and return the node with the lowest key
			TreeSet<Integer> keys = new TreeSet<Integer>(map.keySet()); 
			BridgeNode output = map.get(keys.first());
			map.remove(keys.first());
			return output;
		}
	}
	
	public BridgeNode showNode(){
		if(dataStructure==0){
			return stack.peek();
		} else if(dataStructure==1){
			return queue.peek();
		} else {
			//sort the keyset as a treeset and return the node with the lowest key
			TreeSet<Integer> keys = new TreeSet<Integer>(map.keySet()); 
			return map.get(keys.first());
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
			if(map.size()==0){
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
			//don't really care about the order
			for(BridgeNode n: map.values()){
				output+=n.toString();
				output+="-";
			}
		}
		return output;
	}

}
