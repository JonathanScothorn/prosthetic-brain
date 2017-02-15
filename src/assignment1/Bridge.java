package assignment1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

public class Bridge {
	
	private ArrayList<Integer> crossingTimes;
	
	public Bridge(){
		crossingTimes = new ArrayList<Integer>();
	}
	
	public void addCrossingTime(int time){
		crossingTimes.add(new Integer(time));
	}
	
	public int getCrossingTimeSize(){
		return crossingTimes.size();
	}
	

	private boolean compareBridgeNode(BridgeNode node1, BridgeNode node2){
		//System.out.println("Comparing nodes:");
		//System.out.println(node1.toString());
		//System.out.println(node2.toString());
		if(Arrays.equals(node1.getPeople(), node2.getPeople()) && node1.getTorchLeft() == node2.getTorchLeft()){
			return true;
		} else {
			return false;
		}
	}
	
	private boolean containsNode(ArrayList<BridgeNode> list, BridgeNode node){
		for(BridgeNode n: list){
			if(compareBridgeNode(n, node)){
				return true;
			}
		}
		return false;
	}
	
	private void initialize(BridgeTree openTree){
		
		// create the starting node
		boolean[] people = new boolean[getCrossingTimeSize()];
		Arrays.fill(people, true);
		BridgeNode initialNode = new BridgeNode(people, true, 0);
		openTree.addNode(initialNode, evaluateHeuristic1(initialNode));
		
		// create the goal node and solve
		boolean[] goal = new boolean[getCrossingTimeSize()];
		Arrays.fill(goal, false);
		BridgeNode goalNode = new BridgeNode(goal, false, 0);
		solve(openTree, goalNode);
		
	}
	
	private boolean solve(BridgeTree initialTree, BridgeNode goal){
		
		BridgeTree openTree = initialTree;
		BridgeNode currentNode = openTree.showNode();
		ArrayList<BridgeNode> closedTree = new ArrayList<BridgeNode>();
		
		// done if the tree is empty or the goal state has been reached
		while(!compareBridgeNode(currentNode, goal) && !openTree.isEmpty()){
			
			//if the current node is not terminal, expand it; otherwise skip it
			if(!containsNode(closedTree, currentNode)){
				openTree = expandNextNode(openTree);
				System.out.println(openTree.toString());
				closedTree.add(currentNode);
				currentNode = openTree.showNode();
				System.out.println("Now evaluating node: "+currentNode.toString());
			} else {
				openTree.getNode();//drop the current node from the tree
				currentNode = openTree.showNode();
			}			
			
		}
		
		if(openTree.isEmpty()){
			System.out.println("No solution found.");
			return false;
		} else {
			
			System.out.println("Time taken to reach this solution was: "+currentNode.getTimeToReach()+" time units.");
			
			String solution = "Final node-";
			
			while(currentNode.getParent() != null){
				solution+=currentNode.toString();
				solution+="-";
				currentNode = currentNode.getParent();
			}
			solution+=currentNode.toString()+"-Initial node";
			
			System.out.println(solution);
			
			return true;
		}
		
	}
	
	private BridgeTree expandNextNode(BridgeTree tree){
		
		ArrayList<BridgeMove> legalMoves = new ArrayList<BridgeMove>();
		
		//the torch must move the side opposite where it was last time
		BridgeNode lastNode = tree.getNode(); // removing current node from the tree
		System.out.println("Expanding node: "+lastNode.toString());
		boolean torchSide = lastNode.getTorchLeft();
		
		//for each person
		for(int i = 0; i<lastNode.getNumPeople(); i++){
			//they can go across with any of the other people
			for(int j=i+1; j<lastNode.getNumPeople(); j++){
				//if both people are currently on the same side where the boat is leaving
				if(lastNode.getPerson(i) == lastNode.getTorchLeft() && lastNode.getPerson(j) == lastNode.getTorchLeft()){
					int time;
					if(crossingTimes.get(i)>crossingTimes.get(j)){
						time = crossingTimes.get(i);
					} else {
						time = crossingTimes.get(j);
					}
					legalMoves.add(new BridgeMove(i, j, time));
				}
				
			}
			//they can also go alone
			if(lastNode.getPerson(i) == lastNode.getTorchLeft()){
				legalMoves.add(new BridgeMove(i, -1, crossingTimes.get(i)));
			}
		}
		
		System.out.println("moves: "+Arrays.toString(legalMoves.toArray()));
		
		for(BridgeMove move: legalMoves){
			BridgeNode nextNode = executeMove(lastNode, move);
			System.out.println("Adding node: "+nextNode.toString()+" with heurisitic "+evaluateHeuristic1(nextNode));
			tree.addNode(nextNode, evaluateHeuristic1(nextNode));
		}
		
		return tree;
	}
	
	private int evaluateHeuristic1(BridgeNode node){
		
		int heuristicValue = node.getTimeToReach();
		
		//heuristic 1: the sum of the travel times of all the people who have not yet crossed.  optimistic heuristic because it does not include return trip time.
		for(int i=0; i<node.getNumPeople(); i++){
			//if a person has not yet crossed, add their crossing time
			if(node.getPerson(i)){
				heuristicValue+=crossingTimes.get(i);
			}
		}
		return heuristicValue;
	}
	
	private BridgeNode executeMove(BridgeNode lastState, BridgeMove move){
		
		BridgeNode newState = lastState.copy();
		
		newState.setPerson(move.getPerson1ToMove());
		
		if(move.getPerson2ToMove() != -1){
			newState.setPerson(move.getPerson2ToMove());
		}
		
		newState.setTorchLeft(!lastState.getTorchLeft());
		
		newState.setTimeToReach(lastState.getTimeToReach()+move.getTime());
		
		System.out.println("new node: "+newState.toString()+" from move "+move.toString());
		
		return newState;
	}
	
	public static void main(String args[]){
		Bridge b = new Bridge();
		
		Scanner scanner = new Scanner(System.in);
		String userInput;
		
		System.out.println("Enter the crossing time values first.  Then enter 'b' for BFS, 'd' for DFS, 'a' for A* heuristic 1, or 'c' for A* heuristic 2.  Anything else quits.");
		while(scanner.hasNextInt()){
			b.addCrossingTime(new Integer(scanner.nextInt()));
		}
		
		//System.out.println("Press 'b' for BFS or 'd' for DFS.");
		userInput = scanner.next();
		if(userInput.equals("b")){
			System.out.println("Solving using BFS");
			b.initialize(new BridgeTree(1));;
		} else if(userInput.equals("d")) {
			System.out.println("Solving using DFS");
			b.initialize(new BridgeTree(0));;
		} else if(userInput.equals("a")){
			System.out.println("Solving using A* search, heuristic 1.");
			b.initialize(new BridgeTree(2));
		} else if(userInput.equals("c")){
			System.out.println("Solving using A* search, heuristic 2.");
		} else {
			System.out.println("Terminating.");
		}
		
		scanner.close();
	}

}
