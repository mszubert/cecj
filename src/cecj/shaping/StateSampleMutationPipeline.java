package cecj.shaping;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class StateSampleMutationPipeline extends BreedingPipeline {

	public Parameter defaultBase() {
		return new Parameter("state-samples").push("mutate");
	}

	@Override
	public int numSources() {
		return 1;
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
			((StateSampleIndividual) inds[i]).mutate(state, thread);
			((StateSampleIndividual) inds[i]).evaluated = false;
		}

		return n;
	}

}
