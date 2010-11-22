package cecj.interaction;

import java.util.List;

import cecj.problem.TestBasedProblem;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

/**
 * 
 * 
 * @author Marcin Szubert
 * 
 */
public class IntraPopulationInteractionScheme implements InteractionScheme {

	private static final String P_PLAY_BOTH = "play-both";
	private static final String P_RESULT_INTERPRETER = "result-interpreter";

	private boolean playBoth;
	private TestBasedProblem problem;
	private InteractionResultInterpreter resultInterpreter;

	public void setup(EvolutionState state, Parameter base) {
		if (!(state.evaluator.p_problem instanceof TestBasedProblem)) {
			state.output.fatal("Intrapopulation interactions need symmetric problem definition\n");
		} else {
			problem = (TestBasedProblem) state.evaluator.p_problem;
		}

		Parameter resultInterpreterParam = base.push(P_RESULT_INTERPRETER);
		resultInterpreter = (InteractionResultInterpreter) (state.parameters
				.getInstanceForParameter(resultInterpreterParam, null,
						InteractionResultInterpreter.class));

		Parameter playBothParam = base.push(P_PLAY_BOTH);
		playBoth = state.parameters.getBoolean(playBothParam, null, true);
	}

	public int[][] performInteractions(EvolutionState state, int subpop,
			List<List<Individual>> opponents) {

		Individual[] competitors = state.population.subpops[subpop].individuals;
		List<Individual> curOpponents = opponents.get(subpop);
		int[][] results = new int[competitors.length][playBoth ? curOpponents.size() * 2
				: curOpponents.size()];

		for (int competitorIndex = 0; competitorIndex < competitors.length; competitorIndex++) {
			Individual competitor = competitors[competitorIndex];

			int opponentIndex = 0;
			for (Individual opponent : curOpponents) {
				results[competitorIndex][opponentIndex++] = resultInterpreter
						.getCandidateValue(problem.test(state, competitor, opponent));
				if (playBoth) {
					results[competitorIndex][opponentIndex++] = resultInterpreter
							.getTestValue(problem.test(state, opponent, competitor));
				}
			}
		}

		return results;
	}

	public int getEvaluationsNumber(EvolutionState state, List<List<Individual>> opponents,
			boolean populationOpponents) {
		int result = 0;
		for (int subpop = 0; subpop < state.population.subpops.length; subpop++) {
			int o = opponents.get(subpop).size();
			int n = state.population.subpops[subpop].individuals.length;

			if (populationOpponents) {
				result += (2 * (n - o) * o);
				result += (o * (o - 1));
			} else {
				result += (2 * n * o);
			}
		}

		return result;
	}
}
