package games.player;

import games.Board;
import games.SymmetryExpander;

public class NTuple {

	private static final int VALUES = 3;

	private int n;

	private double[] lut;

	private double[] traces;

	private int[][] symmetricPositions;

	public NTuple(int[] locations, SymmetryExpander expander) {
		this(locations, new double[(int) Math.pow(VALUES, locations.length)], expander);
	}

	public NTuple(int[] locations, double[] weights, SymmetryExpander expander) {
		n = locations.length;
		lut = weights;

		int iterator = 0;
		int[][] symmetries = new int[locations.length][];
		for (int location : locations) {
			symmetries[iterator++] = expander.getSymmetries(location);
		}

		int numSymmetries = symmetries[0].length;
		symmetricPositions = new int[numSymmetries][n];
		for (int i = 0; i < numSymmetries; i++) {
			for (int j = 0; j < n; j++) {
				symmetricPositions[i][j] = symmetries[j][i];
			}
		}
	}

	public double value(Board board) {
		double result = 0;
		for (int[] tuple : symmetricPositions) {
			result += lut[address(tuple, board)];
		}
		return result;
	}

	private int address(int[] tuple, Board board) {
		int result = 0;
		for (int location : tuple) {
			result *= VALUES;
			result += (board.getValueAt(location) + 1);
		}
		return result;
	}

	public int[] getPositions() {
		return symmetricPositions[0];
	}

	public double[] getWeights() {
		return lut;
	}

	public void updateWeights(Board previous, double delta) {
		for (int[] tuple : symmetricPositions) {
			lut[address(tuple, previous)] += delta;
		}
	}

	public void updateWeights(Board previous, double delta, double lambda, double derivative) {
		for (int i = 0; i < traces.length; i++) {
			traces[i] *= lambda;
		}
		
		for (int[] tuple : symmetricPositions) {
			traces[address(tuple, previous)] += derivative;
		}
		
		for (int i = 0; i < lut.length; i++) {
			lut[i] += delta * traces[i];
		}
	}

	public void initializeEligibilityTraces() {
		traces = new double[lut.length];
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{\n" + n + "\n" + symmetricPositions.length + "\n");
		
		for (int[] symmetry : symmetricPositions) {
			builder.append("{ ");
			for (int i = 0; i < n; i++) {
				builder.append(symmetry[i] + " ");
			}
			builder.append("}\n");
		}
		
		builder.append("{ ");
		for (double weight : lut) {
			builder.append(weight + " ");
		}
		builder.append("}\n");
		
		builder.append("}\n");
		return builder.toString();
	}

}
