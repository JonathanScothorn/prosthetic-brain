package assignment1;

public class BridgeNode {
	
	private int leftCannibals;
	private int leftMissionaries;
	private boolean boatIsLeft;
	
	public BridgeNode(int c, int m, boolean boat){
		leftCannibals = c;
		leftMissionaries = m;
		boatIsLeft = boat;
	}
	
	public int getCannibals(){
		return leftCannibals;
	}
	
	public int getMissionaries(){
		return leftMissionaries;
	}
	
	public boolean getBoat(){
		return boatIsLeft;
	}

}
