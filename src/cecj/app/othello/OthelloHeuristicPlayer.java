package cecj.app.othello;

import games.player.Player;
import games.player.WPCPlayer;
import cecj.app.RandomizedPlayerFitnessCalculator;

public class OthelloHeuristicPlayer extends RandomizedPlayerFitnessCalculator {

	private static final double[] wpc = { 1.00f, -0.25f, 0.10f, 0.05f, 0.05f, 0.10f, -0.25f, 1.00f, -0.25f, -0.25f,
			0.01f, 0.01f, 0.01f, 0.01f, -0.25f, -0.25f, 0.10f, 0.01f, 0.05f, 0.02f, 0.02f, 0.05f,
			0.01f, 0.10f, 0.05f, 0.01f, 0.02f, 0.01f, 0.01f, 0.02f, 0.01f, 0.05f, 0.05f, 0.01f,
			0.02f, 0.01f, 0.01f, 0.02f, 0.01f, 0.05f, 0.10f, 0.01f, 0.05f, 0.02f, 0.02f, 0.05f,
			0.01f, 0.10f, -0.25f, -0.25f, 0.01f, 0.01f, 0.01f, 0.01f, -0.25f, -0.25f, 1.00f,
			-0.25f, 0.10f, 0.05f, 0.05f, 0.10f, -0.25f, 1.00f };

	@Override
	protected Player getOpponent() {
		return new WPCPlayer(wpc);
	}
	
	public static Player getPlayer() {
		return new WPCPlayer(wpc);
	}
}
