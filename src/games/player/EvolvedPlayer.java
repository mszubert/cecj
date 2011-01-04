package games.player;

import ec.Individual;
import ec.Setup;
import ec.util.MersenneTwisterFast;

public interface EvolvedPlayer extends LearningPlayer, Setup {
	
	public EvolvedPlayer createEmptyCopy();
	
	public void readFromIndividual(Individual ind) throws IllegalArgumentException;
	
	public Individual createIndividual();

	public void reset();
	
	public void randomizeWeights(MersenneTwisterFast random, double range);
}
