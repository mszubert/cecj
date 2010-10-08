package cecj.app.othello;

import games.player.WPCPlayer;
import cecj.app.WPCPlayerFitnessCalculator;

public class OthelloBestEvolvedPlayer extends WPCPlayerFitnessCalculator {

	double[] wpc = { 4.622507f, -1.477853f, 1.409644f, -0.066975f, -0.305214f, 1.633019f,
			-1.050899f, 4.365550f, -1.329145f, -2.245663f, -1.060633f, -0.541089f, -0.332716f,
			-0.475830f, -2.274535f, -0.032595f, 2.681550f, -0.906628f, 0.229372f, 0.059260f,
			-0.150415f, 0.321982f, -1.145060f, 2.986767f, -0.746066f, -0.317389f, 0.140040f,
			-0.045266f, 0.236595f, 0.158543f, -0.720833f, -0.131124f, -0.305566f, -0.328398f,
			0.073872f, -0.131472f, -0.172101f, 0.016603f, -0.511448f, -0.264125f, 2.777411f,
			-0.769551f, 0.676483f, 0.282190f, 0.007184f, 0.269876f, -1.408169f, 2.396238f,
			-1.566175f, -3.049899f, -0.637408f, -0.077690f, -0.648382f, -0.911066f, -3.329772f,
			-0.870962f, 5.046583f, -1.468806f, 1.545046f, -0.031175f, 0.263998f, 2.063148f,
			-0.148002f, 5.781035f };

	@Override
	protected WPCPlayer getOpponent(int size) {
		return new WPCPlayer(wpc);
	}
}
