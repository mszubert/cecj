package cecj.fitness;

import java.util.ArrayList;
import java.util.List;

import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleFitness;
import ec.util.Parameter;

public class DistinctionFitnessSharing implements FitnessAggregateMethod {

	int subpopSize;
	List<List<Float>> results;

	public void prepareToAggregate(EvolutionState state, int subpop) {
		results = new ArrayList<List<Float>>();
		subpopSize = state.population.subpops[subpop].individuals.length;

		for (int i = 0; i < subpopSize; i++) {
			results.add(new ArrayList<Float>());
		}
	}

	public void addToAggregate(EvolutionState state, int subpop, float[][] subpopulationResults,
			int weight) {
		if (results.size() != subpopSize) {
			throw new IllegalArgumentException(
					"Results list's size must be equal to subpopulation size.");
		}

		for (int ind = 0; ind < subpopSize; ind++) {
			for (float result : subpopulationResults[ind]) {
				results.get(ind).add(result);
			}
		}
	}

	public void assignFitness(EvolutionState state, int subpop) {
		float[] fitnesses = new float[subpopSize];

		int numOpponents = results.get(0).size();
		for (int op1 = 0; op1 < numOpponents; op1++) {
			for (int op2 = 0; op2 < numOpponents; op2++) {
				if (op1 == op2) {
					continue;
				}

				int numDistinctions = 0;
				for (int ind = 0; ind < subpopSize; ind++) {
					if (results.get(ind).get(op1) > results.get(ind).get(op2)) {
						numDistinctions++;
					}
				}

				for (int ind = 0; ind < subpopSize; ind++) {
					if (results.get(ind).get(op1) > results.get(ind).get(op2)) {
						fitnesses[ind] += (1.0f / numDistinctions);
					}
				}
			}
		}

		Individual[] inds = state.population.subpops[subpop].individuals;
		for (int ind = 0; ind < inds.length; ind++) {
			((SimpleFitness) inds[ind].fitness).setFitness(state, fitnesses[ind], false);
		}
	}

	public void setup(EvolutionState state, Parameter base) {
	}
}
