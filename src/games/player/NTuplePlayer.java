package games.player;

import ec.Individual;
import games.Board;
import cecj.app.othello.OthelloSymmetryExpander;
import cecj.ntuple.NTupleIndividual;

public class NTuplePlayer implements EvolvedPlayer {

	private NTuple[] tuples;
	
	public void TDLUpdate(Board previous, double delta) {
		// TODO Auto-generated method stub

	}

	public void TDLUpdate(Board previous, double delta, double[][] traces, double lambda) {
		// TODO Auto-generated method stub

	}

	public double evaluate(Board board) {
		double result = 0;
		for (NTuple tuple: tuples) {
			result += tuple.value(board);
		}
		return result;
	}

	public EvolvedPlayer createEmptyCopy() {
		// TODO Auto-generated method stub
		return null;
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

	public void writeToIndividual(Individual ind) {
		if (ind instanceof NTupleIndividual) {
			int[][] positions = new int[tuples.length][];
			double[][] weights = new double[tuples.length][];
			
			for (int i = 0; i < tuples.length; i++) {
				positions[i] = tuples[i].getPositions();
				weights[i] = tuples[i].getWeights();
			}
			
			NTupleIndividual ntuple = ((NTupleIndividual) ind);
			ntuple.setPositions(positions);
			ntuple.setWeights(weights);
		} else {
			throw new IllegalArgumentException("Individual should be of type NTupleIndividual");
		}
	}

}
