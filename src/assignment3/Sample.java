package assignment3;

import java.util.ArrayList;

public class Sample {

	private ArrayList<Boolean> features;
	
	public Sample(Sample original){
		
		this.features = (ArrayList<Boolean>) original.getFeatures().clone();
		
	}
	
	public Sample(ArrayList<Boolean> features){
		
		this.features = features;
		
	}
	
	public ArrayList<Boolean> getFeatures(){
		return this.features;
	}
	
	public void setFeatures(ArrayList<Boolean> features){
		this.features = features;
	}
	
}
