package games.league;

import ec.simple.SimpleFitness;
import ec.util.MersenneTwisterFast;
import ec.vector.DoubleVectorIndividual;
import games.Player;
import games.WPCPlayer;
import games.scenarios.GameScenario;
import games.scenarios.GnuGoGameScenario;
import games.scenarios.RandomizedTwoPlayersGameScenario;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import cecj.app.go.GoBoard;
import cecj.app.go.GoGame;

public class League {
	private static final int WPC_LENGTH = 25;
	private static final int PLAYERS_PER_TEAM = 25;

	private List<String> names;
	private List<List<Player>> teams;

	private int[] score;
	private int[] wins;
	private int[] draws;
	private int[] losts;

	private Player bestPlayer;
	private int bestPlayerScore;
	private String bestPlayerTeam;

	public League() {
		teams = new ArrayList<List<Player>>();
		names = new ArrayList<String>();
	}

	public void play() {
		score = new int[teams.size()];
		wins = new int[teams.size()];
		draws = new int[teams.size()];
		losts = new int[teams.size()];
		GameScenario scenario;
		GoGame game = new GoGame();

		bestPlayer = null;
		bestPlayerTeam = null;
		bestPlayerScore = 0;

		for (int team = 0; team < teams.size(); team++) {
			for (Player player : teams.get(team)) {
				int playerScore = 0;

				for (int opponentTeam = 0; opponentTeam < teams.size(); opponentTeam++) {
					if (team == opponentTeam)
						continue;

					for (Player opponent : teams.get(opponentTeam)) {
						scenario = new RandomizedTwoPlayersGameScenario(new MersenneTwisterFast(
								1987), new Player[] { player, opponent }, new double[] { 0, 0 });
						game.reset();
						double result = scenario.play(game);
						score[team] += points(result);
						playerScore += points(result);
						if (result > 0)
							wins[team]++;
						if (result == 0)
							draws[team]++;
						if (result < 0)
							losts[team]++;

						scenario = new RandomizedTwoPlayersGameScenario(new MersenneTwisterFast(
								1987), new Player[] { opponent, player }, new double[] { 0, 0 });
						game.reset();
						result = -scenario.play(game);
						score[team] += points(result);
						playerScore += points(result);
						if (result > 0)
							wins[team]++;
						if (result == 0)
							draws[team]++;
						if (result < 0)
							losts[team]++;
					}
				}

				if (playerScore > bestPlayerScore) {
					bestPlayerScore = playerScore;
					bestPlayer = player;
					bestPlayerTeam = names.get(team);
				}
			}
		}
	}

	private static int points(double result) {
		if (result > 0) {
			return 3;
		} else if (result == 0) {
			return 1;
		} else {
			return 0;
		}
	}

	public void printTable() {
		for (int team = 0; team < teams.size(); team++) {
			System.out.println(names.get(team) + " : " + score[team] + "\t\t wins = " + wins[team]
					+ " draws = " + draws[team] + " losts = " + losts[team]);
		}
	}

	public void addTeam(String name, String filename) {
		DoubleVectorIndividual ind = new DoubleVectorIndividual();
		ind.fitness = new SimpleFitness();
		ind.genome = new double[WPC_LENGTH];

		try {
			List<Player> team = new ArrayList<Player>();
			LineNumberReader reader = new LineNumberReader(new FileReader(filename));
			Integer.parseInt(reader.readLine());
			for (int i = 0; i < PLAYERS_PER_TEAM; i++) {
				ind.readIndividual(null, reader);
				team.add(new WPCPlayer(ind.genome));
			}
			teams.add(team);
			names.add(name);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void printBestPlayer() {
		System.out.println("\n Best player comes from team " + bestPlayerTeam + " and its score = "
				+ bestPlayerScore);

		double[] wpc = ((WPCPlayer) bestPlayer).getWPC();
		for (int i = 0; i < WPC_LENGTH; i++) {
			if (i % 5 == 0) {
				System.out.println("");
			} else {
				System.out.print(" ");
			}
			System.out.print(Math.round(wpc[i] * 100) / 100.);
		}
	}

	@SuppressWarnings("unused")
	private void evaluateBestPlayer() {
		float result = 0;
		for (Player player : teams.get(0)) {
			MersenneTwisterFast rng = new MersenneTwisterFast(System.currentTimeMillis());

			GameScenario scenario1 = new GnuGoGameScenario(rng, player, GoBoard.BLACK,
					new double[] { 0.0, 0.0 });
			GameScenario scenario2 = new GnuGoGameScenario(rng, player, GoBoard.WHITE,
					new double[] { 0.0, 0.0 });

			GnuGoGameScenario.startGnuGo();
			GoGame game = new GoGame();
			float sum = 0;
			for (int r = 0; r < 50; r++) {
				game.reset();
				sum += ((scenario1.play(game) > 0) ? 1 : 0);
				game.reset();
				sum += ((scenario2.play(game) < 0) ? 1 : 0);
			}
			GnuGoGameScenario.stopGnuGo();

			System.out.println("Player performance against gnugo = " + sum / 100);
			result += (sum / 100);
		}

		System.out.println("Average performance = " + (result / teams.get(0).size()));
	}

	public static void main(String[] args) {
		League league = new League();
		league.addTeam("CEL", "ctdl-go-4/exp1_best_players");
		league.addTeam("CEL + HoF", "ctdl-go-4/exp2_best_players");
		league.addTeam("TDL", "ctdl-go-4/exp3_best_players");
		league.addTeam("CEL + TDL", "ctdl-go-4/exp4_2_best_players");
		league.addTeam("CEL + TDL + HoF", "ctdl-go-4/exp5_2_best_players");

		// league.addTeam("CEL", "ctdl-go-3/exp1_best_players");
		// league.addTeam("CEL + HoF", "ctdl-go-3/exp2_best_players");
		// league.addTeam("TDL", "ctdl-go-3/exp3_best_players");
		// league.addTeam("CEL + TDL", "ctdl-go-3/exp4_best_players");
		// league.addTeam("CEL + TDL + HoF", "ctdl-go-3/exp5_best_players");

		// league.addTeam("CEL", "ctdl-go-2/exp1_best_players");
		// league.addTeam("CEL + HoF", "ctdl-go-2/exp2_best_players");
		// league.addTeam("TDL", "ctdl-go-2/exp3_best_players");
		// league.addTeam("CEL + TDL", "ctdl-go-2/exp4_best_players");
		// league.addTeam("CEL + TDL + HoF", "ctdl-go-2/exp5_2_best_players");

		league.play();
		league.printTable();
		league.printBestPlayer();

		// league.evaluateBestPlayer();
	}
}