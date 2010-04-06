package cecj.interaction;

public class WinDrawLossResult implements InteractionResult {

	public enum Result {
		LOSS, DRAW, WIN
	}

	private Result result;

	public WinDrawLossResult(Result result) {
		this.result = result;
	}

	public boolean betterThan(InteractionResult other) {
		if (!(other instanceof WinDrawLossResult)) {
			throw new IllegalArgumentException(
				"Interaction result comparison must be done within the same type of results.");
		} else {
			return (this.result.ordinal() > ((WinDrawLossResult) other).result.ordinal());
		}
	}

	public float getNumericValue() {
		if (result == Result.LOSS) {
			return 0;
		} else if (result == Result.DRAW) {
			return 1;
		} else {
			return 3;
		}
	}
	
	public boolean getBooleanValue() {
		if (result == Result.LOSS) {
			return false; 
		} else {
			return true;
		}
	}

	@Override
	public String toString() {
		return result.name().charAt(0) + "";
	}
}
