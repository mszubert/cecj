package utils.league;

import ec.simple.SimpleFitness;
import games.player.NTuplePlayer;
import games.player.Player;
import games.player.mlp.MLPPlayer;
import games.scenario.GameScenario;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import cecj.app.othello.OthelloGame;
import cecj.ntuple.NTupleIndividual;

public class LeagueProgress extends ProgressMeter {

	private List<String> names;
	private List<List<List<Player>>> teams;

	private int[] score;
	private int[] wins;
	private int[] draws;
	private int[] losts;

	private Player bestPlayer;
	private int bestPlayerScore;
	private String bestPlayerTeam;

	public LeagueProgress() {
		teams = new ArrayList<List<List<Player>>>();
		names = new ArrayList<String>();
	}

	
	@Override
	protected void evaluate(List<List<Player>> teamsSnapshot) {
		score = new int[teamsSnapshot.size()];
		wins = new int[teamsSnapshot.size()];
		draws = new int[teamsSnapshot.size()];
		losts = new int[teamsSnapshot.size()];

		GameScenario scenario;
		OthelloGame game = new OthelloGame();

		for (int team = 0; team < teamsSnapshot.size(); team++) {
			bestPlayer = null;
			bestPlayerTeam = null;
			bestPlayerScore = 0;

			// int[] pointsWhite = new int[teamsSnapshot.size()];
			// int[] pointsBlack = new int[teamsSnapshot.size()];

			for (Player player : teamsSnapshot.get(team)) {
				int playerScore = 0;

				for (int opponentTeam = 0; opponentTeam < teamsSnapshot.size(); opponentTeam++) {
					if (team == opponentTeam)
						continue;

					for (Player opponent : teamsSnapshot.get(opponentTeam)) {
						scenario = new GameScenario(new Player[] { player, opponent });

						game.reset();
						int result = scenario.play(game);
						assignPoints(team, opponentTeam, result);
						playerScore += points(result);

						scenario = new GameScenario(new Player[] { opponent, player });

						game.reset();
						result = -scenario.play(game);
						assignPoints(team, opponentTeam, result);
						playerScore += points(result);
					}
				}

				if (playerScore > bestPlayerScore) {
					bestPlayerScore = playerScore;
					bestPlayer = player;
					bestPlayerTeam = names.get(team);
				}
			}

			printBestPlayer();
		}
	}

	private void assignPoints(int team, int opponentTeam, int result) {
		score[team] += points(result);
		// score[opponentTeam] += points(-result);

		// pointsBlack[opponentTeam] += points(result);
		if (result > 0) {
			// losts[opponentTeam]++;
			wins[team]++;
		} else if (result == 0) {
			draws[team]++;
			// draws[opponentTeam]++;
		} else {
			losts[team]++;
			// wins[opponentTeam]++;
		}
	}

	public void printTable() {
		for (int team = 0; team < teams.size(); team++) {
			System.out.println(names.get(team) + " : " + score[team] + "\t\t wins = " + wins[team]
					+ " draws = " + draws[team] + " losts = " + losts[team]);
		}
	}

	private void printBestPlayer() {
		System.out.println("\n Best player comes from team " + bestPlayerTeam + " and its score = "
				+ bestPlayerScore);

		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("ntuple/best_"
					+ bestPlayerTeam + ".out")));
			out.println("name: CTDL_" + bestPlayerTeam);
			out.println("email: CTDL_" + bestPlayerTeam);
			out.println("format: SNT-Text");
			out.println("dateSat Jul 14 16:37:12 BST 2007");
			out.println("solution:");
			out.println(bestPlayer.toString());
			out.println();
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		LeagueProgress league = new LeagueProgress();
		// league.testExternalOpponents();

		// league.addTeam("mTDL", "ntuple/mTDL.players");
		// league.addTeam("pTDL", "ntuple/pTDL.players");
		// league.addTeam("pTDLx", "ntuple/pTDLxover.players");
		// league.addTeam("pTDLmpx", "ntuple/pTDLmpx.players");
		// league.addTeam("pTDLmx", "ntuple/pTDLmx.players");
		// league.addTeam("pTDLmpx001", "ntuple/pTDLmpx001.players");
		// league.addTeam("apTDL", "ntuple/apTDL.players");

		// league.addTeam("epTDLx", "ntuple/epTDLxover.players");
		// league.addTeam("epTDLmpx", "ntuple/epTDLmpx.players");

		// league.addTeam("pcTDL", "ntuple/pcTDL.players");
		// league.addTeam("pcTDLx", "ntuple/pcTDLx.players");
		// league.addTeam("pcTDLmpx", "ntuple/pcTDLmpx.players");
		// league.addTeam("apcTDLx", "ntuple/apcTDLx.players");
		// league.addTeam("a3pcTDLmpx", "ntuple/apcTDLmpx.players");

		// league.addTeam("TDL", "ntuple/TDL.players");
		// league.addTeam("mTDL100", "ntuple/mTDL100.players");
		// league.addTeam("pTDL100", "ntuple/pTDL100.players");
		// league.addTeam("pTDLx100", "ntuple/pTDLx100.players");
		// league.addTeam("pTDLmpx100", "ntuple/pTDLmpx100.players", 3, 5);
		league.readProgressFile("ntuple/apTDLmpx100.players", 3, 5);

		// league.addTeam("apTDLxover30x6", "ntuple/apTDLxover30x6.players");
		// league.addTeam("epTDLxover30x6", "ntuple/epTDLxover30x6.players");
		// league.addTeam("epTDLmpx100", "ntuple/epTDLmpx100.players", 3, 5);

		//league.play();
		// league.printTable();
		// league.printBestPlayer();
	}

}
