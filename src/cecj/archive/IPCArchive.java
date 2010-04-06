/*
  Copyright 2009 by Marcin Szubert
  Licensed under the Academic Free License version 3.0
 */

package cecj.archive;

import java.util.ArrayList;
import java.util.List;

import ec.EvolutionState;
import ec.Individual;

/**
 * Incremental Pareto-Coevolution Archive.
 * 
 * For each of the submitted candidates it is checked if any useful test exists in the archive or
 * currently submitted population, that proves the candidate is non-dominated. If such test is
 * found, the considered individual is added to the archive while all individuals that it dominates
 * are removed.
 * 
 * The implementation relies heavily on the methods provided by the superclass Ð
 * <code>PareroCoevolutionArchive</code>.
 * 
 * @author Marcin Szubert
 * 
 */
public class IPCArchive extends ParetoCoevolutionArchive {

	@Override
	protected void submit(EvolutionState state, List<Individual> candidates,
			List<Individual> cArchive, List<Individual> tests, List<Individual> tArchive) {
		List<Individual> testsCopy = new ArrayList<Individual>(tests);
		List<Individual> usefulTests;

		/*
		 * Is is a right sequence of operations? Dominated candidates are eliminated before new
		 * tests are added to the test archive. New tests can make yet another candidate dominated..
		 */
		for (Individual candidate : candidates) {
			if (isUseful(state, candidate, cArchive, tArchive, testsCopy)) {
				usefulTests = findUsefulTests(state, candidate, cArchive, tArchive, testsCopy);
				eliminateDominatedCandidates(state, candidate, cArchive, tArchive);

				cArchive.add(candidate);
				tArchive.addAll(usefulTests);
				testsCopy.removeAll(usefulTests);
			}
		}
	}

	private void eliminateDominatedCandidates(EvolutionState state, Individual candidate,
			List<Individual> candidateArchive, List<Individual> testArchive) {
		for (int c = candidateArchive.size() - 1; c >= 0; c--) {
			if (dominates(state, candidate, candidateArchive.get(c), testArchive)) {
				candidateArchive.remove(c);
			}
		}
	}
}
