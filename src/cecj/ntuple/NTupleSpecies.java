package cecj.ntuple;

import ec.EvolutionState;
import ec.Individual;
import ec.Species;
import ec.util.Parameter;

/**
 * 
 * Species representing NTyple System representation
 * 
 * @author Marcin Szubert
 *
 */
public class NTupleSpecies extends Species {

	public static final String P_NTUPLE_SPECIES = "species";

	public final static String P_MUTATION_PROB = "mutation-prob";
	public final static String P_CROSSOVER_PROB = "crossover-prob";

	public final static String P_TUPLE_ARITY = "tuple-arity";
	public final static String P_NUM_TUPLES = "num-tuples";
	public final static String P_NUM_VALUES = "num-values";

	private float mutationProbability;
	private float crossoverProbability;

	/**
	 * Number of elements in each tuple
	 */
	private int tupleArity;
	
	/**
	 * Number of tuples in the system
	 */
	private int numTuples;
	
	/**
	 * Number of possible values of each element of the tuple
	 */
	private int numValues;

	
	public Parameter defaultBase() {
		return NTupleDefaults.base().push(P_NTUPLE_SPECIES);
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		mutationProbability = state.parameters.getFloat(base.push(P_MUTATION_PROB), defaultBase()
				.push(P_MUTATION_PROB), 0.0, 1.0);
		if (mutationProbability == -1.0) {
			state.output.error(
					"NTupleSpecies must have a mutation probability between 0.0 and 1.0 inclusive",
					base.push(P_MUTATION_PROB), defaultBase().push(P_MUTATION_PROB));
		}

		crossoverProbability = state.parameters.getFloat(base.push(P_CROSSOVER_PROB), defaultBase()
				.push(P_CROSSOVER_PROB), 0.0, 1.0);
		if (crossoverProbability == -1.0) {
			state.output
					.error(
							"NTupleSpecies must have a crossover probability between 0.0 and 1.0 inclusive",
							base.push(P_CROSSOVER_PROB), defaultBase().push(P_CROSSOVER_PROB));
		}

		tupleArity = state.parameters.getInt(base.push(P_TUPLE_ARITY), defaultBase().push(
				P_TUPLE_ARITY), 1);
		if (tupleArity == 0) {
			state.output.error("NTupleSpecies must have tuple arity which is > 0");
		}

		numTuples = state.parameters.getInt(base.push(P_NUM_TUPLES), defaultBase().push(
				P_TUPLE_ARITY), 1);
		if (numTuples == 0) {
			state.output.error("NTupleSpecies must have number of tuples which is > 0");
		}

		numValues = state.parameters.getInt(base.push(P_NUM_VALUES), defaultBase().push(
				P_NUM_VALUES), 2);
		if (numValues == 1) {
			state.output.error("NTupleSpecies must have number of values which is > 1");
		}
		
		state.output.exitIfErrors();

		super.setup(state, base);
	}

	@Override
	public Individual newIndividual(final EvolutionState state, int thread) {
		NTupleIndividual individual = (NTupleIndividual) (super.newIndividual(state, thread));
		individual.reset(state, thread);
		return individual;
	}

	public float getMutationProbability() {
		return mutationProbability;
	}

	public float getCrossoverProbability() {
		return crossoverProbability;
	}
}
