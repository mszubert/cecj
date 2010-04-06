package cecj.app.numbers_game;

import java.math.BigInteger;
import java.util.List;

import cecj.interaction.InteractionResult;
import cecj.interaction.RealValuedResult;
import cecj.utils.Pair;


public class CompareOnAllGame extends MultiDimensionalNumbersGame {

	@Override
	protected Pair<? extends InteractionResult> compareDimensionsVectors(
			List<BigInteger> candidateVector, List<BigInteger> testVector) {

		for (int dim = 0; dim < candidateVector.size(); dim++) {
			if (candidateVector.get(dim).compareTo(testVector.get(dim)) < 0) {
				return new Pair<RealValuedResult>(new RealValuedResult(-1), new RealValuedResult(1));
			}
		}

		return new Pair<RealValuedResult>(new RealValuedResult(1), new RealValuedResult(-1));
	}

}
