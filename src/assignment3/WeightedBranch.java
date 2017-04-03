package assignment3;

public class WeightedBranch {
	
	private int node1;
	private int node2;
	private double weight;
	
	public WeightedBranch(WeightedBranch b){
		this.node1 = b.getNode1();
		this.node2 = b.getNode2();
		this.weight = b.getWeight();
	}
	
	public WeightedBranch(int node1, int node2, double weight){
		this.node1 = node1;
		this.node2 = node2;
		this.weight = weight;
	}
	
	public int getNode1() {
		return node1;
	}
	public void setNode1(int node1) {
		this.node1 = node1;
	}
	public int getNode2() {
		return node2;
	}
	public void setNode2(int node2) {
		this.node2 = node2;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	@Override
	public String toString(){
		
		return "\nBranch from node "+node1+" to node "+node2+" with weight "+weight;
	}

}
