package cecj.subgame;

import java.util.Arrays;
import java.util.List;

import cecj.ntuple.NTupleDefaults;
import ec.EvolutionState;
import ec.Individual;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import games.BoardGame;
import games.GameMove;

public class SubgameIndividual extends Individual {

	public static final String P_SUBGAME_INDIVIDUAL = "subgame-ind";

	private static final int NUM_MAX_MOVES = 60 * 2;
	
	private static final int INITIAL_SUBGAME_SIZE = 2;
	
	private int lastPosition;

	private int[] trace;

	/**
	 * This method is called only once - on a prototype individual stored in the
	 * species class.
	 */
	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		if (!(species instanceof SubgameSpecies)) {
			state.output.fatal("SubgameIndividual requires a SubgameSpecies", base, defaultBase());
		}
	}

	@Override
	public boolean equals(Object ind) {
		if (!(ind instanceof SubgameIndividual)) {
			return false;
		}

		SubgameIndividual subgame = (SubgameIndividual) ind;
		return (Arrays.equals(trace, subgame.trace) && lastPosition == subgame.lastPosition);
	}

	@Override
	public Object clone() {
		SubgameIndividual clone = (SubgameIndividual) (super.clone());

		if (trace != null) {
			clone.trace = trace.clone();
		}

		clone.lastPosition = lastPosition;
		return clone;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(trace) ^ lastPosition;
	}

	public Parameter defaultBase() {
		return NTupleDefaults.base().push(P_SUBGAME_INDIVIDUAL);
	}

	public void defaultMutate(EvolutionState state, int thread) {
		SubgameSpecies s = (SubgameSpecies) species;
		float prob = s.getMutationProbability();
		if (!(prob > 0.0)) {
			return;
		}

		MersenneTwisterFast rng = state.random[thread];
		if (rng.nextBoolean(prob)) {
			undoLastMove();
			if (rng.nextBoolean()) {
				undoLastMove();
			}
			
			BoardGame game = s.getGame();
			adapt(game);
			
			List<GameMove> moves = game.findMoves();
			int randomMoveIndex = state.random[0].nextInt(moves.size());
			GameMove move = moves.get(randomMoveIndex);
			trace[++lastPosition] = move.flatten();
		}
	}

	@Override
	public String toString() {
		return Arrays.toString(trace);
	}

	private void undoLastMove() {
		do {
			lastPosition--;
		} while (trace[lastPosition + 1] == -1);
	}
	
	public void init(EvolutionState state, BoardGame boardGame) {
		this.trace = new int[NUM_MAX_MOVES];
		this.lastPosition = -1;
		Arrays.fill(trace, -1);

		boardGame.reset();
		while (!boardGame.endOfGame()) {
			lastPosition++;
			List<GameMove> moves = boardGame.findMoves();
			if (!moves.isEmpty()) {
				int randomMoveIndex = state.random[0].nextInt(moves.size());
				GameMove move = moves.get(randomMoveIndex);
				trace[lastPosition] = move.flatten();
				boardGame.makeMove(move);
			} else {
				boardGame.pass();
			}
		}
		
		this.lastPosition -= INITIAL_SUBGAME_SIZE;
	}

	public void adapt(BoardGame boardGame) {
		boardGame.reset();
		for (int i = 0; i <= lastPosition; i++) {
			if (trace[i] != -1) {
				boardGame.makeMove(new GameMove(trace[i], boardGame));
			} else {
				boardGame.pass();
			}
		}
	}

}
