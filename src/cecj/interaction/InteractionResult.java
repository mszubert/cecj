package cecj.interaction;

public interface InteractionResult {
	
	public boolean betterThan(InteractionResult other);
	
	public float getNumericValue();
}
