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
 * Represents the archive for Pareto-Coevolution paradigm where each test is viewed as an objective
 * in the sense of Multi-Objective Optimization.
 * 
 * This class provides methods for comparing individuals on the basis of their interactions outcomes
 * considered in the context of Pareto dominance relation (methods: <code>dominates</code>,
 * <code>isDominated</code>). According to the results of these comparisons a test can be found
 * useful if it proves that a particular candidate solution is not dominated by any other individual
 * in the archive. Such usefulness can be verified using the methods of this class (
 * <code>isUseful</code>, <code>findUsefulTest</code>). It provides methods for comparing
 * individuals on the basis of their interaction results.
 * 
 * @author Marcin Szubert
 * 
 */
public abstract class ParetoCoevolutionArchive extends CandidateTestArchive {

	/**
	 * Tries to find a test for which <code>candidate1</code> has better outcome than
	 * <code>candidate2<code>. It is needed for <code>candidate1</code> to be non-dominated and thus
	 * useful to be stored in the archive. If the test is found it makes distinction between
	 * candidates.
	 * 
	 * @param state
	 *            the current evolution state
	 * @param candidate1
	 *            the candidate which should appear to be better at some test
	 * @param candidate2
	 *            the reference candidate solution which is checked for dominating the candidate1
	 * @param tests
	 *            the list of test to be searched
	 * @return <code>null</code> if there is no such test
	 */
	protected Individual findUsefulTest(EvolutionState state, Individual candidate1,
			Individual candidate2, List<Individual> tests) {
		for (Individual test : tests) {
			if (problem.solves(state, candidate1, test) && !problem.solves(state, candidate2, test)) {
				return test;
			}
		}
		return null;
	}

	/**
	 * Checks if <code>candidate1</code> Pareto-dominates <code>candidate2</code> or both candidates
	 * are equal with respect to <code>tests</code>. One candidate is Pareto-dominated if it has
	 * better or equal outcomes for all <code>tests</code> and for at least one - strictly better.
	 * 
	 * @param state
	 *            the current evolution state
	 * @param candidate1
	 *            the candidate solution which is checked for dominating the other candidate
	 * @param candidate2
	 *            the candidate solutions which is checked for being dominated
	 * @param tests
	 *            the set of tests with respect to which dominance is checked
	 * @return <code>true</code> if <code>candidate1</code> dominates <code>candidate2</code> or
	 *         they are equal with respect to <code>tests</code>, <code>false</code> otherwise
	 */
	protected boolean dominatesOrEqual(EvolutionState state, Individual candidate1,
			Individual candidate2, List<Individual> tests) {
		return (findUsefulTest(state, candidate2, candidate1, tests) == null);
	}

	/**
	 * Checks if <code>candidate1</code> Pareto-dominates <code>candidate2</code> with respect to
	 * <code>tests</code>. One candidate is Pareto-dominated if it has better or equal outcomes for
	 * all <code>tests</code> and for at least one - strictly better.
	 * 
	 * @param state
	 *            the current evolution state
	 * @param candidate1
	 *            the candidate solution which is checked for dominating the other candidate
	 * @param candidate2
	 *            the candidate solutions which is checked for being dominated
	 * @param tests
	 *            the set of tests with respect to which dominance is checked
	 * @return <code>true</code> if <code>candidate1</code> dominates <code>candidate2</code> with
	 *         respect to <code>tests</code>, <code>false</code> otherwise
	 */
	protected boolean dominates(EvolutionState state, Individual candidate1, Individual candidate2,
			List<Individual> tests) {
		if ((findUsefulTest(state, candidate2, candidate1, tests) == null)
				&& (findUsefulTest(state, candidate1, candidate2, tests) != null)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Verifies if <code>candidate</code> is dominated by any candidate solution present in
	 * <code>cArchive</code> with respect to tests in <code>tArchive</code> or if another solution
	 * has equivalent outcomes for all tests in <code>tArchive</code>.
	 * 
	 * If the archive contains a <code>candidate</code>, the outcome is <code>true</code>.
	 * 
	 * @param state
	 *            the current evolution state
	 * @param candidate
	 *            the candidate which is checked for being dominated; it should not be in the
	 *            archive
	 * @param cArchive
	 *            the archive of candidate solutions
	 * @param tArchive
	 *            the archive of tests
	 * @return <code>true</code> if <code>candidate</code> is dominated by or equal to another
	 *         solution in the archive
	 */
	protected boolean isDominatedOrEqual(EvolutionState state, Individual candidate,
			List<Individual> cArchive, List<Individual> tArchive) {
		for (Individual otherCandidate : cArchive) {
			if (dominatesOrEqual(state, otherCandidate, candidate, tArchive)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Verifies if <code>candidate</code> is dominated by any candidate solution present in
	 * <code>cArchive</code> with respect to tests in <code>tArchive</code>.
	 * 
	 * @param state
	 *            the current evolution state
	 * @param candidate
	 *            the candidate which is checked for being dominated
	 * @param cArchive
	 *            the archive of candidate solutions
	 * @param tArchive
	 *            the archive of tests
	 * @return <code>true</code> if <code>candidate</code> is dominated by any solution in the
	 *         archive
	 */
	protected boolean isDominated(EvolutionState state, Individual candidate,
			List<Individual> cArchive, List<Individual> tArchive) {
		for (Individual otherCandidate : cArchive) {
			if (dominates(state, otherCandidate, candidate, tArchive)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if <code>newCandidate</code> is useful with respect to current archives of candidates
	 * and tests and new population of generated tests. A candidate solutions is considered
	 * <i>useful</i> if it is non-dominated and no solution is already present with identical
	 * outcomes for all tests.
	 * 
	 * @param state
	 *            the current evolution state
	 * @param newCandidate
	 *            the candidate whose usefulness is verified
	 * @param cArchive
	 *            the archive of candidate solutions
	 * @param tArchive
	 *            the archive of tests
	 * @param newTests
	 *            the population of new tests
	 * @return <code>true</code> if <code>newCandidate</code> is <i>useful</i> and should be
	 *         included in the archive.
	 */
	protected boolean isUseful(EvolutionState state, Individual newCandidate,
			List<Individual> cArchive, List<Individual> tArchive, List<Individual> newTests) {
		if (isDominatedOrEqual(state, newCandidate, cArchive, tArchive)
				&& isDominatedOrEqual(state, newCandidate, cArchive, newTests)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Finds tests which are needed to prove usefulness of given <code>newCandidate</code>. These
	 * tests should make distinctions between existing solutions in the <code>cArchive</code> and
	 * the new one. If the <code>newCandidate</code> solution is non-dominated without adding any of
	 * <code>newTests</code> to the test archive, the returned list is empty.
	 * 
	 * @param state
	 *            the current evolution state
	 * @param newCandidate
	 *            the candidate solution whose non-dominated status is to be guaranteed
	 * @param cArchive
	 *            the archive of candidate solutions
	 * @param tArchive
	 *            the archive of tests
	 * @param newTests
	 *            the population of new tests
	 * @return the list of test individuals from <code>newTests</code> which should be added to the
	 *         <code>tArchive</code> to make <code>newCandidate</code> non-dominated. If even adding
	 *         all test is not sufficient, <code>null</code> is returned.
	 */
	protected List<Individual> findUsefulTests(EvolutionState state, Individual newCandidate,
			List<Individual> cArchive, List<Individual> tArchive, List<Individual> newTests) {
		List<Individual> selected = new ArrayList<Individual>();
		List<Individual> rest = new ArrayList<Individual>(newTests);

		for (Individual candidate : cArchive) {
			if (dominatesOrEqual(state, candidate, newCandidate, tArchive)
					&& dominatesOrEqual(state, candidate, newCandidate, selected)) {
				Individual test = findUsefulTest(state, newCandidate, candidate, rest);
				if (test == null) {
					return null;
				} else {
					selected.add(test);
					rest.remove(test);
				}
			}
		}

		return selected;
	}
}
