package games;


public class NTuple {

	private static final int VALUES = 3;

	private int n;

	private double[] lut;

	private int[][] symmetricLocations;
	
	
	public NTuple(int[] locations, SymmetryExpander expander) {
		n = locations.length;
		lut = new double[(int)Math.pow(VALUES, n)];
		
		int iterator = 0;
		int[][] symmetries = new int[locations.length][];
		for (int location : locations) {
			symmetries[iterator++] = expander.getSymmetries(location);
		}
		
		int numSymmetries = symmetries[0].length;
		symmetricLocations = new int[numSymmetries][n];
		for (int i = 0; i < numSymmetries; i++) {
			for (int j = 0; j < n; j++) {
				symmetricLocations[i][j] = symmetries[j][i];
			}
		}
    }
	
	public double value(Board board) {
		double result = 0;
		for (int[] tuple : symmetricLocations) {
			result += lut[address(tuple, board)];
		}
		return result;
	}

	private int address(int[] tuple, Board board) {
		int result = 0;
		for (int location : tuple) {
			result *= VALUES;
			board.getPiece(location);
		}
		return result;
	}

}
