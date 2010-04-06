/*
  Copyright 2009 by Marcin Szubert
  Licensed under the Academic Free License version 3.0
 */

package cecj.eval;

import java.util.ArrayList;
import java.util.List;

import cecj.archive.ArchivingSubpopulation;
import cecj.archive.CoevolutionaryArchive;
import cecj.interaction.InteractionResult;
import cecj.sampling.SamplingMethod;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

/**
 * Extends the simple evaluation process with an archiving mechanism.
 * 
 * The evaluation procedure is realized in the following manner. Firstly, after taking simple
 * evaluation steps as in the superclass, additional opponents are selected among archival
 * individuals (an archive is maintained for each subpopulation). Outcomes of the interactions with
 * such opponents are added to results obtained by the superclass and aggregated together.
 * Eventually, subpopulation individuals are submitted to the archive which decides if any of them
 * is worth keeping. While interaction scheme and fitness aggregation method are inherited from the
 * <code>SimpleCoevolutionaryEvaluator</code>, archival sampling methods must be defined separately
 * for each of the archives.
 * 
 * Often, opponents sampled from the population are less competent than these from the archive; to
 * address this issue, additional parameters were created that specify the relative importance of
 * opponents from both sources - <code>archive-inds-weight</code> and <code>pop-inds-weight</code>.
 * 
 * @author Marcin Szubert
 * 
 */
public class ArchivingCoevolutionaryEvaluator extends SimpleCoevolutionaryEvaluator {

	private static final String P_ARCHIVE = "archive";
	private static final String P_ARCHIVE_INDS_WEIGHT = "archive-inds-weight";
	private static final String P_ARCHIVE_SAMPLING_METHOD = "archive-sampling-method";

	private CoevolutionaryArchive archive;

	private List<List<Individual>> archiveOpponents;

	private SamplingMethod[] archiveSamplingMethod;

	private int archiveIndsWeight;
	
	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		Parameter archiveParam = base.push(P_ARCHIVE);
		archive = (CoevolutionaryArchive) (state.parameters.getInstanceForParameter(archiveParam,
				null, CoevolutionaryArchive.class));
		archive.setup(state, base.push(P_ARCHIVE));

		Parameter archiveIndsWeightParam = base.push(P_ARCHIVE_INDS_WEIGHT);
		archiveIndsWeight = state.parameters.getIntWithDefault(archiveIndsWeightParam, null, 1);

				
		archiveOpponents = new ArrayList<List<Individual>>(numSubpopulations);
		archiveSamplingMethod = new SamplingMethod[numSubpopulations];

		for (int subpop = 0; subpop < numSubpopulations; subpop++) {
			archiveOpponents.add(new ArrayList<Individual>());
			setupArchivingSubpopulation(state, base, subpop);
		}
	}

	private void setupArchivingSubpopulation(EvolutionState state, Parameter base, int subpop) {
		Parameter samplingMethodParam = base.push(P_SUBPOP).push("" + subpop).push(
				P_ARCHIVE_SAMPLING_METHOD);
		archiveSamplingMethod[subpop] = (SamplingMethod) (state.parameters.getInstanceForParameter(
				samplingMethodParam, null, SamplingMethod.class));
		archiveSamplingMethod[subpop].setup(state, samplingMethodParam);
	}

	@Override
	public void evaluatePopulation(EvolutionState state) {
		if (!(state.population.subpops[0] instanceof ArchivingSubpopulation)) {
			state.output.fatal("Archiving evaluator requires archiving subpopulation");
		}

		super.evaluatePopulation(state);

		for (int subpop = 0; subpop < numSubpopulations; subpop++) {
			archiveOpponents.set(subpop, findOpponentsFromArchive(state, subpop));
		}
			
		evaluationsPerformed += interactionScheme.getEvaluationsNumber(state, archiveOpponents, false);
			
		for (int subpop = 0; subpop < numSubpopulations; subpop++) {
			List<List<InteractionResult>> subpopulationResults = interactionScheme
					.performInteractions(state, subpop, archiveOpponents);

			fitnessAggregateMethod[subpop].addToAggregate(state, subpop, subpopulationResults,
					archiveIndsWeight);
			fitnessAggregateMethod[subpop].assignFitness(state, subpop);

			if (statistics != null) {
				statistics.printInteractionResults(state, subpopulationResults, subpop);
			}
		}

		archive.submit(state);
	}
	
	private List<Individual> findOpponentsFromArchive(EvolutionState state, int subpop) {
		List<Individual> archivalInds = ((ArchivingSubpopulation) state.population.subpops[subpop])
				.getArchivalIndividuals();
		return archiveSamplingMethod[subpop].sample(state, archivalInds);
	}
}
