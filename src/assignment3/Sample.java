package assignment3;

import java.util.ArrayList;
import java.util.Arrays;

public class Sample {

	private ArrayList<Boolean> features; // true is 1, false is 0
	
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
	
	
	@Override
	public String toString(){
		
		return "Sample with features vector: "+Arrays.toString(features.toArray());
		
	}
}
