package assignment2;

import java.util.ArrayList;

public class Node {

	private Board boardState;
	private int heuristic;
	private Node parent;
	private int alphaOrBeta;
	private boolean isMaxNode; // false indicates a min node
	private ArrayList<Move> moveList;
	private int playerNumber;
	private Move bestNextMove;
	private Move createdFrom;
	
	public Node(Board board, ArrayList<Move> moveList, boolean isMaxNode, int playerNumber){
		
		this(board, moveList, isMaxNode, null, null, playerNumber);
	}
	
	public Node(Board board, ArrayList<Move> moveList, boolean isMaxNode, Node parent, Move createdFrom, int playerNumber){
		
		this.setBoardState(board);
		this.setMoveList(moveList);
		this.setParent(parent);
		this.createdFrom = createdFrom;
		this.setIsMaxNode(isMaxNode);
		this.setPlayerNumber(playerNumber);
		setBestNextMove(null);
		
		if(isMaxNode){
			setAlphaOrBeta(Integer.MIN_VALUE); // represents an alpha value, initialized to an arbitrarily small value
		} else {
			setAlphaOrBeta(Integer.MAX_VALUE); // represents a beta value, initialized to an arbitrarily large value
		}
	}

	public Board getBoardState() {
		return boardState;
	}

	public void setBoardState(Board boardState) {
		this.boardState = boardState;
	}

	public ArrayList<Move> getMoveList() {
		return moveList;
	}

	public void setMoveList(ArrayList<Move> moveList) {
		this.moveList = moveList;
	}

	public int getPlayerNumber() {
		return playerNumber;
	}

	public void setPlayerNumber(int playerNumber) {
		this.playerNumber = playerNumber;
	}

	public boolean getIsMaxNode() {
		return isMaxNode;
	}

	public void setIsMaxNode(boolean isMaxNode) {
		this.isMaxNode = isMaxNode;
	}

	public int getAlphaOrBeta() {
		return alphaOrBeta;
	}

	public void setAlphaOrBeta(int alphaOrBeta) {
		this.alphaOrBeta = alphaOrBeta;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Move getBestNextMove() {
		return bestNextMove;
	}

	public void setBestNextMove(Move bestNextMove) {
		this.bestNextMove = bestNextMove;
	}
	
	@Override
	public String toString(){
		return createdFrom.toString();
	}
	
}
