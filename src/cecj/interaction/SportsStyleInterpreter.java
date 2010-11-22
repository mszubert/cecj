package cecj.interaction;

public class SportsStyleInterpreter implements InteractionResultInterpreter {

	public static final int WIN = 3;
	public static final int DRAW = 1;
	public static final int LOSS = 0;
	
	public int getCandidateValue(int interactionResult) {
		if (interactionResult > 0) {
			return WIN;
		} else if (interactionResult == 0) {
			return DRAW;
		} else {
			return LOSS;
		}
	}

	public int getTestValue(int interactionResult) {
		return getCandidateValue(-interactionResult);
	}

}
