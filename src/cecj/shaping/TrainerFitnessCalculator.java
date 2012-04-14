package cecj.shaping;

import cecj.app.GamePlayerFitnessCalculator;
import cecj.statistics.ObjectiveFitnessCalculator;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;
import games.player.Player;

public class TrainerFitnessCalculator implements ObjectiveFitnessCalculator {

	private static final String P_FITNESS_CALCULATOR = "fitness-calc";

	private GamePlayerFitnessCalculator fitnessCalc;

	public void setup(EvolutionState state, Parameter base) {
		Parameter fitnessCalcParameter = base.push(P_FITNESS_CALCULATOR);
		fitnessCalc = (GamePlayerFitnessCalculator) state.parameters.getInstanceForParameter(
				fitnessCalcParameter, null, GamePlayerFitnessCalculator.class);
		fitnessCalc.setup(state, fitnessCalcParameter);
	}

	public float calculateObjectiveFitness(EvolutionState state, Individual ind) {
		TrainerIndividual trainer = (TrainerIndividual) ind;
		Player player = trainer.trainPlayer();

		return fitnessCalc.calculateObjectiveFitness(state, player);
	}

}
