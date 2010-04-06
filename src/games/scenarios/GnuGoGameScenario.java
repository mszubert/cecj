package games.scenarios;

import ec.util.MersenneTwisterFast;
import games.BoardGame;
import games.GameMove;
import games.Player;
import games.WPCPlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import cecj.app.go.GoBoard;
import cecj.app.go.GoGame;

public class GnuGoGameScenario extends GameScenario {

	private Player player;
	private int color;

	private double[] prob;

	public GnuGoGameScenario(MersenneTwisterFast random, Player player, int color, double[] prob) {
		super(random);

		this.player = player;
		this.color = color;

		this.prob = prob;
	}

	@Override
	public double play(BoardGame game) {
		clearBoard();
		while (!game.endOfGame()) {
			// System.err.println("Current PLayer = " + game.getCurrentPlayer());
			// System.err.println(game.getBoard());
			// showBoard();

			GameMove bestMove = null;
			List<? extends GameMove> moves = game.findMoves();
			if (random.nextBoolean(prob[game.getCurrentPlayer()])) {
				bestMove = moves.get(random.nextInt(moves.size()));
			} else {
				if (game.getCurrentPlayer() == color) {
					bestMove = chooseBestMove(game, player, moves);
				} else {
					bestMove = getGnuGoMove((GoGame) game, game.getCurrentPlayer());
				}
			}

			makeGnuGoMove(bestMove, game.getCurrentPlayer());
			game.makeMove(bestMove);
		}

		return game.getOutcome();
	}

	private static Process gnuGoProcess;
	private static BufferedReader stdout;
	private static OutputStream stdin;

	@SuppressWarnings("unused")
	private static void printWinner(BoardGame game, int color) {
		if ((game.getOutcome() > 0 && color == GoBoard.BLACK)
				|| (game.getOutcome() < 0 && color == GoBoard.WHITE)) {
			System.err.println("TD Player won!");
		} else if (game.getOutcome() == 0) {
			System.err.println("It was a draw");
		} else {
			System.err.println("Gnu GO player won!");
		}
	}

	private static GameMove getGnuGoMove(GoGame game, int currentPlayer) {
		String[] answer = new String[1];
		if (currentPlayer == GoBoard.BLACK) {
			executeGTPCommand("reg_genmove black", 0, false, answer);
		} else {
			executeGTPCommand("reg_genmove white", 0, false, answer);
		}

		if (answer[0].charAt(2) == 'P') {
			return null;
		}

		int col = answer[0].charAt(2) - 'A' + 1;
		int row = GoBoard.size() - (answer[0].charAt(3) - '0') + 1;

		GameMove move = game.tryPlace(row, col);
		if (move == null) {
			System.err.println("GnuGo has generated illegal move!");
			System.err.println(game.getBoard());
			System.err.println("Move = " + row + " , " + col);
		}
		return move;
	}

	private static void makeGnuGoMove(GameMove move, int currentPlayer) {
		String pos;
		if (move == null || move.getCol() == -1 || move.getRow() == -1) {
			pos = "pass";
		} else {
			char col = (char) ('A' + move.getCol() - 1);
			int row = (GoBoard.size() - move.getRow() + 1);
			pos = "" + col + row;
		}

		if (currentPlayer == GoBoard.BLACK) {
			executeGTPCommand("play black " + pos, 0, false, null);
		} else {
			executeGTPCommand("play white " + pos, 0, false, null);
		}
	}

	@SuppressWarnings("unused")
	private static void showBoard() {
		executeGTPCommand("showboard", 7, true, null);
	}

	private static void clearBoard() {
		executeGTPCommand("clear_board", 0, false, null);
	}

	public static void startGnuGo() {
		try {
			// gnuGoProcess =
			// Runtime.getRuntime().exec("/home/inf71369/gnugo-3.4/interface/gnugo --mode gtp --quiet");
			gnuGoProcess = Runtime.getRuntime().exec(
					"/Users/marcin/Sites/gnugo-3.4/interface/gnugo --mode gtp --quiet");
			stdout = new BufferedReader(new InputStreamReader(gnuGoProcess.getInputStream()));
			stdin = gnuGoProcess.getOutputStream();

			executeGTPCommand("boardsize 5", 0, false, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void stopGnuGo() {
		executeGTPCommand("quit", 0, false, null);
		gnuGoProcess.destroy();
		try {
			stdout.close();
			stdin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void executeGTPCommand(String cmd, int answerLines, boolean verbose,
			String[] answer) {
		try {
			if (verbose) {
				System.out.println("Executing command \"" + cmd + "\"");
			}

			stdin.write((cmd + "\n").getBytes());
			stdin.flush();

			String result = stdout.readLine();
			if (!result.startsWith("=")) {
				System.err.println("Answer : " + result);
				return;
			} else if (verbose) {
				System.out.println("Answer : " + result);
			}

			if (answer != null) {
				answer[0] = result;
			}

			for (int i = 0; i < answerLines; i++) {
				String line = stdout.readLine();

				if (answer != null) {
					answer[i + 1] = line;
				}

				if (verbose) {
					System.out.println(line);
				}
			}
			stdout.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		startGnuGo();
		GnuGoGameScenario scenario = new GnuGoGameScenario(new MersenneTwisterFast(2010),
				new WPCPlayer(new double[25]), GoBoard.WHITE, new double[] { 0.1, 0.1 });
		scenario.play(new GoGame());
		stopGnuGo();
	}
}
