package framsticks;

import ec.DefaultsForm;
import ec.util.Parameter;

public final class FramsticksDefaults implements DefaultsForm {

	public static final String P_FRAMSTICKS = "framsticks";
	
	public static final Parameter base() {
		return new Parameter(P_FRAMSTICKS);
	}
}
