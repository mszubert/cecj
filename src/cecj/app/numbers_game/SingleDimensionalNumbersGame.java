package cecj.app.numbers_game;

import java.math.BigDecimal;
import java.math.RoundingMode;

import cecj.interaction.InteractionResult;
import cecj.interaction.RealValuedResult;
import cecj.problems.TestBasedProblem;
import cecj.statistics.ObjectiveFitnessCalculator;
import cecj.utils.Pair;
import ec.EvolutionState;
import ec.Individual;
import ec.vector.BitVectorIndividual;

public class SingleDimensionalNumbersGame extends TestBasedProblem implements ObjectiveFitnessCalculator {

	@Override
	public Pair<? extends InteractionResult> test(EvolutionState state, Individual candidate,
			Individual test) {
		return new Pair<RealValuedResult>(new RealValuedResult(100), new RealValuedResult(100));
		
//		if (!(candidate instanceof BitVectorIndividual) || !(test instanceof BitVectorIndividual)) {
//			state.output.error("Competitor individuals should be represented by bit vectors\n");
//		}
//
//		BitVectorIndividual bitCandidate = (BitVectorIndividual) candidate;
//		BitVectorIndividual bitTest = (BitVectorIndividual) test;
//
//		if (bitCandidate.genomeLength() != bitTest.genomeLength()) {
//			state.output.error("Competitors' bit vectors should be equal length\n");
//		}
//
//		for (int i = 0; i < bitCandidate.genomeLength(); i++) {
//			if (bitCandidate.genome[i] != bitTest.genome[i]) {
//				if (bitCandidate.genome[i]) {
//					return new Pair<RealValuedResult>(new RealValuedResult(1),
//						new RealValuedResult(0));
//				} else {
//					return new Pair<RealValuedResult>(new RealValuedResult(0),
//						new RealValuedResult(1));
//				}
//			}
//		}
//
//		return new Pair<RealValuedResult>(new RealValuedResult(0), new RealValuedResult(0));
	}

	public float calculateObjectiveFitness(EvolutionState state, Individual ind) {
		if (!(ind instanceof BitVectorIndividual)) {
			state.output.error("Competitor individuals should be represented by bit vectors\n");
		}
		BitVectorIndividual bitIndividual = (BitVectorIndividual) ind;

		BigDecimal two = new BigDecimal(2);
		BigDecimal maxValue = two.pow(bitIndividual.genome.length).subtract(BigDecimal.ONE);

		BigDecimal result = BigDecimal.ZERO;
		for (int i = 0; i < bitIndividual.genomeLength(); i++) {
			result = result.multiply(two);
			if (bitIndividual.genome[i]) {
				result = result.add(BigDecimal.ONE);
			}
		}

		return result.divide(maxValue, 10, RoundingMode.DOWN).floatValue();
	}

	@Deprecated
	public float calculateLongObjectiveFitness(EvolutionState state, Individual ind) {
		if (!(ind instanceof BitVectorIndividual)) {
			state.output.error("Competitor individuals should be represented by bit vectors\n");
		}

		BitVectorIndividual bitIndividual = (BitVectorIndividual) ind;
		long result = 0;
		for (int i = 0; i < bitIndividual.genomeLength(); i++) {
			result *= 2;
			if (bitIndividual.genome[i]) {
				result++;
			}
		}
		return result;
	}
}
