/*
  Copyright 2009 by Marcin Szubert
  Licensed under the Academic Free License version 3.0
 */

package cecj.archive;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

/**
 * Layered Pareto-Coevolution Archive.
 * 
 * This archive is a modified version of the IPCA archive. While the original one can grow
 * indefinitely (tests are never removed from the archive), this type of archive limits the maximum
 * number of stored individuals. However, this goal is achieved for the price of reducing the
 * reliability of the algorithm. After appending non-dominated candidate solutions and useful tests
 * to appropriate archives, <code>maintainLayers</code> and <code>updateTestArchive</code> methods
 * are invoked in order to decrease the amount of used memory. The first one checks which candidate
 * solutions belong to the first <code>num-layers</code> Pareto layers and keeps them in the
 * archive. The second retains only these tests which make distinctions between neighboring layers.
 * 
 * @author Marcin Szubert
 * 
 */
public class LAPCArchive extends ParetoCoevolutionArchive {

	private static final String P_NUM_LAYERS = "num-layers";

	private int numLayers;

	private List<List<Individual>> layers;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		Parameter numLayersParameter = base.push(P_NUM_LAYERS);
		numLayers = state.parameters.getInt(numLayersParameter, null, 1);
		if (numLayers <= 0) {
			state.output.fatal("Number of LAPCA layers must be > 0.\n");
		}

		layers = new ArrayList<List<Individual>>(numLayers);
	}

	/*
	 * It is implemented in a IPCA-like way. Another method is to extend both existing archives by
	 * new individuals, then find first n layers of candidates with respect to all tests in the
	 * archive and in the population and finally select necessary tests making distinctions between
	 * layers.
	 */
	@Override
	protected void submit(EvolutionState state, List<Individual> candidates,
			List<Individual> cArchive, List<Individual> tests, List<Individual> tArchive) {
		List<Individual> testsCopy = new ArrayList<Individual>(tests);
		List<Individual> usefulTests;

		for (Individual candidate : candidates) {
			if (isUseful(state, candidate, cArchive, tArchive, testsCopy)) {
				usefulTests = findUsefulTests(state, candidate, cArchive, tArchive, testsCopy);

				cArchive.add(candidate);
				tArchive.addAll(usefulTests);
				testsCopy.removeAll(usefulTests);
			}
		}

		maintainLayers(state, cArchive, tArchive);
		updateTestArchive(state, tArchive);
	}

	private void updateTestArchive(EvolutionState state, List<Individual> tArchive) {
		Set<Individual> tset = new HashSet<Individual>();
		tset.addAll(findDistinguishingTests(state, layers.get(0), layers.get(0), tArchive));
		for (int l = 1; l < numLayers; l++) {
			tset.addAll(findDistinguishingTests(state, layers.get(l - 1), layers.get(l), tArchive));
		}

		tArchive.clear();
		tArchive.addAll(tset);
	}

	private List<Individual> findDistinguishingTests(EvolutionState state, List<Individual> layer1,
			List<Individual> layer2, List<Individual> tests) {
		List<Individual> distinguishingTests = new ArrayList<Individual>();
		for (Individual candidate1 : layer1) {
			for (Individual candidate2 : layer2) {
				if (candidate1.equals(candidate2))
					continue;
				Individual test = findUsefulTest(state, candidate1, candidate2, tests);
				if ((test != null) && (!distinguishingTests.contains(test))) {
					distinguishingTests.add(test);
				}
			}
		}
		return distinguishingTests;
	}

	private void maintainLayers(EvolutionState state, List<Individual> cArchive,
			List<Individual> tArchive) {
		List<Individual> cArchiveCopy = new ArrayList<Individual>(cArchive);
		for (int layer = 0; layer < numLayers; layer++) {
			List<Individual> frontPareto = findNonDominatedCandidates(state, cArchiveCopy, tArchive);
			layers.set(layer, frontPareto);
			cArchiveCopy.removeAll(frontPareto);
		}
		cArchive.removeAll(cArchiveCopy);
	}

	private List<Individual> findNonDominatedCandidates(EvolutionState state,
			List<Individual> cArchive, List<Individual> tArchive) {
		List<Individual> result = new ArrayList<Individual>();
		for (Individual candidate : cArchive) {
			if (!isDominated(state, candidate, cArchive, tArchive)) {
				result.add(candidate);
			}
		}
		return result;
	}

}
