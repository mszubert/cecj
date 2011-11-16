package cecj.app;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;
import games.BoardGame;
import games.player.EvolvedPlayer;
import games.player.Player;
import games.scenario.GameScenario;
import cecj.app.othello.OthelloHeuristicPlayer;
import cecj.interaction.IntegerTestResult;
import cecj.interaction.TestResult;
import cecj.problem.TestBasedProblem;
import cecj.subgame.SubgameIndividual;

public class BoardSubgameProblem extends TestBasedProblem {

	private static final String P_GAME = "game";
	private static final String P_PLAYER = "player";

	private BoardGame boardGame;
	private EvolvedPlayer playerPrototype;

	private RandomizedPlayerFitnessCalculator fitnessCalc;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		Parameter gameParam = new Parameter(P_GAME);
		boardGame = (BoardGame) state.parameters.getInstanceForParameter(gameParam, null,
				BoardGame.class);

		Parameter playerParam = new Parameter(P_PLAYER);
		playerPrototype = (EvolvedPlayer) state.parameters.getInstanceForParameter(playerParam,
				null, EvolvedPlayer.class);

		fitnessCalc = new OthelloHeuristicPlayer();
	}

	@Override
	public TestResult test(EvolutionState state, Individual candidate, Individual test) {

		if (!(test instanceof SubgameIndividual)) {
			state.output.fatal("Tests must be of type SubgameIndividual in this problem");
		}
		SubgameIndividual subgame = (SubgameIndividual) test;

		EvolvedPlayer player = playerPrototype.createEmptyCopy();
		try {
			player.readFromIndividual(candidate);
		} catch (IllegalArgumentException ex) {
			state.output.fatal("Players can not be constructed from this type of individual");
		}

		GameScenario scenario = new GameScenario(new Player[] { player, fitnessCalc.getOpponent() });
		subgame.adapt(boardGame);

		return new IntegerTestResult(scenario.play(boardGame));
	}
}
