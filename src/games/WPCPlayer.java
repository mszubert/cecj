package games;

import ec.util.MersenneTwisterFast;

public class WPCPlayer implements Player {

	private int boardSize;

	private double[] wpc;

	public WPCPlayer(int boardSize) {
		this.boardSize = boardSize;
		this.wpc = new double[boardSize * boardSize];
	}
	
	public WPCPlayer(double[] wpc) {
		this.boardSize = (int) Math.sqrt(wpc.length);
		this.wpc = wpc;
	}

	public double getValue(int row, int col) {
		return wpc[(row - 1) * boardSize + (col - 1)];
	}
	
	public double[] getWPC() {
		return wpc;
	}

	public void setValue(int row, int col, double value) {
		wpc[(row - 1) * boardSize + (col - 1)] = value; 
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				builder.append(wpc[i * boardSize + j] + "\t");
			}
			builder.append("\n");
		}
		String res = builder.toString();
		return res;
	}
	
	public void randomize(MersenneTwisterFast random, double range) {
		for (int i = 0; i < wpc.length; i++) {
			wpc[i] = random.nextDouble() * range;
			if (random.nextBoolean()) {
				wpc[i] *= -1;
			}
		}
	}

}
