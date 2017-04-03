package assignment3;

import java.util.Arrays;

public class Sample {

	private Boolean[] features; // true is 1, false is 0
	
	public Sample(Sample original){
		
		this.features = original.getFeatures().clone();
		
	}
	
	public Sample(Boolean[] features){
		
		this.features = features;
		
	}
	
	public Boolean[] getFeatures(){
		return this.features;
	}
	
	public void setFeatures(Boolean[] features){
		this.features = features;
	}
	
	public Boolean getFeature(int index){
		return this.features[index];
	}
	
	@Override
	public String toString(){
		
		return "Sample with features vector: "+Arrays.toString(features);
		
	}
}
