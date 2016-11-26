package org.nerif;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.nerif.ml.DecisionTree;
import org.nerif.model.ConjuntoTreinamentoArvore;
import org.nerif.model.FormatoLog;
import org.nerif.model.InfoPropriedade;
import org.nerif.util.Config;

public class ModuloAnalise {

	private static ModuloAnalise instance = null;
	private ConjuntoTreinamentoArvore conjuntoTreinamento = null;
	private DecisionTree tree = null;

	public static ModuloAnalise getInstance() {
		if (instance == null)
			instance = new ModuloAnalise();
		return instance;
	}

	private ModuloAnalise() {
		if (Config.initTree) {
			conjuntoTreinamento = Config.conjuntoTreinamento;
			List<List<String>> rows = preparaLinhasArvore(null);
			tree = new DecisionTree(rows);
		} else {
			conjuntoTreinamento = new ConjuntoTreinamentoArvore();
		}
	}

	public void processaLinha(final HashMap<String, String> coluns, final boolean indicadorAtivado) {
		if (indicadorAtivado) {
			String url = coluns.get(InfoPropriedade.URL.name());
			if (!conjuntoTreinamento.urlVerificada.containsKey(url)) {
				HashMap<String, HashMap<String, String>> maps = new HashMap<>();
				maps.put(Config.RUIM, coluns);
				conjuntoTreinamento.urlVerificada.put(url, maps);
			}
			conjuntoTreinamento.urlQuantidade.compute(url, (k, v) -> v == null ? 1l : v + 1);
			long tempo = Long.valueOf(coluns.get(InfoPropriedade.TEMPO.name()));
			conjuntoTreinamento.urlMedia.compute(url, (k, v) -> v == null ? tempo : v + tempo);
		}

		if (tree != null) {

		}
	}

	public void gerarRelatorio() {
		EstatisticaArquivo estatisticas = ModuloEstatistico.getInstance().getEstatisticasURL();

		List<List<String>> rows = preparaLinhasArvore(estatisticas);

		try {
			Path p = Paths.get(Config.URI_TRAINING);
			Files.write(p, Config.GSON.toJson(conjuntoTreinamento).getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}

		long start = System.nanoTime();
		tree = new DecisionTree(rows);
		long total = System.nanoTime() - start;
		System.out.println("Nano -> " + total);
		System.out.println("Seila -> " + total / 1000);
		System.out.println("milis -> " + total / 1000000);
		System.out.println("segundos -> " + total / 1000000000);
		tree.print();
	}

	private List<List<String>> preparaLinhasArvore(EstatisticaArquivo estatisticas) {
		List<List<String>> rows = new ArrayList<>();

		conjuntoTreinamento.urlVerificada.forEach((k, v) -> {
			List<String> data = new ArrayList<>();
			HashMap<String, String> mapRuim = v.get(Config.RUIM);
			HashMap<String, String> mapBom = v.get(Config.BOM);

			for (FormatoLog formato : Config.colunasLog) {
				data.add(mapRuim.get(formato.getInfoPropriedade().name()));
			}
			data.add(Config.RUIM);
			rows.add(data);

			if (mapBom != null) {
				for (FormatoLog formato : Config.colunasLog) {
					data.add(mapBom.get(formato.getInfoPropriedade().name()));
				}
				data.add(Config.BOM);
				rows.add(data);
			} else if (estatisticas != null) {
				mapBom = new HashMap<>();

				String urlTempoMedio = String.valueOf(estatisticas.getUrlDuracaoEstatistica().get(k)
						/ estatisticas.getUrlQuantidadeEstatistica().get(k));

				for (FormatoLog formato : Config.colunasLog) {
					if (formato.getInfoPropriedade().name().equals(InfoPropriedade.TEMPO.name())) {
						mapBom.put(formato.getInfoPropriedade().name(), urlTempoMedio);
					} else {
						mapBom.put(formato.getInfoPropriedade().name(),
								mapRuim.get(formato.getInfoPropriedade().name()));
					}

					data.add(mapBom.get(formato.getInfoPropriedade().name()));
				}

				data.add(Config.BOM);
				rows.add(data);
			}
		});
		return rows;
	}
}