package cecj.app;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;
import ec.vector.DoubleVectorIndividual;
import games.BoardGame;
import games.GameFactory;
import games.Player;
import games.WPCPlayer;
import games.scenarios.GameScenario;
import games.scenarios.RandomizedTwoPlayersGameScenario;
import games.scenarios.TwoPlayerTDLScenario;
import cecj.interaction.BinaryResult;
import cecj.interaction.InteractionResult;
import cecj.interaction.WinDrawLossResult;
import cecj.interaction.WinDrawLossResult.Result;
import cecj.problems.SymmetricTestBasedProblem;
import cecj.utils.Pair;

public class BoardGameProblem extends SymmetricTestBasedProblem {

	private static final String P_GAME = "game";
	private static final String P_RANDOMNESS = "randomness";
	private static final String P_LEARNING_RATE = "learning-rate";
	private static final String P_BINARY_OUTCOUME = "binary-outcomes";

	private double randomness;
	private boolean randomizedPlay;
	private boolean binaryOutcome;

	private double learningRate;
	private boolean learningPlay;

	private GameFactory gameFactory;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		Parameter gameParam = new Parameter(P_GAME);
		gameFactory = (GameFactory) state.parameters.getInstanceForParameter(gameParam, null,
				GameFactory.class);

		Parameter randomnessParam = base.push(P_RANDOMNESS);
		if (state.parameters.exists(randomnessParam)) {
			randomness = state.parameters.getDoubleWithDefault(randomnessParam, null, 0);
			randomizedPlay = true;
		} else {
			randomizedPlay = false;
		}

		Parameter learningRateParam = base.push(P_LEARNING_RATE);
		if (state.parameters.exists(learningRateParam)) {
			learningRate = state.parameters.getDoubleWithDefault(learningRateParam, null, 0.01);
			learningPlay = true;
		} else {
			learningPlay = false;
		}

		Parameter binaryOutcomeParam = base.push(P_BINARY_OUTCOUME);
		binaryOutcome = state.parameters.getBoolean(binaryOutcomeParam, null, false);
	}

	@Override
	public Pair<? extends InteractionResult> test(EvolutionState state, Individual candidate,
			Individual test) {
		if (!(candidate instanceof DoubleVectorIndividual)
				|| !(test instanceof DoubleVectorIndividual)) {
			state.output.error("Othello players should be represented by floats vectors\n");
		}

		double[] wpc1 = ((DoubleVectorIndividual) candidate).genome;
		double[] wpc2 = ((DoubleVectorIndividual) test).genome;

		WPCPlayer player1 = new WPCPlayer(wpc1);
		WPCPlayer player2 = new WPCPlayer(wpc2);
		BoardGame game = gameFactory.createGame();

		GameScenario scenario;
		Player[] players = new Player[] { player1, player2 };
		if (learningPlay) {
			scenario = new TwoPlayerTDLScenario(state.random[0], players, randomness, learningRate);
		} else if (randomizedPlay) {
			scenario = new RandomizedTwoPlayersGameScenario(state.random[0], players, new double[] {
					randomness, randomness });
		} else {
			scenario = new RandomizedTwoPlayersGameScenario(state.random[0], players, new double[] {
					0, 0 });
		}

		double result = scenario.play(game);
		if (binaryOutcome) {
			if (result >= 0) {
				return new Pair<BinaryResult>(new BinaryResult(true),
						new BinaryResult(false));
			} else {
				return new Pair<BinaryResult>(new BinaryResult(false),
						new BinaryResult(true));
			}
		} else {
			if (result > 0) {
				return new Pair<WinDrawLossResult>(new WinDrawLossResult(Result.WIN),
						new WinDrawLossResult(Result.LOSS));
			} else if (result < 0) {
				return new Pair<WinDrawLossResult>(new WinDrawLossResult(Result.LOSS),
						new WinDrawLossResult(Result.WIN));
			} else {
				return new Pair<WinDrawLossResult>(new WinDrawLossResult(Result.DRAW),
						new WinDrawLossResult(Result.DRAW));
			}
		}
	}
}
