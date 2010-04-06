package cecj.app;

import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.Statistics;
import ec.Subpopulation;
import ec.simple.SimpleEvolutionState;
import ec.simple.SimpleFitness;
import ec.util.MersenneTwisterFast;
import ec.util.Output;
import ec.util.Parameter;
import ec.util.ParameterDatabase;
import ec.vector.DoubleVectorIndividual;
import games.BoardGame;
import games.GameFactory;
import games.WPCPlayer;
import games.scenarios.SelfPlayTDLScenario;

public class TDL {
	private static final String P_TDL = "tdl";
	private static final String P_GAME = "game";

	private static final String P_STAT = "stat";
	private static final String P_SEED = "seed";
	private static final String P_VERBOSITY = "verbosity";

	private static final String P_RANDOMNESS = "randomness";
	private static final String P_LEARNING_RATE = "learning-rate";
	private static final String P_LAMBDA = "lambda";

	private static final String P_GAMES = "games";

	public static void main(String[] args) {
		ParameterDatabase parameters = ec.Evolve.loadParameterDatabase(args);

		Parameter verbosityParam = new Parameter(P_VERBOSITY);
		int verbosity = parameters.getInt(verbosityParam, null, 0);
		if (verbosity < 0) {
			Output.initialError("Verbosity should be an integer >= 0.\n", verbosityParam);
		}

		Output output = new Output(true, verbosity);
		output.addLog(ec.util.Log.D_STDOUT, Output.V_VERBOSE, false);
		output.addLog(ec.util.Log.D_STDERR, Output.V_VERBOSE, true);

		int time = (int) (System.currentTimeMillis());
		Parameter seedParam = new Parameter(P_SEED);
		int seed = ec.Evolve.determineSeed(output, parameters, seedParam, time, 0, false);
		MersenneTwisterFast random = new MersenneTwisterFast(seed);

		EvolutionState state = new SimpleEvolutionState();
		state.parameters = parameters;
		state.random = new MersenneTwisterFast[] { random };
		state.output = output;

		state.generation = 0;
		state.population = new Population();
		state.population.subpops = new Subpopulation[1];
		state.population.subpops[0] = new Subpopulation();
		state.population.subpops[0].individuals = new Individual[1];

		new TDL(state).run();
	}

	private Statistics stat;
	private EvolutionState state;
	private GameFactory gameFactory;
	private MersenneTwisterFast random;

	private int numGames;
	private double lambda;
	private double randomness;
	private double learningRate;

	public TDL(EvolutionState state) {
		this.state = state;
		this.random = state.random[0];

		Parameter base = new Parameter(P_TDL);
		Parameter randomnessParam = base.push(P_RANDOMNESS);
		randomness = state.parameters.getDoubleWithDefault(randomnessParam, null, 0.1);

		Parameter learningRateParam = base.push(P_LEARNING_RATE);
		learningRate = state.parameters.getDoubleWithDefault(learningRateParam, null, 0.01);

		Parameter lambdaParam = base.push(P_LAMBDA);
		lambda = state.parameters.getDoubleWithDefault(lambdaParam, null, 0.0);

		Parameter numGamesParam = base.push(P_GAMES);
		numGames = state.parameters.getIntWithDefault(numGamesParam, null, 1000000);
		state.numGenerations = numGames;

		Parameter gameParam = new Parameter(P_GAME);
		gameFactory = (GameFactory) state.parameters.getInstanceForParameter(gameParam, null,
				GameFactory.class);

		Parameter statParam = base.push(P_STAT);
		stat = (Statistics) state.parameters.getInstanceForParameterEq(statParam, null,
				Statistics.class);
		stat.setup(state, statParam);
	}

	public void run() {
		WPCPlayer player = initializePlayer();
		BoardGame boardGame = gameFactory.createGame();
		SelfPlayTDLScenario scenario = new SelfPlayTDLScenario(random, player, randomness,
				learningRate, lambda);

		for (int game = 0; game < numGames; game++) {
			stat.postEvaluationStatistics(state);
			boardGame.reset();
			scenario.play(boardGame);
			state.generation++;
		}
	}

	private WPCPlayer initializePlayer() {
		WPCPlayer player = gameFactory.createPlayer();
		DoubleVectorIndividual ind = new DoubleVectorIndividual();
		ind.genome = player.getWPC();
		ind.fitness = new SimpleFitness();
		state.population.subpops[0].individuals[0] = ind;
		return player;
	}
}
