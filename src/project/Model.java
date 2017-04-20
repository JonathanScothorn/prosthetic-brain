package project;

import java.util.ArrayList;

public class Model {

	SimulationObject[][][] space;
	private int maxX;
	private int maxY;
	private int maxZ;
	private double heuristic;
	ArrayList<Drone> activeDrones;
	private int lastDroneIndex;
	private Model parent;
	
	// copy constructor: want to create a new space array so that objects can be moved independently, but keep the object references the same
	public Model(Model m){
		maxX = m.getMaxX();
		maxY = m.getMaxY();
		maxZ = m.getMaxZ();
		space = new SimulationObject[maxZ][maxY][maxX];
		
		for(int z=0; z<maxZ; z++){
			for(int y=0; y<maxY; y++){
				for(int x=0; x<maxX; x++){
					if(m.getObject(x, y, z) != null){
						space[z][y][x] = m.getObject(x, y, z);
					} else {
						space[z][y][x] = null;
					}
				}
			}
		}
		
		setHeuristic(m.getHeuristic());
		activeDrones = m.getDrones();
		lastDroneIndex = m.getLastDroneIndex();
		parent = m;
	}
	
	public Model(int x, int y, int z){
		space = new SimulationObject[z][y][x];
		setHeuristic(0.0);
		activeDrones = new ArrayList<Drone>();
		maxX = x;
		maxY = y;
		maxZ = z;
		lastDroneIndex = 0;
		parent = null;
	}
	
	public int getMaxX(){
		return maxX;
	}
	
	public int getMaxY(){
		return maxY;
	}
	
	public int getMaxZ(){
		return maxZ;
	}
	
	public SimulationObject[][][] getSpace(){
		return space;
	}
	
	public SimulationObject getObject(int x, int y, int z){
		return space[z][y][x];
	}
	
	public void setObject(int x, int y, int z, SimulationObject object){
		space[z][y][x] = object;
	}
	
	public Coordinate getCoordinates(SimulationObject target){
		for(int z=0; z<maxZ; z++){
			for(int y=0; y<maxY; y++){
				for(int x=0; x<maxX; x++){
					if(target.equals(space[z][y][x])){
						return new Coordinate(x,y,z);
					}
				}
			}
		}
		return null;
	}
	
	public void addDrone(Drone drone){
		Coordinate arrival = drone.getArrival();
		space[arrival.getZ()][arrival.getY()][arrival.getX()] = drone;
		drone.setCurrent(arrival);
		activeDrones.add(drone);
	}
	
	public void addGoalDrone(Drone drone){
		Coordinate target = drone.getDestination();
		space[target.getZ()][target.getY()][target.getX()] = drone;
	}
	
	public String header(){
		String output = "";
		
		for(int z=0; z<space.length; z++){
			output+=
			output+="  ";
		}
		
		return output;
	}
	
	public ArrayList<Drone> getDrones(){
		return activeDrones;
	}
	
	public void removeDrone(Drone drone){
		activeDrones.remove(drone);
		Coordinate current = getCoordinates(drone);
		space[current.getZ()][current.getY()][current.getX()] = null;
	}
	
	public void moveDrone(Drone drone, Coordinate target){
		Coordinate current = getCoordinates(drone);
		space[current.getZ()][current.getY()][current.getX()] = null;
		space[target.getZ()][target.getY()][target.getX()] = drone;
	}
	
	@Override
	public String toString(){
		String output = "";

		//output+="heuristic: "+heuristic+"\n";
		output+="Moving drone "+activeDrones.get(lastDroneIndex).getToken()+"\n";
		for(int y=0; y<space[0].length; y++){
			
			for(int z=0; z<space.length; z++){
				
				for(int x=0; x<space[0][0].length; x++){
					
					if(space[z][y][x] != null){
						output+=space[z][y][x].toString();
					} else {
						output+="-";
					}
				}
				
				output+="  ";
			}
			
			output+="\n";
		}
		return output;
	}

	public double getHeuristic() {
		return heuristic;
	}

	public void setHeuristic(double heuristic) {
		this.heuristic = heuristic;
	}
	
	private double calculateDroneHeuristic(Drone drone){
		Coordinate target = drone.getDestination();
		Coordinate current = getCoordinates(drone);
		
		return -1*(Math.pow(Math.pow(target.getX() - current.getX(), 2) + 
				Math.pow(target.getY() - current.getY(), 2) + 
				Math.pow(target.getZ() - current.getZ(), 2), 0.5));
	}
	
	public void updateHeuristic(){
		double updated = 0.0;
		for(Drone drone: activeDrones){
			updated+=calculateDroneHeuristic(drone);
		}
		heuristic = updated;
	}
	
	// compare the contents of the space array
	public boolean compare(Model m){
		
		for(int z=0; z<space.length; z++){
			for(int y=0; y<space[0].length; y++){
				for(int x=0; x<space[0][0].length; x++){
					if(space[z][y][x] != null){
						if(m.getSpace()[z][y][x] != null){
							if(!space[z][y][x].equals(m.getSpace()[z][y][x])){
								return false;// both contain an unequal object
							}
						} else {
							return false;// first is object, second is null
						}
					} else {
						if(m.getSpace()[z][y][x] != null){
							return false;// first is null, second is object
						}
					}
				}
			}
		}
		
		if(lastDroneIndex != m.getLastDroneIndex()){
			return false;
		}
		return true;
	}

	public int getLastDroneIndex() {
		return lastDroneIndex;
	}

	public void incrementLastDroneIndex() {
		lastDroneIndex++;
		if(lastDroneIndex >= activeDrones.size()){
			lastDroneIndex = 0;
		}
	}
	
	public Model getParent(){
		return parent;
	}
	
}
