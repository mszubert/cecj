package cecj.problem;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;

public abstract class TestBasedProblem extends Problem {

	public abstract int test(EvolutionState state, Individual candidate, Individual test);

	public boolean solves(EvolutionState state, Individual candidate, Individual test) {
		return (test(state, candidate, test) > 0);
	}
}
