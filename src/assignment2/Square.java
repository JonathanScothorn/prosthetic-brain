package assignment2;

import java.util.ArrayList;
import java.util.Stack;

public class Square {
	
	// array contents should be a single -1 for out of bounds, a single 0 for empty, or some combination of 1s, 2s, etc. to represent each player's pieces
	private ArrayList<Integer> pieces;
	
	public Square(){
		pieces = new ArrayList<Integer>();
	}
	
	// initialize square with only one entry
	public Square(int contents){
		pieces = new ArrayList<Integer>();
		//System.out.println(contents);
		addPiece(contents);
	}
	
	// copy constructor
	public Square(Square old){
		pieces = (ArrayList<Integer>) old.getPieces().clone();
	}
	
	public void addPiece(int piece){
		pieces.add(piece);
	}
	
	/////////////////////////////////////// make sure that they are added in the right order!
	// pieces are added, and some may be captured
	public int addPieces(ArrayList<Integer> newPieces){
		pieces.addAll(newPieces);
		return capturePieces();
	}
	
	// pieces are captured from the "bottom" of the stack and removed from the board
	// returns the number of pieces captured
	private int capturePieces(){
		int piecesCaptured = 0;
		
		while(pieces.size() > 5){
			//System.out.println("Pieces remaining "+pieces.size());
			pieces.remove(0);
			piecesCaptured++;
		}
		return piecesCaptured;
	}
	
	// remove pieces from the "top" of the stack, which will be moved elsewhere
	// returns the pieces that were removed so that they can be added to another stack
	public ArrayList<Integer> removePieces(int piecesToRemove){
		
		if(piecesToRemove <= pieces.size()){
		
			ArrayList<Integer> removed = new ArrayList<Integer>();
			Stack<Integer> tempStack = new Stack<Integer>();
			
			// push the removed elements onto a temporary stack, to preserve the ordering
			for(int i=0; i<piecesToRemove; i++){
				tempStack.push(pieces.remove(pieces.size()-1));
			}
			
			// pop the elements from the temporary stack into the new array list so they are in the right order
			for(int i=0; i<piecesToRemove; i++){
				removed.add(tempStack.pop());
			}
			
			return removed;
		} else {
			System.out.println("Error: too many pieces ("+piecesToRemove+") were to be removed.  Square had "+getSize()+" pieces.");
			return null;
		}
	}
	
	public int getController(){
		if(pieces.size()==0){
			return 0;
		}
		return pieces.get(pieces.size()-1);
	}
	
	public int getSize(){
		return pieces.size();
	}
	
	public ArrayList<Integer> getPieces(){
		return pieces;
	}
	
	public int getPiece(int i){
		return pieces.get(i);
	}
	
	public boolean equals(Square s){
		
		if(getSize() != s.getSize()){
			return false;
		}
		
		for(int i=0; i<getSize(); i++){
			if(getPiece(i) != s.getPiece(i)){
				return false;
			}
		}
		
		return true;
	}
	
	public int getPiecesByPlayer(int playerToken){
		int output = 0;
		for(int piece: pieces){
			if(piece == playerToken){
				output++;
			}
		}
		return output;
		
	}
	
	@Override
	public String toString(){
		if(pieces.size()==0){
			return "0";
		}
		String output = "";
		for(int piece: pieces){
			output+=piece;
			//output+="-";
		}
		return output;
	}

}
