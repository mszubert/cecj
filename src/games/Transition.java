package games;

public class Transition {

	private Board previousBoard;
	private Board nextBoard;
	
	public Transition(Board previousBoard, Board nextBoard) {
		this.nextBoard = nextBoard;
		this.previousBoard = previousBoard;
	}
	
	public Board getPreviousBoard() {
		return previousBoard;
	}

	public Board getNextBoard() {
		return nextBoard;
	}
}
