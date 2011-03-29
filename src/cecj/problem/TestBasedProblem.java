package cecj.problem;

import cecj.interaction.TestResult;
import ec.EvolutionState;
import ec.Individual;
import ec.Problem;

public abstract class TestBasedProblem extends Problem {

	public abstract TestResult test(EvolutionState state, Individual candidate, Individual test);

	public boolean solves(EvolutionState state, Individual candidate, Individual test) {
		TestResult testResult = test(state, candidate, test);
		return (testResult.getCandidateScore() > testResult.getTestScore());
	}
}
