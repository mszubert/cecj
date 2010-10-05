package cecj.app.othello;

import games.Board;
import games.GameMove;

public class OthelloMove implements GameMove {
	private int row;
	private int col;

	public OthelloMove(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof OthelloMove))
			return false;
		OthelloMove move = (OthelloMove) obj;
		return (this.row == move.row && this.col == move.col);
	}
	
	@Override
	public String toString() {
		return "(" + row + ", " + col + ")";
	}
	
	public int compareTo(GameMove o) {
		if (getRow() != o.getRow()) {
			return getRow() - o.getRow();
		} else {
			return getCol() - o.getCol();
		}
	}

	public Board getAfterState() {
		// TODO Auto-generated method stub
		return null;
	}
}
