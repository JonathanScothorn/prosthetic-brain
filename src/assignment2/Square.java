package assignment2;

import java.util.ArrayList;

public class Square {
	
	// array contents should be a single -1 for out of bounds, a single 0 for empty, or some combination of 1s, 2s, etc. to represent each player's pieces
	private ArrayList<Integer> pieces;
	
	// initialize square with only one entry
	public Square(int contents){
		pieces = new ArrayList<Integer>();
		//System.out.println(contents);
		addPiece(contents);
	}
	
	public void addPiece(int piece){
		pieces.add(piece);
	}
	
	public void capturePieces(){
		pieces.clear();
	}
	
	public int removePiece(){
		return pieces.remove(pieces.size());
	}
	
	@Override
	public String toString(){
		String output = "";
		for(int piece: pieces){
			output+=piece;
			//output+="-";
		}
		return output;
	}

}
