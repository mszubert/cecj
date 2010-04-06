package cecj.app;

import games.WPCPlayer;

import java.util.Random;

public class RandomStationaryPlayer extends WPCPlayerFitnessCalculator {

	private static final float WPC_POSITION_RANGE = 1;

	@Override
	protected WPCPlayer getOpponent(int size) {
		double[] wpc = new double[size];
		Random r = new Random(System.currentTimeMillis());

		for (int i = 0; i < size; i++) {
			wpc[i] = r.nextDouble() * WPC_POSITION_RANGE;
			if (r.nextBoolean()) {
				wpc[i] *= -1;
			}
		}

		return new WPCPlayer(wpc);
	}
}
