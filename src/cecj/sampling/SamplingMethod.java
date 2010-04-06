package cecj.sampling;

import java.util.Arrays;
import java.util.List;

import ec.EvolutionState;
import ec.Individual;
import ec.Setup;

/**
 * A method of sampling a collection of individuals. Defines methods used by
 * <code>CoevolutionaryEvaluator</code> to choose opponents for evaluating concrete individual.
 * 
 * @author Marcin Szubert
 * 
 */
public abstract class SamplingMethod implements Setup {

	/**
	 * Samples the subpopulation specified by <code>subpop</code> index and puts the results into
	 * the <code>inds</code> array.
	 * 
	 * @param state
	 *            current evolution state
	 * @param subpop
	 *            the index of sampled subpopulation
	 * @param inds
	 *            the array of sampling results
	 */
	public List<Individual> sample(EvolutionState state, Individual[] source) {
		return this.sample(state, Arrays.asList(source));
	}

	/**
	 * Samples given list of individuals and puts the results int the <code>inds</code> array.
	 * 
	 * @param state
	 *            current evolution state
	 * @param list
	 *            the list of individuals to be sampled
	 * @param inds
	 *            the array of sampling results
	 */
	public abstract List<Individual> sample(EvolutionState state, List<Individual> source);
}
