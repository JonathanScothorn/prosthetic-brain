package assignment1;

import java.util.Comparator;

public class NodeComparator implements Comparator<Node>{

	@Override
	public int compare(Node n1, Node n2){
		if(n1.getTotalCost()>n2.getTotalCost()){
			return 1;
		} else {
			return -1;
		}
	}
	
}
