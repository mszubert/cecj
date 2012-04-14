package cecj.shaping;

import java.util.List;

import cecj.app.othello.OthelloGame;
import cecj.interaction.IntraPopulationInteractionScheme;
import ec.EvolutionState;
import ec.Individual;
import games.player.Player;
import games.scenario.GameScenario;

public class TrainerInteractionScheme extends IntraPopulationInteractionScheme {

	@Override
	public float[][] performInteractions(EvolutionState state, int subpop,
			List<List<Individual>> opponents) {
	
		Individual[] stateSamples = state.population.subpops[subpop].individuals;
		Player[] players = new Player[stateSamples.length];
		for (int i = 0; i < stateSamples.length; i++) {
			TrainerIndividual trainer = (TrainerIndividual) stateSamples[i];
			players[i] = trainer.trainPlayer();
		}
		
		OthelloGame othello = new OthelloGame();
		
		float[][] results = new float[players.length][players.length * 2];
		for (int p = 0; p < players.length; p++) {
			for (int o = 0; o < players.length; o++) {
				GameScenario scenario = new GameScenario(new Player[] {players[p], players[o]});
				othello.reset();
				int gameResult = scenario.play(othello);
				
				results[p][o * 2] = gameResult;
				results[o][p * 2 + 1] = -gameResult;
			}
		}
		
		return results;
	}

}
