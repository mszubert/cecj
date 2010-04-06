package framsticks;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class FramsticksCrossoverPipeline extends BreedingPipeline {

	public static final String P_CROSSOVER = "xover";
	public static final int NUM_SOURCES = 2;

	public boolean tossSecondParent;
	FramsticksIndividual parents[];

	public FramsticksCrossoverPipeline() {
		parents = new FramsticksIndividual[2];
	}

	@Override
	public int numSources() {
		return NUM_SOURCES;
	}

	@Override
	public int produce(int min, int max, int start, int subpopulation, Individual[] inds,
			EvolutionState state, int thread) {
		int n = typicalIndsProduced();
		n = Math.max(Math.min(n, max), min);

		for (int i = start; i < n + start; i++) {
			if (sources[0] == sources[1]) {
				sources[0].produce(2, 2, 0, subpopulation, parents, state, thread);
				if (!(sources[0] instanceof BreedingPipeline)) {
					parents[0] = (FramsticksIndividual) (parents[0].clone());
					parents[1] = (FramsticksIndividual) (parents[1].clone());
				}
			} else {
				sources[0].produce(1, 1, 0, subpopulation, parents, state, thread);
				sources[1].produce(1, 1, 1, subpopulation, parents, state, thread);
				if (!(sources[0] instanceof BreedingPipeline)) {
					parents[0] = (FramsticksIndividual) (parents[0].clone());
				}
				if (!(sources[1] instanceof BreedingPipeline)) {
					parents[1] = (FramsticksIndividual) (parents[1].clone());
				}
			}

			parents[0].crossover(state, parents[1]);
			parents[0].evaluated = false;

			inds[i] = parents[0];
		}

		return n;
	}

	public Parameter defaultBase() {
		return FramsticksDefaults.base().push(P_CROSSOVER);
	}

	@Override
	public Object clone() {
		FramsticksCrossoverPipeline clone = (FramsticksCrossoverPipeline) (super.clone());
		clone.parents = parents.clone();
		return clone;
	}

}
