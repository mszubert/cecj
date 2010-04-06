package cecj.app.go;

import java.util.Arrays;

import games.Board;
import games.Player;

public class GoBoard implements Board {

	protected static final int BOARD_MARGIN = 2;
	protected static final int BOARD_SIZE = 5;

	public static final int EMPTY = -1;
	public static final int BLACK = 0;
	public static final int WHITE = 1;
	public static final int WALL = 2;

	public static final int NUM_DIRECTIONS = 4;
	public static final int ROW_DIR[] = { -1, 0, 0, 1 };
	public static final int COL_DIR[] = { 0, 1, -1, 0 };

	private int board[][];

	public GoBoard() {
		board = new int[BOARD_SIZE + BOARD_MARGIN][BOARD_SIZE + BOARD_MARGIN];
		reset();
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

	public double evaluate(Player player) {
		double result = 0;
		for (int row = 1; row <= BOARD_SIZE; row++) {
			for (int col = 1; col <= BOARD_SIZE; col++) {
				result += getValueAt(row, col) * player.getValue(row, col);
			}
		}
		return result;
	}

	public int getPiece(int row, int col) {
		return board[row][col];
	}

	public void setPiece(int row, int col, int player) {
		board[row][col] = player;
	}

	public int getSize() {
		return BOARD_SIZE;
	}

	public static int size() {
		return BOARD_SIZE;
	}

	public int getValueAt(int row, int col) {
		return getColorValue(board[row][col]);
	}

	public int getColorValue(int color) {
		if (color == BLACK) {
			return 1;
		} else if (color == WHITE) {
			return -1;
		} else {
			return 0;
		}
	}

	public boolean isEmpty(int row, int col) {
		return (getPiece(row, col) == EMPTY);
	}

	public boolean isWall(int row, int col) {
		return (getPiece(row, col) == WALL);
	}

	@Override
	public GoBoard clone() {
		GoBoard clone = new GoBoard();
		for (int row = 0; row < board.length; row++) {
			System.arraycopy(board[row], 0, clone.board[row], 0, board[row].length);
		}
		return clone;
	}

	public void reset() {
		Arrays.fill(board[0], WALL);
		for (int row = 1; row <= BOARD_SIZE; row++) {
			Arrays.fill(board[row], EMPTY);
			board[row][0] = WALL;
			board[row][BOARD_SIZE + 1] = WALL;
		}
		Arrays.fill(board[BOARD_SIZE + 1], WALL);
	}

	public GoMove createMove(int row, int col, int currentPlayer) {
		GoBoard clonedBoard = clone();
		clonedBoard.board[row][col] = currentPlayer;

		int cc[][] = new int[BOARD_SIZE + BOARD_MARGIN][BOARD_SIZE + BOARD_MARGIN];
		int ccNum = 0;
		int tr, tc;

		for (int dir = 0; dir < NUM_DIRECTIONS; dir++) {
			tr = row + ROW_DIR[dir];
			tc = col + COL_DIR[dir];
			if (!isEmpty(tr, tc) && !isWall(tr, tc) && board[tr][tc] != currentPlayer
					&& cc[tr][tc] == 0) {
				ccNum++;
				if (!hasLiberties(clonedBoard, tr, tc, cc, ccNum)) {
					removeStoneGroup(clonedBoard, cc, ccNum);
				}
			}
		}

		ccNum++;
		cc = new int[BOARD_SIZE + BOARD_MARGIN][BOARD_SIZE + BOARD_MARGIN];
		if (!hasLiberties(clonedBoard, row, col, cc, ccNum)) {
			removeStoneGroup(clonedBoard, cc, ccNum);
		}

		return new GoMove(row, col, clonedBoard);
	}

	private void removeStoneGroup(GoBoard clonedBoard, int[][] cc, int ccNum) {
		for (int row = 1; row <= BOARD_SIZE; row++) {
			for (int col = 1; col <= BOARD_SIZE; col++) {
				if (cc[row][col] == ccNum) {
					clonedBoard.board[row][col] = EMPTY;
				}
			}
		}
	}

	private boolean hasLiberties(GoBoard clonedBoard, int row, int col, int[][] visited, int num) {
		visited[row][col] = num;
		boolean liberty = false;
		int tr, tc;

		for (int dir = 0; dir < NUM_DIRECTIONS; dir++) {
			tr = row + ROW_DIR[dir];
			tc = col + COL_DIR[dir];
			if (visited[tr][tc] == 0) {
				if (clonedBoard.board[tr][tc] == clonedBoard.board[row][col]) {
					liberty |= hasLiberties(clonedBoard, tr, tc, visited, num);
				} else if (clonedBoard.board[tr][tc] == EMPTY) {
					liberty = true;
				}
			}
		}

		return liberty;
	}

	public boolean isSuicidal(int row, int col, int currentPlayer) {
		board[row][col] = currentPlayer;
		int visited[][] = new int[BOARD_SIZE + BOARD_MARGIN][BOARD_SIZE + BOARD_MARGIN];
		boolean result = !hasLiberties(this, row, col, visited, 1);
		board[row][col] = EMPTY;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		GoBoard other = (GoBoard) obj;
		for (int row = 1; row <= BOARD_SIZE; row++) {
			for (int col = 1; col <= BOARD_SIZE; col++) {
				if (other.board[row][col] != board[row][col]) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("    ");
		for (int i = 1; i <= BOARD_SIZE; i++) {
			builder.append((char) ('A' + i - 1) + " ");
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

	public int countTerritory(int color) {
		int[][] cc = new int[BOARD_SIZE + BOARD_MARGIN][BOARD_SIZE + BOARD_MARGIN];
		int[] territory = new int[BOARD_SIZE * BOARD_SIZE];
		Arrays.fill(territory, -1);

		markTerritory(cc, territory);

		int result = 0;
		for (int row = 1; row <= BOARD_SIZE; row++) {
			for (int col = 1; col <= BOARD_SIZE; col++) {
				if (isEmpty(row, col) && (territory[cc[row][col]] == color)) {
					result++;
				}
			}
		}

		return result;
	}

	public void markTerritory(int[][] cc, int[] territory) {
		int ccNumber = 1;
		for (int row = 1; row <= BOARD_SIZE; row++) {
			for (int col = 1; col <= BOARD_SIZE; col++) {
				if (isEmpty(row, col) && (cc[row][col] == 0)) {
					territory[ccNumber] = -1;
					mark(row, col, cc, territory, ccNumber);
					ccNumber++;
				}
			}
		}
	}

	private void mark(int row, int col, int[][] cc, int[] territory, int ccNumber) {
		cc[row][col] = ccNumber;
		int tr, tc;

		for (int dir = 0; dir < NUM_DIRECTIONS; dir++) {
			tr = row + ROW_DIR[dir];
			tc = col + COL_DIR[dir];

			if (isWall(tr, tc)) {
				continue;
			} else if (isEmpty(tr, tc) && (cc[tr][tc] == 0)) {
				mark(tr, tc, cc, territory, ccNumber);
			} else if (!isEmpty(tr, tc)) {
				if (territory[ccNumber] == -1) {
					territory[ccNumber] = board[tr][tc];
				} else if (territory[ccNumber] != board[tr][tc]) {
					territory[ccNumber] = 2;
				}
			}
		}
	}

	public boolean isSinglePointEye(int row, int col, int currentPlayer) {
		int tr, tc;
		for (int dir = 0; dir < NUM_DIRECTIONS; dir++) {
			tr = row + ROW_DIR[dir];
			tc = col + COL_DIR[dir];

			if (!isWall(tr, tc) && board[tr][tc] != currentPlayer) {
				return false;
			}
		}

		return true;
	}

	public int countLiberties(int color) {
		int result = 0;
		for (int row = 1; row <= BOARD_SIZE; row++) {
			for (int col = 1; col <= BOARD_SIZE; col++) {
				if (!isEmpty(row, col)) {
					continue;
				}

				for (int dir = 0; dir < NUM_DIRECTIONS; dir++) {
					int tr = row + ROW_DIR[dir];
					int tc = col + COL_DIR[dir];
					if (board[tr][tc] == color) {
						result++;
						break;
					}
				}
			}
		}
		
		return result;
	}
}
