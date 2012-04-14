package cecj.app.go;

import games.player.Player;
import games.player.WPCPlayer;
import cecj.app.RandomizedPlayerFitnessCalculator;

public class GoHeuristicPlayer extends RandomizedPlayerFitnessCalculator {

	double[] wpc = { -0.125668, 0.265170, 0.179982, 0.264306, -0.135056, 0.255316, 0.285938,
			0.285971, 0.286838, 0.264588, 0.122406, 0.348292, 0.286885, 0.348299, 0.108398,
			0.264661, 0.286838, 0.286762, 0.286891, 0.263816, -0.125170, 0.265835, 0.172941,
			0.265845, -0.110329 };

	double[] rounded = { -0.1, 0.2, 0.15, 0.2, -0.1, 0.2, 0.25, 0.25, 0.25, 0.2, 0.1, 0.3, 0.25,
			0.3, 0.1, 0.2, 0.25, 0.25, 0.25, 0.2, -0.1, 0.2, 0.15, 0.2, -0.1 };

	@Override
	public Player getOpponent() {
		return new WPCPlayer(rounded);
	}
}
