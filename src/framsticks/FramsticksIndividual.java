package framsticks;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class FramsticksIndividual extends Individual {

	private static final String P_FRAMSTICKS_INDIVIDUAL = "framsticks-ind";
	private static final String P_INIT_TYPE = "init-type"; 

	public String genotype;

	@Override
	public boolean equals(Object ind) {
		if (!this.getClass().equals(ind.getClass())) {
			return false;
		}

		FramsticksIndividual framsticksIndividual = (FramsticksIndividual) ind;
		return genotype.equals(framsticksIndividual.genotype);
	}

	@Override
	public int hashCode() {
		int hash = this.getClass().hashCode();
		hash = (hash << 1 | hash >>> 31) ^ genotype.hashCode();
		return hash;
	}

	public Parameter defaultBase() {
		return FramsticksDefaults.base().push(P_FRAMSTICKS_INDIVIDUAL);
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		
		Parameter initTypeParam = base.push(P_INIT_TYPE);
		int initializationType = state.parameters.getIntWithDefault(initTypeParam, null, 0);
		genotype = FramsticksUtils.getInstance(state).getNewGenotype(initializationType);
	}

	@Override
	public Object clone() {
		FramsticksIndividual clone = (FramsticksIndividual) (super.clone());
		clone.genotype = genotype;
		return clone;
	}

	public void mutate(final EvolutionState state) {
		genotype = FramsticksUtils.getInstance(state).mutateGenotype(genotype);
	}

	public void crossover(final EvolutionState state, FramsticksIndividual other) {
		genotype = FramsticksUtils.getInstance(state).crossoverGenotypes(genotype, other.genotype);
	}

	@Override
	public String genotypeToStringForHumans() {
		return genotype;
	}

	@Override
	public void writeGenotype(EvolutionState state, DataOutput dataOutput) throws IOException {
		dataOutput.writeUTF(genotype);
	}

	@Override
	public void readGenotype(EvolutionState state, DataInput dataInput) throws IOException {
		genotype = dataInput.readUTF();
	}
}