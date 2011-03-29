package framsticks;

import ec.EvolutionState;
import ec.Individual;
import ec.Species;
import ec.util.Parameter;

public class FramsticksSpecies extends Species {

	public static final String P_FRAMSTICKSSPECIES = "species";
	public final static String P_MUTATIONPROB = "mutation-prob";
	public final static String P_CROSSOVERPROB = "crossover-prob";

	public float mutationProbability;
	public float crossoverProbability;

	public Parameter defaultBase() {
		return FramsticksDefaults.base().push(P_FRAMSTICKSSPECIES);
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		Parameter def = defaultBase();

		mutationProbability = state.parameters.getFloatWithMax(base.push(P_MUTATIONPROB), def
				.push(P_MUTATIONPROB), 0.0, 1.0);
		if (mutationProbability == -1.0)
			state.output
					.error(
							"FramsticksSpecies must have a mutation probability between 0.0 and 1.0 inclusive",
							base.push(P_MUTATIONPROB), def.push(P_MUTATIONPROB));

		crossoverProbability = state.parameters.getFloatWithMax(base.push(P_CROSSOVERPROB), def
				.push(P_CROSSOVERPROB), 0.0, 1.0);
		if (crossoverProbability == -1.0)
			state.output
					.error(
							"FramsticksSpecies must have a crossover probability between 0.0 and 1.0 inclusive",
							base.push(P_CROSSOVERPROB), def.push(P_CROSSOVERPROB));

		state.output.exitIfErrors();
		super.setup(state, base);
	}

	@Override
	public Individual newIndividual(final EvolutionState state, int thread) {
		FramsticksIndividual newind = (FramsticksIndividual) (super.newIndividual(state, thread));
		newind.mutate(state);
		return newind;
	}
}
