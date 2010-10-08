package games;

public class NTuplePlayer implements Player {

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

}
