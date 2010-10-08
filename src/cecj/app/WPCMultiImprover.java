package cecj.app;

import ec.EvolutionState;
import ec.Individual;
import games.BoardGame;
import games.player.Player;
import games.player.WPCPlayer;
import games.scenario.TwoPlayerTDLScenario;

public class WPCMultiImprover extends WPCImprover {

	@Override
	public void improve(EvolutionState state, Individual ind) {
		Player player = new WPCPlayer(getWPC(state, ind));
		BoardGame game = gameFactory.createGame();

		Individual[] inds = state.population.subpops[0].individuals;

		for (int r = 0; r < repeats; r++) {
			Individual ind2 = inds[state.random[0].nextInt(inds.length)];
			WPCPlayer opponent = new WPCPlayer(getWPC(state, ind2));

			TwoPlayerTDLScenario scenario = new TwoPlayerTDLScenario(state.random[0], new Player[] {
					player, opponent }, randomness, learningRate, 0);
			TwoPlayerTDLScenario scenario2 = new TwoPlayerTDLScenario(state.random[0],
					new Player[] { opponent, player }, randomness, learningRate, 1);

			game.reset();
			scenario.play(game);
			game.reset();
			scenario2.play(game);
		}
	}
}
