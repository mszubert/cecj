package cecj.app.go;

import games.BoardGame;
import games.GameFactory;
import games.WPCPlayer;

public class Go implements GameFactory {

	public static final int WPC_LENGTH = 25;
	
	public BoardGame createGame() {
		return new GoGame();
	}

	public WPCPlayer createPlayer() {
		return new WPCPlayer(new double[WPC_LENGTH]);
	}
}
