package cecj.app;

import ec.EvolutionState;
import games.player.Player;
import games.scenario.GameScenario;
import games.scenario.RandomizedTwoPlayersGameScenario;

public abstract class RandomizedPlayerFitnessCalculator extends GamePlayerFitnessCalculator {

	@Override
	protected GameScenario getInverseScenario(EvolutionState state, Player player) {
		return new RandomizedTwoPlayersGameScenario(state.random[0], new Player[] { getOpponent(),
				player }, new double[] { evaluatorRandomness, evaluatedRandomness });
	}

	@Override
	protected GameScenario getScenario(EvolutionState state, Player player) {
		return new RandomizedTwoPlayersGameScenario(state.random[0], new Player[] { player,
				getOpponent() }, new double[] { evaluatedRandomness, evaluatorRandomness });
	}
	
	protected abstract Player getOpponent();
}
