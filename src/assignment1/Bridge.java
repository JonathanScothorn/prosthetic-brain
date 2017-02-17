package assignment1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Bridge {
	
	public static int MAX_NODES_EXPANDED = 50000;
	
	private ArrayList<Integer> crossingTimes;
	private int heuristicAlgorithm;//1 for heuristic 1, 2 for heuristic 2, 3 for an average of heuristic 1 and 2.
	private boolean suppressPrinting = true;
	private boolean problem1 = true;
	private BoardNode targetNode;
	
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
	
	private boolean containsNode(ArrayList<Node> list, Node node){
		for(Node n: list){
			if(node.compare(n)){
				return true;
			}
		}
		return false;
	}
	
	private boolean containsMove(ArrayList<BoardMove> list, BoardMove move){
		for(BoardMove m: list){
			if(move.compare(m)){
				return true;
			}
		}
		return false;
	}
	
	private void initializeP1(SolutionTree openTree){
		
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
	
	private void initializeP2(SolutionTree openTree, int[][]boardState){
		
		//create the starting node
		BoardNode initial = new BoardNode(boardState, 0, 0);
		openTree.addNode(initial);
		
		//create goal node and solve.  Note that if the board is larger than 3x3 there will be empty spaces. Would need a fancy recursive spiral solution to fill a board of any arbitrary dimensions.	
		int[][] goal = new int[boardState.length][boardState[0].length];
		
		/* fill first row with sequence
		|1|2|3|
		| | | |
		| | | |		
		*/
		int currentInt = 1;
		int maxInt = boardState.length*boardState[0].length - 1;
		for(int x=0; x<boardState[0].length; x++){
			goal[0][x] = currentInt;
			currentInt++;
		}
		/* fill last column with sequence
		|1|2|3|
		| | |4|
		| | |5|		
		*/
		for(int y=1; y<boardState.length; y++){
			goal[y][boardState[0].length-1] = currentInt;
			currentInt++;
		}
		
		/* fill last row with sequence
		|1|2|3|
		| | |4|
		|7|6|5|		
		*/
		for(int x=boardState[0].length-2; x>=0; x--){
			if(currentInt <= maxInt){	
				goal[boardState.length-1][x] = currentInt;
				currentInt++;
			} else {//catch the case where the matrix is n x 2 and the blank space should be the last position
				goal[boardState.length-1][x] = 0;
				break;
			}
		}
		
		/* fill first column with sequence
		|1|2|3|
		|8| |4|
		|7|6|5|		
		*/
		if(currentInt<=maxInt){
			for(int y=boardState.length-2; y>0; y--){
				if(currentInt <= maxInt){	
					goal[y][0] = currentInt;
					currentInt++;
				} else {//catch the case where the matrix is n x 2 and the blank space should be the last position
					goal[y][0] = 0;
					break;
				}
			}
		}
		
		// if 3x3, place the zero in the centre of the board
		if(boardState.length==3 && boardState[0].length==3){
			goal[1][1]=0;
		}
		
		BoardNode goalNode = new BoardNode(goal, 0, 0);
		System.out.println("Goal node: \n"+goalNode.toString());
		setTargetNode(goalNode);
		solve(openTree, goalNode);
		
	}
	
	private boolean solve(SolutionTree initialTree, Node goal){
		
		SolutionTree openTree = initialTree;
		Node currentNode = openTree.showNode();
		ArrayList<Node> closedTree = new ArrayList<Node>();
		int nodesExpanded = 0;
		
		// done if the tree is empty or the goal state has been reached
		while(!currentNode.compare(goal) && !openTree.isEmpty() && nodesExpanded < MAX_NODES_EXPANDED){
			
			//if the current node is not terminal, expand it; otherwise skip it
			if(!containsNode(closedTree, currentNode)){
				openTree = expandNextNode(openTree);
				nodesExpanded++;
				if(nodesExpanded%1000 == 0){
					System.out.println(nodesExpanded+" nodes have been searched.");
				}
				
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
		} else if(nodesExpanded >= MAX_NODES_EXPANDED){
			System.out.println("Search deemed unsolvable after "+nodesExpanded+" nodes were processed.");
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
		if(problem1){
			return expandNextBridgeNode(tree);
		} else {
			return expandNextBoardNode(tree);
		}
	}
	
	private SolutionTree expandNextBridgeNode(SolutionTree tree){
		
		ArrayList<BridgeMove> legalMoves = new ArrayList<BridgeMove>();
		
		//the torch must move the side opposite where it was last time
		BridgeNode lastNode = (BridgeNode) tree.getNode(); // removing current node from the tree
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
	
	private SolutionTree expandNextBoardNode(SolutionTree tree){
		
		ArrayList<BoardMove> legalMoves = new ArrayList<BoardMove>();
		
		BoardNode lastNode = (BoardNode) tree.getNode();
		if(!suppressPrinting)
			System.out.println("Expanding node: "+lastNode.toString());
		
		// generate legal moves
		//first need to find the location of the "blank" tile (0)
		int x0=0;
		int y0=0;
		int maxX = lastNode.getBoard()[0].length-1;//maximum index value, which is array length-1
		int maxY = lastNode.getBoard().length-1;
		
		outerloop:
		for(int y=0; y<=maxY; y++){
			for(int x=0; x<=maxX; x++){
				if(lastNode.getTile(x, y)==0){
					x0=x;
					y0=y;
					break outerloop;//don't waste time examining other locations if the 0 coordinates have been found
				}
			}
		}
		
		// add all possible non-blank tile "horse" moves
		//test each non-blank tile
		for(int i=0; i<=maxX; i++){
			for(int j=0; j<=maxY; j++){
				if(lastNode.getTile(i, j)!=0){
					
					//now generate the move. moves are: -2,+1 | -2,-1 | -1,+2 | -1,-2 | +1,+2 | +1,-2 | +2,-1 | +2,+1
					int l = 0;
					for(int k=-2; k<=2; k++){
						if(k!=0){
							l = 3-Math.abs(k);
							if(i+k>=0 && i+k<=maxX && j+l>=0 && j+l<=maxY){
								BoardMove move = new BoardMove(i,j,i+k,j+l);
								if(!containsMove(legalMoves, move)){
									legalMoves.add(move);
								}
								
							}
							if(i+k>=0 && i+k<=maxX && j-l>=0 && j-l<=maxY){
								BoardMove move = new BoardMove(i,j,i+k,j-l);
								if(!containsMove(legalMoves, move)){
									legalMoves.add(move);
								}
							}
						}
						
					}
				}
			}
		}
		
		
		
		// add all possible blank tile moves
		for(int i=-1; i<=1; i++){
			for(int j=-1; j<=1; j++){
				if(!(i==0 && j==0)){//0,0 would be a null move
					//only accept moves that keep the blank tile within the board area
					if(x0+i>=0 && x0+i<=maxX && y0+j>=0 && y0+j<=maxY){
						legalMoves.add(new BoardMove(x0,y0,x0+i,y0+j));
					}
				}
			}
		}
		
		if(!suppressPrinting)
			System.out.println("moves: "+Arrays.toString(legalMoves.toArray()));
		
		for(BoardMove move: legalMoves){
			BoardNode nextNode = executeBoardMove(lastNode, move);
			if(!suppressPrinting)
				System.out.println("Adding node: "+nextNode.toString()+" with path cost "+nextNode.getTimeToReach()+", and total cost "+nextNode.getTotalCost());
			tree.addNode(nextNode);
		}
		
		return tree;
	}
	
	private int evaluateHeuristic(BridgeNode node){
		if(heuristicAlgorithm==1){
			return evaluateHeuristic1(node);
		} else if(heuristicAlgorithm==2) {
			return evaluateHeuristic2(node);
		} else  {
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
	
	private int evaluateBoardHeuristic(BoardNode node){
		if(heuristicAlgorithm==4){
			return evaluateHeuristic4(node);
		} else if(heuristicAlgorithm==5){
			return evaluateHeuristic5(node);
		} else {
			return evaluateHeuristic6(node);
		}
	}
	
	// find the total number of misplaced tiles
	private int evaluateHeuristic4(BoardNode node){
		int heuristic = 0;
		
		for(int i=0; i<node.getBoard()[0].length; i++){
			for(int j=0;j<node.getBoard().length; j++){
				if(node.getTile(i,j) != getTargetNode().getTile(i,j)){
					heuristic++;
				}
			}
		}
		heuristic--;//to account for the blank tile, which is not counted as misplaced
		
		return heuristic;
	}
	
	// find the total manhattan distance
	private int evaluateHeuristic5(BoardNode node){
		
		int heuristic = 0;
		
		int maxX = node.getBoard()[0].length-1;//maximum index value, which is array length-1
		int maxY = node.getBoard().length-1;
		
		//for each tile, get its distance from the desired location
		for(int x=0;x<=maxX;x++){
			for(int y=0; y<=maxY;y++){
				
				if(node.getTile(x, y)!=0){// ignore the blank tile
					
					goalNodeSearch:
					for(int x2=0;x2<=maxX;x2++){
						for(int y2=0;y2<=maxY;y2++){
							if(node.getTile(x,y)==getTargetNode().getTile(x2,y2)){
								
								//manhattan distance is the sum of the differences in the x and y coordinates.
								heuristic+=Math.abs(x-x2)+Math.abs(y-y2);
								
								break goalNodeSearch;//don't keep looking once the node is found
							}
						}
					}
				}
				
			}
		}
		
		return heuristic;
	}
	
	private int evaluateHeuristic6(BoardNode node){
		return (evaluateHeuristic4(node)+evaluateHeuristic5(node))/2;
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
	
	private BoardNode executeBoardMove(BoardNode lastState, BoardMove move){
		
		int swapValue = lastState.getTile(move.getX2(), move.getY2());
		
		BoardNode newState = lastState.createChild();
		
		// "move" the value stored at the tile x1,y1 to the tile x2,y2
		newState.setTile(move.getX2(), move.getY2(), lastState.getTile(move.getX1(), move.getY1()));
		// then swap the second value into the first tile space
		newState.setTile(move.getX1(), move.getY1(), swapValue);
		newState.setTimeToReach(lastState.getTimeToReach()+1);
		
		newState.setTotalCost(evaluateBoardHeuristic(newState)+newState.getTimeToReach());
		
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
			
			b.setProblem1(true);
			
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
				b.initializeP1(new SolutionTree(1));;
			} else if(userInput.equals("d")) {
				System.out.println("Solving using DFS");
				b.initializeP1(new SolutionTree(0));;
			} else if(userInput.equals("a")){
				System.out.println("Solving using A* search, heuristic 1.");
				b.setHeuristicAlgorithm(1);
				b.initializeP1(new SolutionTree(2));
			} else if(userInput.equals("c")){
				System.out.println("Solving using A* search, heuristic 2.");
				b.setHeuristicAlgorithm(2);
				b.initializeP1(new SolutionTree(2));
			} else if(userInput.equals("e")){
				System.out.println("Solving using A* search, heuristic average.");
				b.setHeuristicAlgorithm(3);
				b.initializeP1(new SolutionTree(2));
			} else {
				System.out.println("Terminating.");
			}
			scanner.close();
		} else if(userInput.equals("2")){
			
			b.setProblem1(false);
			
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
				int[][] inputArray = new int[height][width];
				int maxEntries = height*width;
				int entries = 0;
				int x = 0;
				int y = 0;
				while(scanner.hasNext() && entries < maxEntries){
					entries++;
					String csvValue = scanner.next();
					String trimmed = csvValue.trim();
					int value = Integer.parseInt(trimmed);
					System.out.println(value);
					if(x<width){
						inputArray[y][x] = value;
						x++;
					} else {
						x=0;
						y++;
						inputArray[y][x] = value;
						x++;
					}
				}
				scanner = new Scanner(System.in);
				
				System.out.println("Please enter 'b' for BFS, 'd' for DFS, 'a' for A* heuristic 1, 'c' for A* heuristic 2, or 'e' for A* using the average of the heuristics.  \n"
					+ "Anything else quits.");
				userInput = scanner.next();
				if(userInput.equals("b")){
					System.out.println("Solving using BFS");
					b.initializeP2(new SolutionTree(1), inputArray);;
				} else if(userInput.equals("d")) {
					System.out.println("Solving using DFS");
					b.initializeP2(new SolutionTree(0), inputArray);;
				} else if(userInput.equals("a")){
					System.out.println("Solving using A* search, heuristic 1.");
					b.setHeuristicAlgorithm(4);
					b.initializeP2(new SolutionTree(2), inputArray);
				} else if(userInput.equals("c")){
					System.out.println("Solving using A* search, heuristic 2.");
					b.setHeuristicAlgorithm(5);
					b.initializeP2(new SolutionTree(2), inputArray);
				} else if(userInput.equals("e")){
					System.out.println("Solving using A* search, heuristic average.");
					b.setHeuristicAlgorithm(6);
					b.initializeP2(new SolutionTree(2), inputArray);
				} else {
					System.out.println("Terminating.");
				}
				scanner.close();
				
				
			}
			
			
			scanner.close();
			
		} else {
			System.out.println("Unrecognized input.  Please restart.");
			scanner.close();
		}
		
	}

	public boolean getProblem1() {
		return problem1;
	}

	public void setProblem1(boolean problem1) {
		this.problem1 = problem1;
	}

	public BoardNode getTargetNode() {
		return targetNode;
	}

	public void setTargetNode(BoardNode targetNode) {
		this.targetNode = targetNode;
	}

}
