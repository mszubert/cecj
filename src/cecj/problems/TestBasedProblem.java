package cecj.problems;

import cecj.interaction.InteractionResult;
import cecj.utils.Pair;
import ec.EvolutionState;
import ec.Individual;
import ec.Problem;

public abstract class TestBasedProblem extends Problem {

	public abstract Pair<? extends InteractionResult> test(EvolutionState state,
			Individual candidate, Individual test);

	public boolean solves(EvolutionState state, Individual candidate, Individual test) {
		return (test(state, candidate, test).first.getNumericValue() > 0);
	}
}
