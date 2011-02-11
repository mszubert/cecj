package games.league;

import ec.simple.SimpleFitness;
import ec.util.MersenneTwisterFast;
import games.player.NTuplePlayer;
import games.player.Player;
import games.scenario.GameScenario;
import games.scenario.RandomizedTwoPlayersGameScenario;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import cecj.app.othello.OthelloGame;
import cecj.ntuple.NTupleIndividual;

public class LeagueProgress {

	// private static final int WPC_LENGTH = 25;
	private static final int PLAYERS_PER_TEAM = 24;
	private static final int EVAL_FREQUENCY = 50000;
	private static final int EVALUATIONS = 41;

	private List<String> names;
	private List<List<List<Player>>> teams;

	private int[] score;
	private int[] wins;
	private int[] draws;
	private int[] losts;

	// private Player bestPlayer;
	// private int bestPlayerScore;
	// private String bestPlayerTeam;

	public LeagueProgress() {
		teams = new ArrayList<List<List<Player>>>();
		names = new ArrayList<String>();
	}

	public void play() {
		for (int i = 0; i < teams.size(); i++) {
			System.err.println("Team " + names.get(i) + " was evaluated " + teams.get(i).size() + " times");
			if (teams.get(i).size() != EVALUATIONS) {
				System.err
						.println("Team " + names.get(i)
								+ " was evaluated different number of times, namely "
								+ teams.get(i).size());
			}
		}

		for (int eval = 0; eval < EVALUATIONS; eval++) {
			List<List<Player>> teamsSnapshot = new ArrayList<List<Player>>();
			for (int team = 0; team < teams.size(); team++) {
				if (teams.get(team).size() > eval) {
					teamsSnapshot.add(teams.get(team).get(eval));
				} else {
					teamsSnapshot.add(teams.get(team).get(teams.get(team).size() - 1));
				}
			}

			playSingleLeague(teamsSnapshot);
			StringBuilder builder = new StringBuilder();
			builder.append(eval * EVAL_FREQUENCY);
			for (int team = 0; team < teams.size(); team++) {
				builder.append("\t" + score[team]);
			}
			System.out.println(builder.toString());
		}
	}

	public void playSingleLeague(List<List<Player>> teamsSnapshot) {
		score = new int[teamsSnapshot.size()];
		wins = new int[teamsSnapshot.size()];
		draws = new int[teamsSnapshot.size()];
		losts = new int[teamsSnapshot.size()];

		GameScenario scenario;
		OthelloGame game = new OthelloGame();

		// bestPlayer = null;
		// bestPlayerTeam = null;
		// bestPlayerScore = 0;

		for (int team = 1; team < teamsSnapshot.size(); team++) {
			// int[] pointsWhite = new int[teamsSnapshot.size()];
			// int[] pointsBlack = new int[teamsSnapshot.size()];

			for (Player player : teamsSnapshot.get(team)) {
				// int playerScore = 0;

				for (int opponentTeam = 0; opponentTeam < team; opponentTeam++) {
					// if (team == opponentTeam)
					// continue;

					for (Player opponent : teamsSnapshot.get(opponentTeam)) {
						scenario = new RandomizedTwoPlayersGameScenario(new MersenneTwisterFast(
								1987), new Player[] { player, opponent }, new double[] { 0, 0 });

						game.reset();
						assignPoints(team, opponentTeam, scenario.play(game));

						scenario = new RandomizedTwoPlayersGameScenario(new MersenneTwisterFast(
								1987), new Player[] { opponent, player }, new double[] { 0, 0 });

						game.reset();
						assignPoints(team, opponentTeam, -scenario.play(game));
					}
				}

				// if (playerScore > bestPlayerScore) {
				// bestPlayerScore = playerScore;
				// bestPlayer = player;
				// bestPlayerTeam = names.get(team);
				// }
			}

			// for (int opponentTeam = 0; opponentTeam < teamsSnapshot.size(); opponentTeam++) {
			// System.err
			// .println("Team " + team + " vs team " + opponentTeam + " : "
			// + pointsBlack[opponentTeam] + "as Black"
			// + pointsWhite[opponentTeam] + "as White = \t"
			// + (pointsWhite[opponentTeam] + pointsBlack[opponentTeam]));
			// }
		}
	}

	private void assignPoints(int team, int opponentTeam, int result) {
		score[team] += points(result);
		score[opponentTeam] += points(-result);
		// playerScore += points(result);
		// pointsBlack[opponentTeam] += points(result);
		if (result > 0) {
			losts[opponentTeam]++;
			wins[team]++;
		} else if (result == 0) {
			draws[team]++;
			draws[opponentTeam]++;
		} else {
			losts[team]++;
			wins[opponentTeam]++;
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

	private void addTeam(String name, String filename) {
		addTeam(name, filename, -1, 1);
	}

	private void addTeam(String name, String filename, int rest, int mod) {
		String line;
		List<List<Player>> teamProgress = new ArrayList<List<Player>>();

		System.err.println("Reading in team " + name + " from file " + filename);

		try {
			LineNumberReader reader = new LineNumberReader(new FileReader(filename));
			while (((line = reader.readLine()) != null) && (teamProgress.size() < EVALUATIONS)) {
				int teamSize = Integer.parseInt(line.split(" ")[1]);
				int generation = Integer.parseInt(line.split(" ")[0]);
				List<Player> team = new ArrayList<Player>(teamSize);

				System.err.println("Generation " + generation + " has " + teamSize + "players");
				if (teamSize < PLAYERS_PER_TEAM) {
					System.err.println("Generation " + line.split(" ")[0]
							+ "has less players than required!");
				}

				for (int i = 0; i < teamSize; i++) {
					NTupleIndividual ind = new NTupleIndividual();
					ind.fitness = new SimpleFitness();
					ind.readIndividual(null, reader);
					if (i < PLAYERS_PER_TEAM) {
						NTuplePlayer player = new NTuplePlayer();
						player.readFromIndividual(ind);
						team.add(player);
					}
				}

				if (generation % mod != rest) {
					System.err.println("Generation " + generation + " ADDED");
					teamProgress.add(team);
				}
			}

			teams.add(teamProgress);
			names.add(name);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// private void printBestPlayer() {
	// System.out.println("\n Best player comes from team " + bestPlayerTeam + " and its score = "
	// + bestPlayerScore);
	//
	// double[] wpc = ((WPCPlayer) bestPlayer).getWPC();
	// for (int i = 0; i < WPC_LENGTH; i++) {
	// if (i % 5 == 0) {
	// System.out.println("");
	// } else {
	// System.out.print(" ");
	// }
	// System.out.print(Math.round(wpc[i] * 100) / 100.);
	// }
	// }

	public static void main(String[] args) {
		LeagueProgress league = new LeagueProgress();
		// league.addTeam("EXP3v2 0", "ctdl-mc-go-2/exp3v2_0.players");
		// league.addTeam("EXP3v2 20", "ctdl-mc-go-2/exp3v2_20.players");
		// league.addTeam("EXP3v2 40", "ctdl-mc-go-2/exp3v2_40.players");
		// league.addTeam("EXP3v2 60", "ctdl-mc-go-2/exp3v2_60.players");
		// league.addTeam("EXP3v2 80", "ctdl-mc-go-2/exp3v2_80.players");
		// league.addTeam("EXP3v2 90", "ctdl-mc-go-2/exp3v2_90.players");
		// league.addTeam("EXP3v2 95", "ctdl-mc-go-2/exp3v2_95.players");
		// league.addTeam("EXP3v2 100", "ctdl-mc-go-2/exp3v2_100.players");

		// league.addTeam("mTDL", "ntuple/mTDL.players");
		// league.addTeam("pTDL", "ntuple/pTDL.players");
		// league.addTeam("pTDLx", "ntuple/pTDLxover.players");
		// league.addTeam("pTDLmpx", "ntuple/pTDLmpx.players");
		// league.addTeam("pTDLmx", "ntuple/pTDLmx.players");
		//league.addTeam("pTDLmpx", "ntuple/pTDLmpx001.players");
		// league.addTeam("apTDL", "ntuple/apTDL.players");

		// league.addTeam("epTDLx", "ntuple/epTDLxover.players");
		//league.addTeam("epTDLmpx", "ntuple/epTDLmpx.players");

		// league.addTeam("pcTDL", "ntuple/pcTDL.players");
		league.addTeam("pcTDLx", "ntuple/pcTDLx.players");
		// league.addTeam("pcTDLmpx", "ntuple/pcTDLmpx.players");
		// league.addTeam("apcTDLx", "ntuple/apcTDLx.players");
		league.addTeam("a3pcTDLmpx", "ntuple/apcTDLmpx.players");

		//league.addTeam("TDL", "ntuple/TDL.players");
		//league.addTeam("mTDL100", "ntuple/mTDL100.players");
		//league.addTeam("pTDL100", "ntuple/pTDL100.players");
		//league.addTeam("pTDLx100", "ntuple/pTDLx100.players");
		league.addTeam("pTDLmpx100", "ntuple/pTDLmpx100.players", 3, 5);
		league.addTeam("apTDLmpx100", "ntuple/apTDLmpx100.players", 3, 5);
		
		league.addTeam("epTDLmpx100", "ntuple/epTDLmpx100.players", 3, 5);

		// league.addTeam("EXP4", "ctdl-mc-go-2/exp4v10_988.players");
		// league.addTeam("EXP5", "ctdl-mc-go-2/exp5v10_988.players");

		// league.addTeam("EXP1", "othello/exp1.players");
		// league.addTeam("EXP2", "othello/exp2.players");
		// league.addTeam("EXP3", "othello/exp3.players");
		// league.addTeam("EXP4", "othello/exp4.players");
		// league.addTeam("EXP5", "othello/exp5.players");

		// league.addTeam("EXP4v5 0", "ctdl-mc-go-2/exp4v5_0.players");
		// league.addTeam("EXP4v5 80", "ctdl-mc-go-2/exp4v5_80.players");
		// league.addTeam("EXP4v5 98", "ctdl-mc-go-2/exp4v5_98.players");
		// league.addTeam("EXP5v5 0", "ctdl-mc-go-2/exp5v5_0.players");
		// league.addTeam("EXP5v5 80", "ctdl-mc-go-2/exp5v5_80.players");
		// league.addTeam("EXP5v5 98", "ctdl-mc-go-2/exp5v5_98.players");

		// league.addTeam("EXP5v6 980", "ctdl-mc-go-2/exp5v10_980.players");
		// league.addTeam("EXP5v6 981", "ctdl-mc-go-2/exp5v10_981.players");
		// league.addTeam("EXP5v6 982", "ctdl-mc-go-2/exp5v10_982.players");
		// league.addTeam("EXP5v6 984", "ctdl-mc-go-2/exp5v10_984.players");
		// league.addTeam("EXP5v6 988", "ctdl-mc-go-2/exp5v10_988.players");
		// league.addTeam("EXP5v6 9816", "ctdl-mc-go-2/exp5v10_9816.players");

		league.play();
		league.printTable();
		// league.printBestPlayer();
	}
}
