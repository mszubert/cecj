/*
  Copyright 2009 by Marcin Szubert
  Licensed under the Academic Free License version 3.0
 */

package cecj.eval;

import cecj.interaction.InteractionResult;
import cecj.problems.TestBasedProblem;
import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleFitness;
import ec.util.Parameter;

/**
 * Single elimination tournament competitive evaluator. It is different from the other
 * coevolutionary evaluators because interactions between individuals must be simulated in strict
 * order. It depends on the outcome of previous interaction if the individual can compete further.
 * 
 * Assumes that individuals use <code>SimpleFitness</code>. The fitness assigned by this method is
 * equal to the height of the tournament subtree that particular individual has traversed - the
 * number of games won. To reduce the inherent noise of the tournament evaluation scheme, a few
 * rounds can be played. The number of rounds is specified by a <code>repeats</code> parameter which
 * is equal to 1 by default. This evaluator can be used if problem being solved implements
 * <code>SymmetricCompetitionProblem</code> interface.
 * 
 * Since it would be hard to extend this evaluator with generic archiving or fitness sharing, only
 * the simplest settings are available.
 * 
 * @author Marcin Szubert
 * 
 */
public class TournamentCoevolutionaryEvaluator extends CoevolutionaryEvaluator {

	private static final String P_REPEATS = "repeats";

	/**
	 * Specifies how many times the tournament should be repeated during single evaluation process.
	 * More repeats can reduce the noise of this evaluation scheme.
	 */
	private int tournamentRepeats;


	/**
	 * Represents competing individuals.
	 */
	private Individual[] competitors;

	/**
	 * Number of competitors - size of the particular subpopulation.
	 */
	private int numCompetitors;

	/**
	 * Points gathered during the course of competition.
	 */
	private int[] points;

	/**
	 * An array used as a tournament tree representation. It stores indices of competing
	 * individuals. Neighboring indices compete with each other in certain round.
	 */
	private int[] competition;

	/**
	 * Stores active competitors ready to be divided into pairs.
	 */
	private int[] activeCompetitors;

	/**
	 * Indicates if particular competitor is still in game.
	 */
	private boolean[] active;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		problem = (TestBasedProblem) p_problem;
		
		Parameter repeatsParameter = base.push(P_REPEATS);
		tournamentRepeats = state.parameters.getIntWithDefault(repeatsParameter, null, 1);
		if (tournamentRepeats <= 0) {
			state.output.fatal("Tournament repeats parameter can not be negative.",
					repeatsParameter);
		}
	}

	@Override
	public void evaluatePopulation(EvolutionState state) {
		for (int subpop = 0; subpop < numSubpopulations; subpop++) {
			prepareTournament(state, subpop);
			for (int r = 0; r < tournamentRepeats; r++) {
				makeTournament(state);
			}
			assignFitness(state);
		}
	}

	/**
	 * Initializes structures used in the tournament series.
	 * 
	 * @param state
	 *            current evolutionary state
	 * @param subpop
	 *            index of subpopulation
	 */
	private void prepareTournament(EvolutionState state, int subpop) {
		competitors = state.population.subpops[subpop].individuals;
		numCompetitors = competitors.length;

		points = new int[numCompetitors];
		active = new boolean[numCompetitors];
		competition = new int[numCompetitors];
		activeCompetitors = new int[numCompetitors];
	}

	/**
	 * Plays a single tournament between earlier selected competitors from particular subpopulation.
	 * Each tournament consists of a sequence of rounds. In each round number of active competitors
	 * is reduced by half according to the results of their games (approximately - if at the start
	 * of the round number of players is odd, one player is given a "bye" and advances to the next
	 * round directly). At the beginning of each round there is a drawing which assigns competitors
	 * in pairs.
	 * 
	 * @param state
	 *            current evolutionary state
	 */
	private void makeTournament(EvolutionState state) {
		int numActiveCompetitors;
		for (int c = 0; c < numCompetitors; c++) {
			active[c] = true;
		}

		while ((numActiveCompetitors = findActiveCompetitors()) > 1) {
			shuffleCompetitors(state, numActiveCompetitors);
			playTournamentRound(state, numActiveCompetitors);
		}
	}

	/**
	 * Assigns fitness value to each competing individual according to overall points which it has
	 * gathered during the series of tournaments.
	 * 
	 * @param state
	 *            current evolutionary state
	 */
	private void assignFitness(EvolutionState state) {
		for (int c = 0; c < numCompetitors; c++) {
			Individual competitor = competitors[c];
			((SimpleFitness) competitor.fitness).setFitness(state, points[c], false);
		}
	}

	/**
	 * Finds still active competitors according to <code>active</code> array. Found competitor
	 * indices are stored in <code>activeCompetitors</code> array and their number is returned.
	 * 
	 * @return number of still active competitors
	 */
	private int findActiveCompetitors() {
		int leftCompetitors = 0;
		for (int c = 0; c < numCompetitors; c++) {
			if (active[c]) {
				activeCompetitors[leftCompetitors++] = c;
			}
		}
		return leftCompetitors;
	}

	/**
	 * Randomly shuffles competitors indices taken from <
	 * 
	 * @param state
	 *            current evolutionary state
	 * @param count
	 *            the number of shuffled competitors
	 */
	private void shuffleCompetitors(EvolutionState state, int count) {
		int left = count;
		for (int i = 0; i < count; i++) {
			int rand = state.random[0].nextInt(left);
			competition[i] = activeCompetitors[rand];
			activeCompetitors[rand] = activeCompetitors[--left];
		}
	}

	/**
	 * Arranges a competition between neighbors in <code>competition</code> array.
	 * 
	 * @param state
	 *            current evolutionary state
	 * @param numLeftCompetitors
	 *            the number of competitors left
	 */
	private void playTournamentRound(EvolutionState state, int numLeftCompetitors) {
		for (int i = 0; i + 1 < numLeftCompetitors; i += 2) {
			Individual c1 = competitors[competition[i]];
			Individual c2 = competitors[competition[i + 1]];

			// TODO: consider if it is needed to call compete method twice
			// maybe it should use internal individual's fitness or return both
			// results at once?
			InteractionResult score1 = problem.test(state, c1, c2).first;
			InteractionResult score2 = problem.test(state, c2, c1).first;

			if (score1.betterThan(score2)) {
				points[competition[i]]++;
				active[competition[i + 1]] = false;
			} else {
				points[competition[i + 1]]++;
				active[competition[i]] = false;
			}
		}

		// TODO: in case of odd number of competitors, should the one given a
		// "bye" achieve a point
		// in this round?
		if (numLeftCompetitors % 2 != 0) {
			points[competition[numLeftCompetitors - 1]]++;
		}
	}
}
