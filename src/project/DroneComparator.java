package project;

import java.util.Comparator;

public class DroneComparator implements Comparator<Drone> {

	@Override
	public int compare(Drone d1, Drone d2) {
		
		if(d1.getArrivalTime() > d2.getArrivalTime()){
			return 1;
		} else if(d2.getArrivalTime() > d1.getArrivalTime()){
			return -1;
		}
		
		return 0;
	}

}
