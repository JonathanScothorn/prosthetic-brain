package project;

import java.util.Comparator;

public class ModelComparator implements Comparator<Model> {

	@Override
	public int compare(Model m1, Model m2){
		if(m1.getHeuristic() > m2.getHeuristic()){
			return -1;
		} else {
			return 1;
		}
	}
}
