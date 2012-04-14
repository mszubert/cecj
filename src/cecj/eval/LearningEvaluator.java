/*
  Copyright 2009 by Marcin Szubert
  Licensed under the Academic Free License version 3.0
 */

package cecj.eval;

import ec.Evaluator;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

/**
 * Wrapper for <code>CoevolutionaryEvaluator</code> performing an additional
 * learning phase before actual evaluation.
 * 
 * The only role of this class is improving each individual of the population by
 * running a specific temporal difference learning (TDL) algorithm before the
 * evaluation. Since the exact implementation of this algorithm depends on the
 * problem, evaluator delegates the learning task to the provided
 * <code>TDLImprover</code> interface realization. At the beginning of
 * evolutionary process individuals may require some preparation for running
 * TDL. For this reason, appropriate interface methods are invoked before the
 * first evaluation. Clearly, not every problem can be approached by
 * reinforcement learning paradigm so this class has also a limited scope of
 * applicability.
 * 
 * Note that this evaluator realizes the Coevolutionary Reinforcement Learning
 * idea.
 * 
 * @author Marcin Szubert
 * @see LearningImprover
 * 
 */
public class LearningEvaluator extends Evaluator {

	private static final String P_INNER_EVALUATOR = "inner-evaluator";
	private static final String P_LEARNING_IMPROVER = "learning-improver";
	private static final String P_LEARNING_FREQUENCY = "learning-frequency";
	private static final String P_LEARNING_PREPARE = "learning-prepare";
	private static final String P_LEARNING_POP = "learning-pop";

	private Evaluator innerEvaluator;
	private LearningImprover learningImprover;
	private boolean firstEvaluation = true;
	private int learningFrequency;
	private boolean learningPrepare;
	private int learningPopulation;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		Parameter innerEvaluatorParam = base.push(P_INNER_EVALUATOR);
		innerEvaluator = (Evaluator) (state.parameters.getInstanceForParameter(innerEvaluatorParam,
				null, Evaluator.class));
		innerEvaluator.setup(state, innerEvaluatorParam);

		Parameter tdlImproverParam = base.push(P_LEARNING_IMPROVER);
		learningImprover = (LearningImprover) (state.parameters.getInstanceForParameter(
				tdlImproverParam, null, LearningImprover.class));
		learningImprover.setup(state, tdlImproverParam);

		Parameter tdlImprovingFrequency = base.push(P_LEARNING_FREQUENCY);
		learningFrequency = state.parameters.getIntWithDefault(tdlImprovingFrequency, null, 1);

		Parameter tdlPrepareParam = base.push(P_LEARNING_PREPARE);
		learningPrepare = state.parameters.getBoolean(tdlPrepareParam, null, false);
		
		Parameter learningPopParam = base.push(P_LEARNING_POP);
		learningPopulation = state.parameters.getIntWithDefault(learningPopParam, null, 0);
		
	}

	@Override
	public void evaluatePopulation(EvolutionState state) {
		if (firstEvaluation && learningPrepare) {
			Individual[] inds = state.population.subpops[learningPopulation].individuals;
			for (Individual ind : inds) {
				learningImprover.prepareForImproving(state, ind);
			}
			firstEvaluation = false;
		}

		if ((state.generation % learningFrequency) == 0) {
			Individual[] inds = state.population.subpops[learningPopulation].individuals;
			for (Individual ind : inds) {
				learningImprover.improve(state, ind);
			}
		}

		innerEvaluator.evaluatePopulation(state);
	}

	@Override
	public boolean runComplete(EvolutionState state) {
		return false;
	}
}
