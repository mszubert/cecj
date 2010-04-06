package cecj.app.go;

import games.GameMove;

public class GoMove implements GameMove {

	private int row;
	private int col;
	private GoBoard board;
	
	public GoMove(int row, int col, GoBoard board) {
		this.row = row;
		this.col = col;
		this.board = board;
	}

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}
	
	public GoBoard getResultingBoard() {
		return board;
	}
	
	@Override
	public String toString() {
		return "(" + row + ", " + col + ")";
	}
	
	@Override
	public boolean equals(Object other) {
		GoMove o = (GoMove) other;
		return (row == o.row && col == o.col && ((board == null && o.board == null) || board.equals(o.board)));
	}
	
	public int compareTo(GameMove o) {
		if (getRow() != o.getRow()) {
			return getRow() - o.getRow();
		} else {
			return getCol() - o.getCol();
		}
	}
}
