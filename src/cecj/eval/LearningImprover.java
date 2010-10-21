/*
  Copyright 2009 by Marcin Szubert
  Licensed under the Academic Free License version 3.0
 */

package cecj.eval;

import ec.EvolutionState;
import ec.Individual;
import ec.Setup;

/**
 * Represents temporal difference learning (TDL) algorithm which is applied to population
 * individuals to improve them between subsequent evolutionary generations.
 * 
 * @author Marcin Szubert
 * 
 */
public interface LearningImprover extends Setup {

	/**
	 * Prepares an individual for being improved by TDL algorithm.
	 * 
	 * @param state
	 *            current evolutionary state
	 * @param ind
	 *            individual to be prepared
	 */
	public void prepareForImproving(EvolutionState state, Individual ind);

	/**
	 * Applies TDL algorithm to the given individual to improve its fitness
	 * 
	 * @param state
	 *            current evolutionary state
	 * @param ind
	 *            an individual to be improved
	 */
	public void improve(EvolutionState state, Individual ind);
}
