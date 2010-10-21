package cecj.app.go;

import ec.EvolutionState;
import games.SimpleBoard;
import games.player.Player;
import games.scenario.ALPGameScenario;
import games.scenario.GameScenario;
import cecj.app.GamePlayerFitnessCalculator;

public class AverageLibertyPlayer extends GamePlayerFitnessCalculator {

	@Override
	protected GameScenario getInverseScenario(EvolutionState state, Player player) {
		return new ALPGameScenario(state.random[0], player, SimpleBoard.WHITE, new double[] {
				evaluatedRandomness, evaluatorRandomness });
	}

	@Override
	protected GameScenario getScenario(EvolutionState state, Player player) {
		return new ALPGameScenario(state.random[0], player, SimpleBoard.BLACK, new double[] {
				evaluatedRandomness, evaluatorRandomness });
	}
}
