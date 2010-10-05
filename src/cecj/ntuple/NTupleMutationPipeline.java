package cecj.ntuple;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class NTupleMutationPipeline extends BreedingPipeline {

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

		if (!(sources[0] instanceof BreedingPipeline)) {
			for (int i = start; i < n + start; i++) {
				inds[i] = (Individual) (inds[i].clone());
			}
		}

		for (int i = start; i < n + start; i++) {
			((NTupleIndividual) inds[i]).defaultMutate(state, thread);
			((NTupleIndividual) inds[i]).evaluated = false;
		}

		return n;
	}

	public Parameter defaultBase() {
		return NTupleDefaults.base().push(P_MUTATION);
	}

}
