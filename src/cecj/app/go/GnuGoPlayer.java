package cecj.app.go;

import cecj.app.GamePlayerFitnessCalculator;
import ec.EvolutionState;
import ec.Individual;
import games.WPCPlayer;
import games.scenarios.GameScenario;
import games.scenarios.GnuGoGameScenario;

public class GnuGoPlayer extends GamePlayerFitnessCalculator {

	@Override
	public float calculateObjectiveFitness(EvolutionState state, Individual ind) {
		GnuGoGameScenario.startGnuGo();
		float result = super.calculateObjectiveFitness(state, ind);
		GnuGoGameScenario.stopGnuGo();
		return result;
	}
	
	@Override
	protected GameScenario getInverseScenario(EvolutionState state, double[] player) {
		return new GnuGoGameScenario(state.random[0], new WPCPlayer(player), GoBoard.WHITE,
				new double[] {evaluatedRandomness, evaluatorRandomness});
	}

	@Override
	protected GameScenario getScenario(EvolutionState state, double[] player) {
		return new GnuGoGameScenario(state.random[0], new WPCPlayer(player), GoBoard.BLACK,
				new double[] {evaluatedRandomness, evaluatorRandomness});
	}
}
