/*
  Copyright 2009 by Marcin Szubert
  Licensed under the Academic Free License version 3.0
 */

package cecj.eval;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

/**
 * Wrapper for <code>CoevolutionaryEvaluator</code> performing an additional learning phase before
 * actual evaluation.
 * 
 * The only role of this class is improving each individual of the population by running a specific
 * temporal difference learning (TDL) algorithm before the evaluation. Since the exact
 * implementation of this algorithm depends on the problem, evaluator delegates the learning task to
 * the provided <code>TDLImprover</code> interface realization. At the beginning of evolutionary
 * process individuals may require some preparation for running TDL. For this reason, appropriate
 * interface methods are invoked before the first evaluation. Clearly, not every problem can be
 * approached by reinforcement learning paradigm so this class has also a limited scope of
 * applicability.
 * 
 * Note that this evaluator realizes the Coevolutionary Reinforcement Learning idea.
 * 
 * @author Marcin Szubert
 * @see TDLImprover
 * 
 */
public class TDLImprovingEvaluator extends CoevolutionaryEvaluator {

	private static final String P_INNER_EVALUATOR = "inner-evaluator";
	private static final String P_TDL_IMPROVER = "tdl-improver";
	private static final String P_TDL_FREQUENCY = "tdl-frequency";
	private static final String P_TDL_PREPARE = "tdl-prepare";
	
	private CoevolutionaryEvaluator innerEvaluator;
	private TDLImprover temporalDifferenceImprover;
	private boolean firstEvaluation = true;
	private int tdlFrequency;
	private boolean tdlPrepare;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		Parameter innerEvaluatorParam = base.push(P_INNER_EVALUATOR);
		innerEvaluator = (CoevolutionaryEvaluator) (state.parameters.getInstanceForParameter(
				innerEvaluatorParam, null, CoevolutionaryEvaluator.class));
		innerEvaluator.setup(state, innerEvaluatorParam);

		Parameter tdlImproverParam = base.push(P_TDL_IMPROVER);
		temporalDifferenceImprover = (TDLImprover) (state.parameters.getInstanceForParameter(
				tdlImproverParam, null, TDLImprover.class));
		temporalDifferenceImprover.setup(state, tdlImproverParam);

		Parameter tdlImprovingFrequency = base.push(P_TDL_FREQUENCY);
		tdlFrequency = state.parameters.getIntWithDefault(tdlImprovingFrequency, null, 1);
		
		Parameter tdlPrepareParam = base.push(P_TDL_PREPARE);
		tdlPrepare = state.parameters.getBoolean(tdlPrepareParam, null, false);
	}

	@Override
	public void evaluatePopulation(EvolutionState state) {
		if (firstEvaluation && tdlPrepare) {
			for (int subpop = 0; subpop < numSubpopulations; subpop++) {
				Individual[] inds = state.population.subpops[subpop].individuals;
				for (Individual ind : inds) {
					temporalDifferenceImprover.prepareForImproving(state, ind);
				}
			}
			firstEvaluation = false;
		}

		if ((state.generation % tdlFrequency) == 0) {
			for (int subpop = 0; subpop < numSubpopulations; subpop++) {
				Individual[] inds = state.population.subpops[subpop].individuals;
				for (Individual ind : inds) {
					temporalDifferenceImprover.improve(state, ind);
				}
			}
		}

		innerEvaluator.evaluatePopulation(state);
	}
}
