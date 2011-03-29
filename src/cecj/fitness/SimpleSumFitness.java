/*
  Copyright 2009 by Marcin Szubert
  Licensed under the Academic Free License version 3.0
 */

package cecj.fitness;

import java.util.Arrays;

import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleFitness;
import ec.util.Parameter;

public class SimpleSumFitness implements FitnessAggregateMethod {

	protected float[] fitnesses;

	public void prepareToAggregate(EvolutionState state, int subpop) {
		fitnesses = new float[state.population.subpops[subpop].individuals.length];
		Arrays.fill(fitnesses, 0.0f);
	}

	public void addToAggregate(EvolutionState state, int subpop, float[][] subpopulationResults,
			int weight) {

		Individual[] inds = state.population.subpops[subpop].individuals;
		if (subpopulationResults.length != inds.length) {
			throw new IllegalArgumentException(
					"Results list's size must be equal to subpopulation size.");
		}

		for (int ind = 0; ind < inds.length; ind++) {
			float fitness = 0;
			for (float result : subpopulationResults[ind]) {
				fitness += result;
			}
			fitnesses[ind] += fitness * weight;
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
