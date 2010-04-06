package cecj.statistics;

import java.util.List;

import cecj.interaction.InteractionResult;

import ec.EvolutionState;
import ec.vector.DoubleVectorIndividual;

public class SimpleCoevolutionaryStatistics extends CoevolutionaryStatistics {

	@Override
	public void printInteractionResults(EvolutionState state,
			List<List<InteractionResult>> results, int subpop) {

		System.out.println("Subpopulation " + subpop + " interactions result:");

		for (int i = 0; i < results.size(); i++) {
			System.out.print("Individual " + i + " : ");
			for (int j = 0; j < results.get(i).size(); j++) {
				System.out.print(results.get(i).get(j) + " ");
			}
			System.out.println(state.population.subpops[subpop].individuals[i].fitness
				.fitnessToStringForHumans());
		}

		System.out.println("");
	}

	public void printIndividualGenome(DoubleVectorIndividual ind) {
		for (int g = 0; g < ind.genomeLength(); g++) {
			System.out.print((int) ind.genome[g] + " ");
		}
		System.out.println("");
	}
}
