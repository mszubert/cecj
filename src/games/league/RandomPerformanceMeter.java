package games.league;

import ec.util.MersenneTwisterFast;
import games.Player;
import games.WPCPlayer;
import games.scenarios.GameScenario;
import games.scenarios.RandomizedTwoPlayersGameScenario;

import java.io.InputStream;
import java.util.Scanner;

import cecj.app.othello.OthelloBoard;
import cecj.app.othello.OthelloGame;

public class RandomPerformanceMeter {

	private WPCPlayer player;

	public void readPlayer(InputStream input) {
		Scanner s = new Scanner(input);
		double[] wpc = new double[64];
		for (int i = 0; i < 64; i++) {
			wpc[i] = s.nextDouble();
		}
		player = new WPCPlayer(wpc);

		System.out.println("Player = " + player);
	}

	private int testPlayer(int repeats) {
		MersenneTwisterFast rng = new MersenneTwisterFast(System.currentTimeMillis());

		GameScenario scenario1 = new RandomizedTwoPlayersGameScenario(rng, new Player[] { player,
				new WPCPlayer(OthelloBoard.BOARD_SIZE) }, new double[] { 0, 1.0 });
		GameScenario scenario2 = new RandomizedTwoPlayersGameScenario(rng, new Player[] {
				new WPCPlayer(OthelloBoard.BOARD_SIZE), player }, new double[] { 1.0, 0 });
		OthelloGame game = new OthelloGame();

		int sum = 0;
		for (int i = 0; i < repeats; i++) {
			game.reset();
			sum += ((scenario1.play(game) > 0) ? 1 : 0);
			game.reset();
			sum += ((scenario2.play(game) < 0) ? 1 : 0);
		}

		return sum;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RandomPerformanceMeter rpm = new RandomPerformanceMeter();
		rpm.readPlayer(System.in);
		System.out.println(rpm.testPlayer(500) + " games won out of 1000");
	}

}
