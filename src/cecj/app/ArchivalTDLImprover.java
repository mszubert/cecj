package cecj.app;

import java.util.List;

import cecj.archive.ArchivingSubpopulation;
import ec.EvolutionState;
import ec.Individual;
import games.player.EvolvedPlayer;
import games.player.LearningPlayer;
import games.scenario.TwoPlayerTDLScenario;

public class ArchivalTDLImprover extends SelfPlayTDLImprover {

	@Override
	public void improve(EvolutionState state, Individual ind) {
		if (!(state.population.subpops[0] instanceof ArchivingSubpopulation)) {
			state.output.fatal("Archival improver requires archiving subpopulation");
		}

		EvolvedPlayer player = playerPrototype.createEmptyCopy();
		player.readFromIndividual(ind);

		List<Individual> archive = ((ArchivingSubpopulation) state.population.subpops[0])
				.getArchivalIndividuals();
		for (Individual archivalOpponent : archive) {
			EvolvedPlayer opponent = playerPrototype.createEmptyCopy();
			opponent.readFromIndividual(archivalOpponent);

			TwoPlayerTDLScenario scenario = new TwoPlayerTDLScenario(state.random[0],
					new LearningPlayer[] { player, opponent }, randomness, learningRate, 0);
			TwoPlayerTDLScenario scenario2 = new TwoPlayerTDLScenario(state.random[0],
					new LearningPlayer[] { opponent, player }, randomness, learningRate, 1);

			for (int r = 0; r < repeats; r++) {
				boardGame.reset();
				scenario.play(boardGame);
			}

			for (int r = 0; r < repeats; r++) {
				boardGame.reset();
				scenario2.play(boardGame);
			}
		}
	}
}
