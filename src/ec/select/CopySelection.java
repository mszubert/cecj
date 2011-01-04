package ec.select;

import ec.EvolutionState;
import ec.SelectionMethod;
import ec.util.Parameter;

public class CopySelection extends SelectionMethod {

	public static final String P_COPY = "copy";

	public int currentIndividual;

	@Override
	public void prepareToProduce(final EvolutionState s, final int subpopulation, final int thread) {
		currentIndividual = 0;
	}

	@Override
	public int produce(int subpopulation, EvolutionState state, int thread) {
		int result = currentIndividual % state.population.subpops[subpopulation].individuals.length;
		currentIndividual++;
		return result;
	}

	public Parameter defaultBase() {
		return SelectDefaults.base().push(P_COPY);
	}

}
