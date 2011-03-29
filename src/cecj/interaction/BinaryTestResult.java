package cecj.interaction;

public class BinaryTestResult implements TestResult {

	boolean result;
	
	public BinaryTestResult(int result) {
		this.result = (result > 0);
	}

	public float getCandidateScore() {
		return result ? 1 : 0;
	}

	public float getTestScore() {
		return result ? 0 : 1;
	}

}
