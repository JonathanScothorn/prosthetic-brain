package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Solver {
	
	public static final int MAX_NODES_EXPANDED = 50000;
	
	public Solver(){
		
	}
	
	public Model generateModel(String filename, int xDimension, int yDimension, int zDimension){
		
		Scanner scanner;
		Model output = new Model(xDimension, yDimension, zDimension);
		
		try {
			scanner = new Scanner(new File(filename));
		} catch (FileNotFoundException e){
			System.out.println("File "+filename+" could not be opened.");
			return null;
		}
		
		scanner.useDelimiter(",");
		
		int maxEntries = xDimension*yDimension*zDimension;
		int entries = 0;
		int x = 0;
		int y = 0;
		int z = 0;
		while(scanner.hasNext() && entries < maxEntries){
			entries++;
			String csvValue = scanner.next();
			String trimmed = csvValue.trim();
			int value = Integer.parseInt(trimmed);
			
			// only accept boolean values
			SimulationObject tempObject;
			
			if(value == 1){
				tempObject = new Obstacle();
			} else if(value == 0){
				tempObject = null;
			} else {
				System.out.println("Invalid entry in the Layout input file.");
				return null;
			}
			
			if(x<xDimension){
				output.setObject(x, y, z, tempObject); 
				x++;
			} else {
				x=0;
				y++;
				if(y<yDimension){
					output.setObject(x, y, z, tempObject); 
				} else {
					y=0; 
					z++;
					output.setObject(x, y, z, tempObject); 
				}
				x++;
			}
		}
		
		scanner.close();
		
		return output;
		
	}
	
	private ArrayList<Drone> generateDrones(String filename){
		
		ArrayList<Drone> output = new ArrayList<Drone>();
		Scanner scanner;
		try {
			scanner = new Scanner(new File(filename));
			System.out.println("Extracting drone information from file "+filename);
		} catch (FileNotFoundException e){
			System.out.println("File "+filename+" could not be opened as a drone input file.");
			return null;
		}
		
		scanner.useDelimiter(",");
		
		// read in next drone values
		while(scanner.hasNext()){
			
			// read all 13 required values for a drone.  
			// Expect 6 ints, 7 doubles: 3 initial position int, 3 destination int, 3 max velocity doubles, 3 max accel doubles, 1 entry time double 
			int[] intValues = new int[6];
			for(int i=0; i<6; i++){
				String csvValue = scanner.next();
				String trimmed = csvValue.trim();
				intValues[i] = Integer.parseInt(trimmed);
			}
			double[] doubleValues = new double[7];
			for(int i=0; i<7; i++){
				String csvValue = scanner.next();
				String trimmed = csvValue.trim();
				doubleValues[i] = Double.parseDouble(trimmed);
			}
			
			// now create the drone object and place it at its initial position
			Drone drone = new Drone(doubleValues[0], doubleValues[1], doubleValues[2], doubleValues[3], doubleValues[4], 
					doubleValues[5], intValues[0], intValues[1], intValues[2], intValues[3], intValues[4], intValues[5], doubleValues[6]);
			output.add(drone);
		}
		
		scanner.close();
		return output;
	}
	
	private ArrayList<Move> generateMoves(Model model, Drone drone){
		
		ArrayList<Move> moves = new ArrayList<Move>();
		for(int z=-1; z<=1; z++){
			for(int y=-1; y<=1; y++){
				for(int x=-1; x<=1; x++){
					Coordinate droneCoords = model.getCoordinates(drone);
					//System.out.println(drone.printContents());
					//System.out.println(droneCoords.toString());
					int testX = droneCoords.getX()+x;
					int testY = droneCoords.getY()+y;
					int testZ = droneCoords.getZ()+z;
					if(0<=testX && 0<=testY && 0<=testZ && model.getMaxX()>testX && model.getMaxY()>testY && model.getMaxZ()>testZ){
						if(model.getObject(testX, testY, testZ) == null){
							Coordinate move = new Coordinate(testX,testY,testZ);
							moves.add(new Move(drone, move));
						}
					}
					
				}
			}
		}
		/*for(Move m: moves){
			System.out.println(m.toString());
		}*/
		return moves;
	}
	
	private boolean containsNode(ArrayList<Model> nodes, Model node){
		
		for(Model m: nodes){
			if(node.compare(m)){
				return true;
			}
		}
		return false;
	}
	
	private Model executeMove(Model initial, Move move){
		
		Model next = new Model(initial);
		next.moveDrone(move.getDrone(), move.getMove());
		
		return next;
	}
	
	private SolutionTree expandNextNode(SolutionTree tree){
		
		Model lastNode = tree.getNode(); // removes the node from the tree
		ArrayList<Move> moves = generateMoves(lastNode, lastNode.getDrones().get(lastNode.getLastDroneIndex()));// generate moves using the next drone
		
		for(Move move: moves){
			Model nextNode = executeMove(lastNode, move);
			nextNode.incrementLastDroneIndex();
			nextNode.updateHeuristic();
			//System.out.println("Adding node with heuristic "+nextNode.getHeuristic());
			tree.addNode(nextNode);
		}
		return tree;
	}
	
	public void solve(Model model, ArrayList<Drone> arrivingDrones){
		
		// copy the model's configuration to the goal model
		Model goal = new Model(model);
		
		
		// populate the goal and initial models using the drone list
		for(Drone d: arrivingDrones){
			model.addDrone(d);
			goal.addGoalDrone(d);
		}
		model.updateHeuristic();
		
		System.out.println("Initial Model: \n"+model.toString());
		System.out.println("Goal Model: \n"+goal.toString());

		
		SolutionTree openTree = new SolutionTree();
		ArrayList<Model> closedNodes = new ArrayList<Model>();
		openTree.addNode(model);
		
		int nodesExpanded = 0;
		Model currentNode = model;
		
		// loop until the tree is empty, the goal node has been reached, or the maximum number of nodes has been expanded.
		while(!currentNode.compare(goal) && !openTree.isEmpty() && nodesExpanded < MAX_NODES_EXPANDED){
			
			//System.out.println("Evaluating node with heuristic "+currentNode.getHeuristic()+" : \n"+currentNode.toString());
			//System.out.println("Goal Model: \n"+goal.toString());
			
			if(!containsNode(closedNodes, currentNode)){
				openTree = expandNextNode(openTree);
				
				closedNodes.add(currentNode);
				currentNode = openTree.peekNode();
			} else {
				openTree.getNode();// drop the node since it is a duplicate
				currentNode = openTree.peekNode();
			}
			if(nodesExpanded%1000 == 0){
				System.out.println(nodesExpanded+" nodes have been searched.");
			}
			nodesExpanded++;
		}
		
		if(openTree.isEmpty()){
			System.out.println("No solution found.");
		} else if(nodesExpanded >= MAX_NODES_EXPANDED){
			System.out.println("Search deemed unsolvable after "+nodesExpanded+" nodes were processed.");
		} else {
			
			System.out.println(nodesExpanded+" nodes were expanded to reach this solution.");
			
			String solution = "Final node\n";
			
			while(currentNode.getParent() != null){
				solution+=currentNode.toString();
				solution+="\n";
				currentNode = currentNode.getParent();
			}
			solution+=currentNode.toString()+"\nInitial node";
			
			System.out.println(solution);
		}
		
			
	}
	
	private String inputFilename(Scanner scanner, String message){
	
		//Scanner scanner = new Scanner(System.in);
		
		boolean fileFound = false;
		String filename = "";
		
		while(!fileFound){
			try {
				System.out.println(message);
				filename = scanner.next();
				if(filename.equals("q")){
					break;
				}
					
				System.out.println("Opening "+filename);
				
				scanner = new Scanner(new File(filename));
				fileFound = true;
				
			} catch(FileNotFoundException e){
				System.out.println("File not found.  Please try again.");
			}
		}
		//scanner.close();
		return filename;
	}

	public static void main(String[] args) {
		
		Solver s = new Solver();
		
		Scanner scanner = new Scanner(System.in);
		String filename = s.inputFilename(scanner, "Please enter the full file path to a .CSV file to import for the layout, or 'q' to quit.");
		//String filename = "C:/Users/Jon/git/prosthetic-brain/testfiles/Layout1.csv";
		
		if(!filename.equals("q")){
			
			int[] dimensions = new int[3];
			//int[] dimensions = {10,10,10};
			
			System.out.println("Please enter three integers, indicating the x, y, and z dimensions of the space that is described in the layout file.");
			for(int i=0; i<3; i++){
				dimensions[i] = scanner.nextInt();
			}
			
			
			Model m = s.generateModel(filename, dimensions[0], dimensions[1], dimensions[2]);
			
			String droneFilename = s.inputFilename(scanner, "Please enter the full file path to a .CSV file to import for the drones, or 'q' to quit.");
			//String droneFilename = "C:/Users/Jon/git/prosthetic-brain/testfiles/Drones1.csv";
			
			if(!droneFilename.equals("q")){
				ArrayList<Drone> drones = s.generateDrones(droneFilename);
				for(Drone d: drones){
					d.setToken(""+drones.indexOf(d));
				}
				
				s.solve(m, drones);
			}
		}
		scanner.close();
	}

}
