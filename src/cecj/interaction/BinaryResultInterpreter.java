package cecj.interaction;

public class BinaryResultInterpreter implements InteractionResultInterpreter {

	public static final int WIN = 1;
	public static final int LOSS = 0;
	
	public int getCandidateValue(int interactionResult) {
		if (interactionResult > 0) {
			return WIN;
		} else {
			return LOSS;
		}
 	}

	public int getTestValue(int interactionResult) {
		if (interactionResult > 0) {
			return LOSS;
		} else {
			return WIN;
		}
	}

}
