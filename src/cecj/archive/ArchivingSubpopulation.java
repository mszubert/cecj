/*
  Copyright 2009 by Marcin Szubert
  Licensed under the Academic Free License version 3.0
 */

package cecj.archive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ec.EvolutionState;
import ec.Group;
import ec.Individual;
import ec.Subpopulation;
import ec.util.Parameter;

/**
 * 
 * @author Marcin Szubert
 *
 */
public class ArchivingSubpopulation extends Subpopulation {

	private List<Individual> archiveIndividuals;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);
		archiveIndividuals = new ArrayList<Individual>();
	}

	@Override
	public Group emptyClone() {
		ArchivingSubpopulation subpopulation = (ArchivingSubpopulation) super.emptyClone();
		subpopulation.archiveIndividuals = archiveIndividuals;
		return subpopulation;
	}

	/**
	 * Returns the list of archival individuals.
	 * 
	 * @return the list of archival individuals
	 */
	public List<Individual> getArchivalIndividuals() {
		return archiveIndividuals;
	}

	/**
	 * Returns the list of population individuals.
	 * 
	 * @return the list of population individuals
	 */
	public List<Individual> getIndividuals() {
		return Arrays.asList(individuals);
	}
}
