package cecj.archive.wojtek;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cecj.archive.ParetoCoevolutionArchive;
import cecj.utils.Pair;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class LAPCA extends ParetoCoevolutionArchive {

	private static final String P_NUM_LAYERS = "num-layers";

	private int numLayers;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		Parameter numLayersParameter = base.push(P_NUM_LAYERS);
		numLayers = state.parameters.getInt(numLayersParameter, null, 1);
		if (numLayers <= 0) {
			state.output.fatal("Number of LAPCA layers must be > 0.\n");
		}
	}

	@Override
	protected void submit(EvolutionState state, List<Individual> candidates,
			List<Individual> cArchive, List<Individual> tests, List<Individual> tArchive) {
		addUnique(state, candidates, cArchive, tests, tArchive);
		boolean[][] dominationDAG = findDominationDAG(state, cArchive, tArchive);
		int[] layerRank = findLongestPathFromSource(dominationDAG);
		removeUnnecessaryTests(state, tArchive, cArchive, layerRank);
		removeUnrankedCandidates(cArchive, layerRank);
	}

	private void removeUnnecessaryTests(EvolutionState state, List<Individual> tArchive,
			List<Individual> cArchive, int[] layerRank) {

		List<Pair<Integer>> distinctions = new ArrayList<Pair<Integer>>();
		for (int c1 = 0; c1 < cArchive.size(); c1++) {
			if (layerRank[c1] == -1) {
				continue;
			}

			for (int c2 = 0; c2 < cArchive.size(); c2++) {
				if (layerRank[c2] == -1 || c1 == c2) {
					continue;
				}

				if (layerRank[c1] == layerRank[c2] || layerRank[c1] + 1 == layerRank[c2]) {
					distinctions.add(new Pair<Integer>(c1, c2));
				}
			}
		}

		List<Individual> testsToRemove = new ArrayList<Individual>();
		List<Pair<Integer>> madeDistinctions = new ArrayList<Pair<Integer>>();
		for (Individual test : tArchive) {
			madeDistinctions.clear();
			for (Pair<Integer> dist : distinctions) {
				if (problem.solves(state, cArchive.get(dist.first), test)
						&& !problem.solves(state, cArchive.get(dist.second), test)) {
					madeDistinctions.add(dist);
				}
			}
			
			if (madeDistinctions.size() > 0) {
				distinctions.removeAll(madeDistinctions);
			} else {
				testsToRemove.add(test);
			}
		}

		tArchive.removeAll(testsToRemove);
	}

	private void removeUnrankedCandidates(List<Individual> archive, int[] layerRank) {
		List<Individual> toRemove = new ArrayList<Individual>();
		for (int ind = 0; ind < archive.size(); ind++) {
			if (layerRank[ind] == -1) {
				toRemove.add(archive.get(ind));
			}
		}

		archive.removeAll(toRemove);
	}

	private int[] findLongestPathFromSource(boolean[][] dominationDAG) {
		int graphSize = dominationDAG.length;
		int[] distance = new int[graphSize];
		int[] inDegree = new int[graphSize];

		for (int i = 0; i < graphSize; i++) {
			for (int j = 0; j < graphSize; j++) {
				if (dominationDAG[i][j]) {
					inDegree[j]++;
				}
			}
		}

		Arrays.fill(distance, -1);
		Queue<Integer> sourceQueue = new LinkedList<Integer>();
		for (int i = 0; i < graphSize; i++) {
			if (inDegree[i] == 0) {
				distance[i] = 0;
				sourceQueue.add(i);
			}
		}

		while (!sourceQueue.isEmpty()) {
			int top = sourceQueue.poll();
			for (int i = 0; i < graphSize; i++) {
				if (dominationDAG[top][i]) {
					if ((--inDegree[i] == 0) && (distance[top] + 1 < numLayers)) {
						distance[i] = distance[top] + 1;
						sourceQueue.add(i);
					}
				}
			}
		}

		return distance;
	}

	private boolean[][] findDominationDAG(EvolutionState state, List<Individual> candidates,
			List<Individual> tests) {
		int archiveSize = candidates.size();
		boolean domination[][] = new boolean[archiveSize][archiveSize];
		
		for (int c1 = 0; c1 < archiveSize; c1++) {
			for (int c2 = 0; c2 < archiveSize; c2++) {
				if (c1 == c2) {
					continue;
				}
				
				if (dominatesOrEqual(state, candidates.get(c1), candidates.get(c2), tests)) {
					domination[c1][c2] = true;
				}
			}
		}
		
		return domination;
	}

	private void addUnique(EvolutionState state, List<Individual> candidates,
			List<Individual> cArchive, List<Individual> tests, List<Individual> tArchive) {

		for (Individual newCandidate : candidates) {
			boolean unique = true;

			for (int archiveIndex = 0; archiveIndex < cArchive.size(); archiveIndex++) {
				Individual archivalCandidate = cArchive.get(archiveIndex);
				if (areIndiscernibleCandidates(state, newCandidate, archivalCandidate, tArchive)
						&& areIndiscernibleCandidates(state, newCandidate, archivalCandidate, tests)) {
					unique = false;
					break;
				}
			}
			
			if (unique) {
				cArchive.add(newCandidate);
			}
		}

		for (Individual newTest : tests) {
			boolean unique = true;
			for (int archiveIndex = 0; archiveIndex < tArchive.size(); archiveIndex++) {
				Individual archivalTest = tArchive.get(archiveIndex);
				if (areIndiscernibleTests(state, newTest, archivalTest, cArchive)) {
					unique = false;
					break;
				}
			}

			if (unique) {
				tArchive.add(newTest);
			}
		}
	}

	private boolean areIndiscernibleTests(EvolutionState state, Individual test1, Individual test2,
			List<Individual> candidates) {
		for (Individual candidate : candidates) {
			if (problem.solves(state, candidate, test1) != problem.solves(state, candidate, test2)) {
				return false;
			}
		}

		return true;
	}

	private boolean areIndiscernibleCandidates(EvolutionState state, Individual candidate1,
			Individual candidate2, List<Individual> tests) {
		for (Individual test : tests) {
			if (problem.solves(state, candidate1, test) != problem.solves(state, candidate2, test)) {
				return false;
			}
		}

		return true;
	}
}
