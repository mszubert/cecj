package cecj.fitness;

import java.util.List;

import cecj.interaction.InteractionResult;
import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleFitness;
import ec.util.Parameter;

public class DistinctionSumFitness implements FitnessAggregateMethod {

	private int[] numChallenged;
	private int[] numOpponents;

	public void prepareToAggregate(EvolutionState state, int subpop) {
		numChallenged = new int[state.population.subpops[subpop].individuals.length];
		numOpponents = new int[state.population.subpops[subpop].individuals.length];
	}

	public void addToAggregate(EvolutionState state, int subpop,
			List<List<InteractionResult>> subpopulationResults, int weight) {

		Individual[] inds = state.population.subpops[subpop].individuals;
		if (subpopulationResults.size() != inds.length) {
			throw new IllegalArgumentException(
					"Results list's size must be equal to subpopulation size.");
		}

		for (int ind = 0; ind < inds.length; ind++) {
			for (InteractionResult result : subpopulationResults.get(ind)) {
				if (result.getNumericValue() > 0) {
					numChallenged[ind]++;
				}
			}

			numOpponents[ind] += subpopulationResults.get(ind).size();
		}
	}

	public void assignFitness(EvolutionState state, int subpop) {
		Individual[] inds = state.population.subpops[subpop].individuals;
		for (int ind = 0; ind < inds.length; ind++) {
			((SimpleFitness) inds[ind].fitness).setFitness(state, numChallenged[ind]
					* (numOpponents[ind] - numChallenged[ind]), false);
		}
	}

	public void setup(EvolutionState state, Parameter base) {
	}
}
