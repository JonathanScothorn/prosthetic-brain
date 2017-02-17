package assignment1;

import java.util.Arrays;

public class BridgeNode extends Node{
	
	private BridgeNode parent;
	private boolean[] peopleLeft;
	private boolean torchLeft;
	private int timeToReach;
	private int totalCost;
	
	
	public BridgeNode(boolean[] people, boolean torchLeft, int timeTaken, int heuristic){
		this(people, torchLeft, null, timeTaken, heuristic);
	}
	
	public BridgeNode(boolean[] people, boolean torchLeft, BridgeNode parent, int timeTaken, int heuristic){
		peopleLeft = people;
		this.setTorchLeft(torchLeft);
		this.parent = parent;
		timeToReach = timeTaken;
		totalCost = timeToReach + heuristic;
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
	
	public BridgeNode createChild(){
		return new BridgeNode(peopleLeft.clone(), torchLeft, this, timeToReach, totalCost);
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

	public int getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(int totalCost) {
		this.totalCost = totalCost;
	}
	
	public boolean compare(Node n){
		if(n.getClass().equals(this.getClass())){
			BridgeNode node = (BridgeNode) n;
			if(Arrays.equals(getPeople(), node.getPeople()) && getTorchLeft() == node.getTorchLeft()){
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
