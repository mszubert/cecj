package cecj.ntuple;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class NTupleIndividual extends Individual {

	public static final String P_NTUPLE_INDIVIDUAL = "ntuple-ind";

	/**
	 * 
	 */
	private int[][] positions;

	private double[][] weights;

	/**
	 * This method is called only once - on a prototype individual stored in the species class.
	 */
	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);
		
		if (!(species instanceof NTupleSpecies)) {
            state.output.fatal("NTupleIndividual requires a NTupleSpecies", base, defaultBase());
		}
		
        NTupleSpecies s = (NTupleSpecies) species;
        positions = new int[s.getNumTuples()][];
        weights = new double[s.getNumTuples()][];
	}

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
		//generate random tuples shapes
	}

	public void defaultMutate(EvolutionState state, int thread) {
		// TODO Auto-generated method stub

	}

	public void defaultCrossover(EvolutionState state, int thread, NTupleIndividual tupleIndividual) {
		// TODO Auto-generated method stub

	}

}
