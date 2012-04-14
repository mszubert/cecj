package games.player;

import ec.Individual;
import ec.Setup;
import ec.util.MersenneTwisterFast;

public interface EvolvedPlayer extends Player, Setup {
	
	public EvolvedPlayer createEmptyCopy();
	
	public void readFromIndividual(Individual ind) throws IllegalArgumentException;
	
	public void readFromString(String s);
	
	public Individual createIndividual();
	
	public void randomizeWeights(MersenneTwisterFast random, double range);
}
