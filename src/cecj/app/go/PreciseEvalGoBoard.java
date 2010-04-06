package cecj.app.go;

import games.Player;

import java.util.Arrays;

public class PreciseEvalGoBoard extends GoBoard {
	
	@Override
	public double evaluate(Player player) {
		int[][] cc = new int[BOARD_SIZE + BOARD_MARGIN][BOARD_SIZE + BOARD_MARGIN];
		int[] territory = new int[BOARD_SIZE * BOARD_SIZE];
		Arrays.fill(territory, -1);

		markTerritory(cc, territory);

		double result = 0;
		for (int row = 1; row <= BOARD_SIZE; row++) {
			for (int col = 1; col <= BOARD_SIZE; col++) {
				if (isEmpty(row, col)) {
					result += getColorValue(territory[cc[row][col]]) * player.getValue(row, col);
				} else {
					result += getValueAt(row, col) * player.getValue(row, col);
				}
			}
		}
		return result;
	}
}
