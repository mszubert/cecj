package cecj.app;

import java.util.List;

import cecj.archive.ArchivingSubpopulation;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;
import games.player.EvolvedPlayer;
import games.player.LearningPlayer;
import games.scenario.SelfPlayTDLScenario;
import games.scenario.TwoPlayerTDLScenario;

public class ArchivalTDLImprover extends SelfPlayTDLImprover {

	private static final String P_OPPONENTS = "opponents";
	private static final String P_SELF_PLAY_GAMES = "self-play-games";

	private int opponents;
	private int selfPlayGames;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		Parameter opponentsParam = base.push(P_OPPONENTS);
		opponents = state.parameters.getInt(opponentsParam, null);

		Parameter selfPlayGamesParam = base.push(P_SELF_PLAY_GAMES);
		selfPlayGames = state.parameters.getIntWithDefault(selfPlayGamesParam, null, 0);
	}

	@Override
	public void improve(EvolutionState state, Individual ind) {
		if (!(state.population.subpops[0] instanceof ArchivingSubpopulation)) {
			state.output.fatal("Archival improver requires archiving subpopulation");
		}

		List<Individual> archivalIndividuals = ((ArchivingSubpopulation) state.population.subpops[0])
				.getArchivalIndividuals();
		int archiveSize = archivalIndividuals.size();

		EvolvedPlayer player = playerPrototype.createEmptyCopy();
		player.readFromIndividual(ind);

		if (player instanceof LearningPlayer) {
			SelfPlayTDLScenario selfPlayScenario = new SelfPlayTDLScenario((LearningPlayer) player,
					randomness, learningRate, lambda);
			for (int r = 0; r < selfPlayGames; r++) {
				boardGame.reset();
				selfPlayScenario.play(boardGame);
			}

			if (archiveSize < opponents) {
				for (int r = 0; r < (opponents - archiveSize) * repeats; r++) {
					boardGame.reset();
					selfPlayScenario.play(boardGame);
				}
			}

			for (int i = 1; i <= Math.min(opponents, archiveSize); i++) {
				Individual archivalOpponent = archivalIndividuals.get(archiveSize - i);
				EvolvedPlayer opponent = playerPrototype.createEmptyCopy();
				opponent.readFromIndividual(archivalOpponent);

				TwoPlayerTDLScenario scenario = new TwoPlayerTDLScenario(new LearningPlayer[] {
						(LearningPlayer) player, (LearningPlayer) opponent }, randomness,
						learningRate, 0);
				TwoPlayerTDLScenario scenario2 = new TwoPlayerTDLScenario(new LearningPlayer[] {
						(LearningPlayer) opponent, (LearningPlayer) player }, randomness,
						learningRate, 1);

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
}
