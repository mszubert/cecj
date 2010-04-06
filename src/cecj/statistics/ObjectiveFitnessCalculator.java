package cecj.statistics;

import ec.EvolutionState;
import ec.Individual;
import ec.Setup;

public interface ObjectiveFitnessCalculator extends Setup {
	public float calculateObjectiveFitness(EvolutionState state, Individual ind);
}
