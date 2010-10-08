package cecj.app;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;
import ec.vector.DoubleVectorIndividual;
import games.BoardGame;
import games.GameFactory;
import games.player.Player;
import games.player.WPCPlayer;
import games.scenario.SelfPlayTDLScenario;
import cecj.eval.TDLImprover;

public class WPCImprover implements TDLImprover {

	private static final String P_GAME = "game";
	private static final String P_REPEATS = "repeats";
	private static final String P_LAMBDA = "lambda";
	private static final String P_RANDOMNESS = "randomness";
	private static final String P_LEARNING_RATE = "learning-rate";
	
	protected int repeats;
	protected double lambda;
	protected double randomness;
	protected double learningRate;
	protected GameFactory gameFactory;

	public void setup(EvolutionState state, Parameter base) {
		Parameter gameParam = new Parameter(P_GAME);
		gameFactory = (GameFactory) state.parameters.getInstanceForParameter(gameParam, null,
				GameFactory.class);

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
		Player player = new WPCPlayer(getWPC(state, ind));
		BoardGame game = gameFactory.createGame();

		SelfPlayTDLScenario selfPlayScenario = new SelfPlayTDLScenario(state.random[0], player,
				randomness, learningRate, lambda);

		for (int r = 0; r < repeats; r++) {
			game.reset();
			selfPlayScenario.play(game);
		}
	}

	public void prepareForImproving(EvolutionState state, Individual ind) {
		double[] wpc = getWPC(state, ind);
		for (int i = 0; i < wpc.length; i++) {
			wpc[i] = 0.0;
		}
	}

	protected double[] getWPC(EvolutionState state, Individual ind) {
		if (!(ind instanceof DoubleVectorIndividual)) {
			state.output.error("Othello players should be represented by floats vectors\n");
		}

		double[] wpc = ((DoubleVectorIndividual) ind).genome;
		return wpc;
	}
}
