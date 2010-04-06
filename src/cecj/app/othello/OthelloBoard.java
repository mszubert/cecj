package cecj.app.othello;

import java.util.Arrays;
import games.Board;
import games.Player;

public class OthelloBoard implements Board {
	private static final int BOARD_SIZE = 8;
	private static final int BOARD_MARGIN = 2;
	
	private static final int EMPTY = -1;
	
	public static final int BLACK = 0;
	public static final int WHITE = 1;
	
	public static final int NUM_DIRECTIONS = 8;
	public static final int ROW_DIR[] = { -1, -1, -1, 0, 0, 1, 1, 1 };
	public static final int COL_DIR[] = { -1, 0, 1, -1, 1, -1, 0, 1 };

	private int board[][];

	public OthelloBoard() {
		board = new int[BOARD_SIZE + BOARD_MARGIN][BOARD_SIZE + BOARD_MARGIN];
		reset();
	}

	private void initBoard() {
		setPiece(4, 4, WHITE);
		setPiece(4, 5, BLACK);
		setPiece(5, 4, BLACK);
		setPiece(5, 5, WHITE);
	}
	
	public int getSize() {
		return BOARD_SIZE;
	}

	public int countPieces(int player) {
		int count = 0;
		for (int row = 1; row <= BOARD_SIZE; row++) {
			for (int col = 1; col <= BOARD_SIZE; col++) {
				if (getPiece(row, col) == player) {
					count++;
				}
			}
		}
		return count;
	}

	public static int size() {
		return BOARD_SIZE;
	}

	public int getPiece(int row, int col) {
		return board[row][col];
	}

	public void setPiece(int row, int col, int player) {
		board[row][col] = player;
	}

	public boolean isEmpty(int row, int col) {
		return (getPiece(row, col) == EMPTY);
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
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("    ");
		for (int i = 1; i <= BOARD_SIZE; i++) {
			builder.append(i + " ");
		}
		builder.append("\n");
		
		for (int i = 0; i < BOARD_SIZE + BOARD_MARGIN; i++) {
			if (i > 0 && i <= BOARD_SIZE) {
				builder.append(i + " ");
			} else {
				builder.append("  ");				
			}
			for (int j = 0; j < BOARD_SIZE + BOARD_MARGIN; j++) {
				builder.append(((board[i][j] == -1) ? "-" : board[i][j]) + " ");
			}
			builder.append("\n");
		}
		return builder.toString();
	}

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
	
	public double evaluate(Player player) {
		double result = 0;
		for (int row = 1; row <= BOARD_SIZE; row++) {
			for (int col = 1; col <= BOARD_SIZE; col++) {
				result += getValueAt(row, col) * player.getValue(row, col);
			}
		}
		return result;
	}
	
	public int getValueAt(int row, int col) {
		if (board[row][col] == BLACK) {
			return 1;
		} else if (board[row][col] == WHITE) {
			return -1;
		} else {
			return 0;
		}
	}
}
