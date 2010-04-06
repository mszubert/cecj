/*
  Copyright 2009 by Marcin Szubert
  Licensed under the Academic Free License version 3.0
 */

package cecj.archive;

import java.util.List;

import cecj.eval.ArchivingCoevolutionaryEvaluator;
import cecj.interaction.LearnerTeacherInteractionScheme;
import cecj.interaction.LearnerTeacherInteractionScheme.Role;
import cecj.problems.TestBasedProblem;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

/**
 * Represents archive dedicated for learner-teacher coevolution, where two distinct roles can be
 * distinguished. The first one (leaner) represents candidate solution to the problem and it is
 * rewarded for performing well on tests which are representatives of the second role (teacher).
 * 
 * It requires <code>LearnerTeacherInteractionScheme</code> and <code>TestBasedProblem</code>
 * definition.
 * 
 * @author Marcin Szubert
 * 
 */
public abstract class CandidateTestArchive extends CoevolutionaryArchive {

	/**
	 * The interaction scheme between coevolving populations.
	 */
	protected LearnerTeacherInteractionScheme interactionScheme;

	/**
	 * Problem which determines the interaction form between candidates and tests.
	 */
	protected TestBasedProblem problem;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		ArchivingCoevolutionaryEvaluator e = (ArchivingCoevolutionaryEvaluator) state.evaluator;
		if (!(e.getInteractionScheme() instanceof LearnerTeacherInteractionScheme)) {
			state.output
					.fatal("This archive can be used only with learner-teacher interaction scheme.\n");
		}
		interactionScheme = (LearnerTeacherInteractionScheme) e.getInteractionScheme();
		problem = e.getProblem();
	}

	@Override
	public void submit(EvolutionState state) {
		List<Integer> candidatePops = interactionScheme.getSubpopulationIndices(Role.LEARNER);
		List<Integer> testPops = interactionScheme.getSubpopulationIndices(Role.TEACHER);
		for (int candidatePop : candidatePops) {
			List<Individual> cArchive = getArchive(state, candidatePop);
			List<Individual> candidates = getIndividuals(state, candidatePop);
			for (int testPop : testPops) {
				List<Individual> tArchive = getArchive(state, testPop);
				List<Individual> tests = getIndividuals(state, testPop);
				submit(state, candidates, cArchive, tests, tArchive);
			}
		}
	}

	/**
	 * Submits new candidates and tests to the archives. The role of the archive is to determine
	 * which individuals of both roles are useful and should be kept in the archive. It is possible
	 * also to remove some existing individual because new one is strictly better.
	 * 
	 * @param state
	 *            the current evolution state
	 * @param candidates
	 *            the list of newly generated candidate solutions
	 * @param cArchive
	 *            the list of archival candidate solutions
	 * @param tests
	 *            the list of newly generated tests
	 * @param tArchive
	 *            the list of archival tests
	 */
	protected abstract void submit(EvolutionState state, List<Individual> candidates,
			List<Individual> cArchive, List<Individual> tests, List<Individual> tArchive);
}
