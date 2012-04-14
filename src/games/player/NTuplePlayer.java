package games.player;

import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleFitness;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import games.Board;

import java.util.Arrays;
import java.util.StringTokenizer;

import cecj.app.othello.OthelloSymmetryExpander;
import cecj.ntuple.NTupleIndividual;
import cecj.ntuple.NTupleSystem;

public class NTuplePlayer implements EvolvedPlayer, LearningPlayer {

	private static final String P_NTUPLE_SYSTEM = "ntuple-system";

	private NTuple[] tuples;

	public int getNumTuples() {
		return tuples.length;
	}
	
	public NTuple getTuple(int t) {
		return tuples[t];
	}
	
	public void setup(EvolutionState state, Parameter base) {
		NTupleSystem system = new NTupleSystem();
		system.setup(state, base.push(P_NTUPLE_SYSTEM));

		NTupleIndividual ind = new NTupleIndividual();
		system.randomizeIndividual(state, 0, ind);

		readFromIndividual(ind);
	}

	public double evaluate(Board board) {
		double result = 0;
		for (NTuple tuple : tuples) {
			result += tuple.value(board);
		}
		return result;
	}

	public EvolvedPlayer createEmptyCopy() {
		return new NTuplePlayer();
	}

	public void TDLUpdate(Board previous, double delta) {
		for (NTuple tuple : tuples) {
			tuple.updateWeights(previous, delta);
		}
	}

	public void initializeEligibilityTraces() {
		for (NTuple tuple : tuples) {
			tuple.initializeEligibilityTraces();
		}
	}
	
	public void TDLUpdate(Board previous, double delta, double lambda) {
		double evalBefore = Math.tanh(evaluate(previous));
		double derivative = (1 - (evalBefore * evalBefore));

		for (NTuple tuple : tuples) {
			tuple.updateWeights(previous, delta, lambda, derivative);
		}
	}

	public void reset() {
		for (int i = 0; i < tuples.length; i++) {
			double[] weights = tuples[i].getWeights();
			Arrays.fill(weights, 0.0);
		}
	}

	public void randomizeWeights(MersenneTwisterFast random, double range) {
		for (int i = 0; i < tuples.length; i++) {
			double[] weights = tuples[i].getWeights();
			for (int j = 0; j < weights.length; j++) {
				weights[j] = random.nextDouble() * range;
				if (random.nextBoolean()) {
					weights[j] *= -1;
				}
			}
		}
	}
	
	public void readFromIndividual(Individual ind) {
		if (ind instanceof NTupleIndividual) {
			NTupleIndividual ntuple = ((NTupleIndividual) ind);
			int[][] positions = ntuple.getPositions();
			double[][] weights = ntuple.getWeights();

			tuples = new NTuple[positions.length];
			for (int i = 0; i < tuples.length; i++) {
				tuples[i] = new NTuple(positions[i], weights[i], new OthelloSymmetryExpander());
			}
		} else {
			throw new IllegalArgumentException("Individual should be of type NTupleIndividual");
		}
	}

	@Override
	public LearningPlayer clone() {
		NTuplePlayer clone = new NTuplePlayer();
		
		return clone;
	}
	
	public Individual createIndividual() {
		NTupleIndividual ind = new NTupleIndividual();

		int[][] positions = new int[tuples.length][];
		double[][] weights = new double[tuples.length][];

		for (int i = 0; i < tuples.length; i++) {
			positions[i] = tuples[i].getPositions();
			weights[i] = tuples[i].getWeights();
		}

		ind.setPositions(positions);
		ind.setWeights(weights);
		ind.fitness = new SimpleFitness();

		return ind;
	}
	
	public void readFromString(String s) {
		StringTokenizer tokenizer = new StringTokenizer(s);
		while (!tokenizer.nextToken().equals("{"));
		
		int tuplesNum = Integer.parseInt(tokenizer.nextToken());
		tuples = new NTuple[tuplesNum];
		
		for (int i = 0; i < tuplesNum; i++) {
			tuples[i] = NTuple.readNTuple(tokenizer);
		}

		while (!tokenizer.nextToken().equals("}"));
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{\n" + tuples.length + "\n");
		for (NTuple tuple : tuples) {
			builder.append(tuple.toString());
		}
		builder.append("}");
		
		return builder.toString();
	}

	public void prepareForOfflineLearning() {
		// TODO Auto-generated method stub
		
	}

	public void updateWeights(Board board, double d) {
		// TODO Auto-generated method stub
		
	}

	public double[] getWeightDerivatives(Board[] boards, double[] errors) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateWeights(double[] derivatives) {
		// TODO Auto-generated method stub
		
	}
}
