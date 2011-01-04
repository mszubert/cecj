package cecj.app.othello;

import games.Board;
import games.BoardGame;
import games.GameMove;
import games.SimpleBoard;
import games.player.Player;
import games.player.WPCPlayer;

import java.util.ArrayList;
import java.util.List;

public class OthelloGame implements BoardGame {

	private static final int NUM_PLAYERS = 2;
	private static final int MAX_PLAYER = SimpleBoard.BLACK;

	private int currentPlayer;
	private OthelloBoard board;

	public OthelloGame() {
		this.board = new OthelloBoard();
	}

	public OthelloGame(OthelloBoard board) {
		this.board = board;
	}

	public boolean endOfGame() {
		for (int row = 1; row <= board.getSize(); row++) {
			for (int col = 1; col <= board.getSize(); col++) {
				if (canPlace(row, col, currentPlayer)
						|| canPlace(row, col, getOpponent(currentPlayer))) {
					return false;
				}
			}
		}
		return true;
	}

	public double evalMove(Player player, GameMove move) {
		if (player instanceof WPCPlayer) {
			return evalMove((WPCPlayer) player, move);
		}

		double result = player.evaluate(move.getAfterState());
		if (currentPlayer == MAX_PLAYER) {
			return result;
		} else {
			return -result;
		}
	}

	public double evalMove(WPCPlayer player, GameMove move) {
		List<Integer> directions = board
				.findDirections(move.getRow(), move.getCol(), currentPlayer);

		float result = 0;
		for (int dir : directions) {
			for (int dist = 1; board.getPiece(move.getRow(), move.getCol(), dir, dist) == getOpponent(currentPlayer); dist++) {
				result += 2 * player.getValue(move.getRow() + dist * OthelloBoard.ROW_DIR[dir],
						move.getCol() + dist * OthelloBoard.COL_DIR[dir]);
			}
		}

		return result + player.getValue(move.getRow(), move.getCol());
	}

	public List<GameMove> findMoves() {
		List<GameMove> moves = new ArrayList<GameMove>();
		for (int row = 1; row <= board.getSize(); row++) {
			for (int col = 1; col <= board.getSize(); col++) {
				if (canPlace(row, col, currentPlayer)) {
					moves.add(new GameMove(row, col, board, currentPlayer));
				}
			}
		}
		return moves;
	}

	public void makeMove(GameMove move) {
		board = (OthelloBoard) (move.getAfterState());
		currentPlayer = getOpponent(currentPlayer);
	}

	private boolean canPlace(int row, int col, int curPlayer) {
		return (!board.findDirections(row, col, curPlayer).isEmpty());
	}

	public static int getOpponent(int player) {
		return ((player + 1) % NUM_PLAYERS);
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}

	public int getOutcome() {
		return (board.countPieces(SimpleBoard.BLACK) - board.countPieces(SimpleBoard.WHITE));
	}

	public void reset() {
		board.reset();
		currentPlayer = 0;
	}

	public Board getBoard() {
		return board;
	}

	public void pass() {
		currentPlayer = getOpponent(currentPlayer);
	}
}
