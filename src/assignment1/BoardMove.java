package assignment1;

public class BoardMove {

	private int x1;
	private int x2;
	private int y1;
	private int y2;
	
	public BoardMove(int x1, int y1, int x2, int y2){
		this.setX1(x1);
		this.setX2(x2);
		this.setY1(y1);
		this.setY2(y2);
	}

	public int getX1() {
		return x1;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int getX2() {
		return x2;
	}

	public void setX2(int x2) {
		this.x2 = x2;
	}

	public int getY1() {
		return y1;
	}

	public void setY1(int y1) {
		this.y1 = y1;
	}

	public int getY2() {
		return y2;
	}

	public void setY2(int y2) {
		this.y2 = y2;
	}
	
	@Override
	public String toString(){
		return x1+","+y1+"-"+x2+","+y2;
	}
	
	public boolean compare(BoardMove move){
		if(x1==move.getX1() && x2==move.getX2() && y1==move.getY1() && y2==move.getY2()){
			return true;
		} else if(x1==move.getX2() && x2==move.getX1() && y1==move.getY2() && y2==move.getY1()){
			return true;
		}
		return false;
	}
	
}
