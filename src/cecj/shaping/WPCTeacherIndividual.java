package cecj.shaping;

import cecj.app.othello.OthelloGame;
import ec.vector.DoubleVectorIndividual;
import games.player.LearningPlayer;
import games.player.Player;
import games.player.WPCPlayer;
import games.scenario.TwoPlayerTDLScenario;

public class WPCTeacherIndividual extends DoubleVectorIndividual implements TrainerIndividual {

	public Player trainPlayer() {
		WPCPlayer trainer = new WPCPlayer(8);
		trainer.readFromIndividual(this);

		WPCPlayer player = new WPCPlayer(8);
		player.reset();

		TwoPlayerTDLScenario scenario = new TwoPlayerTDLScenario(new LearningPlayer[] {
				(LearningPlayer) player, (LearningPlayer) trainer }, 0.1, 0.01, 0);

		OthelloGame othello = new OthelloGame();
		for (int r = 0; r < 1000; r++) {
			othello.reset();
			scenario.play(othello);
		}

		return player;
	}

}
