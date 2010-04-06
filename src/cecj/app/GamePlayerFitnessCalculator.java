package cecj.app;

import cecj.statistics.ObjectiveFitnessCalculator;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;
import ec.vector.DoubleVectorIndividual;
import games.BoardGame;
import games.GameFactory;
import games.scenarios.GameScenario;

public abstract class GamePlayerFitnessCalculator implements ObjectiveFitnessCalculator {

	protected static final String P_EVALUATOR_RANDOMNESS = "evaluator-randomness";
	protected static final String P_EVALUATED_RANDOMNESS = "evaluated-randomness";

	private static final String P_PLAY_BOTH = "play-both";
	private static final String P_REPEATS = "repeats";
	private static final String P_GAME = "game";

	protected double evaluatedRandomness;
	protected double evaluatorRandomness;
	protected boolean playBoth;
	protected int repeats;

	protected GameFactory gameFactory;

	public void setup(EvolutionState state, Parameter base) {
		Parameter randomnessParam = base.push(P_EVALUATED_RANDOMNESS);
		evaluatedRandomness = state.parameters.getDoubleWithDefault(randomnessParam, null, 0);

		randomnessParam = base.push(P_EVALUATOR_RANDOMNESS);
		evaluatorRandomness = state.parameters.getDoubleWithDefault(randomnessParam, null, 0);

		Parameter repetitionsParam = base.push(P_REPEATS);
		repeats = state.parameters.getIntWithDefault(repetitionsParam, null, 1);

		Parameter playBothParam = base.push(P_PLAY_BOTH);
		playBoth = state.parameters.getBoolean(playBothParam, null, false);

		Parameter gameParam = new Parameter(P_GAME);
		gameFactory = (GameFactory) state.parameters.getInstanceForParameter(gameParam, null,
				GameFactory.class);
	}

	public float calculateObjectiveFitness(EvolutionState state, Individual ind) {
		double[] player = ((DoubleVectorIndividual) ind).genome;

		BoardGame game = gameFactory.createGame();
		GameScenario scenario1 = getScenario(state, player);
		GameScenario scenario2 = getInverseScenario(state, player);

		float sum = 0;
		for (int r = 0; r < repeats; r++) {
			game.reset();
			sum += ((scenario1.play(game) >= 0) ? 1 : 0);
			if (playBoth) {
				game.reset();
				sum += ((scenario2.play(game) <= 0) ? 1 : 0);
			}
		}

		if (playBoth) {
			return sum / (repeats * 2);
		} else {
			return sum / repeats;
		}
	}

	protected abstract GameScenario getScenario(EvolutionState state, double[] player);

	protected abstract GameScenario getInverseScenario(EvolutionState state, double[] player);
}
