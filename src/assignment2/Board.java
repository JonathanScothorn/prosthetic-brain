package assignment2;

public class Board {

	private Square[][] grid;
	
	public Board(){
		grid = new Square[8][8];
		//initialize the board here
		grid[3][3] = new Square(2);
	}
	
	public Square getSquare(int x, int y){
		if(x + y < 2){
			System.out.println("Top left corner.");
			return null;
		} else if(x + y > 12){
			System.out.println("Bottom right corner.");
			return null;
		} else if((x==7 && (y == 0 || y == 1)) || (x == 6 && y == 0)){
			System.out.println("Top right corner.");
			return null;
		} else if((x==0 && (y == 6 || y == 7)) || (x == 1 && y == 7)){
			System.out.println("Bottom left corner.");
			return null;
		} else {
			return grid[y][x];
		}
		
	}
	
	@Override
	public String toString(){
		String output = "";
		for(int y=0; y<8; y++){
			for(int x=0; x<8; x++){
				output+=getSquare(x,y).toString();
			}
			output+="\n";
		}
		return output;
	}
	
}
