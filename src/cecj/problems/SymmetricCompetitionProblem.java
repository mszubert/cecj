package cecj.problems;

import cecj.interaction.InteractionResult;
import cecj.utils.Pair;
import ec.EvolutionState;
import ec.Individual;

/**
 * Problem is symmetric if sets of possible behaviours of both competitors are identical. It does
 * not mean that the game is symmetric itself in game-theoretic meaning ( )
 * 
 * @author Marcin Szubert
 */
public interface SymmetricCompetitionProblem extends CoevolutionaryProblem {

	/**
	 * 
	 * @param state
	 * @param competitor1
	 * @param competitor2
	 * @return
	 */
	public Pair<? extends InteractionResult> compete(EvolutionState state, Individual competitor1,
			Individual competitor2);
}
