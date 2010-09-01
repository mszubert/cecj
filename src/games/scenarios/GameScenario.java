package games.scenarios;

import java.util.ArrayList;
import java.util.List;

import ec.util.MersenneTwisterFast;

import games.BoardGame;
import games.GameMove;
import games.Player;

public abstract class GameScenario {

	protected MersenneTwisterFast random;

	public GameScenario(MersenneTwisterFast random) {
		this.random = random;
	}

	public abstract int play(BoardGame game);

	protected GameMove chooseBestMove(BoardGame game, Player player, List<? extends GameMove> moves) {
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

		return bestMoves.get(random.nextInt(bestMoves.size()));
	}
}