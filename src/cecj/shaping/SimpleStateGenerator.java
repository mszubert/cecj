package cecj.shaping;

import games.BoardGame;
import games.GameMove;
import games.player.Player;
import games.scenario.GameScenario;

import java.util.ArrayList;
import java.util.List;

import cecj.app.othello.OthelloGame;

public class SimpleStateGenerator implements StateGenerator {

	private double randomness;
	private Player[] players;

	public SimpleStateGenerator(double randomness, Player[] players) {
		this.randomness = randomness;
		this.players = players;
	}

	public List<BoardGame> generateTrace(BoardGame game) {
		List<BoardGame> states = new ArrayList<BoardGame>();

		while (!game.endOfGame()) {
			List<? extends GameMove> moves = game.findMoves();
			if (!moves.isEmpty()) {
				states.add(game.clone());

				if (Math.random() < randomness) {
					game.makeMove(moves.get((int) (Math.random() * moves.size())));
				} else {
					GameMove bestMove = GameScenario.chooseBestMove(game,
							players[game.getCurrentPlayer()], moves);
					game.makeMove(bestMove);
				}
			} else {
				game.pass();
			}
		}

		return states;
	}

	public BoardGame generateSingleState() {
		OthelloGame othello = new OthelloGame();
		othello.reset();

		List<BoardGame> trace = generateTrace(othello);
		return trace.get((int) (Math.random() * trace.size()));
	}

	public BoardGame generatePreTerminalState() {
		OthelloGame othello = new OthelloGame();
		othello.reset();

		List<BoardGame> trace = generateTrace(othello);
		return trace.get(trace.size() - 1);
	}

	public List<BoardGame> generateRandomStateSet(BoardGame boardGame, int n) {
		List<BoardGame> states = new ArrayList<BoardGame>();
		for (int s = 0; s < n; s++) {
			states.add(generateSingleState());
		}
		return states;
	}
}
