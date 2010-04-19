package cecj.problems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cecj.interaction.InteractionResult;
import cecj.utils.Pair;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class TestBasedProblemCachingDecorator extends TestBasedProblem {
	public static final String P_CACHE_SIZE = "cache-size";
	public static final String P_INNER_PROBLEM = "inner-problem";

	public static final int UNBOUNDED_CACHE = Integer.MAX_VALUE;

	private Map<Pair<Individual>, Pair<? extends InteractionResult>> cache;
	private Map<Pair<Individual>, Integer> LRUtimer;

	private TestBasedProblem problem;
	private int cacheSizeLimit;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		Parameter innerProblemParam = base.push(P_INNER_PROBLEM);
		problem = (TestBasedProblem) state.parameters.getInstanceForParameter(innerProblemParam,
				null, TestBasedProblem.class);
		problem.setup(state, base);

		Parameter cacheSizeParam = base.push(P_CACHE_SIZE);
		cacheSizeLimit = state.parameters.getIntWithDefault(cacheSizeParam, null, UNBOUNDED_CACHE);

		cache = new HashMap<Pair<Individual>, Pair<? extends InteractionResult>>();
		LRUtimer = new HashMap<Pair<Individual>, Integer>();
	}

	public TestBasedProblem getProblem() {
		return problem;
	}

	@Override
	public Pair<? extends InteractionResult> test(EvolutionState state, Individual candidate,
			Individual test) {

		Pair<Individual> key = new Pair<Individual>(candidate, test);
		Pair<? extends InteractionResult> result = cache.get(key);
		if (result == null) {
			result = problem.test(state, candidate, test);
			cache.put(key, result);
		}

		LRUtimer.put(key, state.generation);
		if (cache.size() > cacheSizeLimit) {
			clearLeastRecentlyUsed();
		}

		return result;
	}

	/**
	 * Clears least recently used pairs of individuals. Sorts the list of stored pairs of
	 * individuals according to time of last use. Removes a half of cache storage.
	 */
	private void clearLeastRecentlyUsed() {
		List<Pair<Individual>> list = new ArrayList<Pair<Individual>>(LRUtimer.keySet());
		Collections.sort(list, new Comparator<Pair<Individual>>() {
			public int compare(Pair<Individual> o1, Pair<Individual> o2) {
				if (LRUtimer.get(o1) > LRUtimer.get(o2)) {
					return -1;
				} else if (LRUtimer.get(o1) < LRUtimer.get(o2)) {
					return 1;
				} else {
					return 0;
				}
			}
		});

		for (int i = cacheSizeLimit / 2; i < list.size(); i++) {
			cache.remove(list.get(i));
			LRUtimer.remove(list.get(i));
		}
	}
}
