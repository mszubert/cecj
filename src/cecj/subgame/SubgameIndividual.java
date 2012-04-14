package cecj.subgame;

import java.util.Arrays;
import java.util.List;

import cecj.app.othello.OthelloGame;
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

	private static final int PASS = -1;
	

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
			if (s.getMutationType() == SubgameSpecies.M_UP_MUTATION) {
				mutateDown(s.getGame(), rng);
			} else {
				mutateUp(s.getGame(), rng);
			}
		}
	}

	private void mutateDown(BoardGame game, MersenneTwisterFast random) {
		adapt(game);

		List<GameMove> moves = game.findMoves();
		if (moves.isEmpty()) {
			trace[++lastPosition] = PASS;
		} else {
			int randomMoveIndex = random.nextInt(moves.size());
			GameMove move = moves.get(randomMoveIndex);
			trace[++lastPosition] = move.flatten();
		}
	}

	private void mutateUp(BoardGame game, MersenneTwisterFast random) {
		undoLastMove();
		if (random.nextBoolean()) {
			undoLastMove();
		}

		mutateDown(game, random);
	}

	@Override
	public String toString() {
		return Arrays.toString(trace);
	}

	private void undoLastMove() {
		if (lastPosition >= 0) {
			lastPosition--;
		}
	}

	public void init(BoardGame boardGame, MersenneTwisterFast random, int depth) {
		this.trace = new int[NUM_MAX_MOVES];
		this.lastPosition = -1;
		Arrays.fill(trace, PASS);

		boardGame.reset();
		while (lastPosition + 1 < depth && !boardGame.endOfGame()) {
			lastPosition++;
			List<GameMove> moves = boardGame.findMoves();
			if (!moves.isEmpty()) {
				int randomMoveIndex = random.nextInt(moves.size());
				GameMove move = moves.get(randomMoveIndex);
				trace[lastPosition] = move.flatten();
				boardGame.makeMove(move);
			} else {
				boardGame.pass();
			}
		}
	}

	public void adapt(BoardGame boardGame) {
		boardGame.reset();
		for (int i = 0; i <= lastPosition; i++) {
			if (trace[i] != PASS) {
				boardGame.makeMove(new GameMove(trace[i], boardGame));
			} else {
				boardGame.pass();
			}
		}
	}

	public static void main(String args[]) {
		SubgameIndividual subgame = new SubgameIndividual();
		MersenneTwisterFast random = new MersenneTwisterFast();
		BoardGame othello = new OthelloGame();
		
//		for (int i = 0; i <= 60; i++) {
//			subgame.init(othello, random, i);
//			System.out.println(othello.getBoard());
//		}
		
		subgame.init(othello, random, 0);
		for (int i = 0; i < 60; i++) {
			System.out.println("Mutate down, step:" + i);
			subgame.mutateDown(othello, random);
			System.out.println(othello.getBoard());
		}
		
		for (int i = 0; i < 200; i++) {
			System.out.println("Mutate up, step:" + i);
			subgame.mutateUp(othello, random);
			System.out.println(othello.getBoard());
		}
	}
}
