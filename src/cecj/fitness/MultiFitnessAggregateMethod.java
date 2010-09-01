package cecj.fitness;

import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleFitness;
import ec.util.Parameter;

public class MultiFitnessAggregateMethod implements FitnessAggregateMethod {

	private static final String P_NUM_METHODS = "num-methods";
	private static final String P_METHOD = "method";
	private static final String P_WEIGHT = "weight";

	private int numMethods;

	private FitnessAggregateMethod[] methods;
	private float[] methodsWeights;

	public void setup(EvolutionState state, Parameter base) {
		Parameter numMethodsParam = base.push(P_NUM_METHODS);
		numMethods = state.parameters.getInt(numMethodsParam, null, 1);
		if (numMethods == 0) {
			state.output.fatal("The number of multi-fitness aggregate methods must be >= 1).",
					numMethodsParam, null);
		}

		methods = new FitnessAggregateMethod[numMethods];
		methodsWeights = new float[numMethods];

		for (int i = 0; i < numMethods; i++) {
			Parameter methodParam = base.push(P_METHOD).push("" + i);
			methods[i] = (FitnessAggregateMethod) state.parameters.getInstanceForParameter(
					methodParam, null, FitnessAggregateMethod.class);
			methods[i].setup(state, methodParam);

			Parameter methodWeightParam = methodParam.push(P_WEIGHT);
			methodsWeights[i] = state.parameters.getFloatWithDefault(methodWeightParam, null, 1.0f);
		}
	}

	public void prepareToAggregate(EvolutionState state, int subpop) {
		for (int i = 0; i < numMethods; i++) {
			methods[i].prepareToAggregate(state, subpop);
		}
	}

	public void addToAggregate(EvolutionState state, int subpop, int[][] subpopulationResults,
			int weight) {
		for (int i = 0; i < numMethods; i++) {
			methods[i].addToAggregate(state, subpop, subpopulationResults, weight);
		}
	}

	public void assignFitness(EvolutionState state, int subpop) {
		Individual[] inds = state.population.subpops[subpop].individuals;
		float[] fitnesses = new float[inds.length];

		for (int i = 0; i < numMethods; i++) {
			methods[i].assignFitness(state, subpop);

			for (int ind = 0; ind < inds.length; ind++) {
				fitnesses[ind] += methodsWeights[i] * ((SimpleFitness) inds[ind].fitness).fitness();
			}
		}

		for (int ind = 0; ind < inds.length; ind++) {
			((SimpleFitness) inds[ind].fitness).setFitness(state, fitnesses[ind], false);
		}
	}
}
