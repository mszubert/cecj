package games;

import games.player.Player;

import java.util.List;

/**
 * Interface describing game rules. Implementing class manages the game state.
 * 
 * @author Marcin Szubert
 * 
 */
public interface BoardGame extends Cloneable {

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
	
	public List<GameMove> findMoves();

	public int getCurrentPlayer();

	public int getOutcome();

	public void makeMove(GameMove move);

	public void reset();

	public Board getBoard();

	public void pass();

	public BoardGame clone();
}
