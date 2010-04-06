package cecj.sampling;

import java.util.ArrayList;
import java.util.List;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class NullSamplingMethod extends SamplingMethod {

	@Override
	public List<Individual> sample(EvolutionState state, List<Individual> source) {
		return new ArrayList<Individual>();
	}

	public void setup(EvolutionState state, Parameter base) {
	}

}
