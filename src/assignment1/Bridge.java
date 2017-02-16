package assignment1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Bridge {
	
	private ArrayList<Integer> crossingTimes;
	private int heuristicAlgorithm;//1 for heuristic 1, 2 for heuristic 2, 3 for an average of heuristic 1 and 2.
	private boolean suppressPrinting = true;
	
	public Bridge(){
		crossingTimes = new ArrayList<Integer>();
		heuristicAlgorithm = 1;
	}
	
	public void addCrossingTime(int time){
		crossingTimes.add(new Integer(time));
	}
	
	public int getCrossingTimeSize(){
		return crossingTimes.size();
	}
	
	public void setHeuristicAlgorithm(int i){
		heuristicAlgorithm = i;
	}
	
	public int getHeuristicAlgorithm(){
		return heuristicAlgorithm;
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
	
	private void initialize(SolutionTree openTree){
		
		// create the starting node
		boolean[] people = new boolean[getCrossingTimeSize()];
		Arrays.fill(people, true);
		BridgeNode initialNode = new BridgeNode(people, true, 0, 0); //initial total distance will be 0.  This is ok because we always want this node to be evaluated first.
		openTree.addNode(initialNode);
		
		// create the goal node and solve
		boolean[] goal = new boolean[getCrossingTimeSize()];
		Arrays.fill(goal, false);
		BridgeNode goalNode = new BridgeNode(goal, false, 0, 0);
		solve(openTree, goalNode);
		
	}
	
	private boolean solve(SolutionTree initialTree, BridgeNode goal){
		
		SolutionTree openTree = initialTree;
		BridgeNode currentNode = openTree.showNode();
		ArrayList<BridgeNode> closedTree = new ArrayList<BridgeNode>();
		int nodesExpanded = 0;
		
		// done if the tree is empty or the goal state has been reached
		while(!compareBridgeNode(currentNode, goal) && !openTree.isEmpty()){
			
			//if the current node is not terminal, expand it; otherwise skip it
			if(!containsNode(closedTree, currentNode)){
				openTree = expandNextNode(openTree);
				nodesExpanded++;
				if(!suppressPrinting)
					System.out.println(openTree.toString()+"\n");
				closedTree.add(currentNode);
				currentNode = openTree.showNode();
				if(!suppressPrinting)
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
			System.out.println(nodesExpanded+" nodes were expanded to reach this solution.");
			
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
	
	private SolutionTree expandNextNode(SolutionTree tree){
		
		ArrayList<BridgeMove> legalMoves = new ArrayList<BridgeMove>();
		
		//the torch must move the side opposite where it was last time
		BridgeNode lastNode = tree.getNode(); // removing current node from the tree
		if(!suppressPrinting)
			System.out.println("Expanding node: "+lastNode.toString());
		
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
		
		if(!suppressPrinting)
			System.out.println("moves: "+Arrays.toString(legalMoves.toArray()));
		
		for(BridgeMove move: legalMoves){
			BridgeNode nextNode = executeMove(lastNode, move);
			if(!suppressPrinting)
				System.out.println("Adding node: "+nextNode.toString()+" with path cost "+nextNode.getTimeToReach()+", heuristic "+evaluateHeuristic(nextNode)+" and total cost "+nextNode.getTotalCost());
			tree.addNode(nextNode);
		}
		
		return tree;
	}
	
	private int evaluateHeuristic(BridgeNode node){
		if(heuristicAlgorithm==1){
			return evaluateHeuristic1(node);
		} else if(heuristicAlgorithm==2) {
			return evaluateHeuristic2(node);
		} else {
			return evaluateHeuristic3(node);
		}
	}
	
	private int evaluateHeuristic1(BridgeNode node){
		
		int heuristicValue = 0;
		
		//heuristic 1: the sum of the travel times of all the people who have not yet crossed.  optimistic heuristic because it does not include return trip time.
		for(int i=0; i<node.getNumPeople(); i++){
			//if a person has not yet crossed, add their crossing time
			if(node.getPerson(i)){
				heuristicValue+=crossingTimes.get(i);
			}
		}
		return heuristicValue;
	}
	
	private int evaluateHeuristic2(BridgeNode node){
		
		int heuristicValue = 0;
		boolean lowestPersonFound = false;
		for(int i=0; i<node.getNumPeople(); i++){
			//if a person has not yet crossed, add their crossing time
			if(node.getPerson(i)){
				heuristicValue+=crossingTimes.get(i);
			} else if(!lowestPersonFound){//if a person has already crossed; the first one to be found is the lowest travel time.
				if(!node.getTorchLeft()){// only if the torch needs to be returned
					heuristicValue+=crossingTimes.get(i)*2;
				}
				lowestPersonFound = true;
			}
		}
		return heuristicValue;
	}
	
	private int evaluateHeuristic3(BridgeNode node){
		return (evaluateHeuristic1(node)+evaluateHeuristic2(node))/2;
	}
	
	private BridgeNode executeMove(BridgeNode lastState, BridgeMove move){
		
		BridgeNode newState = lastState.createChild();
		
		// child temporarily in a bad state
		newState.setPerson(move.getPerson1ToMove());
		
		if(move.getPerson2ToMove() != -1){
			newState.setPerson(move.getPerson2ToMove());
		}
		
		newState.setTorchLeft(!lastState.getTorchLeft());
		
		newState.setTimeToReach(lastState.getTimeToReach()+move.getTime());
		
		newState.setTotalCost(evaluateHeuristic(newState)+newState.getTimeToReach());
		
		
		if(!suppressPrinting)
			System.out.println("new node: "+newState.toString()+" from move "+move.toString());
		
		return newState;
	}
	
	public static void main(String args[]){
		Bridge b = new Bridge();
		
		Scanner scanner = new Scanner(System.in);
		String userInput;
		int last = 0;
		
		System.out.println("Please enter '1' for problem 1 or '2' for problem 2.");
		userInput = scanner.next();
		
		if(userInput.equals("1")){
			
			System.out.println("Enter the crossing time values first.  Then enter 'b' for BFS, 'd' for DFS, \n"
					+ "'a' for A* heuristic 1, 'c' for A* heuristic 2, or 'e' for A* using the average of the heuristics.  \n"
					+ "Anything else quits.");
			while(scanner.hasNextInt()){
				int next = scanner.nextInt();
				if(next > last){
					b.addCrossingTime(new Integer(next));
					last = next;
				} else {
					System.out.println("Invalid entry.  Integers must be in ascending order.");
				}
				
				
			}
			
			//System.out.println("Press 'b' for BFS or 'd' for DFS.");
			System.out.println("The crossing time values are: "+b.crossingTimes.toString());
			userInput = scanner.next();
			if(userInput.equals("b")){
				System.out.println("Solving using BFS");
				b.initialize(new SolutionTree(1));;
			} else if(userInput.equals("d")) {
				System.out.println("Solving using DFS");
				b.initialize(new SolutionTree(0));;
			} else if(userInput.equals("a")){
				System.out.println("Solving using A* search, heuristic 1.");
				b.setHeuristicAlgorithm(1);
				b.initialize(new SolutionTree(2));
			} else if(userInput.equals("c")){
				System.out.println("Solving using A* search, heuristic 2.");
				b.setHeuristicAlgorithm(2);
				b.initialize(new SolutionTree(2));
			} else if(userInput.equals("e")){
				System.out.println("Solving using A* search, heuristic average.");
				b.setHeuristicAlgorithm(3);
				b.initialize(new SolutionTree(2));
			} else {
				System.out.println("Terminating.");
			}
			scanner.close();
		} else if(userInput.equals("2")){
			
			boolean intFound = false;
			boolean fileFound = false;
			int width = 1;
			int height = 1;
			String filename = "";
			
			System.out.println("Now performing problem 2.");
			
			System.out.println("Please enter the height of the board.");
			while(!intFound){
				if(scanner.hasNextInt()){
					height = scanner.nextInt();
					if(height > 0){
						intFound = true;
					} else {
						System.out.println("Invalid entry.  Must be greater than zero.");
					}
				} else {
					userInput = scanner.next();
					System.out.println("Invalid entry.  Please enter a valid integer for the board height.");
				}
			}
			intFound = false;
			
			System.out.println("Please enter the width of the board.");
			while(!intFound){
				if(scanner.hasNextInt()){
					width = scanner.nextInt();
					if(width > 0){
						intFound = true;
					} else {
						System.out.println("Invalid entry.  Must be greater than zero.");
					}
					
				} else {
					userInput = scanner.next();
					System.out.println("Invalid entry.  Please enter a valid integer for the board width.");
				}
			}
			
			
			while(!fileFound){
				try {
					System.out.println("Please enter the full file path to a .CSV file to import, or 'q' to quit.");
					filename = scanner.next();
					if(filename.equals("q")){
						break;
					}
						
					System.out.println("Opening "+filename);
					
					scanner = new Scanner(new File(filename));
					fileFound = true;
					
				} catch(FileNotFoundException e){
					System.out.println("File not found.  Please try again.");
					//scanner = new Scanner(System.in);
				}
			}
			
			if(!filename.equals("q")){
				scanner.useDelimiter(",");
				int[][] board = new int[height][width];
				int maxEntries = height*width;
				int entries = 0;
				int x = 0;
				int y = 0;
				while(scanner.hasNextInt() && entries < maxEntries){
					int value = scanner.nextInt();
					System.out.println(value);
					if(x<width){
						board[y][x] = value;
						x++;
					} else {
						x=0;
						y++;
						board[y][x] = value;
						x++;
					}
				}
			}
			
			
			scanner.close();
			
		} else {
			System.out.println("Unrecognized input.  Please restart.");
			scanner.close();
		}
		
	}

}
