package assignment1;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

public class Bridge {
	
	public Bridge(){
		
	}
	
	public void solveBFS(){
		LinkedList<BridgeNode> solutionTree = new LinkedList<BridgeNode>();
		solutionTree.add(new BridgeNode(3,3,true));
		
		
	}
	
	public void solveDFS(){
		Stack<BridgeNode> solutionTree = new Stack<BridgeNode>();
	}
	
	public boolean compareBridgeNode(BridgeNode node1, BridgeNode node2){
		if(node1.getBoat() == node2.getBoat() && node2.getCannibals() == node2.getCannibals() && node1.getMissionaries() == node2.getMissionaries()){
			return true;
		} else {
			return false;
		}
	}
	
	public static void main(String args[]){
		Bridge b = new Bridge();
		
		Scanner scanner = new Scanner(System.in);
		String userInput;
		
		System.out.println("Press 'b' for BFS or 'd' for DFS.");
		userInput = scanner.next();
		if(userInput.equals("b")){
			b.solveBFS();
		}
		
		scanner.close();
	}

}
