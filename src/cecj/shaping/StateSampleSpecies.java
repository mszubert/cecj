package cecj.shaping;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class StateSampleSpecies extends TrainerSpecies {

	public final static String P_SAMPLE_SIZE = "sample-size";
	private static final String P_RPROP_LEARNING = "rprop-learning";
	private static final String P_ONLINE_LEARNING = "online-learning";
	private static final String P_RANDOMNESS = "randomness";
	
	private int sampleSize = 50;
	public double randomness = 0.0;
	private boolean rpropLearning = false;
	private boolean onlineLearning = true;

	private StateGenerator stateGenerator;

	@Override
	public Parameter defaultBase() {
		return new Parameter("state-samples").push("species");
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {

		sampleSize = state.parameters.getIntWithDefault(base.push(P_SAMPLE_SIZE), defaultBase()
				.push(P_SAMPLE_SIZE), 50);

		rpropLearning = state.parameters.getBoolean(base.push(P_RPROP_LEARNING), null, false);
		onlineLearning = state.parameters.getBoolean(base.push(P_ONLINE_LEARNING), null, true);
		randomness = state.parameters.getDoubleWithDefault(base.push(P_RANDOMNESS), null, 0.0);
		
		stateGenerator = new SimpleStateGenerator(1.0, null);
		state.output.exitIfErrors();
		super.setup(state, base);
	}

	@Override
	public Individual newIndividual(final EvolutionState state, int thread) {
		StateSampleIndividual individual = (StateSampleIndividual) (super.newIndividual(state,
				thread));
		individual.randomize(state, thread, stateGenerator);
		return individual;
	}

	
	public StateGenerator getStateGenerator() {
		return stateGenerator;
	}

	public int getSampleSize() {
		return sampleSize;
	}

	public boolean isRPropLearning() {
		return rpropLearning;
	}

	public boolean isOnlineLearning() {
		return onlineLearning;
	}

	public double getRandomness() {
		return randomness;
	}
}
