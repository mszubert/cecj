package cecj.interaction;

public class FloatPairTestResult implements TestResult {

	private float candidateScore;
	private float testScore;
	
	public FloatPairTestResult(float candidateScore, float testScore) {
		this.candidateScore = candidateScore;
		this.testScore = testScore;
	}
	
	public float getCandidateScore() {
		return candidateScore;
	}

	public float getTestScore() {
		return testScore;
	}

}
