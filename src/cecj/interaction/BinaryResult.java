package cecj.interaction;

public class BinaryResult implements InteractionResult {
	
	private boolean result;
	
	public BinaryResult(boolean result)
	{
		this.result = result;
	}
	
	public boolean betterThan(InteractionResult other) {
		if (!(other instanceof BinaryResult)) {
			throw new IllegalArgumentException(
				"Interaction result comparison must be done within the same type of results.");
		} else {
			return (result && !((BinaryResult) other).result);
		}
	}

	public float getNumericValue() {
		return ((result) ? 1 : 0);
	}

}
