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

	public final static String P_MUTATION_STDEV = "mutation-stdev";
	public static final String P_SYSTEM = "system";

	private float mutationProbability;
	private float crossoverProbability;

	private float mutationStdev;
	private NTupleSystem tupleSystem;
	
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

		mutationStdev = state.parameters.getFloat(base.push(P_MUTATION_STDEV), defaultBase().push(
				P_MUTATION_STDEV), 0);
		if (mutationStdev <= 0) {
			state.output.fatal("NTupleSpecies must have a strictly positive standard deviation",
					base.push(P_MUTATION_STDEV), defaultBase().push(P_MUTATION_STDEV));
		}

		tupleSystem = new NTupleSystem();
		tupleSystem.setup(state, NTupleDefaults.base().push(P_SYSTEM));
		
		state.output.exitIfErrors();
		super.setup(state, base);
	}

	@Override
	public Individual newIndividual(final EvolutionState state, int thread) {
		NTupleIndividual individual = (NTupleIndividual) (super.newIndividual(state, thread));
		tupleSystem.randomizeIndividual(state, thread, individual);
		return individual;
	}

	public float getMutationProbability() {
		return mutationProbability;
	}

	public float getCrossoverProbability() {
		return crossoverProbability;
	}

	public double getMutationStdev() {
		return mutationStdev;
	}
}
