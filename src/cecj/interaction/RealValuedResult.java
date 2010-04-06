package cecj.interaction;

/**
 * Immutable class representing real-valued interaction results.
 * 
 * @author Marcin Szubert
 * 
 */
public class RealValuedResult implements InteractionResult {

	private float value;

	public RealValuedResult(float value) {
		this.value = value;
	}

	public boolean betterThan(InteractionResult other) {
		if (!(other instanceof RealValuedResult)) {
			throw new IllegalArgumentException(
				"Interaction result comparison must be done within the same type of results.");
		} else {
			return (this.value > ((RealValuedResult) other).value);
		}
	}

	public float getNumericValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RealValuedResult)) {
			return false;
		}

		return (((RealValuedResult) obj).value == this.value);
	}
	
	@Override
	public String toString() {
		return Float.toString(value);
	}
}
