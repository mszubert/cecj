package cecj.subgame;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class SubgameMutationPipeline extends BreedingPipeline {

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
			((SubgameIndividual) inds[i]).defaultMutate(state, thread);
			((SubgameIndividual) inds[i]).evaluated = false;
		}

		return n;
	}

	public Parameter defaultBase() {
		return SubgameDefaults.base().push(P_MUTATION);
	}

}
