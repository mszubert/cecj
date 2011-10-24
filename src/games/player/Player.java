package games.player;

import ec.Setup;
import games.Board;

public interface Player extends Setup {
	
	public double evaluate(Board board);
	
	public void reset();
}
