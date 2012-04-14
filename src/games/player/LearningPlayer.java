package games.player;

import games.Board;

public interface LearningPlayer extends Player {
	
	public void initializeEligibilityTraces();
	
	public void TDLUpdate(Board previous, double delta);
	
	public void TDLUpdate(Board previous, double delta, double lambda);
	
	public void prepareForOfflineLearning();

	public void updateWeights(Board board, double d);

	public LearningPlayer clone();

	public double[] getWeightDerivatives(Board[] boards, double[] errors);

	public void updateWeights(double[] derivatives);
}
