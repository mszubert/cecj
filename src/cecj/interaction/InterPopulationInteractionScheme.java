package cecj.interaction;

import java.util.List;

import cecj.problem.TestBasedProblem;
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

	public float[][] performInteractions(EvolutionState state, int subpop,
			List<List<Individual>> opponents) {
		Individual[] competitors = state.population.subpops[subpop].individuals;

		int numOpponents = 0;
		for (int subpop2 = 0; subpop2 < numSubpopulations; subpop2++) {
			if (subpop2 != subpop) {
				numOpponents += opponents.get(subpop2).size();
			}
		}

		float[][] subpopulationResults = new float[competitors.length][numOpponents];
		for (int competitorIndex = 0; competitorIndex < competitors.length; competitorIndex++) {
			Individual competitor = competitors[competitorIndex];

			int opponentIndex = 0;
			for (int subpop2 = 0; subpop2 < numSubpopulations; subpop2++) {
				if (subpop2 != subpop) {
					List<Individual> curOpponents = opponents.get(subpop2);
					for (Individual opponent : curOpponents) {
						subpopulationResults[competitorIndex][opponentIndex] = problem.test(state,
								competitor, opponent).getCandidateScore();
						opponentIndex++;
					}
				}
			}
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
