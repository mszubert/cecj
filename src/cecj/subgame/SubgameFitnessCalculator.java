package cecj.subgame;

import ec.EvolutionState;
import ec.Individual;
import ec.Setup;

public interface SubgameFitnessCalculator extends Setup {

	int calculatePoints(EvolutionState state, Individual candidate, SubgameIndividual subgame);

}
