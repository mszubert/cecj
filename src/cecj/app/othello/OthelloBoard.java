package cecj.app.othello;

import games.SimpleBoard;

import java.util.Arrays;

public class OthelloBoard extends SimpleBoard {

	public static final int BOARD_SIZE = 8;
	
	public static final int NUM_DIRECTIONS = 8;
	public static final int ROW_DIR[] = { -1, -1, -1, 0, 0, 1, 1, 1 };
	public static final int COL_DIR[] = { -1, 0, 1, -1, 1, -1, 0, 1 };


	public OthelloBoard() {
		super(BOARD_SIZE);
	}

	@Override
	public void reset() {
		for (int row = 0; row < board.length; row++) {
			Arrays.fill(board[row], EMPTY);
		}

		initBoard();		
	}
	
	@Override
	public OthelloBoard clone() {
		OthelloBoard clone = new OthelloBoard();
		for (int row = 1; row <= BOARD_SIZE; row++) {
			for (int col = 1; col <= BOARD_SIZE; col++) {
				clone.board[row][col] = board[row][col];
			}
		}
		return clone;
	}
	
	private void initBoard() {
		setPiece(4, 4, WHITE);
		setPiece(4, 5, BLACK);
		setPiece(5, 4, BLACK);
		setPiece(5, 5, WHITE);
	}
	
	public int getPiece(int row, int col, int dir, int dist) {
		return getPiece(row + dist * ROW_DIR[dir], col + dist * COL_DIR[dir]);
	}

	public void setPiece(int row, int col, int dir, int dist, int player) {
		setPiece(row + dist * ROW_DIR[dir], col + dist * COL_DIR[dir], player);
	}
	
	public OthelloMove getShiftedMove(int row, int col, int dir, int dist) {
		return new OthelloMove(row + dist * ROW_DIR[dir], col + dist * COL_DIR[dir]);
	}
}
