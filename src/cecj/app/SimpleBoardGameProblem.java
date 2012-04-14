package cecj.app;

import cecj.statistics.ObjectiveFitnessCalculator;
import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;

public class SimpleBoardGameProblem extends Problem implements SimpleProblemForm {

	private static final String P_FITNESS_CALCULATOR = "fitness-calc";

	private ObjectiveFitnessCalculator fitnessCalc;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		Parameter fitnessCalcParameter = base.push(P_FITNESS_CALCULATOR);
		fitnessCalc = (ObjectiveFitnessCalculator) state.parameters.getInstanceForParameter(
				fitnessCalcParameter, null, ObjectiveFitnessCalculator.class);
		fitnessCalc.setup(state, fitnessCalcParameter);
	}

	public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) {
		float fitness = fitnessCalc.calculateObjectiveFitness(state, ind);
		((SimpleFitness)ind.fitness).setFitness(state, fitness, false);
	}
}
