package org.nerif;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.nerif.ml.DecisionTree;
import org.nerif.model.InfoPropriedade;
import org.nerif.util.Config;

public class ModuloAnalise {

	private static ModuloAnalise instance = null;

	private HashMap<String, List<HashMap<String, String>>> urlJaVerificada = new HashMap<>();
	private HashMap<String, HashMap<String, String>> linhasNaoClassificadas = new HashMap<>();
	private List<List<String>> data;
	private List<String> result;

	private DecisionTree tree;

	public static ModuloAnalise getInstance() {
		if (instance == null)
			instance = new ModuloAnalise();
		return instance;
	}

	private ModuloAnalise() {
		data = new ArrayList<>();
		result = new ArrayList<>();
	}

	public void processaLinha(final HashMap<String, String> coluns, final boolean indicadorAtivado) {
		/*
		 * String url = coluns.get(InfoPropriedade.URL.name()); if
		 * (!urlJaVerificada.containsKey(url)) { if (indicadorAtivado) {
		 * data.add(coluns.values().stream().collect(Collectors.toList()));
		 * result.add(Config.RUIM); urlJaVerificada.put(url, coluns); } }
		 */
		if (indicadorAtivado) {
			data.add(coluns.values().stream().collect(Collectors.toList()));
			result.add(Config.RUIM);
			String url = coluns.get(InfoPropriedade.URL.name());
			if (!urlJaVerificada.containsKey(url)) {
				urlJaVerificada.put(url, new ArrayList<HashMap<String, String>>() {
					private static final long serialVersionUID = 4550248614142415584L;

					{
						add(coluns);
					}
				});
			} else {
				urlJaVerificada.get(url).add(coluns);
			}
		}
	}

	public void dump() {
		Config.lock.lock();
		
		/*EstatisticaArquivo estatisticas = ModuloEstatistico.getInstance().getEstatisticaArquivo();
		urlJaVerificada.forEach((k, v) -> {
			String urlTempoMedio = String.valueOf(
					estatisticas.getUrlDuracaoEstatistica().get(k) / estatisticas.getUrlQuantidadeEstatistica().get(k));
			v.forEach(hash -> {
				hash.put(InfoPropriedade.TEMPO.name(), urlTempoMedio);
				data.add(hash.values().stream().collect(Collectors.toList()));
				result.add(Config.BOM);
			});
		});*/

		tree = new DecisionTree(data, result);
		tree.build();
		tree.print();

		/*System.out.println(tree.classify(new ArrayList<String>() {
			private static final long serialVersionUID = -413906126211789308L;

			{
				add("25000");
				add("8000");
				add("2016-02-29");
				add("/Admin.ww8/clientServerSync");
				add("10:50:40");
			}
		}));
		System.out.println(tree.classify(new ArrayList<String>() {
			private static final long serialVersionUID = -413906126211789308L;

			{
				add("5000000");
				add("8000");
				add("2016-02-29");
				add("/Admin.ww8/clientServerSync");
				add("09:20:10");
			}
		}));*/

		Config.lock.unlock();
	}
}