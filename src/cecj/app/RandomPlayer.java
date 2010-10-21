package cecj.app;

import ec.EvolutionState;
import ec.util.Parameter;
import games.player.Player;

public class RandomPlayer extends RandomizedPlayerFitnessCalculator {

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);
		evaluatorRandomness = 1;
	}
	
	@Override
	protected Player getOpponent() {
		return null;
	}
}
