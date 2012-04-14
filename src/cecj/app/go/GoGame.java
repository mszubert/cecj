package cecj.app.go;

import java.util.ArrayList;
import java.util.List;

import games.Board;
import games.BoardGame;
import games.GameMove;
import games.SimpleBoard;
import games.player.Player;

public class GoGame implements BoardGame {

	private static final int NUM_PLAYERS = 2;
	private static final int HISTORY_LENGTH = 2;
	private static final int MOVE_LIMIT = 125;

	private int moveCounter;
	private int currentPlayer;
	private int[] captures;
	private static GameMove passMove = new GameMove(-1, -1, null, -1);

	GoBoard board;
	GameMove[] historicalMoves;
	GoBoard[] historicalBoards;

	@Override
	public GoGame clone() {
		throw new UnsupportedOperationException();
	}
	
	public GoGame() {
		board = new GoBoard();
		reset();
	}

	public boolean endOfGame() {
		return (moveCounter > MOVE_LIMIT) || (historicalMoves[0] == passMove)
				&& (historicalMoves[1] == passMove);
	}

	public double evalMove(Player player, GameMove move) {
		double result;
		if (move == passMove) {
			result = player.evaluate(board);
		} else {
			result = player.evaluate(move.getAfterState());
		}

		if (getCurrentPlayer() == SimpleBoard.WHITE) {
			return -result;
		} else {
			return result;
		}
	}

	public int getLibertyDifference(GameMove move) {
		GoBoard afterState = board;
		if (move != passMove) {
			afterState = (GoBoard)(move.getAfterState());
		}

		if (getCurrentPlayer() == SimpleBoard.WHITE) {
			return afterState.countLiberties(SimpleBoard.WHITE)
					- afterState.countLiberties(SimpleBoard.BLACK);
		} else {
			return afterState.countLiberties(SimpleBoard.BLACK)
			- afterState.countLiberties(SimpleBoard.WHITE);
		}
	}

	public List<GameMove> findMoves() {
		List<GameMove> moves = new ArrayList<GameMove>();
		for (int row = 1; row <= board.getSize(); row++) {
			for (int col = 1; col <= board.getSize(); col++) {
				GameMove move = tryPlace(row, col);
				if (move != null) {
					moves.add(move);
				}
			}
		}

		moves.add(passMove);
		return moves;
	}

	public GameMove tryPlace(int row, int col) {
		if (!board.isEmpty(row, col)) {//|| board.isSinglePointEye(row, col, currentPlayer)) {
			return null;
		}

		GameMove move = new GameMove(row, col, board, currentPlayer);
		GoBoard afterState = (GoBoard)(move.getAfterState());

		if (afterState.countPieces(currentPlayer) <= board.countPieces(currentPlayer)) {
			return null;
		}

		for (int i = 0; i < HISTORY_LENGTH; i++) {
			if (historicalBoards[i] != null && historicalBoards[i].equals(afterState)) {
				return null;
			}
		}

		return move;
	}

	public Board getBoard() {
		return board;
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}

	public int getOutcome() {
		if (moveCounter > MOVE_LIMIT) {
			return 0;
		}

		int result = board.countPieces(SimpleBoard.BLACK) - board.countPieces(SimpleBoard.WHITE);
		result += board.countTerritory(SimpleBoard.BLACK) - board.countTerritory(SimpleBoard.WHITE);

		return result;
	}

	public void makeMove(GameMove move) {
		if (move == null) {
			move = passMove;
		}

		moveCounter++;
		updateHistory(move);
		int opponent = getOpponent(currentPlayer);

		if (move != passMove) {
			GoBoard afterState = (GoBoard)(move.getAfterState());
			captures[currentPlayer] += (board.countPieces(opponent) - afterState
					.countPieces(opponent));
			board = afterState;
		}

		currentPlayer = opponent;
	}

	public void pass() {
		makeMove(passMove);
	}

	private int getOpponent(int player) {
		return ((player + 1) % NUM_PLAYERS);
	}

	public void reset() {
		board.reset();
		currentPlayer = 0;
		moveCounter = 0;
		captures = new int[NUM_PLAYERS];
		historicalMoves = new GameMove[HISTORY_LENGTH];
		historicalBoards = new GoBoard[HISTORY_LENGTH];
	}

	public int getMoveCount() {
		return moveCounter;
	}

	private void updateHistory(GameMove move) {
		for (int i = 1; i < HISTORY_LENGTH; i++) {
			historicalMoves[i] = historicalMoves[i - 1];
			historicalBoards[i] = historicalBoards[i - 1];
		}
		historicalMoves[0] = move;
		historicalBoards[0] = board;
	}
}
