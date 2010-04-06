package cecj.sampling;

import java.util.ArrayList;
import java.util.List;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

/**
 * A random sampling method samples randomly with repetitions given individuals collection. It can
 * be used for so called "k-random opponents" evaluation scheme.
 * 
 * @author Marcin Szubert
 * 
 */
public class RandomSamplingMethod extends SamplingMethod {

	private static final String P_SAMPLE_SIZE = "sample-size";

	private int sampleSize;

	public void setup(EvolutionState state, Parameter base) {
		Parameter sampleSizeParameter = base.push(P_SAMPLE_SIZE);
		sampleSize = state.parameters.getIntWithDefault(sampleSizeParameter, null, 1);
	}

	@Override
	public List<Individual> sample(EvolutionState state, List<Individual> source) {
		List<Individual> result = new ArrayList<Individual>(sampleSize);
		if (!source.isEmpty()) {
			for (int i = 0; i < sampleSize; i++) {
				result.add(source.get(state.random[0].nextInt(source.size())));
			}
		}

		return result;
	}
}