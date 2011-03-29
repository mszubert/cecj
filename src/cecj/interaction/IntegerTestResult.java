package cecj.interaction;

public class IntegerTestResult implements TestResult {

	private int result;
	
	public IntegerTestResult(int result) {
		this.result = result;
	}
	
	public float getCandidateScore() {
		return result;
	}

	public float getTestScore() {
		return -result;
	}

}
