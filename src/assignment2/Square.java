package assignment2;

import java.util.ArrayList;

public class Square {
	
	private ArrayList<Integer> pieces;
	
	public Square(int contents){
		pieces = new ArrayList<Integer>();
		if(contents > 0){
			addPiece(contents);
		}
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
			output+="-";
		}
		return output;
	}

}
