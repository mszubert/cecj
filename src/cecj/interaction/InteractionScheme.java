package cecj.interaction;

import java.util.List;

import ec.EvolutionState;
import ec.Individual;
import ec.Setup;

/**
 * Interface representing an abstract scheme of interactions between individuals.
 * 
 * Its implementations should read population roles in interactions and depending on their roles
 * assign given interaction sequence. Moreover it is the only entity conscious of what method of
 * problem definition must be called to properly evaluate interacting individuals.
 * 
 * @author Marcin Szubert
 * 
 */
public interface InteractionScheme extends Setup {

	public int[][] performInteractions(EvolutionState state, int subpop,
			List<List<Individual>> opponents);

	public int getEvaluationsNumber(EvolutionState state, List<List<Individual>> opponents,
			boolean populationOpponents);
}
