package assignment1;

public class BridgeNode {
	
	private boolean[] peopleLeft;
	private boolean boatLeft;
	
	public BridgeNode(boolean[] people, boolean boatLeft){
		peopleLeft = people;
		this.setBoatLeft(boatLeft);
	}
	
	public void setPerson(int personNumber, boolean value){
		peopleLeft[personNumber] = value;
	}
	
	public boolean[] getPeople(){
		return peopleLeft;
	}

	public boolean getBoatLeft() {
		return boatLeft;
	}
	
	public boolean getPerson(int index){
		return peopleLeft[index]; //potential array out of bounds
	}
	
	public void setPerson(int index){
		peopleLeft[index] = !peopleLeft[index]; // array out of bounds potential
	}

	public void setBoatLeft(boolean boatLeft) {
		this.boatLeft = boatLeft;
	}
	
	public int getNumPeople(){
		return peopleLeft.length;
	}

}
