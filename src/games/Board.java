package games;

public interface Board extends Cloneable {

	public int getSize();
	
	public int countPieces(int i);

	public int getPiece(int flatLocation);
	
	public int getPiece(int row, int col);

	public void setPiece(int row, int col, int player);
	
	public boolean isEmpty(int row, int col);
	
	public int getValueAt(int flatLocation);

	public int getValueAt(int row, int col);

	public Board createAfterState(int row, int col, int player);
	
	public Board clone();
}
