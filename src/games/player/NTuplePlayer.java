package games.player;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;
import games.Board;

import java.util.Arrays;

import cecj.app.othello.OthelloSymmetryExpander;
import cecj.ntuple.NTupleIndividual;
import cecj.ntuple.NTupleSystem;

public class NTuplePlayer implements EvolvedPlayer {

	private static final String P_NTUPLE_SYSTEM = "ntuple-system";

	private NTuple[] tuples;

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
		// TODO Auto-generated method stub

	}

	public void TDLUpdate(Board previous, double delta, double[][] traces, double lambda) {
		// TODO Auto-generated method stub

	}

	public void reset() {
		for (int i = 0; i < tuples.length; i++) {
			double[] weights = tuples[i].getWeights();
			Arrays.fill(weights, 0.0);
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

		return ind;
	}
}
