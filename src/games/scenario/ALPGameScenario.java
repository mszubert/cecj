package games.scenario;

import java.util.ArrayList;
import java.util.List;

import cecj.app.go.GoGame;

import games.BoardGame;
import games.GameMove;
import games.player.Player;

public class ALPGameScenario extends GameScenario {

	private Player player;
	private double[] prob;
	private int color;

	public ALPGameScenario(Player player, int color, double[] prob) {
		this.player = player;
		this.color = color;
		this.prob = prob;
	}

	@Override
	public int play(BoardGame game) {
		while (!game.endOfGame()) {
			List<? extends GameMove> moves = game.findMoves();
			if (!moves.isEmpty()) {
				GameMove bestMove = null;
				if (Math.random() < prob[game.getCurrentPlayer()]) {
					bestMove = moves.get((int) (Math.random() * moves.size()));
				} else if (game.getCurrentPlayer() == color) {
					bestMove = chooseBestMove(game, player, moves);
				} else {
					bestMove = chooseBestALPMove((GoGame) game, moves);
				}
				game.makeMove(bestMove);
			} else {
				game.pass();
			}
		}
		return game.getOutcome();
	}

	private GameMove chooseBestALPMove(GoGame game, List<? extends GameMove> moves) {
		int bestEval = Integer.MIN_VALUE;
		List<GameMove> bestMoves = new ArrayList<GameMove>();

		for (GameMove move : moves) {
			int eval = game.getLibertyDifference(move);
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
}
