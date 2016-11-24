package org.nerif.ml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.nerif.util.Config;

@SuppressWarnings("serial")
public class DecisionTree {

	private List<List<String>> data;
	private List<String> result;
	private DecisionNode tree;

	private static HashMap<Integer, Double> rowEntropy;
	private static HashMap<Integer, HashMap<String, Long>> rowUniques;
	private static HashMap<String, List<List<List<String>>>> rowDividedSets;

	public DecisionTree(List<List<String>> data, List<String> result) {
		this.tree = null;
		this.data = data;
		this.result = result;
		rowEntropy = new HashMap<>();
		rowUniques = new HashMap<>();
		rowDividedSets = new HashMap<>();
	}

	public void print() {
		if (tree != null) {
			tree.print();
		}
	}

	public DecisionNode build() {
		final List<List<String>> rows = new ArrayList<>();
		for (int i = 0; i != data.size(); i++) {
			data.get(i).add(result.get(i));
			rows.add(data.get(i));
		}
		tree = buildTree(rows);
		return tree;
	}

	public void prune(final double mingain) {
		prune(tree, mingain);
	}

	private void prune(final DecisionNode node, final double mingain) {
		if (node.tb.results == null) {
			prune(node.tb, mingain);
		}
		if (node.fb.results == null) {
			prune(node.fb, mingain);
		}

		if (node.tb.results != null && node.fb.results != null) {
			List<List<String>> tb = new ArrayList<>();
			List<List<String>> fb = new ArrayList<>();
			List<List<String>> tbfb = new ArrayList<>();

			System.out.println("TB");
			node.tb.results.forEach((key, value) -> {
				System.out.println(key + " " + value);
				for (int i = 0; i != value; i++) {
					tb.add(new ArrayList<String>() {
						{
							add(key);
						}
					});
					tbfb.add(new ArrayList<String>() {
						{
							add(key);
						}
					});
				}
			});

			System.out.println("FB");

			node.fb.results.forEach((key, value) -> {
				System.out.println(key + " " + value);
				for (int i = 0; i != value; i++) {
					fb.add(new ArrayList<String>() {
						{
							add(key);
						}
					});
					tbfb.add(new ArrayList<String>() {
						{
							add(key);
						}
					});
				}
			});

			double p = 1.0 * tb.size() / tbfb.size();
			double delta = entropy(tbfb) - (p * entropy(tb)) - ((1 - p) * entropy(fb));
			if (delta < mingain) {
				node.tb = null;
				node.fb = null;
				node.results = uniqueCounts(tbfb);
			}
		}
	}

	public boolean classify(List<String> entry) {
		return classify(tree, entry);
	}

	private boolean classify(final DecisionNode node, final List<String> entry) {
		if (node.results != null) {
			return node.results.get(Config.BOM) > node.results.get(Config.RUIM);
		}
		final String arg1 = entry.get(node.col);
		DecisionNode branchNode;

		if (isNumeric(arg1)) {
			branchNode = Double.parseDouble(arg1) >= Double.parseDouble(node.value) ? node.tb : node.fb;
		} else {
			branchNode = arg1.equals(node.value) ? node.tb : node.fb;
		}
		return classify(branchNode, entry);
	}

	public DecisionNode buildTree(final List<List<String>> rows) {
		if (rows.isEmpty())
			return new DecisionNode();
		double currentScore = entropy(rows);
		double bestGain = 0;
		int columnCount = rows.get(0).size() - 1;
		final HashMap<Integer, String> bestCriteria = new HashMap<>();
		List<List<List<String>>> bestSets = new ArrayList<>();
		for (int col = 0; col < columnCount; col++) {
			List<String> columnValues = new ArrayList<>();
			for (int i = 0; i < rows.size(); i++) {
				columnValues.add(rows.get(i).get(col));
			}
			for (String o : columnValues) {
				List<List<List<String>>> sets = divideSet(rows, col, o);
				double p = 1. * sets.get(0).size() / rows.size();
				double gain = currentScore - p * entropy(sets.get(0)) - (1 - p) * entropy(sets.get(1));
				if (gain > bestGain && sets.get(0).size() > 0 && sets.get(1).size() > 0) {
					bestGain = gain;
					bestCriteria.clear();
					bestCriteria.put(col, o);
					bestSets = sets;
				}
			}
		}
		if (bestGain > 0) {
			final DecisionNode trueBranch = buildTree(bestSets.get(0));
			final DecisionNode falseBranch = buildTree(bestSets.get(1));
			return new DecisionNode() {
				{
					col = bestCriteria.keySet().iterator().next();
					value = bestCriteria.values().iterator().next();
					tb = trueBranch;
					fb = falseBranch;
				}
			};
		} else {
			return new DecisionNode() {
				{
					results = uniqueCounts(rows);
				}
			};

		}
	}

	public double entropy(final List<List<String>> rows) {
		final int rowHash = rows.hashCode();
		if (rowEntropy.containsKey(rowHash))
			return rowEntropy.get(rowHash);
		double ent = 0;
		final HashMap<String, Long> results = uniqueCounts(rows);
		for (String o : results.keySet()) {
			final double p = 1.0 * (results.get(o) + 1) / rows.size();
			ent -= 1.0 * p * (Math.log(p) / Math.log(2));
		}
		rowEntropy.put(rowHash, ent);
		return ent;
	}

	public HashMap<String, Long> uniqueCounts(final List<List<String>> rows) {
		final int rowHash = rows.hashCode();
		if (rowUniques.containsKey(rowHash))
			return rowUniques.get(rowHash);
		final HashMap<String, Long> results = new HashMap<>();
		results.put(Config.BOM, rows.parallelStream().filter(p -> p.get(p.size() - 1).equals(Config.BOM)).count());
		results.put(Config.RUIM, rows.parallelStream().filter(p -> p.get(p.size() - 1).equals(Config.RUIM)).count());
		rowUniques.put(rowHash, results);
		return results;
	}

	private boolean isNumeric(String str) {
		return str.matches("-?\\d+(\\.\\d+)?");
	}

	public List<List<List<String>>> divideSet(final List<List<String>> rows, final int col, final String value) {
		final String rowHash = col + ";" + value;
		if (rowDividedSets.containsKey(rowHash))
			return rowDividedSets.get(rowHash);
		List<List<List<String>>> sets = new ArrayList<List<List<String>>>() {
			{
				add(new ArrayList<>());
				add(new ArrayList<>());
			}
		};

		if (isNumeric(value)) {
			double valueD = Double.parseDouble(value);
			rows.forEach(v -> {
				boolean res = Double.parseDouble(v.get(col)) >= valueD;
				sets.get(res ? 0 : 1).add(v);
			});
		} else {
			rows.forEach(v -> {
				sets.get(v.get(col).equals(value) ? 0 : 1).add(v);
			});
		}

		rowDividedSets.put(rowHash, sets);
		return sets;
	}
}
