package assignment1;

public abstract class Node {
	
	public abstract Node getParent();
	
	public abstract int getTimeToReach();
	
	public abstract int getTotalCost();

	public abstract boolean compare(Node n);
	
}
