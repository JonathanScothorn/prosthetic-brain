package assignment1;

public class BridgeNode {
	
	private BridgeNode parent;
	private boolean[] peopleLeft;
	private boolean torchLeft;
	private int timeToReach;
	
	
	public BridgeNode(boolean[] people, boolean torchLeft, int timeTaken){
		this(people, torchLeft, null, timeTaken);
	}
	
	public BridgeNode(boolean[] people, boolean torchLeft, BridgeNode parent, int timeTaken){
		peopleLeft = people;
		this.setTorchLeft(torchLeft);
		this.parent = parent;
		timeToReach = timeTaken;
	}
	
	public void setPerson(int personNumber, boolean value){
		peopleLeft[personNumber] = value;
	}
	
	public boolean[] getPeople(){
		return peopleLeft;
	}

	public boolean getTorchLeft() {
		return torchLeft;
	}
	
	public boolean getPerson(int index){
		return peopleLeft[index]; //potential array out of bounds
	}
	
	public void setPerson(int index){
		peopleLeft[index] = !peopleLeft[index]; // array out of bounds potential
	}

	public void setTorchLeft(boolean torchLeft) {
		this.torchLeft = torchLeft;
	}
	
	public int getNumPeople(){
		return peopleLeft.length;
	}
	
	public BridgeNode getParent(){
		return parent;
	}
	
	public BridgeNode copy(){
		return new BridgeNode(peopleLeft.clone(), torchLeft, this, timeToReach);
	}
	
	@Override
	public String toString(){
		
		String output = "";
		
		for(boolean b: peopleLeft){
			if(b){
				output += "T";
			} else {
				output += "F";
			}
		}
		
		output+=torchLeft;
		
		return output;
	}

	public int getTimeToReach() {
		return timeToReach;
	}

	public void setTimeToReach(int timeToReach) {
		this.timeToReach = timeToReach;
	}

}
