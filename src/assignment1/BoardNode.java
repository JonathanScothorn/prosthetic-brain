package assignment1;

public class BoardNode {

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
		board[x][y] = value;
	}
	
	public int getTile(int x, int y){
		return board[x][y];
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
		String output = "";
		for(int i=0; i<board.length; i++){
			for(int j=0; j<board[0].length; j++){
				output+=board[i][j];
				output+="-";
			}
			output+="\n";
		}
		return output;
	}
	
}
