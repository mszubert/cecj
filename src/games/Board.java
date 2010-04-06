package games;

public interface Board extends Cloneable {

	public int getSize();
	
	public int countPieces(int i);

	public int getPiece(int row, int col);

	public boolean isEmpty(int row, int col);

	public double evaluate(Player player);

	public int getValueAt(int row, int col);

	public Board clone();
}
