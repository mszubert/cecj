/*
  Copyright 2009 by Marcin Szubert
  Licensed under the Academic Free License version 3.0
 */

package cecj.eval;

import cecj.problems.CoevolutionaryProblem;
import cecj.problems.TestBasedProblem;
import ec.Evaluator;
import ec.EvolutionState;
import ec.util.Parameter;

/**
 * The base class for all coevolutionary evaluators.
 * 
 * This class is responsible for verifying if problem and population are consistent with the idea of
 * coevolution.
 * 
 * @author Marcin Szubert
 * 
 */
public abstract class CoevolutionaryEvaluator extends Evaluator {

	private static final String P_POP = "pop";
	private static final String P_SIZE = "subpops";

	/**
	 * Number of subpopulations.
	 */
	protected int numSubpopulations;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		if (!(p_problem instanceof CoevolutionaryProblem)) {
			state.output
					.fatal("Coevolutionary evaluator is dedicated to coevolutionary problems\n");
		}

		Parameter popSizeParameter = new Parameter(P_POP).push(P_SIZE);
		numSubpopulations = state.parameters.getInt(popSizeParameter, null, 0);
		if (numSubpopulations <= 0) {
			state.output.fatal("Population size must be > 0.\n", popSizeParameter);
		}
	}

	@Override
	public boolean runComplete(EvolutionState state) {
		return false;
	}

	/**
	 * Returns a problem that forms a base of evaluation of the individuals.
	 * 
	 * @return the instance of test-based problem used by this evaluator
	 */
	public TestBasedProblem getProblem() {
		return (TestBasedProblem) p_problem;
	}
}
