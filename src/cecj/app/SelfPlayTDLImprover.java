package cecj.app;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;
import games.BoardGame;
import games.player.EvolvedPlayer;
import games.player.LearningPlayer;
import games.scenario.SelfPlayTDLScenario;
import cecj.eval.LearningImprover;

public class SelfPlayTDLImprover implements LearningImprover {

	private static final String P_GAME = "game";
	private static final String P_PLAYER = "player";

	private static final String P_REPEATS = "repeats";
	private static final String P_LAMBDA = "lambda";
	private static final String P_RANDOMNESS = "randomness";
	private static final String P_LEARNING_RATE = "learning-rate";

	protected int repeats;
	protected double lambda;
	protected double randomness;
	protected double learningRate;

	protected BoardGame boardGame;
	protected EvolvedPlayer playerPrototype;

	public void setup(EvolutionState state, Parameter base) {
		Parameter gameParam = new Parameter(P_GAME);
		boardGame = (BoardGame) state.parameters.getInstanceForParameter(gameParam, null,
				BoardGame.class);

		Parameter playerParam = new Parameter(P_PLAYER);
		playerPrototype = (EvolvedPlayer) state.parameters.getInstanceForParameter(playerParam,
				null, EvolvedPlayer.class);

		Parameter randomnessParam = base.push(P_RANDOMNESS);
		randomness = state.parameters.getDoubleWithDefault(randomnessParam, null, 0.1);

		Parameter learningRateParam = base.push(P_LEARNING_RATE);
		learningRate = state.parameters.getDoubleWithDefault(learningRateParam, null, 0.01);

		Parameter lambdaParam = base.push(P_LAMBDA);
		lambda = state.parameters.getDoubleWithDefault(lambdaParam, null, 0.0);

		Parameter repeatsParam = base.push(P_REPEATS);
		repeats = state.parameters.getIntWithDefault(repeatsParam, null, 10);
	}

	public void improve(EvolutionState state, Individual ind) {
		EvolvedPlayer player = playerPrototype.createEmptyCopy();
		player.readFromIndividual(ind);

		if (player instanceof LearningPlayer) {
			SelfPlayTDLScenario selfPlayScenario = new SelfPlayTDLScenario((LearningPlayer) player,
					randomness, learningRate, lambda);
			for (int r = 0; r < repeats; r++) {
				boardGame.reset();
				selfPlayScenario.play(boardGame);
			}
		} else {
			state.output.fatal("Can not improve player which is not a LearningPlayer instance.");
		}
	}

	public void prepareForImproving(EvolutionState state, Individual ind) {
		EvolvedPlayer player = playerPrototype.createEmptyCopy();
		player.readFromIndividual(ind);
		player.reset();
	}
}
