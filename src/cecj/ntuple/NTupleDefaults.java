package cecj.ntuple;

import ec.DefaultsForm;
import ec.util.Parameter;

public class NTupleDefaults implements DefaultsForm {
	
	public static final String P_NTUPLE = "ntuple";

	public static final Parameter base() {
		return new Parameter(P_NTUPLE);
	}
}
