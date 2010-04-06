package cecj.problems;

import cecj.interaction.InteractionResult;
import cecj.utils.Pair;
import ec.EvolutionState;
import ec.Individual;

public abstract class SymmetricTestBasedProblem extends TestBasedProblem implements
		SymmetricCompetitionProblem {

	public Pair<? extends InteractionResult> compete(EvolutionState state, Individual competitor1,
			Individual competitor2) {
		return test(state, competitor1, competitor2);
	}
}
