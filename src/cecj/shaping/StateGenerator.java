package cecj.shaping;

import games.BoardGame;

import java.util.List;

public interface StateGenerator {
	
	List<BoardGame> generateTrace(BoardGame initialState);
	
	List<BoardGame> generateRandomStateSet(BoardGame boardGame, int i);
	
	BoardGame generateSingleState();

	BoardGame generatePreTerminalState();
}
