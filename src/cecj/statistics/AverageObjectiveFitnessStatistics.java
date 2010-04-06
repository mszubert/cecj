package cecj.statistics;

import java.io.File;
import java.io.IOException;

import ec.EvolutionState;
import ec.Individual;
import ec.Statistics;
import ec.util.Output;
import ec.util.Parameter;

public class AverageObjectiveFitnessStatistics extends Statistics {

	private static final String P_COUNT_LAST = "count-last";
	private static final String P_FREQUENCY = "frequency";
	private static final String P_FITNESS_CALCULATOR = "fitness-calc";
	private static final String P_FITNESS_FILE = "fitness-file";

	public int fitnessStatisticslog;

	public AverageObjectiveFitnessStatistics() {
		fitnessStatisticslog = 0;
	}

	private int frequency;
	private ObjectiveFitnessCalculator fitnessCalc;

	private int countLast;
	private int lastCounter;
	private float sumLastFitnesses;
	private float lastFitnesses[];

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		Parameter countLastParam = base.push(P_COUNT_LAST);
		countLast = state.parameters.getIntWithDefault(countLastParam, null, -1);
		if (countLast != -1) {
			lastFitnesses = new float[countLast];
			lastCounter = 0;
			sumLastFitnesses = 0.0f;
		}

		Parameter fitnessCalcParameter = base.push(P_FITNESS_CALCULATOR);
		fitnessCalc = (ObjectiveFitnessCalculator) state.parameters
			.getInstanceForParameter(fitnessCalcParameter, null, ObjectiveFitnessCalculator.class);
		fitnessCalc.setup(state, fitnessCalcParameter);

		Parameter frequencyParam = base.push(P_FREQUENCY);
		frequency = state.parameters.getIntWithDefault(frequencyParam, null, 1);

		File fitnessStatisticsFile = state.parameters.getFile(base.push(P_FITNESS_FILE), null);
		if (fitnessStatisticsFile != null) {
			try {
				fitnessStatisticslog = state.output.addLog(fitnessStatisticsFile, Output.V_VERBOSE,
															false, true, false);
			} catch (IOException i) {
				state.output.fatal("An IOException occurred while trying to create the log "
						+ fitnessStatisticsFile + ":\n" + i);
			}
		}
	}

	@Override
	public void postEvaluationStatistics(EvolutionState state) {
		super.postEvaluationStatistics(state);

		if ((state.generation % frequency != 0) && (state.generation != state.numGenerations - 1)) {
			return;
		}

		for (int subpop = 0; subpop < state.population.subpops.length; ++subpop) {
			float sumFitness = 0;
			float maxFitness = Float.NEGATIVE_INFINITY;
			float maxSubjectiveFitness = Float.NEGATIVE_INFINITY;
			float objectiveFitnessOfSubjectivelyBest = -1;

			float[] fitnesses = new float[state.population.subpops[subpop].individuals.length];

			for (int i = 0; i < state.population.subpops[subpop].individuals.length; ++i) {
				Individual ind = state.population.subpops[subpop].individuals[i];
				fitnesses[i] = fitnessCalc.calculateObjectiveFitness(state, ind);

				sumFitness += fitnesses[i];
				maxFitness = Math.max(maxFitness, fitnesses[i]);
				if (ind.fitness.fitness() > maxSubjectiveFitness) {
					maxSubjectiveFitness = ind.fitness.fitness();
					objectiveFitnessOfSubjectivelyBest = fitnesses[i];
				}
			}

			float sumSquareDiffs = 0;
			float avgFitness = sumFitness / state.population.subpops[subpop].individuals.length;
			for (int i = 0; i < state.population.subpops[subpop].individuals.length; ++i) {
				float diff = fitnesses[i] - avgFitness;
				sumSquareDiffs += (diff * diff);
			}

			double stdev = Math.sqrt(sumSquareDiffs
					/ state.population.subpops[subpop].individuals.length);

			if (countLast != -1) {
				sumLastFitnesses -= lastFitnesses[lastCounter];
				sumLastFitnesses += objectiveFitnessOfSubjectivelyBest;

				lastFitnesses[lastCounter] = objectiveFitnessOfSubjectivelyBest;
				lastCounter++;
				lastCounter %= countLast;

				float avgLastFitness = sumLastFitnesses / countLast;
				state.output.println(state.generation + "\t" + avgFitness + "\t" + stdev + "\t"
						+ maxFitness + "\t" + objectiveFitnessOfSubjectivelyBest + "\t"
						+ avgLastFitness, Output.V_VERBOSE + 1, fitnessStatisticslog);
			} else {
				state.output.println(state.generation + "\t" + avgFitness + "\t" + stdev + "\t"
						+ maxFitness + "\t" + objectiveFitnessOfSubjectivelyBest,
										Output.V_VERBOSE + 1, fitnessStatisticslog);
			}
		}

		if (state.generation == state.numGenerations - 1) {
			saveIndividuals(state);
		}
	}

	private void saveIndividuals(EvolutionState state) {
		for (int subpop = 0; subpop < state.population.subpops.length; ++subpop) {
			state.output.println("\nSubpopulation " + subpop + " individuals : ",
									Output.V_VERBOSE + 1, fitnessStatisticslog);
			Individual[] inds = state.population.subpops[subpop].individuals;
			for (Individual ind : inds) {
				ind.printIndividual(state, fitnessStatisticslog, Output.V_VERBOSE + 1);
			}
		}
	}
}
