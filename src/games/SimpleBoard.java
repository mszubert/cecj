package games;

public abstract class SimpleBoard implements Board {

	protected static final int BOARD_MARGIN = 2;

	public static final int EMPTY = -1;
	public static final int BLACK = 0;
	public static final int WHITE = 1;
	public static final int WALL = 2;

	protected int board[][];
	protected int size;

	public SimpleBoard(int size) {
		this.size = size;
		board = new int[size + BOARD_MARGIN][size + BOARD_MARGIN];
		reset();
	}

	public abstract void reset();

	public int countPieces(int player) {
		int count = 0;
		for (int row = 1; row <= size; row++) {
			for (int col = 1; col <= size; col++) {
				if (getPiece(row, col) == player) {
					count++;
				}
			}
		}
		return count;
	}

	public int getPiece(int flatLocation) {
		return getPiece(flatLocation / size + 1, flatLocation % size + 1);
	}
	
	public int getPiece(int row, int col) {
		return board[row][col];
	}

	public void setPiece(int row, int col, int player) {
		board[row][col] = player;
	}

	public int getSize() {
		return size;
	}

	
	public int getValueAt(int flatLocation) {
		return getValueAt(flatLocation / size + 1, flatLocation % size + 1);
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("    ");
		for (int i = 1; i <= size; i++) {
			builder.append((char) ('A' + i - 1) + " ");
		}
		builder.append("\n");

		for (int i = 0; i < size + BOARD_MARGIN; i++) {
			if (i > 0 && i <= size) {
				builder.append(i + " ");
			} else {
				builder.append("  ");
			}
			for (int j = 0; j < size + BOARD_MARGIN; j++) {
				builder.append(((board[i][j] == -1) ? "-" : board[i][j]) + " ");
			}
			builder.append("\n");
		}
		return builder.toString();
	}

	@Override
	public abstract SimpleBoard clone();
}
