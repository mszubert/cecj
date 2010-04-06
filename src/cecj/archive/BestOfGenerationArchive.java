/*
  Copyright 2009 by Marcin Szubert
  Licensed under the Academic Free License version 3.0
 */

package cecj.archive;

import java.util.List;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

/**
 * Represents the simplest archive type which just finds the best individual from the submitted
 * population and appends it to the list of individuals found in previous generations. Note that, by
 * default, the archive size is unbounded and grows steadily over time because no archival
 * individuals are removed. By setting the <code>archive-size</code> parameter value to <i>x</i>,
 * only the best competitors from last <i>x</i> generations are maintained.
 * 
 * @author Marcin Szubert
 * 
 */
public class BestOfGenerationArchive extends CoevolutionaryArchive {

	private static final int UNBOUNDED_ARCHIVE_SIZE = -1;

	private static final String P_ARCHIVE_SIZE = "size";
	private static final String P_SUBPOP = "subpop";

	private int[] archiveSize;
	private int[] currentArchivePosition;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		currentArchivePosition = new int[numSubpopulations];
		archiveSize = new int[numSubpopulations];

		for (int subpop = 0; subpop < numSubpopulations; subpop++) {
			Parameter archiveSizeParameter = base.push(P_SUBPOP).push("" + subpop).push(
					P_ARCHIVE_SIZE);
			archiveSize[subpop] = state.parameters.getIntWithDefault(archiveSizeParameter, null,
					UNBOUNDED_ARCHIVE_SIZE);
			if (archiveSize[subpop] <= 0 && archiveSize[subpop] != UNBOUNDED_ARCHIVE_SIZE) {
				state.output.fatal("Archive size must be > 0.\n", archiveSizeParameter);
			}
		}
	}

	@Override
	public void submit(EvolutionState state) {
		for (int subpop = 0; subpop < numSubpopulations; subpop++) {
			Individual bestOfGeneration = findBestIndividual(getIndividuals(state, subpop));
			List<Individual> archive = getArchive(state, subpop);

			if ((archiveSize[subpop] == UNBOUNDED_ARCHIVE_SIZE)
					|| (archive.size() < archiveSize[subpop])) {
				archive.add(bestOfGeneration);
			} else {
				archive.set(currentArchivePosition[subpop], bestOfGeneration);
				currentArchivePosition[subpop]++;
				currentArchivePosition[subpop] %= archiveSize[subpop];
			}
		}
	}

	private Individual findBestIndividual(List<Individual> inds) {
		Individual result = inds.get(0);
		for (Individual ind : inds) {
			if (ind.fitness.betterThan(result.fitness)) {
				result = ind;
			}
		}
		return result;
	}
}
