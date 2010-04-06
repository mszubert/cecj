package cecj.app.othello;

import games.WPCPlayer;
import cecj.app.WPCPlayerFitnessCalculator;

public class OthelloHeuristicPlayer extends WPCPlayerFitnessCalculator {

	double[] wpc = { 1.00f, -0.25f, 0.10f, 0.05f, 0.05f, 0.10f, -0.25f, 1.00f, -0.25f, -0.25f,
			0.01f, 0.01f, 0.01f, 0.01f, -0.25f, -0.25f, 0.10f, 0.01f, 0.05f, 0.02f, 0.02f, 0.05f,
			0.01f, 0.10f, 0.05f, 0.01f, 0.02f, 0.01f, 0.01f, 0.02f, 0.01f, 0.05f, 0.05f, 0.01f,
			0.02f, 0.01f, 0.01f, 0.02f, 0.01f, 0.05f, 0.10f, 0.01f, 0.05f, 0.02f, 0.02f, 0.05f,
			0.01f, 0.10f, -0.25f, -0.25f, 0.01f, 0.01f, 0.01f, 0.01f, -0.25f, -0.25f, 1.00f,
			-0.25f, 0.10f, 0.05f, 0.05f, 0.10f, -0.25f, 1.00f };

	@Override
	protected WPCPlayer getOpponent(int size) {
		return new WPCPlayer(wpc);
	}
}
