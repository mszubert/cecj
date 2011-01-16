package cecj.ntuple;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Code;
import ec.util.DecodeReturn;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;

public class NTupleIndividual extends Individual {

	public static final String P_NTUPLE_INDIVIDUAL = "ntuple-ind";

	/**
	 * 
	 */
	private int[][] positions;

	private double[][] weights;

	public double[][] getWeights() {
		return weights;
	}

	public void setWeights(double[][] weights) {
		this.weights = weights;
	}

	public int[][] getPositions() {
		return positions;
	}

	public void setPositions(int[][] positions) {
		this.positions = positions;
	}

	/**
	 * This method is called only once - on a prototype individual stored in the species class.
	 */
	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		if (!(species instanceof NTupleSpecies)) {
			state.output.fatal("NTupleIndividual requires a NTupleSpecies", base, defaultBase());
		}
	}

	@Override
	public boolean equals(Object ind) {
		if (!(ind instanceof NTupleIndividual)) {
			return false;
		}

		NTupleIndividual ntuple = (NTupleIndividual) ind;
		return (Arrays.deepEquals(positions, ntuple.positions) && Arrays.deepEquals(weights,
				ntuple.weights));
	}

	@Override
	public Object clone() {
		NTupleIndividual clone = (NTupleIndividual) (super.clone());

		if (positions != null) {
			clone.positions = positions.clone();
		}

		if (weights != null) {
			clone.weights = weights.clone();
		}

		return clone;
	}

	@Override
	public int hashCode() {
		return Arrays.deepHashCode(weights) ^ Arrays.deepHashCode(positions);
	}

	public Parameter defaultBase() {
		return NTupleDefaults.base().push(P_NTUPLE_INDIVIDUAL);
	}

	public void defaultMutate(EvolutionState state, int thread) {
		NTupleSpecies s = (NTupleSpecies) species;
		float prob = s.getMutationProbability();
		if (!(prob > 0.0)) {
			return;
		}

		MersenneTwisterFast rng = state.random[thread];
		for (int i = 0; i < weights.length; i++) {
			for (int j = 0; j < weights[i].length; j++) {
				if (rng.nextBoolean(prob)) {
					weights[i][j] = rng.nextGaussian() * s.getMutationStdev() + weights[i][j];
				}
			}
		}
	}

	public void defaultCrossover(EvolutionState state, int thread, NTupleIndividual tupleIndividual) {
		NTupleSpecies s = (NTupleSpecies) species;
		float prob = s.getCrossoverProbability();
		if (!(prob > 0.0)) {
			return;
		}

		MersenneTwisterFast rng = state.random[thread];
		if (rng.nextBoolean(prob)) {
			List<Integer> list = drawCombination(rng, positions.length, positions.length / 2);
			for (int tuple : list) {
				int[] tempPositions = positions[tuple].clone();
				double[] tempWeights = weights[tuple].clone();
				positions[tuple] = tupleIndividual.positions[tuple].clone();
				weights[tuple] = tupleIndividual.weights[tuple].clone();
				tupleIndividual.positions[tuple] = tempPositions;
				tupleIndividual.weights[tuple] = tempWeights;
			}
		}
	}

	private List<Integer> drawCombination(MersenneTwisterFast rng, int n, int k) {
		int[] drawArray = new int[n];
		for (int i = 0; i < n; i++) {
			drawArray[i] = i;
		}

		List<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < k; i++) {
			int draw = rng.nextInt(n - i);
			result.add(drawArray[draw]);
			drawArray[draw] = drawArray[n - i - 1];
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < positions.length; i++) {
			builder.append(Arrays.toString(positions[i]));
			builder.append("\n");
		}
		return builder.toString();
	}

	@Override
	public String genotypeToString() {
		StringBuilder builder = new StringBuilder();
		builder.append(Code.encode(positions.length));
		for (int i = 0; i < positions.length; i++) {
			builder.append(Code.encode(positions[i].length));
			for (int j = 0; j < positions[i].length; j++) {
				builder.append(Code.encode(positions[i][j]));
			}
			builder.append(Code.encode(weights[i].length));
			for (int j = 0; j < weights[i].length; j++) {
				builder.append(Code.encode(weights[i][j]));
			}
		}
		return builder.toString();
	}

	@Override
	protected void parseGenotype(final EvolutionState state, final LineNumberReader reader)
			throws IOException {
		String s = reader.readLine();
		DecodeReturn decoder = new DecodeReturn(s);
		Code.decode(decoder);
		int numTuples = (int) decoder.l;
		
		positions = new int[numTuples][];
		weights = new double[numTuples][];
		
		for (int i = 0; i < positions.length; i++) {
			Code.decode(decoder);
			positions[i] = new int[(int)(decoder.l)];
			for (int j = 0; j < positions[i].length; j++) {
				Code.decode(decoder);
				positions[i][j] = (int)(decoder.l);
			}
			
			Code.decode(decoder);
			weights[i] = new double[(int)(decoder.l)];
			for (int j = 0; j < weights[i].length; j++) {
				Code.decode(decoder);
				weights[i][j] = decoder.d;
			}
		}
	}

	public static void main(String args[]) {
		NTupleIndividual ind = new NTupleIndividual();
		System.out.println(ind.drawCombination(new MersenneTwisterFast(System.currentTimeMillis()),
				10, 5));
	}
}
