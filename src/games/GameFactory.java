package games;

public interface GameFactory {
	
	public WPCPlayer createPlayer();
	
	public BoardGame createGame();
}
