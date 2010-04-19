package cecj.app.numbers_game;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import cecj.interaction.InteractionResult;
import cecj.problems.TestBasedProblem;
import cecj.statistics.ObjectiveFitnessCalculator;
import cecj.utils.Pair;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;
import ec.vector.BitVectorIndividual;

public abstract class MultiDimensionalNumbersGame extends TestBasedProblem implements
		ObjectiveFitnessCalculator {

	private static final String P_DIMENSIONS = "dimensions";
	private static final String P_DIM_SIZE = "dimension-size";

	private int numDimensions;
	private int dimensionSize;
	private int expectedGenomeLength;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);
		
		Parameter numDimensionsParam = base.push(P_DIMENSIONS);
		numDimensions = state.parameters.getInt(numDimensionsParam, null, 1);
		if (numDimensions <= 0) {
			state.output
				.fatal("Multi Dimensional Numbers Game dimensions must be specified and >= 0\n", numDimensionsParam);
		}

		Parameter dimensionSizeParam = base.push(P_DIM_SIZE);
		dimensionSize = state.parameters.getInt(dimensionSizeParam, null, 1);
		if (dimensionSize <= 0) {
			state.output
				.fatal("Multi Dimensional Numbers Game dimension size must be specified and >= 0\n", dimensionSizeParam);
		}

		expectedGenomeLength = numDimensions * dimensionSize;
	}

	@Override
	public Pair<? extends InteractionResult> test(EvolutionState state, Individual candidate,
			Individual test) {
		if (!(candidate instanceof BitVectorIndividual) || !(test instanceof BitVectorIndividual)) {
			state.output
				.error("NumbersGame player's individual should be represented by bit vector\n");
		}

		BitVectorIndividual bitCandidate = (BitVectorIndividual) candidate;
		BitVectorIndividual bitTest = (BitVectorIndividual) test;

		if (bitCandidate.genomeLength() != bitTest.genomeLength()) {
			state.output.error("NumbersGame players' bit vectors should be equal length\n");
		}

		if (bitCandidate.genomeLength() != expectedGenomeLength) {
			state.output
				.error("NumbersGame player's bit vector length must be equal to product of dimensions number and dimension size\n");
		}

		List<BigInteger> candidateVector = getIntegerVector(bitCandidate.genome);
		List<BigInteger> testVector = getIntegerVector(bitTest.genome);

		return compareDimensionsVectors(candidateVector, testVector);
	}

	/**
	 * Returns objective fitness normalized to [0, 1] interval;
	 */
	public float calculateObjectiveFitness(EvolutionState state, Individual ind) {
		if (!(ind instanceof BitVectorIndividual)) {
			state.output.error("Competitor individuals should be represented by bit vectors\n");
		}
		BitVectorIndividual bitIndividual = (BitVectorIndividual) ind;

		BigDecimal dimensionMaxValue = new BigDecimal(2).pow(dimensionSize)
			.subtract(BigDecimal.ONE);
		BigDecimal sumMaxValue = dimensionMaxValue.multiply(new BigDecimal(numDimensions));

		List<BigInteger> dimensionVector = getIntegerVector(bitIndividual.genome);
		BigDecimal sum = BigDecimal.ZERO;
		for (BigInteger dimension : dimensionVector) {
			sum = sum.add(new BigDecimal(dimension));
		}

		return sum.divide(sumMaxValue, 10, RoundingMode.DOWN).floatValue();
	}

	private List<BigInteger> getIntegerVector(boolean[] genome) {
		List<BigInteger> result = new ArrayList<BigInteger>();
		BigInteger bigTwo = new BigDecimal(2).toBigInteger();

		for (int dim = 0; dim < numDimensions; dim++) {
			BigInteger dimValue = BigInteger.ZERO;
			for (int i = 0; i < dimensionSize; i++) {
				dimValue = dimValue.multiply(bigTwo);
				if (genome[(dim * dimensionSize) + i]) {
					dimValue = dimValue.add(BigInteger.ONE);
				}
			}
			result.add(dimValue);
		}

		return result;
	}

	protected abstract Pair<? extends InteractionResult> compareDimensionsVectors(
			List<BigInteger> candidateVector, List<BigInteger> testVector);
}
