package assignment1;

import java.util.Arrays;

public class BoardNode extends Node {

	private BoardNode parent;
	private int[][] board;
	private int timeToReach;
	private int totalCost;
	
	public BoardNode(int[][] board, int timeTaken, int heuristic){
		this(board, timeTaken, heuristic, null);
	}
	
	public BoardNode(int[][] board, int timeTaken, int heuristic, BoardNode parent){
		this.board = board;
		timeToReach = timeTaken;
		setTotalCost(timeToReach+heuristic);
		this.setParent(parent);
	}
	
	public int[][] getBoard(){
		return board;
	}
	
	public void setTile(int x, int y, int value){
		board[y][x] = value;
	}
	
	public int getTile(int x, int y){
		return board[y][x];
	}
	
	public int getTimeToReach() {
		return timeToReach;
	}

	public void setTimeToReach(int timeToReach) {
		this.timeToReach = timeToReach;
	}

	public int getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(int totalCost) {
		this.totalCost = totalCost;
	}

	public BoardNode getParent() {
		return parent;
	}

	public void setParent(BoardNode parent) {
		this.parent = parent;
	}
	
	@Override
	public String toString(){
		String output = "\n";
		for(int i=0; i<board.length; i++){
			for(int j=0; j<board[0].length; j++){//search using length of first row
				output+=board[i][j];
				output+="-";
			}
			output+="\n";
		}
		return output;
	}
	
	public boolean compare(Node n){
		if(n.getClass().equals(this.getClass())){
			BoardNode node = (BoardNode) n;
			if(Arrays.deepEquals(getBoard(), node.getBoard())){
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
		
		
	}
	
	public BoardNode createChild(){
		
		int[][] newBoard = new int[board.length][];
		
		for(int i=0; i<board.length; i++){
			newBoard[i] = board[i].clone();
		}
		
		return new BoardNode(newBoard, timeToReach, totalCost, this);
	}
	
}
