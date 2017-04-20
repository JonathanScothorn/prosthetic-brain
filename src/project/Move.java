package project;

public class Move {
	
	private Drone drone;
	private Coordinate move;
	private double heuristic;
	
	public Move(Drone drone, Coordinate move){
		this.setDrone(drone);
		this.setMove(move);
		
		Coordinate target = drone.getDestination();
		
		// the distance to the target
		setHeuristic(-1*Math.pow(Math.pow(target.getX() - move.getX(), 2) + 
				Math.pow(target.getY() - move.getY(), 2) + 
				Math.pow(target.getZ() - move.getZ(), 2), 0.5));
		
	}

	public Drone getDrone() {
		return drone;
	}

	public void setDrone(Drone drone) {
		this.drone = drone;
	}

	public Coordinate getMove() {
		return move;
	}

	public void setMove(Coordinate move) {
		this.move = move;
	}

	public double getHeuristic() {
		return heuristic;
	}

	public void setHeuristic(double heuristic) {
		this.heuristic = heuristic;
	}

	@Override
	public String toString(){
		return move.toString()+"; heuristic: "+heuristic;
	}
}
