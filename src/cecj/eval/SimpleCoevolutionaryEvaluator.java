/*
  Copyright 2009 by Marcin Szubert
  Licensed under the Academic Free License version 3.0
 */

package cecj.eval;

import java.util.ArrayList;
import java.util.List;

import cecj.fitness.FitnessAggregateMethod;
import cecj.interaction.InteractionScheme;
import cecj.sampling.SamplingMethod;
import cecj.statistics.CoevolutionaryStatistics;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

/**
 * 
 * Simple coevolutionary evaluator without any additional mechanisms.
 * 
 * This is the simplest implementation of conventional coevolutionary evaluation where interactions
 * between individuals can be performed in an arbitrary order. However, the character and the scope
 * of interactions can be different Ð it is defined by instantiating appropriate
 * <code>InteractionScheme</code> subclass. The evaluation proceeds as follows. First of all, a
 * reference set of opponent individuals is selected from each subpopulation. This task is handled
 * by a <code>SamplingMethod</code> realization. Distinct sampling methods can be used by different
 * subpopulations. Next, each subpopulation individuals are confronted with previously selected
 * opponents from subpopulations pointed by the concrete <code>InteractionScheme</code> class.
 * Finally, <code>FitnessAggregateMethod</code> is responsible for aggregating outcomes of these
 * confrontations into a single fitness measure which is used later during selection stage of the
 * evolutionary process. It evaluates individuals according to the outcomes of its interactions with
 * other individuals. Interactions are not restricted to intraspecific or interspecific type, i.e.
 * opponents can be chosen from the same population or any other coevolving population.
 * 
 * In contrast to <code>TournamentCoevolutionaryEvaluator</code> all interactions can be simulated
 * in any order. There are no sequential dependencies between interactions.
 * 
 * @author Marcin Szubert
 * 
 */
public class SimpleCoevolutionaryEvaluator extends CoevolutionaryEvaluator {

	protected static final String P_SUBPOP = "subpop";
	private static final String P_STATISTICS = "statistics";
	private static final String P_FITNESS_METHOD = "fitness-method";
	private static final String P_POP_INDS_WEIGHT = "pop-inds-weight";
	private static final String P_SAMPLING_METHOD = "sampling-method";
	private static final String P_INTERACTION_SCHEME = "interaction-scheme";
	private static final String P_EVALUATIONS_LIMIT = "evaluations-limit";

	/**
	 * Tests used to interact with candidate solutions.
	 */
	protected List<List<Individual>> opponents;

	/**
	 * Methods of sampling the opponents from particular populations.
	 */
	protected SamplingMethod[] samplingMethod;

	/**
	 * The Method of aggregating multiple interaction outcomes into single value.
	 */
	protected FitnessAggregateMethod[] fitnessAggregateMethod;

	/**
	 * Specifies how interactions between populations look like.
	 */
	protected InteractionScheme interactionScheme;

	/**
	 * Gathers statistics about evaluation stage.
	 */
	protected CoevolutionaryStatistics statistics;

	/**
	 * Indicates how important are population opponents with respect to archival opponents.
	 */
	private int popIndsWeight;

	protected int evaluationsLimit;

	protected int evaluationsPerformed;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		Parameter interactionSchemeParam = base.push(P_INTERACTION_SCHEME);
		interactionScheme = (InteractionScheme) (state.parameters.getInstanceForParameter(
				interactionSchemeParam, null, InteractionScheme.class));
		interactionScheme.setup(state, interactionSchemeParam);

		Parameter popIndsWeightParam = base.push(P_POP_INDS_WEIGHT);
		popIndsWeight = state.parameters.getIntWithDefault(popIndsWeightParam, null, 1);

		Parameter evaluationsLimitParam = base.push(P_EVALUATIONS_LIMIT);
		evaluationsLimit = state.parameters.getIntWithDefault(evaluationsLimitParam, null,
				Integer.MAX_VALUE);

		Parameter statisticsParam = base.push(P_STATISTICS);
		if (state.parameters.exists(statisticsParam)) {
			statistics = (CoevolutionaryStatistics) (state.parameters.getInstanceForParameter(
					statisticsParam, null, CoevolutionaryStatistics.class));
			statistics.setup(state, statisticsParam);
		}

		opponents = new ArrayList<List<Individual>>(numSubpopulations);
		samplingMethod = new SamplingMethod[numSubpopulations];
		fitnessAggregateMethod = new FitnessAggregateMethod[numSubpopulations];

		for (int subpop = 0; subpop < numSubpopulations; subpop++) {
			opponents.add(new ArrayList<Individual>());
			setupSubpopulation(state, base, subpop);
		}
	}

	/**
	 * Sets up fitness aggregate methods and sampling method for the given subpopulation.
	 * 
	 * @param state
	 *            the current evolutionary state
	 * @param base
	 *            the base parameter
	 * @param subpop
	 *            the subpopulation index
	 */
	private void setupSubpopulation(EvolutionState state, Parameter base, int subpop) {
		Parameter samplingMethodParam = base.push(P_SUBPOP).push("" + subpop).push(
				P_SAMPLING_METHOD);
		samplingMethod[subpop] = (SamplingMethod) (state.parameters.getInstanceForParameter(
				samplingMethodParam, null, SamplingMethod.class));
		samplingMethod[subpop].setup(state, samplingMethodParam);

		Parameter fitnessMethodParam = base.push(P_SUBPOP).push("" + subpop).push(P_FITNESS_METHOD);
		fitnessAggregateMethod[subpop] = (FitnessAggregateMethod) (state.parameters
				.getInstanceForParameter(fitnessMethodParam, null, FitnessAggregateMethod.class));
		fitnessAggregateMethod[subpop].setup(state, fitnessMethodParam);
	}

	@Override
	public void evaluatePopulation(EvolutionState state) {
		for (int subpop = 0; subpop < numSubpopulations; subpop++) {
			opponents.set(subpop, findOpponentsFromSubpopulation(state, subpop));
		}

		evaluationsPerformed += interactionScheme.getEvaluationsNumber(state, opponents, true);

		for (int subpop = 0; subpop < numSubpopulations; subpop++) {
			float[][] subpopulationResults = interactionScheme.performInteractions(state, subpop,
					opponents);

			fitnessAggregateMethod[subpop].prepareToAggregate(state, subpop);
			fitnessAggregateMethod[subpop].addToAggregate(state, subpop, subpopulationResults,
					popIndsWeight);
			fitnessAggregateMethod[subpop].assignFitness(state, subpop);

			if (statistics != null) {
				statistics.printInteractionResults(state, subpopulationResults, subpop);
			}
		}
	}

	@Override
	public boolean runComplete(EvolutionState state) {
		return (evaluationsPerformed >= evaluationsLimit);
	}

	/**
	 * Samples subpopulation to choose a reference set of individuals. Other individuals can be
	 * evaluated on the basis of interactions with this reference set. It may happen that
	 * individuals from the same subpopulation are tested int this way - it depends on
	 * 
	 * @param subpop
	 *            the index of subpopulation
	 * @return a list of individuals sampled from the given subpopulation
	 */
	private List<Individual> findOpponentsFromSubpopulation(EvolutionState state, int subpop) {
		return samplingMethod[subpop].sample(state, state.population.subpops[subpop].individuals);
	}

	/**
	 * Returns the interaction scheme used during the evaluation.
	 * 
	 * @return the interaction scheme used by this evaluator
	 */
	public InteractionScheme getInteractionScheme() {
		return interactionScheme;
	}
}
