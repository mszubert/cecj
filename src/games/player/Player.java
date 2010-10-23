package games.player;

import games.Board;

public interface Player {

	public void initializeEligibilityTraces();
	
	public void TDLUpdate(Board previous, double delta);
	
	public void TDLUpdate(Board previous, double delta, double lambda);
	
	public double evaluate(Board board);

}
