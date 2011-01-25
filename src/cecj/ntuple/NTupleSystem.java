package cecj.ntuple;

import java.util.TreeSet;

import ec.EvolutionState;
import ec.Setup;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;

public class NTupleSystem implements Setup {

	public final static String P_TUPLE_ARITY = "tuple-arity";
	public final static String P_NUM_TUPLES = "num-tuples";
	public final static String P_NUM_VALUES = "num-values";

	public final static String P_INIT_RANGE = "init-range";

	public final static String P_SPACE_SIZE = "space-size";

	/**
	 * Directions on a 2D board represented by a "flat" array
	 */
	private static int[] dirs = { 1, 8, -1, -8, 9, 7, -9, -7 };

	/**
	 * Number of elements in each tuple
	 */
	private int tupleArity;

	/**
	 * Number of tuples in the system
	 */
	private int numTuples;

	/**
	 * Number of possible values of each element of the tuple
	 */
	private int numValues;

	/**
	 * Specifies the size of space which is sampled by NTuple Assuming that space dimensionality is
	 * equal to 2
	 */
	private int spaceSize;

	private double initRange;

	public void setup(EvolutionState state, Parameter base) {
		tupleArity = state.parameters.getInt(base.push(P_TUPLE_ARITY), NTupleDefaults.base().push(
				P_TUPLE_ARITY), 1);
		if (tupleArity == 0) {
			state.output.error("NTupleSpecies must have tuple arity which is > 0");
		}

		numTuples = state.parameters.getInt(base.push(P_NUM_TUPLES), NTupleDefaults.base().push(
				P_NUM_TUPLES), 1);
		if (numTuples == 0) {
			state.output.error("NTupleSpecies must have number of tuples which is > 0");
		}

		numValues = state.parameters.getInt(base.push(P_NUM_VALUES), NTupleDefaults.base().push(
				P_NUM_VALUES), 2);
		if (numValues == 1) {
			state.output.error("NTupleSpecies must have number of values which is > 1");
		}

		spaceSize = state.parameters.getInt(base.push(P_SPACE_SIZE), NTupleDefaults.base().push(
				P_SPACE_SIZE), 1);
		if (spaceSize == 0) {
			state.output.error("NTupleSpecies must have space size which is > 0");
		}

		initRange = state.parameters.getDoubleWithDefault(base.push(P_INIT_RANGE), NTupleDefaults
				.base().push(P_INIT_RANGE), 0.0);

		state.output.exitIfErrors();
	}

	public void randomizeIndividual(EvolutionState state, int thread, NTupleIndividual ind) {
		double[][] weights = new double[numTuples][];
		int[][] positions = new int[numTuples][];
		int maxPosition = spaceSize * spaceSize;

		MersenneTwisterFast rng = state.random[thread];
		for (int i = 0; i < numTuples; i++) {
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
				positions[i][j++] = position;
			}

			weights[i] = new double[(int) (Math.pow(numValues, positions[i].length))];
			for (int w = 0; w < weights[i].length; w++) {
				weights[i][w] = rng.nextDouble() * initRange;
				if (rng.nextBoolean()) {
					weights[i][w] *= -1;
				}
			}
		}

		ind.setPositions(positions);
		ind.setWeights(weights);
	}

	public void mutatePosition(int[] tuple, int position) {
		int maxPosition = spaceSize * spaceSize;
		TreeSet<Integer> positionSet = new TreeSet<Integer>();
		for (int i = 0; i < tuple.length; i++) {
			if (i != position) {
				positionSet.add(tuple[i]);
			}
		}

		int bestDir = -1;
		int bestDirNeighbours = -1;
		for (int d = 0; d < dirs.length; d++) {
			int newPosition = tuple[position] + dirs[d];
			newPosition = (newPosition + maxPosition) % maxPosition;

			if (positionSet.contains(newPosition)) {
				continue;
			} else {
				int neighbours = 0;
				for (int d2 = 0; d2 < dirs.length; d2++) {
					int newPositionNeighbour = newPosition + dirs[d2];
					newPositionNeighbour = (newPositionNeighbour + maxPosition) % maxPosition;
					if (positionSet.contains(newPositionNeighbour)) {
						neighbours++;
					}
				}

				if (neighbours > bestDirNeighbours) {
					bestDirNeighbours = neighbours;
					bestDir = d;
				}
			}
		}

		tuple[position] += dirs[bestDir];
		tuple[position] = (tuple[position] + maxPosition) % maxPosition;
	}

}
