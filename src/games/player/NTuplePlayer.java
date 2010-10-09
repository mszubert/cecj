package games.player;

import cecj.ntuple.NTupleIndividual;
import ec.Individual;
import games.Board;

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
			
		} else {
			throw new IllegalArgumentException("Individual should be of type NTupleIndividual");
		}
	}

	public void writeToIndividual(Individual ind) {
		// TODO Auto-generated method stub
		
	}

}
