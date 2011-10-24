package utils.league;

import games.player.NTuplePlayer;
import games.player.Player;
import games.scenario.GameScenario;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cecj.app.othello.OthelloGame;

public class ExternalPerformanceMeter extends ProgressMeter {

	private static final int NUM_ROUNDS = 30;

	private static final double RANDOMNESS = 0.1;

	private List<Player> externalOpponents;
	private List<Integer> tournamentPoints;

	public ExternalPerformanceMeter() {
		externalOpponents = new ArrayList<Player>();
		tournamentPoints = new ArrayList<Integer>();
	}

	@Override
	public void evaluate(List<List<Player>> teamsSnapshot) {
		OthelloGame game = new OthelloGame();

		for (int team = 0; team < teamsSnapshot.size(); team++) {
			int teamPoints = 0;
			List<Integer> playerRanks = new ArrayList<Integer>();
			for (Player player : teamsSnapshot.get(team)) {
				int playerPoints = 0;
				List<Integer> externalPlayersPoints = new ArrayList<Integer>(tournamentPoints);

				for (int round = 0; round < NUM_ROUNDS; round++) {
					for (int opponent = 0; opponent < externalOpponents.size(); opponent++) {

						GameScenario scenario = new GameScenario(new Player[] { player,
								externalOpponents.get(opponent) }, new double[] { RANDOMNESS,
								RANDOMNESS });

						game.reset();
						int result = scenario.play(game);
						playerPoints += points(result);
						externalPlayersPoints.set(opponent, externalPlayersPoints.get(opponent)
								+ points(-result));

						scenario = new GameScenario(new Player[] { externalOpponents.get(opponent),
								player }, new double[] { RANDOMNESS, RANDOMNESS });

						game.reset();
						result = -scenario.play(game);
						playerPoints += points(result);
						externalPlayersPoints.set(opponent, externalPlayersPoints.get(opponent)
								+ points(-result));
					}
				}
				teamPoints += playerPoints;
				playerRanks.add(getRank(playerPoints, externalPlayersPoints));
			}
			Collections.sort(playerRanks);
			int mode = findMode(playerRanks);
			double median = findMedian(playerRanks);
			
			System.out.println(teamPoints + "\t" + playerRanks.get(0) + "\t"
					+ playerRanks.get(playerRanks.size() - 1) + "\t" + median + "\t" + mode);
		}
	}

	private Integer getRank(int playerPoints, List<Integer> externalPlayersPoints) {
		int rank = 1;
		for (int opponent = 0; opponent < externalPlayersPoints.size(); opponent++) {
			if (externalPlayersPoints.get(opponent) > playerPoints) {
				rank++;
			}
		}
		return rank;
	}

	private double findMedian(List<Integer> playerRanks) {
		if (playerRanks.size() % 2 != 0) {
			return playerRanks.get(playerRanks.size() / 2);
		} else {
			return (playerRanks.get(playerRanks.size() / 2)
					+ playerRanks.get((playerRanks.size() / 2) - 1)) / 2.0;
		}
	}

	private int findMode(List<Integer> playerRanks) {
		int mode = -1;
		int maxFrequency = -1;
		
		for (int rank = 1; rank <= externalOpponents.size() + 1; rank++) {
			int frequency = Collections.frequency(playerRanks, rank);
			if (frequency > maxFrequency) {
				maxFrequency = frequency;
				mode = rank;
			}
		}
		
		return mode;
	}

	private void readExternalOpponents(String filename) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
			int players = Integer.parseInt(bufferedReader.readLine());
			for (int p = 0; p < players; p++) {
				String line;
				StringBuilder builder = new StringBuilder();
				while (!(line = bufferedReader.readLine()).startsWith("#")) {
					builder.append(line);
					builder.append("\n");
				}
				externalOpponents.add(NTuplePlayer.readFromString(builder.toString()));
				// externalOpponents.add(MLPPlayer.readFromString(builder.toString()));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void playExternalTournament() {
		int[] wonGames = new int[externalOpponents.size()];
		int[] drawGames = new int[externalOpponents.size()];
		int[] lostGames = new int[externalOpponents.size()];

		for (int round = 0; round < NUM_ROUNDS; round++) {
			for (int player = 0; player < externalOpponents.size(); player++) {
				for (int opponent = 0; opponent < externalOpponents.size(); opponent++) {
					if (opponent != player) {
						GameScenario scenario = new GameScenario(new Player[] {
								externalOpponents.get(player), externalOpponents.get(opponent) },
								new double[] { RANDOMNESS, RANDOMNESS });
						int result = scenario.play(new OthelloGame());

						if (result > 0) {
							wonGames[player]++;
							lostGames[opponent]++;
						} else if (result < 0) {
							wonGames[opponent]++;
							lostGames[player]++;
						} else {
							drawGames[player]++;
							drawGames[opponent]++;
						}
					}
				}
			}

		}

		for (int player = 0; player < externalOpponents.size(); player++) {
			tournamentPoints.add(wonGames[player] * 3 + drawGames[player]);
		}
	}

	public static void main(String[] args) {
		ExternalPerformanceMeter externalMeter = new ExternalPerformanceMeter();
		externalMeter.readExternalOpponents("ntuple/snt.players");
		externalMeter.playExternalTournament();

		List<List<Player>> ctdlProgress = externalMeter.readProgressFile(
				"ntuple/apTDLmpx100.players", 3, 5);
		
		List<List<List<Player>>> progressRecords = new ArrayList<List<List<Player>>>();
		progressRecords.add(ctdlProgress);
		externalMeter.play(progressRecords);
	}
}
