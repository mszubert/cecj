/*
  Copyright 2009 by Marcin Szubert
  Licensed under the Academic Free License version 3.0
 */

package cecj.fitness;

import java.util.Arrays;
import java.util.List;

import cecj.interaction.InteractionResult;

import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleFitness;
import ec.util.Parameter;

public class CompetitiveFitnessSharing implements FitnessAggregateMethod {

	private float[] fitnesses;

	public void prepareToAggregate(EvolutionState state, int subpop) {
		fitnesses = new float[state.population.subpops[subpop].individuals.length];
		Arrays.fill(fitnesses, 0.0f);
	}

	public void addToAggregate(EvolutionState state, int subpop,
			List<List<InteractionResult>> results, int weight) {

		Individual[] inds = state.population.subpops[subpop].individuals;
		if (results.size() != inds.length) {
			throw new IllegalArgumentException(
					"Results list's size must be equal to subpopulation size.");
		}

		int numOpponents = results.get(0).size();
		float[] opponentSum = new float[numOpponents];
		for (int opponent = 0; opponent < numOpponents; opponent++) {
			for (int ind = 0; ind < inds.length; ind++) {
				opponentSum[opponent] += results.get(ind).get(opponent).getNumericValue();
			}
		}

		for (int ind = 0; ind < inds.length; ind++) {
			float indFitness = 0;
			for (int opponent = 0; opponent < numOpponents; opponent++) {
				if (opponentSum[opponent] == 0) {
					continue;
				}

				indFitness += results.get(ind).get(opponent).getNumericValue()
						/ opponentSum[opponent];
			}

			fitnesses[ind] += indFitness * weight;
			((SimpleFitness) (inds[ind].fitness)).setFitness(state, indFitness, false);
		}
	}

	public void assignFitness(EvolutionState state, int subpop) {
		Individual[] inds = state.population.subpops[subpop].individuals;
		for (int ind = 0; ind < inds.length; ind++) {
			((SimpleFitness) inds[ind].fitness).setFitness(state, fitnesses[ind], false);
		}
	}

	public void setup(EvolutionState state, Parameter base) {
	}
}
