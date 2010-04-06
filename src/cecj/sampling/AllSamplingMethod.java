package cecj.sampling;

import java.util.List;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class AllSamplingMethod extends SamplingMethod {

	@Override
	public List<Individual> sample(EvolutionState state, List<Individual> source) {
		return source;
	}

	public void setup(EvolutionState state, Parameter base) {
	}
}
