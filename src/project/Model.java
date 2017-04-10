package project;

public class Model {

	SimulationObject[][][] space;
	
	public Model(int x, int y, int z){
		space = new SimulationObject[z][y][x];
	}
	
	public SimulationObject[][][] getSpace(){
		return space;
	}
	
	public SimulationObject getObject(int x, int y, int z){
		return space[z][y][x];
	}
	
	public void setObject(int x, int y, int z, SimulationObject object){
		space[z][y][x] = object;
	}
	
	@Override
	public String toString(){
		String output = "";
		
		for(int z=0; z<space.length; z++){
			output+="Layer "+z+": \n";
			for(int y=0; y<space[0].length; y++){
				for(int x=0; x<space[0][0].length; x++){
					if(space[z][y][x] != null){
						output+=space[z][y][x].toString();
					} else {
						output+="-";
					}
				}
				output+="\n";
			}
			output+="\n";
		}
		
		return output;
	}
}
