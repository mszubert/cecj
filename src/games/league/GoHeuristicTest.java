package games.league;

import ec.util.MersenneTwisterFast;
import games.GameMove;
import games.player.Player;

import java.util.List;

import cecj.app.go.GoGame;
import cecj.app.go.GoHeuristicPlayer;

public class GoHeuristicTest {

	private Player[] players;
	private double[] prob;
	private MersenneTwisterFast random;

	public GoHeuristicTest()
	{
		random = new MersenneTwisterFast(2010);
		prob = new double[] {0.1, 0.1};
		Player player = new GoHeuristicPlayer().getOpponent(25);
		players = new Player[] {player, player};
 	}
	
	public double play(GoGame game, int newEvalPlayer) {
		while (!game.endOfGame()) {
			List<? extends GameMove> moves = game.findMoves();
			if (!moves.isEmpty()) {
				GameMove bestMove = null;
				if (random.nextBoolean(prob[game.getCurrentPlayer()])) {
					bestMove = moves.get(random.nextInt(moves.size()));
				} else {
					double bestEval = Double.NEGATIVE_INFINITY;
					for (GameMove move : moves) {
						double eval = 0;
						if (game.getCurrentPlayer() == newEvalPlayer) {
							//eval = game.evalMove2(players[game.getCurrentPlayer()], move);
						} else {
							eval = game.evalMove(players[game.getCurrentPlayer()], move);
						}

						if (eval >= bestEval) {
							bestEval = eval;
							bestMove = move;
						}
					}
				}
				
				game.makeMove(bestMove);
			} else {
				game.pass();
			}
		}
		
		return game.getOutcome();
	}

	public void test() {
		GoGame game = new GoGame();
		int draws = 0;
		float sum = 0;
		float sum2 = 0;
		
		for (int r = 0; r < 1000; r++) {
			game.reset();
			double res = play(game, 0); 
			if (res == 0) {
				draws++;
			}
			
			
			sum += ((res > 0) ? 1 : 0);
			sum2 += ((res < 0) ? 1 : 0);
		}
		
		System.out.println(draws);
		System.out.println(sum / 1000);
		System.out.println(sum2 / 1000);
	}
	
	public static void main(String[] args) {
		new GoHeuristicTest().test();
	}
}
