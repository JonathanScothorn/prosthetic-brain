package assignment2;

public class Move {
	
	private int initialX;
	private int initialY;
	private int finalX;
	private int finalY;
	private int tokensToMove;
	
	public Move(int x1, int y1, int x2, int y2, int tokens){
		setInitialX(x1);
		setInitialY(y1);
		setFinalX(x2);
		setFinalY(y2);
		setTokensToMove(tokens);
	}

	public int getInitialX() {
		return initialX;
	}

	public void setInitialX(int initialX) {
		this.initialX = initialX;
	}

	public int getInitialY() {
		return initialY;
	}

	public void setInitialY(int initialY) {
		this.initialY = initialY;
	}

	public int getFinalX() {
		return finalX;
	}

	public void setFinalX(int finalX) {
		this.finalX = finalX;
	}

	public int getFinalY() {
		return finalY;
	}

	public void setFinalY(int finalY) {
		this.finalY = finalY;
	}

	public int getTokensToMove() {
		return tokensToMove;
	}

	public void setTokensToMove(int tokensToMove) {
		this.tokensToMove = tokensToMove;
	}
	
	public boolean equals(Move m){
		if(m.getInitialX() == initialX && m.getInitialY() == initialY && m.getFinalX() == finalX && m.getFinalY() == finalY && m.getTokensToMove() == tokensToMove){
			return true;
		}
		return false;
	}
	
	@Override
	public String toString(){
		return "Move: "+initialX+","+initialY+" to "+finalX+","+finalY+" with "+tokensToMove+" tokens moved.";
	}

}
