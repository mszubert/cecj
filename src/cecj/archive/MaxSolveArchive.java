/*
  Copyright 2009 by Marcin Szubert
  Licensed under the Academic Free License version 3.0
 */

package cecj.archive;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cecj.utils.EquivalenceComparator;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

/**
 * The simplest type of candidate-test archive.
 * 
 * During the first step of the <code>submit</code> method, new candidates and tests are added to
 * corresponding archives; then duplicate candidate solutions are eliminated (two individuals are
 * considered equal if for each test in the archive they have identical outcomes). After sorting
 * candidates by number of solved tests, the best <code>archive-size</code> individuals are kept in
 * the archive. The last step is elimination of unsolved and duplicated (with respect to the
 * outcomes against archival candidates) tests.
 * 
 * Since duplicates removal is a common task, a purpose-built <code>EquivalenceComparator</code>
 * interface is used for defining custom equivalence criteria.
 * 
 * @author Marcin Szubert
 * 
 */
public class MaxSolveArchive extends CandidateTestArchive {

	private static final String P_ARCHIVE_SIZE = "archive-size";

	private int archiveSize;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		Parameter archiveSizeParameter = base.push(P_ARCHIVE_SIZE);
		archiveSize = state.parameters.getInt(archiveSizeParameter, null, 1);
		if (archiveSize <= 0) {
			state.output.fatal("Archive size must be > 0\n");
		}
	}

	@Override
	protected void submit(EvolutionState state, List<Individual> candidates,
			List<Individual> cArchive, List<Individual> tests, List<Individual> tArchive) {
		cArchive.addAll(candidates);
		tArchive.addAll(tests);

		eliminateDuplicates(cArchive, new CandidateEquivalenceComparator(state, tArchive));
		Map<Individual, Integer> numSolved = countSolved(state, cArchive, tArchive);
		Collections.sort(cArchive, new NumberSolvedComparator(numSolved));

		if (cArchive.size() > archiveSize) {
			cArchive.subList(archiveSize, cArchive.size()).clear();
		}

		eliminateUnsolvedTests(state, cArchive, tArchive);
		eliminateDuplicates(tArchive, new TestEquivalenceComparator(state, cArchive));
	}

	private void eliminateDuplicates(List<Individual> archive,
			EquivalenceComparator<Individual> comparator) {
		for (int ind1 = 0; ind1 < archive.size(); ind1++) {
			for (int ind2 = archive.size(); ind2 > ind1; ind2--) {
				if (comparator.equal(archive.get(ind1), archive.get(ind2))) {
					archive.remove(ind2);
				}
			}
		}
	}

	private void eliminateUnsolvedTests(EvolutionState state, List<Individual> cArchive,
			List<Individual> tArchive) {
		for (int test = 0; test < tArchive.size(); test++) {
			if (!isSolved(state, tArchive.get(test), cArchive)) {
				tArchive.remove(test);
			}
		}
	}

	private boolean isSolved(EvolutionState state, Individual test, List<Individual> candidates) {
		for (Individual candidate : candidates) {
			if (problem.solves(state, candidate, test)) {
				return true;
			}
		}
		return false;
	}

	private Map<Individual, Integer> countSolved(EvolutionState state, List<Individual> candidates,
			List<Individual> tests) {
		Map<Individual, Integer> result = new HashMap<Individual, Integer>();
		for (Individual candidate : candidates) {
			int countTest = 0;
			for (Individual test : tests) {
				if (problem.solves(state, candidate, test)) {
					countTest++;
				}
			}
			result.put(candidate, countTest);
		}
		return result;
	}

	private class NumberSolvedComparator implements Comparator<Individual> {
		private Map<Individual, Integer> solved;

		public NumberSolvedComparator(Map<Individual, Integer> solved) {
			this.solved = solved;
		}

		public int compare(Individual o1, Individual o2) {
			if (solved.get(o1) > solved.get(o2)) {
				return -1;
			} else if (solved.get(o1) > solved.get(o2)) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	private class CandidateEquivalenceComparator implements EquivalenceComparator<Individual> {
		private EvolutionState state;
		private List<Individual> tests;

		public CandidateEquivalenceComparator(EvolutionState state, List<Individual> tests) {
			this.state = state;
			this.tests = tests;
		}

		public boolean equal(Individual o1, Individual o2) {
			for (Individual test : tests) {
				int result1 = problem.test(state, o1, test);
				int result2 = problem.test(state, o2, test);
				if (result1 != result2) {
					return false;
				}
			}
			return false;
		}
	}

	private class TestEquivalenceComparator implements EquivalenceComparator<Individual> {
		private EvolutionState state;
		private List<Individual> candidates;

		public TestEquivalenceComparator(EvolutionState state, List<Individual> candidates) {
			this.state = state;
			this.candidates = candidates;
		}

		public boolean equal(Individual o1, Individual o2) {
			for (Individual candidate : candidates) {
				int result1 = problem.test(state, candidate, o1);
				int result2 = problem.test(state, candidate, o2);
				if (result1 != result2) {
					return false;
				}
			}
			return false;
		}
	}
}