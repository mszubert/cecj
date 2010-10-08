package games;

import games.player.Player;

public interface GameFactory {
	
	public Player createPlayer();
	
	public BoardGame createGame();
}
