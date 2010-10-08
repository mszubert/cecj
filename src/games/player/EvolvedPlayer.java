package games.player;

import ec.Individual;

public interface EvolvedPlayer extends Player {
	
	public EvolvedPlayer createEmptyCopy();
	
	public void readFromIndividual(Individual ind);
	
	public void writeToIndividual(Individual ind);
}
