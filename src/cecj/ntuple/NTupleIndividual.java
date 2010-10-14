package cecj.ntuple;

import java.util.Arrays;
import java.util.TreeSet;

import ec.EvolutionState;
import ec.Individual;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;

public class NTupleIndividual extends Individual {

	public static final String P_NTUPLE_INDIVIDUAL = "ntuple-ind";

	/**
	 * Directions on a 2D board represented by a "flat" array
	 */
	private static int[] dirs = { 1, 8, -1, -8, 9, 7, -9, -7 };

	/**
	 * 
	 */
	private int[][] positions;

	private double[][] weights;

	/**
	 * This method is called only once - on a prototype individual stored in the species class.
	 */
	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		if (!(species instanceof NTupleSpecies)) {
			state.output.fatal("NTupleIndividual requires a NTupleSpecies", base, defaultBase());
		}

		NTupleSpecies s = (NTupleSpecies) species;
		positions = new int[s.getNumTuples()][];
		weights = new double[s.getNumTuples()][];
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
		clone.positions = positions.clone();
		clone.weights = weights.clone();

		return clone;
	}

	@Override
	public int hashCode() {
		return Arrays.deepHashCode(weights) ^ Arrays.deepHashCode(positions);
	}

	public Parameter defaultBase() {
		return NTupleDefaults.base().push(P_NTUPLE_INDIVIDUAL);
	}

	public void reset(EvolutionState state, int thread) {
		NTupleSpecies s = (NTupleSpecies) species;
		int maxPosition = s.getSpaceSize() * s.getSpaceSize();
		int tupleArity = s.getTupleArity();

		MersenneTwisterFast rng = state.random[thread];
		for (int i = 0; i < positions.length; i++) {
			TreeSet<Integer> positionSet = new TreeSet<Integer>();
			int seed = rng.nextInt(maxPosition);
			positionSet.add(seed);
			for (int j = 0; j < tupleArity - 1; j++) {
				seed += dirs[rng.nextInt(dirs.length)];
				seed = (seed + maxPosition) % maxPosition;
				positionSet.add(seed);
			}

			positions[i] = new int[positionSet.size()];
			int j = 0;
			for (int position : positionSet) {
				positions[i][j] = position;
			}

			weights[i] = new double[(int) (Math.pow(s.getNumValues(), positions[i].length))];
		}
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
		for (int i = 0; i < positions.length; i++) {
			if (rng.nextBoolean(prob)) {
				int[] tempPositions = positions[i];
				double[] tempWeights = weights[i];
				positions[i] = tupleIndividual.positions[i];
				weights[i] = tupleIndividual.weights[i];
				tupleIndividual.positions[i] = tempPositions;
				tupleIndividual.weights[i] = tempWeights;
			}
		}
	}
}
