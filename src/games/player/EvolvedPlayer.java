package games.player;

import ec.Individual;
import ec.Setup;

public interface EvolvedPlayer extends Player, Setup {
	
	public EvolvedPlayer createEmptyCopy();
	
	public void readFromIndividual(Individual ind) throws IllegalArgumentException;
	
	public Individual createIndividual();

	public void reset();
}
