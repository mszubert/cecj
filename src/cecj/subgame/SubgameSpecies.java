package cecj.subgame;

import ec.EvolutionState;
import ec.Individual;
import ec.Species;
import ec.util.Parameter;
import games.BoardGame;

public class SubgameSpecies extends Species {

	private static final String P_GAME = "game";

	public static final String P_SUBGAME_SPECIES = "species";

	public final static String P_MUTATION_PROB = "mutation-prob";

	private BoardGame boardGame;

	private float mutationProbability;

	public Parameter defaultBase() {
		return SubgameDefaults.base().push(P_SUBGAME_SPECIES);
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		mutationProbability = state.parameters.getFloatWithMax(base.push(P_MUTATION_PROB),
				defaultBase().push(P_MUTATION_PROB), 0.0, 1.0);
		if (mutationProbability == -1.0) {
			state.output
					.error("SubgameSpecies must have a mutation probability between 0.0 and 1.0 inclusive",
							base.push(P_MUTATION_PROB), defaultBase().push(P_MUTATION_PROB));
		}

		Parameter gameParam = new Parameter(P_GAME);
		boardGame = (BoardGame) state.parameters.getInstanceForParameter(gameParam, null,
				BoardGame.class);

		state.output.exitIfErrors();
		super.setup(state, base);
	}

	@Override
	public Individual newIndividual(final EvolutionState state, int thread) {
		SubgameIndividual individual = (SubgameIndividual) (super.newIndividual(state, thread));
		individual.init(state, boardGame);
		return individual;
	}

	public float getMutationProbability() {
		return mutationProbability;
	}

	public BoardGame getGame() {
		return boardGame;
	}
}
