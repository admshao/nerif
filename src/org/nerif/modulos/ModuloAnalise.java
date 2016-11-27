package org.nerif.modulos;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.nerif.estatistica.EstatisticaArquivo;
import org.nerif.estatistica.EstatisticasAnalise;
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
			System.out.println("Build da arvore em...");
			long start = System.nanoTime();
			tree = new DecisionTree(rows);
			long total = System.nanoTime() - start;
			System.out.println("Nano -> " + total);
			System.out.println("Seila -> " + total / 1000d);
			System.out.println("milis -> " + total / 1000000d);
			System.out.println("segundos -> " + total / 1000000000d);
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
			tree.classify(new ArrayList<String>() {
				private static final long serialVersionUID = 8937334946032327062L;
				{
					for (FormatoLog formato : Config.colunasLog) {
						add(coluns.get(formato.getInfoPropriedade().name()));
					}
				}
			});
		}
	}

	public void gerarRelatorio(final EstatisticaArquivo estatisticasArquivo) {
		List<List<String>> rows = preparaLinhasArvore(estatisticasArquivo.getEstatisticasAnalise());

		try {
			Path p = Paths.get(Config.URI_TRAINING);
			Files.write(p, Config.GSON.toJson(conjuntoTreinamento).getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Build da arvore em...");
		long start = System.nanoTime();
		tree = new DecisionTree(rows);
		long total = System.nanoTime() - start;
		System.out.println("Nano -> " + total);
		System.out.println("Seila -> " + total / 1000d);
		System.out.println("milis -> " + total / 1000000d);
		System.out.println("segundos -> " + total / 1000000000d);
		// tree.print();

		System.out.println(tree.classify(new ArrayList<String>() {
			private static final long serialVersionUID = -413906126211789308L;
			{
				add("2016-02-29");
				add("10:50:40");
				add("8000");
				add("/Admin.ww8/clientServerSync");
				add("25000");
			}
		}));
		System.out.println(tree.classify(new ArrayList<String>() {
			private static final long serialVersionUID = -413906126211789308L;
			{
				add("2016-02-29");
				add("09:20:10");
				add("8000");
				add("/Admin.ww8/clientServerSync");
				add("5000000");
			}
		}));
	}

	private List<List<String>> preparaLinhasArvore(EstatisticasAnalise estatisticas) {
		List<List<String>> rows = new ArrayList<>();
		List<HashMap<String, String>> novos = new ArrayList<>();

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
				data = new ArrayList<>();
				for (FormatoLog formato : Config.colunasLog) {
					data.add(mapBom.get(formato.getInfoPropriedade().name()));
				}
				data.add(Config.BOM);
				rows.add(data);
			} else if (estatisticas != null) {
				Long val = estatisticas.getUrlDuracaoEstatistica().get(k);
				if (val != null) {
					data = new ArrayList<>();
					mapBom = new HashMap<>();

					String urlTempoMedio = String.valueOf(val / estatisticas.getUrlQuantidadeEstatistica().get(k));

					for (FormatoLog formato : Config.colunasLog) {
						if (formato.getInfoPropriedade().name().equals(InfoPropriedade.TEMPO.name())) {
							mapBom.put(formato.getInfoPropriedade().name(), urlTempoMedio);
						} else {
							mapBom.put(formato.getInfoPropriedade().name(),
									mapRuim.get(formato.getInfoPropriedade().name()));
						}

						data.add(mapBom.get(formato.getInfoPropriedade().name()));
					}

					novos.add(mapBom);
					data.add(Config.BOM);
					rows.add(data);
				}
			}
		});

		novos.forEach(mapa -> {
			conjuntoTreinamento.urlVerificada.get(mapa.get(InfoPropriedade.URL.name())).put(Config.BOM, mapa);
		});

		return rows;
	}
}