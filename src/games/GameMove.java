package games;

public interface GameMove extends Comparable<GameMove> {
	
	public int getRow();

	public int getCol();
	
	public Board getAfterState();
}
