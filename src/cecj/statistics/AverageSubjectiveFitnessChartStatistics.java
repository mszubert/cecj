package cecj.statistics;

import ec.EvolutionState;
import ec.display.chart.XYSeriesChartStatistics;
import ec.util.Parameter;

public class AverageSubjectiveFitnessChartStatistics extends XYSeriesChartStatistics {

	private static final String P_POP = "pop";
	private static final String P_SIZE = "subpops";

	private int numSubpopulations;
	private int[] seriesID;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		Parameter popSizeParameter = new Parameter(P_POP).push(P_SIZE);
		numSubpopulations = state.parameters.getInt(popSizeParameter, null, 0);
		seriesID = new int[numSubpopulations];

		for (int i = 0; i < numSubpopulations; ++i) {
			seriesID[i] = addSeries("SubPop " + i);
		}
	}

	@Override
	public void postEvaluationStatistics(EvolutionState state) {
		super.postEvaluationStatistics(state);

		for (int subPop = 0; subPop < numSubpopulations; ++subPop) {
			double averageFitness = 0;
			for (int i = 0; i < state.population.subpops[subPop].individuals.length; ++i) {
				averageFitness += state.population.subpops[subPop].individuals[i].fitness.fitness();
			}
			averageFitness /= state.population.subpops[subPop].individuals.length;

			addDataPoint(seriesID[subPop], state.generation, averageFitness);
		}
	}
}
