package cecj.subgame;

import cecj.interaction.IntegerTestResult;
import cecj.interaction.TestResult;
import cecj.problem.TestBasedProblem;
import cecj.subgame.SubgameFitnessCalculator;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class BoardSubgameProblem extends TestBasedProblem {

	private static final String P_FITNESS_CALCULATOR = "fitness-calc";

	private SubgameFitnessCalculator fitnessCalc;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		Parameter fitnessCalcParameter = base.push(P_FITNESS_CALCULATOR);
		fitnessCalc = (SubgameFitnessCalculator) state.parameters.getInstanceForParameter(
				fitnessCalcParameter, null, SubgameFitnessCalculator.class);
		fitnessCalc.setup(state, fitnessCalcParameter);
	}

	@Override
	public TestResult test(EvolutionState state, Individual candidate, Individual test) {

		if (!(test instanceof SubgameIndividual)) {
			state.output.fatal("Tests must be of type SubgameIndividual in this problem");
		}

		SubgameIndividual subgame = (SubgameIndividual) test;
		return new IntegerTestResult(fitnessCalc.calculatePoints(state, candidate, subgame));
	}
}
