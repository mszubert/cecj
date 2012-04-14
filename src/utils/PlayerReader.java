package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import cecj.ntuple.NTupleIndividual;
import ec.Individual;
import ec.simple.SimpleFitness;
import ec.vector.DoubleVectorIndividual;
import games.player.EvolvedPlayer;
import games.player.NTuplePlayer;
import games.player.Player;
import games.player.WPCPlayer;

public class PlayerReader {

	public enum PlayerFormat {
		WPC(DoubleVectorIndividual.class, WPCPlayer.class), NTUPLE(NTupleIndividual.class,
				NTuplePlayer.class);

		final Class<? extends Individual> individualClass;
		final Class<? extends EvolvedPlayer> playerClass;

		PlayerFormat(Class<? extends Individual> individualClass,
				Class<? extends EvolvedPlayer> playerClass) {
			this.individualClass = individualClass;
			this.playerClass = playerClass;
		}
	}

	public enum Encoding {
		TEXT, ECJ
	}

	private static final String DELIMITER = "#";

	private static Player readIndividual(LineNumberReader reader, PlayerFormat playerFormat)
			throws InstantiationException, IllegalAccessException, IOException {
		Individual ind = playerFormat.individualClass.newInstance();
		ind.fitness = new SimpleFitness();
		ind.readIndividual(null, reader);

		EvolvedPlayer player = playerFormat.playerClass.newInstance();
		player.readFromIndividual(ind);
		return player;
	}

	private static Player readPlayer(BufferedReader reader, PlayerFormat playerFormat)
			throws IOException, InstantiationException, IllegalAccessException {
		String line;
		StringBuilder builder = new StringBuilder();
		while ((line = reader.readLine()) != null && !line.startsWith(DELIMITER)) {
			builder.append(line);
			builder.append("\n");
		}
		EvolvedPlayer player = playerFormat.playerClass.newInstance();
		player.readFromString(builder.toString());
		return player;
	}

	
	private static List<Player> readPlayers(LineNumberReader reader, int teamSize,
			PlayerFormat format, Encoding encoding) throws IOException, InstantiationException,
			IllegalAccessException {

		List<Player> team = new ArrayList<Player>(teamSize);
		for (int i = 0; i < teamSize; i++) {
			if (encoding == Encoding.ECJ) {
				team.add(readIndividual(reader, format));
			} else if (encoding == Encoding.TEXT) {
				team.add(readPlayer(reader, format));
			}
		}
		return team;
	}

	public static Player readLeagueEntry(String file, PlayerFormat playerFormat) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			StringBuilder preamble = new StringBuilder();
			while (((line = reader.readLine()) != null) && !line.matches("[sS]olution:")) {
				preamble.append(line);
				preamble.append("\n");
			}
			return readPlayer(reader, playerFormat);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static List<Player> readTeamFile(String file, PlayerFormat playerFormat,
			Encoding encoding) {
		try {
			LineNumberReader reader = new LineNumberReader(new FileReader(file));
			int teamSize = Integer.parseInt(reader.readLine());
			return readPlayers(reader, teamSize, playerFormat, encoding);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ArrayList<Player>();
	}

	static List<List<Player>> readProgressFile(String file, PlayerFormat playerFormat,
			Encoding encoding) {

		List<List<Player>> progress = new ArrayList<List<Player>>();

		try {
			String line;
			LineNumberReader reader = new LineNumberReader(new FileReader(file));
			while (((line = reader.readLine()) != null)) {
				int teamSize = Integer.parseInt(line.split(" ")[1]);
				// int generation = Integer.parseInt(line.split(" ")[0]);
				List<Player> team = readPlayers(reader, teamSize, playerFormat, encoding);
				progress.add(team);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return progress;
	}

	public static void main(String[] args) {
		List<Player> team1 = readTeamFile("EXP1_best_players", PlayerFormat.WPC, Encoding.ECJ);
		List<Player> team2 = readTeamFile("ntuple/snt.players", PlayerFormat.NTUPLE, Encoding.TEXT);
		List<Player> team3 = readTeamFile("pcTDLx.players", PlayerFormat.NTUPLE, Encoding.ECJ);
		Player player = readLeagueEntry("ntuple/best_apTDLmpx100.out", PlayerFormat.NTUPLE);
		System.out.println(team1.size() + " " + team2.size() + " " + team3.size());
		player.reset();
	}
}
