package cecj.interaction;

import java.util.ArrayList;
import java.util.List;

import cecj.problem.TestBasedProblem;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

/**
 * 
 * @author Marcin Szubert
 * 
 */
public class LearnerTeacherInteractionScheme implements InteractionScheme {

	private static final String P_POP = "pop";
	private static final String P_ROLE = "role";
	private static final String P_SIZE = "subpops";
	private static final String P_SUBPOP = "subpop";

	/**
	 * Number of subpopulations.
	 */
	private int numSubpopulations;

	private TestBasedProblem problem;

	public enum Role {
		LEARNER, TEACHER
	}

	private Role[] subpopulationRoles;

	public void setup(EvolutionState state, Parameter base) {
		if (!(state.evaluator.p_problem instanceof TestBasedProblem)) {
			state.output.fatal("Learner-teacher interactions need asymmetric problem definition\n");
		} else {
			problem = (TestBasedProblem) state.evaluator.p_problem;
		}

		Parameter popSizeParameter = new Parameter(P_POP).push(P_SIZE);
		numSubpopulations = state.parameters.getInt(popSizeParameter, null, 0);
		if (numSubpopulations <= 0) {
			state.output.fatal("Population size must be > 0.\n", popSizeParameter);
		}

		subpopulationRoles = new Role[numSubpopulations];
		for (int subpop = 0; subpop < numSubpopulations; subpop++) {
			Parameter subpopRoleParam = base.push(P_SUBPOP).push("" + subpop).push(P_ROLE);
			String role = state.parameters.getString(subpopRoleParam, null);
			if (role == null) {
				state.output.fatal("Subpopulation role must be specified for the learner-teacher "
						+ "interactions scheme\n", subpopRoleParam);
			} else {
				try {
					String role2 = role.toUpperCase();
					subpopulationRoles[subpop] = Enum.valueOf(Role.class, role2);
				} catch (IllegalArgumentException ex) {
					state.output.fatal("Subpopulation role " + role
							+ " does not exist in the learner-teacher interactions scheme");
				}
			}
		}
	}

	public int[][] performInteractions(EvolutionState state, int subpop,
			List<List<Individual>> opponents) {
		Individual[] inds = state.population.subpops[subpop].individuals;

		int numOpponents = 0;
		for (int subpop2 = 0; subpop2 < numSubpopulations; subpop2++) {
			if (subpopulationRoles[subpop2] != subpopulationRoles[subpop]) {
				numOpponents += opponents.get(subpop2).size();
			}
		}

		int[][] subpopulationResults = new int[inds.length][numOpponents];
		for (int ind = 0; ind < inds.length; ind++) {
			for (int subpop2 = 0; subpop2 < numSubpopulations; subpop2++) {
				if (subpopulationRoles[subpop2] != subpopulationRoles[subpop]) {
					List<Individual> curOpponents = opponents.get(subpop2);
					for (int opp = 0; opp < curOpponents.size(); opp++) {
						if (subpopulationRoles[subpop] == Role.LEARNER) {
							subpopulationResults[ind][opp] = problem.test(state, inds[ind],
									curOpponents.get(opp));
						} else {
							subpopulationResults[ind][opp] = -problem.test(state, curOpponents
									.get(opp), inds[ind]);
						}
					}
				}
			}
		}

		return subpopulationResults;
	}

	public List<Integer> getSubpopulationIndices(Role role) {
		List<Integer> result = new ArrayList<Integer>();
		for (int subpop = 0; subpop < numSubpopulations; subpop++) {
			if (subpopulationRoles[subpop] == role) {
				result.add(subpop);
			}
		}
		return result;
	}

	public int getEvaluationsNumber(EvolutionState state, List<List<Individual>> opponents,
			boolean populationOpponents) {
		int result = 0;
		for (int subpop1 = 0; subpop1 < numSubpopulations; subpop1++) {
			for (int subpop2 = subpop1 + 1; subpop2 < numSubpopulations; subpop2++) {
				if (subpopulationRoles[subpop1] != subpopulationRoles[subpop2]) {
					int n1 = state.population.subpops[subpop1].individuals.length;
					int n2 = state.population.subpops[subpop2].individuals.length;
					int o1 = opponents.get(subpop1).size();
					int o2 = opponents.get(subpop2).size();

					if (populationOpponents) {
						result += n1 * o2;
						result += (n2 - o2) * o1;
					} else {
						result += n1 * o2;
						result += n2 * o1;
					}
				}
			}
		}

		return result;
	}
}
