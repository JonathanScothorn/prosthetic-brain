package project;

public class Obstacle extends SimulationObject {

	
	public Obstacle(){
		
		setxVelocityMax(0);
		setyVelocityMax(0);
		setzVelocityMax(0);
		setxAccelerationMax(0);
		setyAccelerationMax(0);
		setzAccelerationMax(0);
		
		setxVelocity(0);
		setyVelocity(0);
		setzVelocity(0);
		setxAcceleration(0);
		setyAcceleration(0);
		setzAcceleration(0);
		
		setToken("X");
	}
}
