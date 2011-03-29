package cecj.statistics;

import java.io.File;
import java.io.IOException;

import ec.EvolutionState;
import ec.simple.SimpleStatistics;
import ec.util.Parameter;

public abstract class CoevolutionaryStatistics extends SimpleStatistics {

	public CoevolutionaryStatistics() {
		statisticslog = 0;
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		File statisticsFile = state.parameters.getFile(base.push(P_STATISTICS_FILE), null);

		if (statisticsFile != null) {
			try {
				statisticslog = state.output.addLog(statisticsFile, false, false, false);
			} catch (IOException i) {
				state.output.fatal("An IOException occurred while trying to create the log "
						+ statisticsFile + ":\n" + i);
			}
		}
	}

	public abstract void printInteractionResults(EvolutionState state, float[][] result, int subpop);

}
