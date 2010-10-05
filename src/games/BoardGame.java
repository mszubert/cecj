package games;

import java.util.List;

/**
 * Interface describing game rules. Implementing class manages the game state.
 * 
 * @author Marcin Szubert
 * 
 */
public interface BoardGame {

	/**
	 * Verifies if game is in terminal state.
	 *
	 * @return <code>true</code> if game is ended
	 */
	public boolean endOfGame();
	
	/**
	 * Allows for game-specific fast move evaluation
	 * 
	 * @param player
	 * @param move
	 * @return
	 */
	public double evalMove(Player player, GameMove move);

	public List<? extends GameMove> findMoves();

	public int getCurrentPlayer();

	public int getOutcome();

	public void makeMove(GameMove move);

	public void reset();

	public Board getBoard();

	public void pass();
}
