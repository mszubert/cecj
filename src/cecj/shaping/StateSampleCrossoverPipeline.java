package cecj.shaping;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class StateSampleCrossoverPipeline extends BreedingPipeline {

	private StateSampleIndividual parents[];
	
	public StateSampleCrossoverPipeline() {
		parents = new StateSampleIndividual[2];
	}
	
	public Parameter defaultBase() {
		return new Parameter("state-samples").push("xover");
	}

	@Override
	public int numSources() {
		return 2;
	}

	@Override
	public Object clone() {
		StateSampleCrossoverPipeline pipeline = (StateSampleCrossoverPipeline) (super.clone());
		pipeline.parents = parents.clone();
		return pipeline;
	}

	@Override
	public int typicalIndsProduced() {
		return minChildProduction() * 2;
	}
	
	@Override
	public int produce(int min, int max, int start, int subpopulation, Individual[] inds,
			EvolutionState state, int thread) {
		int n = typicalIndsProduced();
		if (n < min)
			n = min;
		if (n > max)
			n = max;

		for (int i = start; i < n + start; i += 2) {
			if (sources[0] == sources[1]) {
				sources[0].produce(2, 2, 0, subpopulation, parents, state, thread);

				if (!(sources[0] instanceof BreedingPipeline)) {
					parents[0] = (StateSampleIndividual) (parents[0].clone());
					parents[1] = (StateSampleIndividual) (parents[1].clone());
				}
			} else {
				sources[0].produce(1, 1, 0, subpopulation, parents, state, thread);
				sources[1].produce(1, 1, 1, subpopulation, parents, state, thread);
				if (!(sources[0] instanceof BreedingPipeline)) {
					parents[0] = (StateSampleIndividual) (parents[0].clone());
				}
				if (!(sources[1] instanceof BreedingPipeline)) {
					parents[1] = (StateSampleIndividual) (parents[1].clone());
				}
			}

			parents[0].crossover(state, thread, parents[1]);
			parents[0].evaluated = false;
			parents[1].evaluated = false;

			inds[i] = parents[0];
			if (i + 1 < n+start) {
				inds[i + 1] = parents[1];
			}
		}

		return n;
	}

}
