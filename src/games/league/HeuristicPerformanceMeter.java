package games.league;

import ec.util.MersenneTwisterFast;
import games.Player;
import games.WPCPlayer;
import games.scenarios.GameScenario;
import games.scenarios.RandomizedTwoPlayersGameScenario;

import java.io.InputStream;
import java.util.Scanner;

import cecj.app.othello.OthelloGame;

public class HeuristicPerformanceMeter {

	double[] wpc = { 1.00f, -0.25f, 0.10f, 0.05f, 0.05f, 0.10f, -0.25f, 1.00f, -0.25f, -0.25f,
			0.01f, 0.01f, 0.01f, 0.01f, -0.25f, -0.25f, 0.10f, 0.01f, 0.05f, 0.02f, 0.02f, 0.05f,
			0.01f, 0.10f, 0.05f, 0.01f, 0.02f, 0.01f, 0.01f, 0.02f, 0.01f, 0.05f, 0.05f, 0.01f,
			0.02f, 0.01f, 0.01f, 0.02f, 0.01f, 0.05f, 0.10f, 0.01f, 0.05f, 0.02f, 0.02f, 0.05f,
			0.01f, 0.10f, -0.25f, -0.25f, 0.01f, 0.01f, 0.01f, 0.01f, -0.25f, -0.25f, 1.00f,
			-0.25f, 0.10f, 0.05f, 0.05f, 0.10f, -0.25f, 1.00f };

	private WPCPlayer player;

	public void readPlayer(InputStream input) {
		Scanner s = new Scanner(input);
		double[] playerWpc = new double[64];
		for (int i = 0; i < 64; i++) {
			playerWpc[i] = s.nextDouble();
		}
		player = new WPCPlayer(playerWpc);

		System.out.println("Player = " + player);
	}

	private void testPlayer(OthelloGame game) {
		WPCPlayer heuristic = new WPCPlayer(wpc);
		GameScenario scenario1 = new RandomizedTwoPlayersGameScenario(
				new MersenneTwisterFast(1987), new Player[] { player, heuristic }, new double[] {
						0, 0 });
		GameScenario scenario2 = new RandomizedTwoPlayersGameScenario(
				new MersenneTwisterFast(1987), new Player[] { heuristic, player }, new double[] {
						0, 0 });

		game.reset();
		System.out.println("Playing as black, the result is " + scenario1.play(game));
		System.out.println(game.getBoard());

		game.reset();
		System.out.println("Playing as white, the result is " + scenario2.play(game));
		System.out.println(game.getBoard());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HeuristicPerformanceMeter hpm = new HeuristicPerformanceMeter();
		hpm.readPlayer(System.in);
		hpm.testPlayer(new OthelloGame());
	}
}
