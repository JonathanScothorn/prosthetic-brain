package assignment3;

import java.util.Comparator;

public class WeightedBranchComparator implements Comparator<WeightedBranch>{

	@Override
	public int compare(WeightedBranch b1, WeightedBranch b2){
		
		if(b1.getWeight() > b2.getWeight()){
			return 1;
		} else if (b1.getWeight() < b2.getWeight()){
			return -1;
		} else {
			if(b1.getNode1() > b2.getNode1()){
				return 1;
			} else if(b1.getNode1() < b2.getNode1()){
				return -1;
			} else {
				if(b1.getNode2() > b2.getNode2()){
					return 1;
				} else {
					return -1;
				}
			}
		}
		
	}
	
}
