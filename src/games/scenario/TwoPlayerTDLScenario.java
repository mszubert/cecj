package games.scenario;

import games.Board;
import games.BoardGame;
import games.GameMove;
import games.player.LearningPlayer;

import java.util.List;

public class TwoPlayerTDLScenario extends GameScenario {

	private int learner;
	private double prob;
	private double learningRate;

	private LearningPlayer[] players;

	public TwoPlayerTDLScenario(LearningPlayer[] players, double prob,
			double learningRate, int learner) {
		this.prob = prob;
		this.players = players;
		this.learningRate = learningRate;
		this.learner = learner;
	}

	public TwoPlayerTDLScenario(LearningPlayer[] players, double prob,
			double learningRate) {
		this(players, prob, learningRate, -1);
	}

	@Override
	public int play(BoardGame game) {
		while (!game.endOfGame()) {
			List<? extends GameMove> moves = game.findMoves();
			if (!moves.isEmpty()) {
				if (Math.random() < prob) {
					game.makeMove(moves.get((int) (Math.random() * moves.size())));
				} else {
					GameMove bestMove = chooseBestMove(game,
							players[game.getCurrentPlayer()], moves);

					if (learner == -1 || game.getCurrentPlayer() == learner) {
						Board previousBoard = game.getBoard().clone();
						LearningPlayer previousPlayer = players[game
								.getCurrentPlayer()];

						game.makeMove(bestMove);
						updateEvaluationFunction(previousBoard, previousPlayer,
								game);
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

	private void updateEvaluationFunction(Board previousBoard,
			LearningPlayer player, BoardGame game) {
		double evalBefore = Math.tanh(player.evaluate(previousBoard));
		double derivative = (1 - (evalBefore * evalBefore));
		double error = getValue(game, player) - evalBefore;

		player.TDLUpdate(previousBoard, learningRate * error * derivative);
	}
}
