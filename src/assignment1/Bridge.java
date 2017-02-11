package assignment1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

public class Bridge {
	
	public Bridge(){
		
	}
	
	public void solveBFS(){
		LinkedList<BridgeNode> solutionTree = new LinkedList<BridgeNode>();
		solutionTree.add(new BridgeNode(new boolean[]{true, true, true, true, true, true}, true));
		
		
	}
	
	public void solveDFS(){
		Stack<BridgeNode> solutionTree = new Stack<BridgeNode>();
		solutionTree.add(new BridgeNode(new boolean[]{true, true, true, true, true, true}, true));
	}
	

	public boolean compareBridgeNode(BridgeNode node1, BridgeNode node2){
		if(Arrays.equals(node1.getPeople(), node2.getPeople()) && node1.getBoatLeft() == node2.getBoatLeft()){
			return true;
		} else {
			return false;
		}
	}
	
	private LinkedList<BridgeNode> expandNodesBFS(LinkedList<BridgeNode> tree){
		
		ArrayList<BridgeMove> legalMoves = new ArrayList<BridgeMove>();
		
		//the boat must move the side opposite where it was last time
		BridgeNode lastNode = tree.getLast();
		boolean boatSide = lastNode.getBoatLeft();
		
		//for each person
		for(int i = 0; i<tree.getLast().getNumPeople(); i++){
			//they can go across with any of the other people
			for(int j=i+1; j<tree.getLast().getNumPeople(); j++){
				//if both people are currently on the same side where the boat is leaving
				if(lastNode.getPerson(i) == lastNode.getBoatLeft() && lastNode.getPerson(j) == lastNode.getBoatLeft()){
					legalMoves.add(new BridgeMove(i, j));
				}
				
			}
			//they can also go alone
			if(lastNode.getPerson(i) == lastNode.getBoatLeft()){
				legalMoves.add(new BridgeMove(i, -1));
			}
		}
		
		for(BridgeMove move: legalMoves){
			tree.add(executeMove(lastNode, move));
		}
		
		return tree;
	}
	
	private BridgeNode executeMove(BridgeNode lastState, BridgeMove move){
		lastState.setPerson(move.getPerson1ToMove());
		if(move.getPerson2ToMove() != -1){
			lastState.setPerson(move.getPerson2ToMove());
		}
		lastState.setBoatLeft(!lastState.getBoatLeft());
		
		return lastState;
	}
	
	public static void main(String args[]){
		Bridge b = new Bridge();
		
		Scanner scanner = new Scanner(System.in);
		String userInput;
		
		System.out.println("Press 'b' for BFS or 'd' for DFS.");
		userInput = scanner.next();
		if(userInput.equals("b")){
			BridgeNode a = new BridgeNode(new boolean[]{true, true, true, true, true, true}, true);
			BridgeNode c = new BridgeNode(new boolean[]{true, true, true, true, true, true}, true);
			System.out.println(b.compareBridgeNode(a,c));
			
			System.out.println("a");
			//b.solveBFS();
		}
		
		scanner.close();
	}

}
