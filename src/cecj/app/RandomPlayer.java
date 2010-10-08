package cecj.app;

import ec.EvolutionState;
import ec.util.Parameter;
import games.player.WPCPlayer;

public class RandomPlayer extends WPCPlayerFitnessCalculator {

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);
		evaluatorRandomness = 1;
	}
	
	@Override
	protected WPCPlayer getOpponent(int size) {
		return new WPCPlayer(new double[size]);
	}
}
