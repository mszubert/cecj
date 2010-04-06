package cecj.app.numbers_game;

import java.math.BigInteger;
import java.util.List;

import cecj.interaction.InteractionResult;
import cecj.interaction.RealValuedResult;
import cecj.utils.Pair;


public class MultiDimensionalNumbersGame1 extends MultiDimensionalNumbersGame {

	@Override
	protected Pair<? extends InteractionResult> compareDimensionsVectors(
			List<BigInteger> candidateVector, List<BigInteger> testVector) {

		int comparisonDimension = 0;
		BigInteger maxDiffOnDimension = candidateVector.get(0).subtract(testVector.get(0)).abs();
		for (int dim = 1; dim < candidateVector.size(); dim++) {
			BigInteger diff = candidateVector.get(dim).subtract(testVector.get(dim)).abs();
			if (diff.compareTo(maxDiffOnDimension) > 0) {
				maxDiffOnDimension = diff;
				comparisonDimension = dim;
			}
		}

		if (candidateVector.get(comparisonDimension).compareTo(testVector.get(comparisonDimension)) > 0) {
			return new Pair<RealValuedResult>(new RealValuedResult(1), new RealValuedResult(0));
		} else {
			return new Pair<RealValuedResult>(new RealValuedResult(0), new RealValuedResult(1));
		}
	}
}
