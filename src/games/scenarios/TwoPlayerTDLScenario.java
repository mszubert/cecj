package games.scenarios;

import java.util.List;

import ec.util.MersenneTwisterFast;
import games.Board;
import games.BoardGame;
import games.GameMove;
import games.Player;

public class TwoPlayerTDLScenario extends GameScenario {

	private int learner;
	private double prob;
	private double learningRate;

	private Player[] players;

	public TwoPlayerTDLScenario(MersenneTwisterFast random, Player[] players, double prob,
			double learningRate, int learner) {
		super(random);

		this.prob = prob;
		this.players = players;
		this.learningRate = learningRate;
		this.learner = learner;
	}

	public TwoPlayerTDLScenario(MersenneTwisterFast random, Player[] players, double prob,
			double learningRate) {
		this(random, players, prob, learningRate, -1);
	}

	@Override
	public int play(BoardGame game) {
		while (!game.endOfGame()) {
			List<? extends GameMove> moves = game.findMoves();
			if (!moves.isEmpty()) {
				if (random.nextBoolean(prob)) {
					game.makeMove(moves.get(random.nextInt(moves.size())));
				} else {
					GameMove bestMove = chooseBestMove(game, players[game.getCurrentPlayer()],
							moves);

					if (learner == -1 || game.getCurrentPlayer() == learner) {
						Board previousBoard = game.getBoard().clone();
						Player previousPlayer = players[game.getCurrentPlayer()];

						game.makeMove(bestMove);
						updateEvaluationFunction(previousBoard, previousPlayer, game);
					} else {
						game.makeMove(bestMove);
					}
				}
			} else {
				game.pass();
			}
		}

		return game.getOutcome();
	}

	private void updateEvaluationFunction(Board previousBoard, Player player, BoardGame game) {
		double evalBefore = tanh(previousBoard.evaluate(player));
		double derivative = (1 - (evalBefore * evalBefore));
		double error;

		if (game.endOfGame()) {
			int result;
			if (game.getOutcome() > 0) {
				result = 1;
			} else if (game.getOutcome() < 0) {
				result = -1;
			} else {
				result = 0;
			}
			error = result - evalBefore;
		} else {
			double evalAfter = tanh(game.getBoard().evaluate(player));
			error = evalAfter - evalBefore;
		}

		int boardSize = previousBoard.getSize();
		double delta = learningRate * error * derivative;
		for (int row = 1; row <= boardSize; row++) {
			for (int col = 1; col <= boardSize; col++) {
				double w = player.getValue(row, col);
				player.setValue(row, col, w + (delta * previousBoard.getValueAt(row, col)));
			}
		}
	}

	private static double tanh(double x) {
		return 2 / (1 + Math.exp(-2 * x)) - 1;
	}
}
