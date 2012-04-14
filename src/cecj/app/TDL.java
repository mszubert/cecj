package cecj.app;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import cecj.app.othello.OthelloBoard;
import cecj.app.othello.OthelloGame;
import cecj.shaping.SimpleStateGenerator;
import cecj.shaping.StateGenerator;
import cecj.shaping.StateSampleIndividual;
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
import games.Board;
import games.BoardGame;
import games.GameMove;
import games.Transition;
import games.player.EvolvedPlayer;
import games.player.LearningPlayer;
import games.player.Player;
import games.scenario.GameScenario;
import games.scenario.SelfPlayTDLScenario;

public class TDL {
	private static final String P_TDL = "tdl";
	private static final String P_GAME = "game";
	private static final String P_PLAYER = "player";

	private static final String P_STAT = "stat";
	private static final String P_SEED = "seed";
	private static final String P_VERBOSITY = "verbosity";

	private static final String P_RANDOMNESS = "randomness";
	private static final String P_LEARNING_RATE = "learning-rate";
	private static final String P_LAMBDA = "lambda";

	private static final String P_GAMES = "games";

	private static final String P_BATCH_STATES = "batch-states";
	private static final String P_BATCH_TRANSITIONS = "batch-transitions";
	private static final String P_BATCH_REPEATS = "batch-repeats";
	
	private static final String P_FILE_STATES = "file-states";
	private static final String P_FILE_NAME = "file-name";
	private static final String P_FILE_INDEX = "file-index";
			
	
	private static StateSampleIndividual getSingleStateInd(String filename, int index) {
		List<StateSampleIndividual> states = new ArrayList<StateSampleIndividual>();
		try {
			LineNumberReader reader = new LineNumberReader(new FileReader(filename));
			while (((reader.readLine()) != null)) {
				StateSampleIndividual stateSample = new StateSampleIndividual();
				stateSample.fitness = new SimpleFitness();
				stateSample.readIndividual(null, reader);
				states.add(stateSample);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return states.get(index);
	}
	
	public static void main(String[] args) {
		ParameterDatabase parameters = ec.Evolve.loadParameterDatabase(args);

		Parameter verbosityParam = new Parameter(P_VERBOSITY);
		int verbosity = parameters.getInt(verbosityParam, null, 0);
		if (verbosity < 0) {
			Output.initialError("Verbosity should be an integer >= 0.\n", verbosityParam);
		}

		Output output = new Output(true);
		output.addLog(ec.util.Log.D_STDOUT, false);
		//output.addLog(ec.util.Log.D_STDERR, true);

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

		Parameter base = new Parameter(P_TDL);
		if (parameters.getBoolean(base.push(P_BATCH_STATES), null, false)) {
			new TDL(state).runBatchStates();
		} else if (parameters.getBoolean(base.push(P_BATCH_TRANSITIONS), null, false)) {
			new TDL(state).runBatchTransitions();
		} else if (parameters.getBoolean(base.push(P_FILE_STATES), null, false)) {
			String filename = parameters.getString(base.push(P_FILE_NAME), null);
			int fileIndex = parameters.getInt(base.push(P_FILE_INDEX), null);
			new TDL(state).run(getSingleStateInd(filename, fileIndex));
		} else {
			new TDL(state).run();
		}
	}

	private Statistics stat;
	private EvolutionState state;
	private BoardGame boardGame;
	private LearningPlayer player;

	private int batchRepeats;
	
	private int numGames;
	private double lambda;
	private double randomness;
	private double learningRate;
	
	public TDL(EvolutionState state) {
		this.state = state;

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

		Parameter batchRepeatsParam = base.push(P_BATCH_REPEATS);
		batchRepeats = state.parameters.getIntWithDefault(batchRepeatsParam, null, 1);
		
		Parameter gameParam = new Parameter(P_GAME);
		boardGame = (BoardGame) state.parameters.getInstanceForParameter(gameParam, null,
				BoardGame.class);

		Parameter playerParam = new Parameter(P_PLAYER);
		player = (LearningPlayer) state.parameters.getInstanceForParameter(playerParam, null,
				LearningPlayer.class);
		player.setup(state, playerParam);
		player.reset();

		state.population.subpops[0].individuals[0] = ((EvolvedPlayer) player).createIndividual();

		Parameter statParam = base.push(P_STAT);
		stat = (Statistics) state.parameters.getInstanceForParameterEq(statParam, null,
				Statistics.class);
		stat.setup(state, statParam);
	}

	public void run() {
		SelfPlayTDLScenario scenario = new SelfPlayTDLScenario(player, randomness, learningRate,
				lambda);

		for (int game = 0; game < numGames; game++) {
			stat.postEvaluationStatistics(state);
			boardGame.reset();
			scenario.play(boardGame);
			state.generation++;
		}
	}

	private void run(StateSampleIndividual stateSample) {
		BoardGame[] states = stateSample.states;
		int repeats = numGames / states.length;
		
		SelfPlayTDLScenario scenario = new SelfPlayTDLScenario(player, randomness, learningRate);
		for (int r = 0; r <= repeats; r++) {
			stat.postEvaluationStatistics(state);
			for (int s = 0; s < states.length; s++) {
				BoardGame stateCopy = states[s].clone();
				scenario.play(stateCopy);
				state.generation++;
			}
		}
	}
	
	public void runBatchTransitions() {
		for (int game = 0; game < numGames; game++) {
			SelfPlayTDLScenario scenario = new SelfPlayTDLScenario(player, randomness, learningRate, lambda);
			
			stat.postEvaluationStatistics(state);
			boardGame.reset();
			List<Transition> transitions = scenario.sampleTransitions(boardGame);
			
			for (int i = 0; i < batchRepeats; i++) {
				for (Transition t : transitions) {
					Board previousBoard = t.getPreviousBoard();
					double evalBefore = Math.tanh(player.evaluate(previousBoard));
					double derivative = (1 - (evalBefore * evalBefore));
					
					OthelloGame othello = new OthelloGame((OthelloBoard)t.getNextBoard());
					double error = GameScenario.getValue(othello, player) - evalBefore;
					player.TDLUpdate(t.getPreviousBoard(), learningRate * error * derivative);
				}
			}
			
			state.generation++;
		}
	}
	
	public void runBatchStates() {
		StateGenerator generator = new SimpleStateGenerator(1.0, new Player[] { null, null });
		
		for (int game = 0; game < numGames; game++) {
			//stat.postEvaluationStatistics(state);
			player.reset();
			boardGame.reset();
			//List<BoardGame> states = generator.generateTrace(boardGame);
			List<BoardGame> states = generator.generateRandomStateSet(boardGame, 50);
			
			for (int i = 0; i < batchRepeats; i++) {
				for (BoardGame state : states) {
					BoardGame stateCopy = state.clone();
					Board previousBoard = stateCopy.getBoard().clone();
					double evalBefore = Math.tanh(player.evaluate(previousBoard));
					double derivative = (1 - (evalBefore * evalBefore));
					
					List<GameMove> moves = stateCopy.findMoves();
					if (!moves.isEmpty()) {
						GameMove action = GameScenario.chooseBestMove(stateCopy, player, moves);
						stateCopy.makeMove(action);
						double error = GameScenario.getValue(stateCopy, player) - evalBefore;
						
						
						player.TDLUpdate(previousBoard, learningRate * error * derivative);
						
						
					}
				}
			}
			stat.postEvaluationStatistics(state);
			
			state.generation++;
		}
	}
}
