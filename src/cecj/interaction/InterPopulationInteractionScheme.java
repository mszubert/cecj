package cecj.interaction;

import java.util.ArrayList;
import java.util.List;

import cecj.problems.TestBasedProblem;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class InterPopulationInteractionScheme implements InteractionScheme {

	private static final String P_POP = "pop";
	private static final String P_SIZE = "subpops";

	/**
	 * Number of subpopulations.
	 */
	private int numSubpopulations;

	private TestBasedProblem problem;

	public void setup(EvolutionState state, Parameter base) {
		if (!(state.evaluator.p_problem instanceof TestBasedProblem)) {
			state.output.fatal("Intrapopulation interactions need symmetric problem definition\n");
		} else {
			problem = (TestBasedProblem) state.evaluator.p_problem;
		}

		Parameter popSizeParameter = new Parameter(P_POP).push(P_SIZE);
		numSubpopulations = state.parameters.getInt(popSizeParameter, null, 0);
		if (numSubpopulations <= 0) {
			state.output.fatal("Population size must be > 0.\n", popSizeParameter);
		}
	}

	public List<List<InteractionResult>> performInteractions(EvolutionState state, int subpop,
			List<List<Individual>> opponents) {
		List<List<InteractionResult>> subpopulationResults = new ArrayList<List<InteractionResult>>();
		Individual[] competitors = state.population.subpops[subpop].individuals;

		for (Individual competitor : competitors) {
			List<InteractionResult> results = new ArrayList<InteractionResult>();

			for (int subpop2 = 0; subpop2 < numSubpopulations; subpop2++) {
				if (subpop2 == subpop) {
					continue;
				}

				List<Individual> curOpponents = opponents.get(subpop2);
				for (Individual opponent : curOpponents) {
					results.add(problem.test(state, competitor, opponent).first);
				}
			}

			subpopulationResults.add(results);
		}

		return subpopulationResults;
	}

	public int getEvaluationsNumber(EvolutionState state, List<List<Individual>> opponents,
			boolean populationOpponents) {
		int result = 0;
		int subpops = state.population.subpops.length;
		for (int subpop = 0; subpop < subpops; subpop++) {
			for (int subpop2 = 0; subpop2 < subpops; subpop2++) {
				if (subpop == subpop2) {
					continue;
				}

				result += state.population.subpops[subpop].individuals.length
						* opponents.get(subpop2).size();
			}
		}

		return result;
	}
}
