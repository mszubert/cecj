package cecj.app.go;

import games.Board;
import games.SimpleBoard;

import java.util.Arrays;

public class GoBoard extends SimpleBoard {

	public static final int BOARD_SIZE = 5;

	public static final int NUM_DIRECTIONS = 4;
	public static final int ROW_DIR[] = { -1, 0, 0, 1 };
	public static final int COL_DIR[] = { 0, 1, -1, 0 };

	public GoBoard() {
		super(BOARD_SIZE);
	}

	public boolean isWall(int row, int col) {
		return (getPiece(row, col) == WALL);
	}

	@Override
	public void reset() {
		Arrays.fill(board[0], WALL);
		for (int row = 1; row <= BOARD_SIZE; row++) {
			Arrays.fill(board[row], EMPTY);
			board[row][0] = WALL;
			board[row][BOARD_SIZE + 1] = WALL;
		}
		Arrays.fill(board[BOARD_SIZE + 1], WALL);
	}

	@Override
	public GoBoard clone() {
		GoBoard clone = new GoBoard();
		for (int row = 0; row < board.length; row++) {
			System.arraycopy(board[row], 0, clone.board[row], 0, board[row].length);
		}
		return clone;
	}

	public Board createAfterState(int row, int col, int player) {
		GoBoard clonedBoard = clone();
		clonedBoard.board[row][col] = player;

		int cc[][] = new int[BOARD_SIZE + BOARD_MARGIN][BOARD_SIZE + BOARD_MARGIN];
		int ccNum = 0;
		int tr, tc;

		for (int dir = 0; dir < NUM_DIRECTIONS; dir++) {
			tr = row + ROW_DIR[dir];
			tc = col + COL_DIR[dir];
			if (!isEmpty(tr, tc) && !isWall(tr, tc) && board[tr][tc] != player && cc[tr][tc] == 0) {
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

		return clonedBoard;
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
