package games;

public class GameMove implements Comparable<GameMove> {

	private int player;

	private int row;
	private int col;

	private Board previousBoard;
	private Board nextBoard;

	public GameMove(int row, int col, Board previousBoard, int player) {
		this.row = row;
		this.col = col;
		this.player = player;
		this.previousBoard = previousBoard;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public Board getAfterState() {
		if (nextBoard == null) {
			nextBoard = previousBoard.createAfterState(row, col, player);
		}

		return nextBoard;
	}

	public int compareTo(GameMove o) {
		if (getRow() != o.getRow()) {
			return getRow() - o.getRow();
		} else {
			return getCol() - o.getCol();
		}
	}

	@Override
	public String toString() {
		return "(" + row + ", " + col + ")";
	}

	@Override
	public boolean equals(Object other) {
		GameMove o = (GameMove) other;
		return (row == o.row && col == o.col && player == o.player && ((previousBoard == null && o.previousBoard == null) || (previousBoard
				.equals(o.previousBoard))));
	}

}
