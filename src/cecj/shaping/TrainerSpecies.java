package cecj.shaping;

import ec.EvolutionState;
import ec.Species;
import ec.util.Parameter;
import games.player.LearningPlayer;

public class TrainerSpecies extends Species {

	private final static String P_MUTATION_PROB = "mutation-prob";
	private final static String P_CROSSOVER_PROB = "crossover-prob";
	private final static String P_TRAIN_REPEATS = "train-repeats";
	private final static String P_LEARNING_RATE = "learning-rate";
	private static final String P_LEARNING_ITERATIONS = "learning-iterations";

	private static final String P_PLAYER = "player";
	
	public int trainRepeats;
	public double learningRate;
	public int learningIterations;

	private float mutationProbability;
	private float crossoverProbability;

	public LearningPlayer learnerPrototype;
	public double resetValue;
	
	public Parameter defaultBase() {
		return new Parameter("trainer").push("species");
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		mutationProbability = state.parameters.getFloatWithMax(base.push(P_MUTATION_PROB),
				defaultBase().push(P_MUTATION_PROB), 0.0, 1.0);
		if (mutationProbability == -1.0) {
			state.output
					.error("StateSampleSpecies must have a mutation probability between 0.0 and 1.0 inclusive",
							base.push(P_MUTATION_PROB), defaultBase().push(P_MUTATION_PROB));
		}

		crossoverProbability = state.parameters.getFloatWithMax(base.push(P_CROSSOVER_PROB),
				defaultBase().push(P_CROSSOVER_PROB), 0.0, 1.0);
		if (crossoverProbability == -1.0) {
			state.output
					.error("StateSampleSpecies must have a crossover probability between 0.0 and 1.0 inclusive",
							base.push(P_CROSSOVER_PROB), defaultBase().push(P_CROSSOVER_PROB));
		}

		trainRepeats = state.parameters.getIntWithDefault(base.push(P_TRAIN_REPEATS), defaultBase()
				.push(P_TRAIN_REPEATS), 100);
		learningRate = state.parameters.getDoubleWithDefault(base.push(P_LEARNING_RATE),
				defaultBase().push(P_LEARNING_RATE), 0.01);
		learningIterations = state.parameters.getIntWithDefault(base.push(P_LEARNING_ITERATIONS),
				defaultBase().push(P_LEARNING_ITERATIONS), 100);
		
		Parameter playerParam = new Parameter(P_PLAYER);
		learnerPrototype = (LearningPlayer) state.parameters.getInstanceForParameter(playerParam, null,
				LearningPlayer.class);
		learnerPrototype.setup(state, playerParam);
		learnerPrototype.reset();
		
		state.output.exitIfErrors();
		super.setup(state, base);
	}

	public LearningPlayer createNewLearner() {
		LearningPlayer learner = learnerPrototype.clone();
		learner.reset();
		return learner;
	}
	
	public float getMutationProbability() {
		return mutationProbability;
	}

	public float getCrossoverProbability() {
		return crossoverProbability;
	}
	
	public double getLearningRate() {
		return learningRate;
	}

	public int getTrainRepeats() {
		return trainRepeats;
	}

	public int getLearningIterations() {
		return learningIterations;
	}
}
