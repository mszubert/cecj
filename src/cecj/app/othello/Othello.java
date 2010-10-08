package cecj.app.othello;

import games.BoardGame;
import games.GameFactory;
import games.player.WPCPlayer;

public class Othello implements GameFactory {

	public static final int WPC_LENGTH = 64;

	public BoardGame createGame() {
		return new OthelloGame();
	}
		
	public WPCPlayer createPlayer() {
		return new WPCPlayer(new double[WPC_LENGTH]);
	}

}
