package cecj.app.othello;

import games.Board;
import games.SimpleBoard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

	public Board createAfterState(int row, int col, int player) {
		OthelloBoard clonedBoard = clone();

		List<Integer> directions = clonedBoard.findDirections(row, col, player);
		for (int dir : directions) {
			clonedBoard.setPiece(row, col, player);
			for (int dist = 1; clonedBoard.getPiece(row, col, dir, dist) == OthelloGame
					.getOpponent(player); dist++) {
				clonedBoard.setPiece(row, col, dir, dist, player);
			}
		}

		return clonedBoard;
	}

	List<Integer> findDirections(int row, int col, int player) {
		List<Integer> directions = new ArrayList<Integer>();

		if (isEmpty(row, col)) {
			for (int dir = 0; dir < NUM_DIRECTIONS; dir++) {
				int dist = 1;
				while (getPiece(row, col, dir, dist) == OthelloGame.getOpponent(player)) {
					dist++;
				}

				if (dist > 1 && getPiece(row, col, dir, dist) == player) {
					directions.add(dir);
				}
			}
		}
		return directions;
	}
}
