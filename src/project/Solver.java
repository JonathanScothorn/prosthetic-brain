package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Solver {
	
	public Solver(){
		
	}
	
	public Model readFile(String filename, int xDimension, int yDimension, int zDimension){
		
		Scanner scanner;
		Model output = new Model(xDimension, yDimension, zDimension);
		
		try {
			scanner = new Scanner(new File(filename));
		} catch (FileNotFoundException e){
			System.out.println("File "+filename+" could not be opened.");
			return null;
		}
		
		scanner.useDelimiter(",");
				
		
		int maxEntries = xDimension*yDimension*zDimension;
		int entries = 0;
		int x = 0;
		int y = 0;
		int z = 0;
		while(scanner.hasNext() && entries < maxEntries){
			entries++;
			String csvValue = scanner.next();
			String trimmed = csvValue.trim();
			int value = Integer.parseInt(trimmed);
			
			// only accept boolean values
			SimulationObject tempObject;
			
			if(value == 1){
				tempObject = new Obstacle();
			} else if(value == 0){
				tempObject = null;
			} else {
				System.out.println("Invalid entry in the input file.");
				return null;
			}
			
			if(x<xDimension){
				output.setObject(x, y, z, tempObject); 
				x++;
			} else {
				x=0;
				y++;
				if(y<yDimension){
					output.setObject(x, y, z, tempObject); 
				} else {
					y=0; 
					z++;
					output.setObject(x, y, z, tempObject); 
				}
				x++;
			}
		}
		
		
		
		scanner.close();
		
		return output;
		
	}
	
	private void addDrones(Model model, String filename){
		
		
		
	}

	public static void main(String[] args) {
		
		Solver s = new Solver();
		Model m = s.readFile("C:/Users/Jon/git/prosthetic-brain/testfiles/Layout1.csv", 4,4,4);
		System.out.println(m.toString());

	}

}
