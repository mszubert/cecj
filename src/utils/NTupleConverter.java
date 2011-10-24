package utils;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;

import ec.simple.SimpleFitness;
import games.player.NTuplePlayer;
import cecj.ntuple.NTupleIndividual;

public class NTupleConverter {

	public static void writeAsLeagueEntry(NTuplePlayer player, String filename, String name) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(filename)));
			out.println("name: " + name);
			out.println("email: " + name);
			out.println("format: SNT-Text");
			out.println("dateSat Jul 14 16:37:12 BST 2007");
			out.println("solution:");
			out.println(player.toString());
			out.println();
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static NTuplePlayer readNTuplePlayer(String filename, int index) {
		try {
			String line;
			LineNumberReader reader = new LineNumberReader(new FileReader(
					filename));
			while ((line = reader.readLine()) != null) {
				int generation = Integer.parseInt(line.split(" ")[1]);
				NTupleIndividual ind = new NTupleIndividual();
				ind.fitness = new SimpleFitness();
				ind.readIndividual(null, reader);
				NTuplePlayer player = new NTuplePlayer();
				player.readFromIndividual(ind);
				if (generation == index) {
					return player;
				}
			}
		} catch (IOException e) {

		}
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NTuplePlayer player = readNTuplePlayer("ntuple/ind_4699.stat", 4);
		writeAsLeagueEntry(player, "ntuple/best_apTDLxover.out", "apTDLxover");
	}

}
