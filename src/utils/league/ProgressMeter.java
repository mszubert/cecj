package utils.league;

import ec.simple.SimpleFitness;
import games.player.NTuplePlayer;
import games.player.Player;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import cecj.ntuple.NTupleIndividual;

public abstract class ProgressMeter {

	private static final int PLAYERS_PER_GENERATION = 20;

	private static final int EVAL_FREQUENCY = 50000;

	private static final int EVALUATIONS = 41;

	protected void play(List<List<List<Player>>> progressRecords) {
		for (int i = 0; i < progressRecords.size(); i++) {
			List<List<Player>> progress = progressRecords.get(i);
			System.err.println("Team " + i + " was evaluated " + progress.size() + " times");
			if (progress.size() != EVALUATIONS) {
				System.err.println("Team " + i
						+ " was evaluated different number of times, namely " + progress.size());
			}
		}

		for (int eval = 0; eval < EVALUATIONS; eval++) {
			List<List<Player>> progressSnapshot = new ArrayList<List<Player>>();
			for (int record = 0; record < progressRecords.size(); record++) {
				if (progressRecords.get(record).size() > eval) {
					progressSnapshot.add(progressRecords.get(record).get(eval));
				} else {
					progressSnapshot.add(progressRecords.get(record).get(
							progressRecords.get(record).size() - 1));
				}
			}

			System.out.print(eval * EVAL_FREQUENCY + "\t");
			evaluate(progressSnapshot);
		}
	}

	protected abstract void evaluate(List<List<Player>> progressSnapshot);

	protected List<List<Player>> readProgressFile(String filename) {
		return readProgressFile(filename, -1, 1);
	}

	protected List<List<Player>> readProgressFile(String filename, int rest, int mod) {
		System.err.println("Reading in file " + filename);

		List<List<Player>> progress = new ArrayList<List<Player>>();
		try {
			String line;
			LineNumberReader reader = new LineNumberReader(new FileReader(filename));
			while (((line = reader.readLine()) != null) && (progress.size() < EVALUATIONS)) {
				int teamSize = Integer.parseInt(line.split(" ")[1]);
				int generation = Integer.parseInt(line.split(" ")[0]);
				List<Player> team = new ArrayList<Player>(teamSize);

				System.err.println("Generation " + generation + " has " + teamSize + "players");
				if (teamSize < PLAYERS_PER_GENERATION) {
					System.err.println("Generation " + line.split(" ")[0]
							+ "has less players than required!");
				}

				for (int i = 0; i < teamSize; i++) {
					NTupleIndividual ind = new NTupleIndividual();
					ind.fitness = new SimpleFitness();
					ind.readIndividual(null, reader);
					if (i < PLAYERS_PER_GENERATION) {
						NTuplePlayer player = new NTuplePlayer();
						player.readFromIndividual(ind);
						team.add(player);
					}
				}

				if (generation % mod != rest) {
					System.err.println("Generation " + generation + " ADDED");
					progress.add(team);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return progress;
	}

	protected static int points(double result) {
		if (result > 0) {
			return 3;
		} else if (result == 0) {
			return 1;
		} else {
			return 0;
		}
	}
}
