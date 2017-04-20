package project;

public class Drone extends SimulationObject {
	
	private Coordinate arrival;
	private Coordinate current;
	private Coordinate destination;

	private double xVelocity;
	private double yVelocity;
	private double zVelocity;
	private double xVelocityMax;
	private double yVelocityMax;
	private double zVelocityMax;
	
	private double xAcceleration;
	private double yAcceleration;
	private double zAcceleration;
	private double xAccelerationMax;
	private double yAccelerationMax;
	private double zAccelerationMax;
	
	private double arrivalTime;
	
	public Drone(double xVelocityMaximum, double yVelocityMaximum, double zVelocityMaximum, double xAccelerationMaximum,
			double yAccelerationMaximum, double zAccelerationMaximum, int xArrival, int yArrival, int zArrival, 
			int xDestination, int yDestination, int zDestination, double arrivalTime){
		
		setxVelocityMax(xVelocityMaximum);
		setyVelocityMax(yVelocityMaximum);
		setzVelocityMax(yVelocityMaximum);
		setxAccelerationMax(xAccelerationMaximum);
		setyAccelerationMax(yAccelerationMaximum);
		setzAccelerationMax(zAccelerationMaximum);
		
		setxVelocity(0);
		setyVelocity(0);
		setzVelocity(0);
		setxAcceleration(0);
		setyAcceleration(0);
		setzAcceleration(0);
		
		setToken("D");
		
		setArrivalTime(arrivalTime);
		
		destination = new Coordinate(xDestination, yDestination, zDestination);
		setArrival(new Coordinate(xArrival, yArrival, zArrival));
		current = null;
	}

	
	public double getxVelocity() {
		return xVelocity;
	}
	public void setxVelocity(double xVelocity) {
		this.xVelocity = xVelocity;
	}
	public double getyVelocity() {
		return yVelocity;
	}
	public void setyVelocity(double yVelocity) {
		this.yVelocity = yVelocity;
	}
	public double getzVelocity() {
		return zVelocity;
	}
	public void setzVelocity(double zVelocity) {
		this.zVelocity = zVelocity;
	}
	public double getxVelocityMax() {
		return xVelocityMax;
	}
	public void setxVelocityMax(double xVelocityMax) {
		this.xVelocityMax = xVelocityMax;
	}
	public double getyVelocityMax() {
		return yVelocityMax;
	}
	public void setyVelocityMax(double yVelocityMax) {
		this.yVelocityMax = yVelocityMax;
	}
	public double getzVelocityMax() {
		return zVelocityMax;
	}
	public void setzVelocityMax(double zVelocityMax) {
		this.zVelocityMax = zVelocityMax;
	}
	public double getxAcceleration() {
		return xAcceleration;
	}
	public void setxAcceleration(double xAcceleration) {
		this.xAcceleration = xAcceleration;
	}
	public double getyAcceleration() {
		return yAcceleration;
	}
	public void setyAcceleration(double yAcceleration) {
		this.yAcceleration = yAcceleration;
	}
	public double getzAcceleration() {
		return zAcceleration;
	}
	public void setzAcceleration(double zAcceleration) {
		this.zAcceleration = zAcceleration;
	}
	public double getxAccelerationMax() {
		return xAccelerationMax;
	}
	public void setxAccelerationMax(double xAccelerationMax) {
		this.xAccelerationMax = xAccelerationMax;
	}
	public double getyAccelerationMax() {
		return yAccelerationMax;
	}
	public void setyAccelerationMax(double yAccelerationMax) {
		this.yAccelerationMax = yAccelerationMax;
	}
	public double getzAccelerationMax() {
		return zAccelerationMax;
	}
	public void setzAccelerationMax(double zAccelerationMax) {
		this.zAccelerationMax = zAccelerationMax;
	}
	
	
	public Coordinate getDestination() {
		return destination;
	}

	public void setDestination(Coordinate destination) {
		this.destination = destination;
	}


	public double getArrivalTime() {
		return arrivalTime;
	}


	public void setArrivalTime(double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	
	public String printContents(){
		String output = "";
		
		output+="Velocity: "+xVelocity+","+yVelocity+","+zVelocity+"\n";
		output+="MaxVelocity: "+xVelocityMax+","+yVelocityMax+","+zVelocityMax+"\n";
		output+="Acceleration: "+xAcceleration+","+yAcceleration+","+zAcceleration+"\n";
		output+="MaxAcceleration: "+xAccelerationMax+","+yAccelerationMax+","+zAccelerationMax+"\n";
		output+="Arrival Time: "+arrivalTime+"\n";
		output+="Arrival Location: "+arrival.toString()+"\n";
		output+="Destination: "+destination.toString()+"\n";
		
		return output;
	}


	public Coordinate getArrival() {
		return arrival;
	}


	public void setArrival(Coordinate arrival) {
		this.arrival = arrival;
	}


	public Coordinate getCurrent() {
		return current;
	}


	public void setCurrent(Coordinate current) {
		this.current = current;
	}
}
