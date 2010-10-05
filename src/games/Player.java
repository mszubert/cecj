package games;

public interface Player {
	
	public void TDLUpdate(Board previous, double delta);
	
	public void TDLUpdate(Board previous, double delta, double[][] traces, double lambda);
	
	public double evaluate(Board board);
}
