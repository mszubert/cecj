package cecj.app.go;

import java.util.ArrayList;
import java.util.List;

import games.Board;
import games.BoardGame;
import games.GameMove;
import games.Player;

public class GoGame implements BoardGame {

	private static final int NUM_PLAYERS = 2;
	private static final int HISTORY_LENGTH = 2;
	private static final int MOVE_LIMIT = 125;

	private int moveCounter;
	private int currentPlayer;
	private int[] captures;
	private static GoMove passMove = new GoMove(-1, -1, null);

	GoBoard board;
	GameMove[] historicalMoves;
	GoBoard[] historicalBoards;

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
			result = board.evaluate(player);
		} else {
			result = ((GoMove) move).getResultingBoard().evaluate(player);
		}

		if (getCurrentPlayer() == GoBoard.WHITE) {
			return -result;
		} else {
			return result;
		}
	}

	public int getLibertyDifference(GameMove move) {
		GoBoard afterState = board;
		if (move != passMove) {
			afterState = ((GoMove) move).getResultingBoard();
		}

		if (getCurrentPlayer() == GoBoard.WHITE) {
			return afterState.countLiberties(GoBoard.WHITE)
					- afterState.countLiberties(GoBoard.BLACK);
		} else {
			return afterState.countLiberties(GoBoard.BLACK)
			- afterState.countLiberties(GoBoard.WHITE);
		}
	}

	public List<? extends GameMove> findMoves() {
		List<GoMove> moves = new ArrayList<GoMove>();
		for (int row = 1; row <= GoBoard.size(); row++) {
			for (int col = 1; col <= GoBoard.size(); col++) {
				GoMove move = tryPlace(row, col);
				if (move != null) {
					moves.add(move);
				}
			}
		}

		moves.add(passMove);
		return moves;
	}

	public GoMove tryPlace(int row, int col) {
		if (!board.isEmpty(row, col)) {//|| board.isSinglePointEye(row, col, currentPlayer)) {
			return null;
		}

		GoMove move = board.createMove(row, col, currentPlayer);
		GoBoard afterState = move.getResultingBoard();

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

		int result = board.countPieces(GoBoard.BLACK) - board.countPieces(GoBoard.WHITE);
		result += board.countTerritory(GoBoard.BLACK) - board.countTerritory(GoBoard.WHITE);

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
			GoBoard afterState = ((GoMove) move).getResultingBoard();
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
		historicalMoves = new GoMove[HISTORY_LENGTH];
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
