/*
  Copyright 2009 by Marcin Szubert
  Licensed under the Academic Free License version 3.0
 */

package cecj.archive;

import java.util.List;

import ec.EvolutionState;
import ec.Individual;
import ec.SelectionMethod;
import ec.select.SelectDefaults;
import ec.util.Parameter;

public class ArchiveRandomSelection extends SelectionMethod {

	/** default base */
	private static final String P_ARCHIVAL_RANDOM = "archival-random";

	private static final String P_SIZE = "size";
	private static final int ENTIRE_ARCHIVE = -1;

	private int size;

	public Parameter defaultBase() {
		return SelectDefaults.base().push(P_ARCHIVAL_RANDOM);
	}

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);
		
		Parameter sizeParam = base.push(P_SIZE);
		size = state.parameters.getIntWithDefault(sizeParam, null, ENTIRE_ARCHIVE);
	}

	@Override
	public int produce(int subpopulation, EvolutionState state, int thread) {
		throw new UnsupportedOperationException(
			"Archival selection methods do not support supopulation index-based produce method.");
	}

	@Override
	public int produce(int min, int max, int start, int subpop, Individual[] inds,
			EvolutionState state, int thread) {
		int n = 1;
		if (n > max)
			n = max;
		if (n < min)
			n = min;

		List<Individual> archive = ((ArchivingSubpopulation) state.population.subpops[subpop])
			.getArchivalIndividuals();
		List<Individual> popInds = ((ArchivingSubpopulation) state.population.subpops[subpop])
			.getIndividuals();

		if (!archive.isEmpty()) {
			for (int q = 0; q < n; q++) {
				int selected;
				if (size <= 0) {
					selected = state.random[thread].nextInt(archive.size());
				} else {
					selected = archive.size() - state.random[thread].nextInt(size) - 1;
					selected = Math.max(selected, 0);
				}
				inds[start + q] = archive.get(selected);
			}
		} else {
			for (int q = 0; q < n; q++) {
				inds[start + q] = popInds.get(state.random[thread].nextInt(popInds.size()));
			}
		}

		return n;
	}
}
