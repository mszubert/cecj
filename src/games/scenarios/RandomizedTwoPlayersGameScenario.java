package games.scenarios;

import ec.util.MersenneTwisterFast;
import games.BoardGame;
import games.GameMove;
import games.Player;

import java.util.List;

public class RandomizedTwoPlayersGameScenario extends GameScenario {

	private Player[] players;
	private double[] prob;

	public RandomizedTwoPlayersGameScenario(MersenneTwisterFast random, Player[] players,
			double[] prob) {
		super(random);

		this.players = players;
		this.prob = prob;
	}

	@Override
	public int play(BoardGame game) {
		while (!game.endOfGame()) {
			List<? extends GameMove> moves = game.findMoves();
			if (!moves.isEmpty()) {
				GameMove bestMove = null;
				if (random.nextBoolean(prob[game.getCurrentPlayer()])) {
					bestMove = moves.get(random.nextInt(moves.size()));
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
}
