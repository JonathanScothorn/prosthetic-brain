package assignment2;

import java.util.Random;

public class Board {

	public static final int BOARD_HEIGHT = 8;
	public static final int BOARD_WIDTH = 8;
	public static final int PLAYERS = 2;
	
	private Square[][] grid;
	
	public Board(){
		
		int totalPlayerTokens = (BOARD_HEIGHT-2)*(BOARD_WIDTH-2);
		
		
		grid = new Square[BOARD_HEIGHT][BOARD_WIDTH];
		
		//initialize the board here
		initializeGrid(totalPlayerTokens);
	}
	
	// assume 2 players, making an indefinite number of players becomes considerably more challenging
	private void initializeGrid(int totalTokens){
		
		Random randomGenerator = new Random();
		int p1TokensPlaced = 0;
		int p2TokensPlaced = 0;
		int maxPlayerTokens = totalTokens/PLAYERS;
		
		for(int y=0; y<BOARD_HEIGHT; y++){
			for(int x=0; x<BOARD_WIDTH; x++){
				//System.out.println(x+"."+y);
				if(isValidCoord(x,y)){
					//initialize squares randomly, but only in the centre of the board
					if(x>=1 && x<BOARD_WIDTH-1 && y>=1 && y<BOARD_HEIGHT-1){
						
						//true represents player 1, false represents player 2
						boolean nextPlayerIsP1 = randomGenerator.nextBoolean();
						//System.out.println("Random value is: "+nextPlayerIsP1);
						if(nextPlayerIsP1){
							//player 1 has been selected to place
							if(p1TokensPlaced < maxPlayerTokens){
								//player 1 has tokens left to place
								p1TokensPlaced++;
								grid[y][x] = new Square(1);
								//System.out.println("Token 1 placed at "+x+","+y);
							} else if(p2TokensPlaced < maxPlayerTokens){
								//player 2 has tokens left to place; only gets here if player 1 has already placed all their tokens
								p2TokensPlaced++;
								grid[y][x] = new Square(2);
								System.out.println("Out of Player 1 Tokens. Token 2 placed at "+x+","+y);
							} else {
								//no one has any tokens left to place, should never get here!
								System.out.println("Error.  Too many tokens have been placed!");
							}
						} else {
							//player 2 has been selected to place
							if(p2TokensPlaced < maxPlayerTokens){
								//player 2 has tokens left to place
								p2TokensPlaced++;
								grid[y][x] = new Square(2);
								//System.out.println("Token 2 placed at "+x+","+y);
							} else if(p1TokensPlaced < maxPlayerTokens){
								//player 1 has tokens left to place; only gets here if player 2 has already placed all their tokens
								p1TokensPlaced++;
								grid[y][x] = new Square(1);
								System.out.println("Out of Player 2 Tokens. Token 1 placed at "+x+","+y);
							} else {
								//no one has any tokens left to place, should never get here!
								System.out.println("Error.  Too many tokens have been placed!");
							}
						}
						//System.out.println("p1 placed: "+p1TokensPlaced+" p2 placed: "+p2TokensPlaced);
						
					} else {
						grid[y][x] = new Square(0);
					}
					
					
				} else {
					grid[y][x] = new Square(-1);
				}
			}
			
		}
		System.out.println("p1 placed: "+p1TokensPlaced+" p2 placed: "+p2TokensPlaced);
	}
	
	public Square getSquare(int x, int y){
		return grid[y][x];
		
	}
	
	private boolean isValidCoord(int x, int y){
		
		/*
		 * Xs are out of bounds, other symbols are described below
		XXTTTTXX
		XCCCCCCX
		LCCCCCCR
		LCCCCCCR
		LCCCCCCR
		LCCCCCCR
		XCCCCCCX
		XXBBBBXX
		*/
		//System.out.println(x+","+y);
		
		//deal with centre square (Cs in the diagram)
		if(x>=1 && x<BOARD_WIDTH-1 && y>=1 && y<BOARD_HEIGHT-1){
			return true;
		}
		
		//deal with top and bottom edges (T and B in the diagram)
		switch(y){
			case 0:
			case BOARD_HEIGHT-1: 
				if(x>=2 && x<BOARD_WIDTH-2){
					return true;
				}
				break;
		}
		
		//deal with left and right edges (L and R in the diagram
		switch(x){
			case 0:
			case BOARD_WIDTH-1: 
				if(y>=2 && y<BOARD_HEIGHT-2){
					return true;
				}
				break;
		}
		
		return false;
	}
	
	@Override
	public String toString(){
		String output = "";
		for(int y=0; y<BOARD_HEIGHT; y++){
			for(int x=0; x<BOARD_WIDTH; x++){
				if(isValidCoord(x,y)){
					//System.out.println(x+","+y+":"+getSquare(x,y).toString());
					output+=getSquare(x,y).toString();
				} else {
					output+="X";
				}
			}
			output+="\n";
		}
		return output;
	}
	
}
