package cecj.eval;

import java.util.Arrays;
import java.util.Comparator;

import ec.Evaluator;
import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;

public class TournamentNoisyEvaluator extends Evaluator {

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
	}

	@Override
	public void evaluatePopulation(EvolutionState state) {
		for (int subpop = 0; subpop < state.population.subpops.length; subpop++) {
			Individual[] inds = state.population.subpops[subpop].individuals;

			Integer[] indexes = new Integer[inds.length];
			final float[] fitnesses = new float[inds.length];
			for (int ind = 0; ind < inds.length; ind++) {
				((SimpleProblemForm) (p_problem)).evaluate(state, inds[ind], subpop, 0);
				fitnesses[ind] = inds[ind].fitness.fitness();
			}

			sortFitnessesAscending(fitnesses, indexes);
			
			int iteration;
			int left = inds.length;
			for (iteration = 1; left > 0; iteration++) {
				for (int i = 0; i < left; i++) {
					if (i < left / 2) {
						((SimpleProblemForm) (p_problem)).evaluate(state, inds[indexes[i]], subpop, 0);
						fitnesses[indexes[i]] += inds[indexes[i]].fitness.fitness();
					} else {
						((SimpleFitness) (inds[indexes[i]].fitness)).setFitness(state, fitnesses[indexes[i]] / iteration, false);
					}		
				}
				
				left /= 2;
				sortFitnessesAscending(fitnesses, indexes); //Assuming that evaluation returns positive value
			}
		}
	}

	private void sortFitnessesAscending(final float[] fitnesses, Integer[] indexes) {
		for (int i = 0; i < indexes.length; i++) {
			indexes[i] = i;
		}
		
		Arrays.sort(indexes, new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				float result = fitnesses[o2]-fitnesses[o1];
				if (result < 0) return -1;
				if (result > 0) return 1;
				else return 0;
			}});
	}
	
	@Override
	public boolean runComplete(EvolutionState state) {
		return false;
	}

}
