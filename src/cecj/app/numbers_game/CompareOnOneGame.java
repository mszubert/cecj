package cecj.app.numbers_game;

import java.math.BigInteger;
import java.util.List;

import cecj.interaction.InteractionResult;
import cecj.interaction.RealValuedResult;
import cecj.utils.Pair;


public class CompareOnOneGame extends MultiDimensionalNumbersGame {

	@Override
	protected Pair<? extends InteractionResult> compareDimensionsVectors(
			List<BigInteger> candidateVector, List<BigInteger> testVector) {

		int comparisonDimension = 0;
		BigInteger maxDimensionValue = testVector.get(0);
		for (int dim = 1; dim < testVector.size(); dim++) {
			if (testVector.get(dim).compareTo(maxDimensionValue) > 0) {
				maxDimensionValue = testVector.get(dim);
				comparisonDimension = dim;
			}
		}

		if (candidateVector.get(comparisonDimension).compareTo(testVector.get(comparisonDimension)) >= 0) {
			return new Pair<RealValuedResult>(new RealValuedResult(1), new RealValuedResult(-1));
		} else {
			return new Pair<RealValuedResult>(new RealValuedResult(-1), new RealValuedResult(1));
		}
	}
}
