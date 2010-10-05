package cecj.app.othello;

import games.Board;
import games.BoardGame;
import games.GameMove;
import games.Player;
import games.SimpleBoard;
import games.WPCPlayer;

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
			return evalMove((WPCPlayer)player, move);
		}

		double result = player.evaluate(move.getAfterState());
		if (currentPlayer == MAX_PLAYER) {
			return result;
		} else {
			return -result;
		}
	}
	
	public double evalMove(WPCPlayer player, GameMove move) {
		List<Integer> directions = findDirections(move.getRow(), move.getCol(), currentPlayer);

		float result = 0;
		for (int dir : directions) {
			int dist = 1;
			while (board.getPiece(move.getRow(), move.getCol(), dir, dist) == getOpponent(currentPlayer)) {
				GameMove shifted = board.getShiftedMove(move.getRow(), move.getCol(), dir, dist);
				result += 2 * player.getValue(shifted.getRow(), shifted.getCol());
				dist++;
			}
		}

		return result + player.getValue(move.getRow(), move.getCol());
	}
	
	public List<? extends GameMove> findMoves() {
		List<OthelloMove> moves = new ArrayList<OthelloMove>();
		for (int row = 1; row <= board.getSize(); row++) {
			for (int col = 1; col <= board.getSize(); col++) {
				if (canPlace(row, col, currentPlayer)) {
					moves.add(new OthelloMove(row, col));
				}
			}
		}
		return moves;
	}

	public void makeMove(GameMove move) {
		List<Integer> directions = findDirections(move.getRow(), move.getCol(), currentPlayer);
		for (int dir : directions) {
			int dist = 1;
			board.setPiece(move.getRow(), move.getCol(), currentPlayer);
			while (board.getPiece(move.getRow(), move.getCol(), dir, dist) == getOpponent(currentPlayer)) {
				board.setPiece(move.getRow(), move.getCol(), dir, dist, currentPlayer);
				dist++;
			}
		}
		
		currentPlayer = getOpponent(currentPlayer);
	}

	List<Integer> findDirections(int row, int col, int player) {
		List<Integer> directions = new ArrayList<Integer>();

		if (board.isEmpty(row, col)) {
			for (int dir = 0; dir < OthelloBoard.NUM_DIRECTIONS; dir++) {
				int dist = 1;
				while (board.getPiece(row, col, dir, dist) == getOpponent(player)) {
					dist++;
				}
				if (dist > 1 && board.getPiece(row, col, dir, dist) == player) {
					directions.add(dir);
				}
			}
		}
		return directions;
	}

	private boolean canPlace(int row, int col, int curPlayer) {
		return (!findDirections(row, col, curPlayer).isEmpty());
	}

	public int getOpponent(int player) {
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
