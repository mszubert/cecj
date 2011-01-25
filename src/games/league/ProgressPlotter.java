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

public class ProgressPlotter {

	private static final int PLAYERS_PER_GENERATION = 25;
	private static final int GENERATIONS = 41;

	private List<List<Player>> progress;
	private double[][] results;

	public ProgressPlotter(String filename) {

		results = new double[GENERATIONS][GENERATIONS];
		progress = new ArrayList<List<Player>>();

		try {
			String line;
			LineNumberReader reader = new LineNumberReader(new FileReader(filename));
			while (((line = reader.readLine()) != null) && (progress.size() < GENERATIONS)) {
				int generationSize = Integer.parseInt(line.split(" ")[1]);
				List<Player> generation = new ArrayList<Player>(generationSize);

				for (int i = 0; i < generationSize; i++) {
					NTupleIndividual ind = new NTupleIndividual();
					ind.fitness = new SimpleFitness();
					ind.readIndividual(null, reader);
					if (i < PLAYERS_PER_GENERATION) {
						NTuplePlayer player = new NTuplePlayer();
						player.readFromIndividual(ind);
						generation.add(player);
					}
				}

				progress.add(generation);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void printTable() {
		for (int i = 0; i < GENERATIONS; i++) {
			for (int j = 0; j < GENERATIONS; j++) {
				System.out.println(i + "\t" + j + "\t" + results[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}

	private void analyzeProgress() {
		for (int g = 1; g < GENERATIONS; g++) {
			List<Player> currentGeneration = progress.get(g);
			for (int o = 0; o < g; o++) {
				List<Player> olderGeneration = progress.get(o);
				results[g][o] = playMatch(currentGeneration, olderGeneration);
				System.out.println(g + "\t" + o + "\t" + results[g][o]);
			}
			System.out.println();
		}
		System.out.println();
	}

	private double playMatch(List<Player> currentGeneration, List<Player> olderGeneration) {
		int games = 0;
		double result = 0;

		GameScenario scenario;
		OthelloGame game = new OthelloGame();

		for (Player cp : currentGeneration) {
			for (Player op : olderGeneration) {
				scenario = new RandomizedTwoPlayersGameScenario(new MersenneTwisterFast(1987),
						new Player[] { cp, op }, new double[] { 0, 0 });
				game.reset();
				result += (scenario.play(game) >= 0) ? 1 : 0;

				scenario = new RandomizedTwoPlayersGameScenario(new MersenneTwisterFast(1987),
						new Player[] { op, cp }, new double[] { 0, 0 });
				game.reset();
				result += (scenario.play(game) <= 0) ? 1 : 0;

				games += 2;
			}
		}

		return result / games;
	}

	public void printBestPlayer() {
		System.out.println(progress.get(0).get(0));
	}
	
	public static void main(String[] args) {
		ProgressPlotter plotter = new ProgressPlotter("ntuple/pTDLxover.players");
		//plotter.analyzeProgress();
		//plotter.printTable();
		plotter.printBestPlayer();
	}

}
