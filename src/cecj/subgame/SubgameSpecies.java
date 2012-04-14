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

	public final static String P_MUTATION_TYPE = "mutation-type";
	
	public static final int M_DOWN_MUTATION = 0;
	public static final String V_DOWN_MUTATION = "weight";

	public static final int M_UP_MUTATION = 1;
	public static final String V_UP_MUTATION = "position";
	
	public static final String P_DEPTH = "depth";
			
	private BoardGame boardGame;

	private int subgameDepth;
	private int mutationType;
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

		String type = state.parameters.getStringWithDefault(base.push(P_MUTATION_TYPE), null, V_DOWN_MUTATION);
		if (type.equalsIgnoreCase(V_UP_MUTATION)) {
			mutationType = M_UP_MUTATION;
		} else if (type.equalsIgnoreCase(V_DOWN_MUTATION)) {
			mutationType = M_DOWN_MUTATION;
		} else {
			state.output.error("SubgameMutationPipeline given a bad mutation type: " + type,
					base.push(P_MUTATION_TYPE), null);
		}
		
		subgameDepth = state.parameters.getIntWithDefault(base.push(P_DEPTH), defaultBase().push(P_DEPTH), 0);
		
		state.output.exitIfErrors();
		super.setup(state, base);
	}

	@Override
	public Individual newIndividual(final EvolutionState state, int thread) {
		SubgameIndividual individual = (SubgameIndividual) (super.newIndividual(state, thread));
		individual.init(boardGame, state.random[thread], subgameDepth);
		return individual;
	}

	public float getMutationProbability() {
		return mutationProbability;
	}

	public BoardGame getGame() {
		return boardGame;
	}
	
	public int getMutationType() {
		return mutationType;
	}
}
