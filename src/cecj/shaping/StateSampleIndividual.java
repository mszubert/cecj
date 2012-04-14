package cecj.shaping;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cecj.app.othello.OthelloBoard;
import cecj.app.othello.OthelloGame;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Code;
import ec.util.DecodeReturn;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import games.Board;
import games.BoardGame;
import games.GameMove;
import games.player.LearningPlayer;
import games.player.Player;
import games.scenario.GameScenario;
import games.scenario.SelfPlayTDLScenario;

public class StateSampleIndividual extends Individual implements TrainerIndividual {

	public BoardGame states[];
	public LearningPlayer player;
	
	
	
	/**
	 * This method is called only once - on a prototype individual stored in the
	 * species class.
	 */
	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		if (!(species instanceof StateSampleSpecies)) {
			state.output.fatal("StateSampleIndividual requires a StateSampleSpecies", base,
					defaultBase());
		}

		states = new BoardGame[((StateSampleSpecies) species).getSampleSize()];
	}

	public void mutate(EvolutionState state, int thread) {
		player = null;
		
		StateSampleSpecies sss = (StateSampleSpecies) species;
		double mutationProb = sss.getMutationProbability();

		for (int s = 0; s < states.length; s++) {
			if (state.random[thread].nextBoolean(mutationProb)) {
				states[s] = sss.getStateGenerator().generateSingleState();
			}
		}
	}

	public void crossover(EvolutionState state, int thread, StateSampleIndividual ind) {
		player = null;
		
		StateSampleSpecies sss = (StateSampleSpecies) species;
		if (state.random[thread].nextBoolean(sss.getCrossoverProbability())) {
			List<Integer> combination = drawCombination(state.random[thread], states.length,
					states.length / 2);
			for (int s : combination) {
				BoardGame gameState = states[s].clone();
				states[s] = ind.states[s].clone();
				ind.states[s] = gameState;
			}
		}
	}

	private List<Integer> drawCombination(MersenneTwisterFast rng, int n, int k) {
		int[] drawArray = new int[n];
		for (int i = 0; i < n; i++) {
			drawArray[i] = i;
		}

		List<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < k; i++) {
			int draw = rng.nextInt(n - i);
			result.add(drawArray[draw]);
			drawArray[draw] = drawArray[n - i - 1];
		}
		return result;
	}

	public void randomize(EvolutionState state, int thread, StateGenerator stateGenerator) {
		player = null;
		
		for (int s = 0; s < states.length; s++) {
			states[s] = stateGenerator.generateSingleState();
//			if (s < states.length / 2) {
//				
//			} else {
//				states[s] = stateGenerator.generatePreTerminalState();
//			}
		}
	}

	public Parameter defaultBase() {
		return new Parameter("state-samples").push("sample-ind");
	}

	@Override
	public Object clone() {
		StateSampleIndividual clone = (StateSampleIndividual) (super.clone());

		if (states != null) {
			clone.states = new BoardGame[states.length];
			for (int s = 0; s < states.length; s++) {
				if (states[s] != null) {
					clone.states[s] = states[s].clone();
				}
			}
		}

		return clone;
	}

	@Override
	public boolean equals(Object ind) {
		if (!(ind instanceof StateSampleIndividual)) {
			return false;
		}

		StateSampleIndividual stateSample = (StateSampleIndividual) ind;
		return Arrays.deepEquals(states, stateSample.states);
	}

	@Override
	public int hashCode() {
		int hashCode = 0;
		for (BoardGame state : states) {
			hashCode ^= state.hashCode();
		}
		return hashCode;
	}

	public Player trainPlayer() {
		if (player != null) {
			return player;
		}
		
		StateSampleSpecies sss = (StateSampleSpecies) species;
		player = sss.createNewLearner();
		int repeats = sss.getTrainRepeats();
		boolean rprop = sss.isRPropLearning();
		boolean online = sss.isOnlineLearning();

		for (int repeat = 0; repeat < repeats; repeat++) {
			if (online) {
				double randomness = sss.getRandomness();
				double learningRate = sss.getLearningRate();
				int learningIterations = sss.getLearningIterations();
				
				SelfPlayTDLScenario scenario = new SelfPlayTDLScenario(player, randomness, learningRate);
				for (int s = 0; s < states.length; s++) {
					for (int i = 0; i < learningIterations; i++) {
						BoardGame stateCopy = states[s].clone();
						scenario.play(stateCopy);
					}
				}
			} else {
				Board[] boards = new Board[states.length];
				double[] targetValues = new double[states.length];

				// generate pattern set
				for (int s = 0; s < states.length; s++) {
					BoardGame stateCopy = states[s].clone();
					boards[s] = stateCopy.getBoard().clone();
					List<GameMove> moves = stateCopy.findMoves();
					if (!moves.isEmpty()) {
						GameMove action = GameScenario.chooseBestMove(stateCopy, player, moves);
						stateCopy.makeMove(action);
						targetValues[s] = GameScenario.getValue(stateCopy, player);
					}
				}

				// learn
				if (rprop) {
					learnRPROP(player, boards, targetValues);
				} else {
					learnBackPropagation(player, boards, targetValues);
				}
			}
		}
		return player;
	}

	private void learnRPROP(LearningPlayer player, Board[] boards, double[] targetValues) {
		StateSampleSpecies sss = (StateSampleSpecies) species;
		int learningEpochIterations = sss.getLearningIterations();

		double[] errors = new double[boards.length];
		for (int i = 0; i < boards.length; i++) {
			double evalBefore = Math.tanh(player.evaluate(boards[i]));
			double derivative = (1 - (evalBefore * evalBefore));
			errors[i] = (targetValues[i] - evalBefore) * derivative;
		}

		double[] previousDerivative = player.getWeightDerivatives(boards, errors);
		double[] weightDelta = new double[previousDerivative.length];
		double[] delta = new double[previousDerivative.length];

		for (int i = 0; i < previousDerivative.length; i++) {
			delta[i] = 0.01;
			weightDelta[i] = -Math.signum(previousDerivative[i]) * delta[i];
		}
		// player.updateWeights(weightDelta);

		double decreaseFactor = 0.5;
		double increaseFactor = 1.2;

		int iteration = 0;
		double previousError = 0;
		double currentError = 0;
		do {
			iteration++;
			previousError = currentError;
			currentError = 0;

			for (int i = 0; i < boards.length; i++) {
				double evalBefore = Math.tanh(player.evaluate(boards[i]));
				double derivative = (1 - (evalBefore * evalBefore));
				errors[i] = (targetValues[i] - evalBefore) * derivative * 0.01;
			}
			double[] currentDerivative = player.getWeightDerivatives(boards, errors);

			for (int i = 0; i < currentDerivative.length; i++) {
				if (currentDerivative[i] * previousDerivative[i] >= 0) {
					if (currentDerivative[i] * previousDerivative[i] > 0) {
						delta[i] = Math.min(delta[i] * increaseFactor, 50.0);
					}
					weightDelta[i] = -Math.signum(currentDerivative[i]) * delta[i];
					previousDerivative = currentDerivative;
				} else {
					delta[i] = Math.max(delta[i] * decreaseFactor, 1e-6);
					previousDerivative = new double[currentDerivative.length];
				}
			}
			player.updateWeights(currentDerivative);
			// player.updateWeights(weightDelta);

			for (int i = 0; i < boards.length; i++) {
				double error = targetValues[i] - Math.tanh(player.evaluate(boards[i]));
				currentError += Math.abs(error);
			}

			System.out.println("Current error = " + currentError);
		} while (Math.abs(previousError - currentError) > 10e-3
				&& iteration < learningEpochIterations);

	}

	private void learnBackPropagation(LearningPlayer player, Board[] boards, double[] targetValues) {
		StateSampleSpecies sss = (StateSampleSpecies) species;
		double learningRate = sss.getLearningRate();
		int learningEpochIterations = sss.getLearningIterations();

		int iteration = 0;
		double previousError = 0;
		double currentError = 0;
		do {
			iteration++;
			previousError = currentError;
			currentError = 0;

			double errors[] = new double[boards.length];
			for (int i = 0; i < boards.length; i++) {
				double evalBefore = Math.tanh(player.evaluate(boards[i]));
				double derivative = (1 - (evalBefore * evalBefore));
				errors[i] = (targetValues[i] - evalBefore) * derivative * learningRate;
			}

			double[] derivatives = player.getWeightDerivatives(boards, errors);
			player.updateWeights(derivatives);

			for (int i = 0; i < boards.length; i++) {
				double error = targetValues[i] - Math.tanh(player.evaluate(boards[i]));
				currentError += Math.abs(error);
			}

			// System.out.println("Current error = " + currentError);
		} while (Math.abs(previousError - currentError) > 10e-3
				&& iteration < learningEpochIterations);
	}
	
	@Override
	public String genotypeToString() {
		StringBuilder builder = new StringBuilder();
		builder.append(Code.encode(states.length));
		for (int s = 0; s < states.length; s++) {
			builder.append(Code.encode(states[s].getCurrentPlayer()));
			
			Board b = states[s].getBoard();
			for (int row = 1; row <= b.getSize(); row++) {
				for (int col = 1; col <= b.getSize(); col++) {
					builder.append(Code.encode(b.getPiece(row, col)));
				}
			}
		}
		
		return builder.toString();
	}
	
	@Override
	protected void parseGenotype(final EvolutionState state, final LineNumberReader reader)
			throws IOException {
		String line = reader.readLine();
		DecodeReturn decoder = new DecodeReturn(line);
		Code.decode(decoder);
		int numStates = (int) decoder.l;
		
		states = new BoardGame[numStates];
		for (int s = 0; s < numStates; s++) {
			Code.decode(decoder);
			int currentPlayer = (int) decoder.l;
			
			OthelloBoard board = new OthelloBoard();
			for (int row = 1; row <= board.getSize(); row++) {
				for (int col = 1; col <= board.getSize(); col++) {
					Code.decode(decoder);
					board.setPiece(row, col, (int) decoder.l);
				}
			}
			
			states[s] = new OthelloGame(currentPlayer, board);
		}
		
	}

	
	@Override
	public String genotypeToStringForHumans() {
		StringBuilder result = new StringBuilder();
		for (int s = 0; s < states.length; s++) {
			result.append(states[s].toString());
		}
		return result.toString();
	}
}
