package cecj.app.go;

import ec.EvolutionState;
import games.WPCPlayer;
import games.scenarios.ALPGameScenario;
import games.scenarios.GameScenario;
import cecj.app.GamePlayerFitnessCalculator;

public class AverageLibertyPlayer extends GamePlayerFitnessCalculator {
	
	@Override
	protected GameScenario getInverseScenario(EvolutionState state, double[] player) {
		return new ALPGameScenario(state.random[0], new WPCPlayer(player), GoBoard.WHITE,
				new double[] {evaluatedRandomness, evaluatorRandomness});
	}

	@Override
	protected GameScenario getScenario(EvolutionState state, double[] player) {
		return new ALPGameScenario(state.random[0], new WPCPlayer(player), GoBoard.BLACK,
				new double[] {evaluatedRandomness, evaluatorRandomness});
	}

}
