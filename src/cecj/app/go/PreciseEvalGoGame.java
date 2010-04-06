package cecj.app.go;

public class PreciseEvalGoGame extends GoGame {
	
	public PreciseEvalGoGame() {
		board = new PreciseEvalGoBoard();
		reset();
	}
}
