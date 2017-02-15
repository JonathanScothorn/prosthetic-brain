package assignment1;

public class BridgeMove {

	private int person1ToMove;
	private int person2ToMove; // -1 if only one person is moving
	private int time;
	
	public BridgeMove(int p1, int p2, int time){
		
		this.setTime(time);
		setPerson1ToMove(p1);
		setPerson2ToMove(p2);
		
	}

	public int getPerson1ToMove() {
		return person1ToMove;
	}

	public void setPerson1ToMove(int person1ToMove) {
		this.person1ToMove = person1ToMove;
	}

	public int getPerson2ToMove() {
		return person2ToMove;
	}

	public void setPerson2ToMove(int person2ToMove) {
		this.person2ToMove = person2ToMove;
	}
	
	@Override
	public String toString(){
		return ""+person1ToMove+person2ToMove;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
	
}
