package cecj.app.numbers_game;

import java.math.BigInteger;
import java.util.List;

public class CompareOnAllGame extends MultiDimensionalNumbersGame {

	@Override
	protected int compareDimensionsVectors(List<BigInteger> candidateVector,
			List<BigInteger> testVector) {

		for (int dim = 0; dim < candidateVector.size(); dim++) {
			if (candidateVector.get(dim).compareTo(testVector.get(dim)) < 0) {
				return -1;
			}
		}

		return 1;
	}

}
