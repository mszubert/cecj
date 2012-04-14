package cecj.subgame;

import cecj.app.SelfPlayTDLImprover;
import ec.EvolutionState;
import ec.Individual;
import games.player.EvolvedPlayer;
import games.player.LearningPlayer;
import games.scenario.SelfPlayTDLScenario;

public class SelfPlaySubgameTDLImprover extends SelfPlayTDLImprover {
	private int subgamePopulation = 1;

	@Override
	public void improve(EvolutionState state, Individual ind) {
		EvolvedPlayer player = playerPrototype.createEmptyCopy();
		player.readFromIndividual(ind);

		if (player instanceof LearningPlayer) {
			SelfPlayTDLScenario selfPlayScenario = new SelfPlayTDLScenario((LearningPlayer) player,
					randomness, learningRate, lambda);
			Individual[] subgames = state.population.subpops[subgamePopulation].individuals;
			for (Individual subgame : subgames) {
				for (int r = 0; r < repeats; r++) {
					((SubgameIndividual)subgame).adapt(boardGame);
					selfPlayScenario.play(boardGame);
				}
			}
		} else {
			state.output.fatal("Can not improve player which is not a LearningPlayer instance.");
		}
	}
}
