package games.scenario;

import games.BoardGame;
import games.GameMove;
import games.player.Player;

import java.util.ArrayList;
import java.util.List;

public class GameScenario {

	private Player[] players;
	private double[] prob;
	
	public GameScenario() { }
	
	public GameScenario(Player[] players) {
		this(players, new double[] {0., 0.});
	}
	
	public GameScenario(Player[] players, double[] prob) {
		this.players = players;
		this.prob = prob;
	}
	
	public int play(BoardGame game) {
		while (!game.endOfGame()) {
			List<? extends GameMove> moves = game.findMoves();
			if (!moves.isEmpty()) {
				GameMove bestMove = null;
				if (Math.random() < prob[game.getCurrentPlayer()]) {
					bestMove = moves.get((int) (Math.random() * moves.size()));
				} else {
					bestMove = chooseBestMove(game, players[game.getCurrentPlayer()], moves);
				}
				game.makeMove(bestMove);
			} else {
				game.pass();
			}
		}
		return game.getOutcome();
	}
	
	public static GameMove chooseBestMove(BoardGame game, Player player, List<? extends GameMove> moves) {
		double bestEval = Float.NEGATIVE_INFINITY;
		List<GameMove> bestMoves = new ArrayList<GameMove>();

		for (GameMove move : moves) {
			double eval = game.evalMove(player, move);
			if (eval == bestEval) {
				bestMoves.add(move);
			} else if (eval > bestEval) {
				bestEval = eval;
				bestMoves.clear();
				bestMoves.add(move);
			}
		}

		return bestMoves.get((int) (Math.random() * bestMoves.size()));
	}
	
	public static double getValue(BoardGame game, Player player) {
		if (game.endOfGame()) {
			int result;
			if (game.getOutcome() > 0) {
				result = 1;
			} else if (game.getOutcome() < 0) {
				result = -1;
			} else {
				result = 0;
			}
			return result;
		} else {
			return Math.tanh(player.evaluate(game.getBoard()));
		}
	}
	
	protected boolean isRandomMove(double prob) {
		return (Math.random() < prob);
	}
}