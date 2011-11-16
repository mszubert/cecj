package cecj.subgame;

import ec.DefaultsForm;
import ec.util.Parameter;

public class SubgameDefaults implements DefaultsForm {

	public static final String P_SUBGAME = "subgame";

	public static final Parameter base() {
		return new Parameter(P_SUBGAME);
	}
}
