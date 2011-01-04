package games.player;

import games.Board;

public interface LearningPlayer extends Player {
	
	public void initializeEligibilityTraces();
	
	public void TDLUpdate(Board previous, double delta);
	
	public void TDLUpdate(Board previous, double delta, double lambda);
}
