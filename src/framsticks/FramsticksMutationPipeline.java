package framsticks;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class FramsticksMutationPipeline extends BreedingPipeline {

	public static final String P_MUTATION = "mutate";
	public static final int NUM_SOURCES = 1;

	@Override
	public int numSources() {
		return NUM_SOURCES;
	}

	@Override
	public int produce(int min, int max, int start, int subpopulation, Individual[] inds,
			EvolutionState state, int thread) {
		int n = sources[0].produce(min, max, start, subpopulation, inds, state, thread);

		if (!(sources[0] instanceof BreedingPipeline))
			for (int q = start; q < n + start; q++)
				inds[q] = (Individual) (inds[q].clone());

		for (int q = start; q < n + start; q++) {
			((FramsticksIndividual) inds[q]).mutate(state);
			((FramsticksIndividual) inds[q]).evaluated = false;
		}

		return n;
	}

	public Parameter defaultBase() {
		return FramsticksDefaults.base().push(P_MUTATION);
	}
}
