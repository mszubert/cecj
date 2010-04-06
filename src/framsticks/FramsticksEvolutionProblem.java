package framsticks;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleProblemForm;
import ec.simple.SimpleFitness;

public class FramsticksEvolutionProblem extends Problem implements SimpleProblemForm {

	public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) {
		if (ind.evaluated) {
			return;
		}

		if (!(ind instanceof FramsticksIndividual)) {
			state.output.fatal("The individuals for this problem should be FramsticksIndividuals.");
		}

		FramsticksIndividual framstickIndividual = (FramsticksIndividual) ind;
		if (!(framstickIndividual.fitness instanceof SimpleFitness)) {
			state.output.fatal("The fitness for this problem should be SimpleFitness");
		}

		String fileName = "ind_subpop" + subpopulation + "_thread" + threadnum + ".gen";
		FramsticksUtils utils = FramsticksUtils.getInstance(state);
		float fitness = utils.evaluateGenotype(framstickIndividual.genotype, fileName);

		((SimpleFitness) framstickIndividual.fitness).setFitness(state, fitness, false);
		framstickIndividual.evaluated = true;
	}

	public void describe(Individual ind, EvolutionState state, int subpopulation, int threadnum,
			int log, int verbosity) {
		// TODO Auto-generated method stub
		
	}
}
