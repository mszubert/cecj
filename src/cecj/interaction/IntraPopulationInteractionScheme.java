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

	protected boolean playBoth;
	protected TestBasedProblem problem;

	public void setup(EvolutionState state, Parameter base) {
		if (!(state.evaluator.p_problem instanceof TestBasedProblem)) {
			state.output.fatal("Intrapopulation interactions need symmetric problem definition\n");
		} else {
			problem = (TestBasedProblem) state.evaluator.p_problem;
		}

		Parameter playBothParam = base.push(P_PLAY_BOTH);
		playBoth = state.parameters.getBoolean(playBothParam, null, true);
	}

	public float[][] performInteractions(EvolutionState state, int subpop,
			List<List<Individual>> opponents) {

		Individual[] competitors = state.population.subpops[subpop].individuals;
		List<Individual> curOpponents = opponents.get(subpop);
		float[][] results = new float[competitors.length][playBoth ? curOpponents.size() * 2
				: curOpponents.size()];

		for (int competitorIndex = 0; competitorIndex < competitors.length; competitorIndex++) {
			Individual competitor = competitors[competitorIndex];

			int opponentIndex = 0;
			for (Individual opponent : curOpponents) {
				results[competitorIndex][opponentIndex++] = problem.test(state, competitor,
						opponent).getCandidateScore();
				if (playBoth) {
					results[competitorIndex][opponentIndex++] = problem.test(state, opponent,
							competitor).getTestScore();
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
