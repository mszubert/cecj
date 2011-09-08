package cecj.app;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;
import games.BoardGame;
import games.player.EvolvedPlayer;
import games.player.Player;
import games.scenario.GameScenario;
import cecj.statistics.ObjectiveFitnessCalculator;

public abstract class GamePlayerFitnessCalculator implements
		ObjectiveFitnessCalculator {

	protected static final String P_EVALUATOR_RANDOMNESS = "evaluator-randomness";
	protected static final String P_EVALUATED_RANDOMNESS = "evaluated-randomness";

	private static final String P_PLAY_BOTH = "play-both";
	private static final String P_REPEATS = "repeats";

	private static final String P_GAME = "game";
	private static final String P_PLAYER = "player";

	private static final String P_WIN_POINTS = "win-points";
	private static final String P_DRAW_POINTS = "draw-points";
	private static final String P_LOSS_POINTS = "loss-points";

	protected double evaluatedRandomness;
	protected double evaluatorRandomness;
	protected boolean playBoth;
	protected int repeats;

	private BoardGame boardGame;
	private EvolvedPlayer playerPrototype;

	private float winPoints;
	private float drawPoints;
	private float lossPoints;

	public void setup(EvolutionState state, Parameter base) {
		Parameter randomnessParam = base.push(P_EVALUATED_RANDOMNESS);
		evaluatedRandomness = state.parameters.getDoubleWithDefault(
				randomnessParam, null, 0);

		randomnessParam = base.push(P_EVALUATOR_RANDOMNESS);
		evaluatorRandomness = state.parameters.getDoubleWithDefault(
				randomnessParam, null, 0);

		Parameter repetitionsParam = base.push(P_REPEATS);
		repeats = state.parameters.getIntWithDefault(repetitionsParam, null, 1);

		Parameter playBothParam = base.push(P_PLAY_BOTH);
		playBoth = state.parameters.getBoolean(playBothParam, null, false);

		Parameter gameParam = new Parameter(P_GAME);
		boardGame = (BoardGame) state.parameters.getInstanceForParameter(
				gameParam, null, BoardGame.class);

		Parameter playerParam = new Parameter(P_PLAYER);
		playerPrototype = (EvolvedPlayer) state.parameters
				.getInstanceForParameter(playerParam, null, EvolvedPlayer.class);

		Parameter winPointsParam = base.push(P_WIN_POINTS);
		winPoints = state.parameters.getFloatWithDefault(winPointsParam, null,
				1.0);

		Parameter drawPointsParam = base.push(P_DRAW_POINTS);
		drawPoints = state.parameters.getFloatWithDefault(drawPointsParam,
				null, 0.0);

		Parameter lossPointsParam = base.push(P_LOSS_POINTS);
		lossPoints = state.parameters.getFloatWithDefault(lossPointsParam,
				null, 0.0);
	}

	public float calculateObjectiveFitness(EvolutionState state, Individual ind) {
		EvolvedPlayer player = playerPrototype.createEmptyCopy();
		player.readFromIndividual(ind);

		GameScenario scenario1 = getScenario(state, player);
		GameScenario scenario2 = getInverseScenario(state, player);

		float sum = 0;
		int gameResult;
		for (int r = 0; r < repeats; r++) {
			boardGame.reset();
			gameResult = scenario1.play(boardGame);
			sum += getPoints(gameResult);
			if (playBoth) {
				boardGame.reset();
				gameResult = scenario2.play(boardGame);
				sum += getPoints(-gameResult);
			}
		}

		if (playBoth) {
			return sum / (repeats * 2);
		} else {
			return sum / repeats;
		}
	}

	private float getPoints(int gameResult) {
		if (gameResult > 0) {
			return winPoints;
		} else if (gameResult < 0) {
			return lossPoints;
		} else {
			return drawPoints;
		}
	}

	protected abstract GameScenario getScenario(EvolutionState state,
			Player player);

	protected abstract GameScenario getInverseScenario(EvolutionState state,
			Player player);
}
