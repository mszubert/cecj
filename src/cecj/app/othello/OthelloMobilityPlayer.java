package cecj.app.othello;

import ec.EvolutionState;
import ec.util.Parameter;
import games.Board;
import games.player.Player;
import cecj.app.RandomizedPlayerFitnessCalculator;

public class OthelloMobilityPlayer extends RandomizedPlayerFitnessCalculator {

	private static final double CORNERS_WEIGHT = 10.0;

	private static final double MOBILITY_WEIGHT = 1.0;

	@Override
	protected Player getOpponent() {
		return new Player() {
			public double evaluate(Board board) {
				double corners = 0;
				int size = board.getSize();
				corners += board.getValueAt(1, 1);
				corners += board.getValueAt(1, size);
				corners += board.getValueAt(size, 1);
				corners += board.getValueAt(size, size);
				corners *= CORNERS_WEIGHT;

				double mobility = 0;
				OthelloGame game = new OthelloGame((OthelloBoard) board);
				int blackMobility = game.findMoves().size();
				game.pass();
				int whiteMobility = game.findMoves().size();
				if (blackMobility + whiteMobility != 0) {
					mobility = ((blackMobility - whiteMobility) / (blackMobility + whiteMobility))
							* MOBILITY_WEIGHT;
				}

				return corners + mobility;
			}

			public void setup(EvolutionState state, Parameter base) {
			}

			public void reset() {
			}
		};
	}
}
