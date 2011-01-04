package cecj.app;

import ec.EvolutionState;
import ec.Individual;
import games.player.EvolvedPlayer;
import games.player.LearningPlayer;
import games.scenario.TwoPlayerTDLScenario;

public class TDLMultiPlayImprover extends TDLSelfPlayImprover {

	@Override
	public void improve(EvolutionState state, Individual ind) {
		EvolvedPlayer player = playerPrototype.createEmptyCopy(); 
		player.readFromIndividual(ind);

		Individual[] inds = state.population.subpops[0].individuals;

		for (int r = 0; r < repeats; r++) {
			Individual ind2 = inds[state.random[0].nextInt(inds.length)];
			EvolvedPlayer opponent = playerPrototype.createEmptyCopy(); 
			player.readFromIndividual(ind2);

			TwoPlayerTDLScenario scenario = new TwoPlayerTDLScenario(state.random[0], new LearningPlayer[] {
					player, opponent }, randomness, learningRate, 0);
			TwoPlayerTDLScenario scenario2 = new TwoPlayerTDLScenario(state.random[0],
					new LearningPlayer[] { opponent, player }, randomness, learningRate, 1);

			boardGame.reset();
			scenario.play(boardGame);
			boardGame.reset();
			scenario2.play(boardGame);
		}
	}
}
