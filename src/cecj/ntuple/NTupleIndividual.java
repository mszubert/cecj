package cecj.ntuple;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class NTupleIndividual extends Individual {

	public static final String P_NTUPLE_INDIVIDUAL = "ntuple-ind";

	/**
	 * 
	 */
	private int[] positions;

	private double[] weights;

	@Override
	public boolean equals(Object ind) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object clone() {
		NTupleIndividual clone = (NTupleIndividual) (super.clone());
		clone.positions = positions.clone();
		clone.weights = weights.clone();

		return clone;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Parameter defaultBase() {
		return NTupleDefaults.base().push(P_NTUPLE_INDIVIDUAL);
	}

	public void reset(EvolutionState state, int thread) {

	}

	public void defaultMutate(EvolutionState state, int thread) {
		// TODO Auto-generated method stub

	}

	public void defaultCrossover(EvolutionState state, int thread, NTupleIndividual tupleIndividual) {
		// TODO Auto-generated method stub

	}

}
