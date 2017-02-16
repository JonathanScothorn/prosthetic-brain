package assignment1;

import java.util.Comparator;

public class BridgeNodeComparator implements Comparator<BridgeNode>{

	@Override
	public int compare(BridgeNode n1, BridgeNode n2){
		if(n1.getTotalCost()>n2.getTotalCost()){
			return 1;
		} else {
			return -1;
		}
	}
	
}
