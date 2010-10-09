package games.player;

import ec.Individual;

public interface EvolvedPlayer extends Player {
	
	public EvolvedPlayer createEmptyCopy();
	
	public void readFromIndividual(Individual ind) throws IllegalArgumentException;
	
	public void writeToIndividual(Individual ind) throws IllegalArgumentException;
}
