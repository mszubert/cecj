package cecj.interaction;

public class IdentityInterpreter implements InteractionResultInterpreter {

	public int getCandidateValue(int interactionResult) {
		return interactionResult;
	}

	public int getTestValue(int interactionResult) {
		return getCandidateValue(-interactionResult);
	}

}
