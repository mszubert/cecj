package games.player;

import java.util.StringTokenizer;

import games.Board;
import games.SymmetryExpander;

public class NTuple {

	private static final int VALUES = 3;

	private int n;

	private double[] lut;

	private double[] traces;

	private int[][] symmetricPositions;
	
	private NTuple() { }

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

	public static NTuple readNTuple(StringTokenizer tokenizer) {
		NTuple result = new NTuple();
		while (!tokenizer.nextToken().equals("{"));
		result.n = Integer.parseInt(tokenizer.nextToken());
		result.symmetricPositions = new int[Integer.parseInt(tokenizer.nextToken())][];
		for (int i = 0; i < result.symmetricPositions.length; i++) {
			result.symmetricPositions[i] = readIntArray(tokenizer, result.n);
		}
		result.lut = readDoubleArray(tokenizer, (int) Math.pow(VALUES, result.n));
		while (!tokenizer.nextToken().equals("}"));
		return result;
	}
	
	private static double[] readDoubleArray(StringTokenizer tokenizer, int n) {
		double[] result = new double[n];
        while (!tokenizer.nextToken().equals("{"));
        for (int i=0; i < n; i++) {
            result[i] = Double.parseDouble(tokenizer.nextToken());
        }
		while (!tokenizer.nextToken().equals("}"));
        return result;
	}

	private static int[] readIntArray(StringTokenizer tokenizer, int n) {
        int[] result = new int[n];
        while (!tokenizer.nextToken().equals("{"));
        for (int i=0; i < n; i++) {
            result[i] = Integer.parseInt(tokenizer.nextToken());
        }
		while (!tokenizer.nextToken().equals("}"));
        return result;
    }
}


