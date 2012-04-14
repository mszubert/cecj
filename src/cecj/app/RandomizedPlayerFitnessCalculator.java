package cecj.app;

import ec.EvolutionState;
import games.player.Player;
import games.scenario.GameScenario;

public abstract class RandomizedPlayerFitnessCalculator extends GamePlayerFitnessCalculator {

	@Override
	protected GameScenario getInverseScenario(EvolutionState state, Player player) {
		return new GameScenario(new Player[] { getOpponent(), player }, new double[] {
				evaluatorRandomness, evaluatedRandomness });
	}

	@Override
	protected GameScenario getScenario(EvolutionState state, Player player) {
		return new GameScenario(new Player[] { player, getOpponent() }, new double[] {
				evaluatedRandomness, evaluatorRandomness });
	}

	protected abstract Player getOpponent();
}
