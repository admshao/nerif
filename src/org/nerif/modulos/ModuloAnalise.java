package org.nerif.modulos;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.nerif.estatistica.EstatisticasAnalise;
import org.nerif.ml.AnaliseURL;
import org.nerif.ml.ConjuntoTreinamentoArvore;
import org.nerif.ml.DecisionTree;
import org.nerif.model.FormatoLog;
import org.nerif.model.InfoPropriedade;
import org.nerif.util.Config;

public class ModuloAnalise {

	private static ModuloAnalise instance = null;
	private ConjuntoTreinamentoArvore conjuntoTreinamento = null;
	private List<String> ultimasUrls = new ArrayList<>(Config.ULTIMAS_N_LINHAS);
	private int indexUltimasUrls = 0;

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
			System.out.println("segundos -> " + total / 1000000000d);
		} else {
			conjuntoTreinamento = new ConjuntoTreinamentoArvore();
		}
	}

	public void processaLinha(final HashMap<String, String> coluns, final boolean indicadorAtivado) {
		String url = coluns.get(InfoPropriedade.URL.name());
		int ultimaBarra = url.lastIndexOf("/");
		int ultimoPonto = url.lastIndexOf(".");
		if (ultimoPonto > ultimaBarra) {
			
		} else {
			
		}
		
		if (indicadorAtivado) {
			if (!conjuntoTreinamento.urlVerificada.containsKey(url)) {
				HashMap<String, HashMap<String, String>> maps = new HashMap<>();
				maps.put(Config.RUIM, coluns);
				conjuntoTreinamento.urlVerificada.put(url, maps);
			}

			ModuloEstatistico.getInstance().processaUltimasUrls(ultimasUrls);
		}
		
		if (ultimasUrls.size() < Config.ULTIMAS_N_LINHAS) {
			ultimasUrls.add(url);
		} else {
			ultimasUrls.set(indexUltimasUrls, url);
		}
		if (++indexUltimasUrls == Config.ULTIMAS_N_LINHAS)
			indexUltimasUrls = 0;

		if (tree != null) {
			boolean resultado = tree.classify(new ArrayList<String>() {
				private static final long serialVersionUID = 8937334946032327062L;
				{
					for (FormatoLog formato : Config.colunasLog) {
						add(coluns.get(formato.getInfoPropriedade().name()));
					}
				}
			});
			
			if (!indicadorAtivado ^ resultado) {
				if (resultado) {
					System.out.println(coluns);
				} else {
					System.out.println(2);
				}
			}
		}
	}

	public void gerarRelatorio(final EstatisticasAnalise estatisticasArquivo) {
		List<List<String>> rows = preparaLinhasArvore(estatisticasArquivo);

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
		System.out.println("segundos -> " + total / 1000000000d);
		//tree.print();
	}

	private List<List<String>> preparaLinhasArvore(EstatisticasAnalise estatisticas) {
		List<List<String>> rows = new ArrayList<>();
		List<HashMap<String, String>> novos = new ArrayList<>();

		conjuntoTreinamento.urlVerificada.forEach((k, v) -> {
			List<String> data = new ArrayList<>();
			HashMap<String, String> mapRuim = v.get(Config.RUIM);
			HashMap<String, String> mapBom = v.get(Config.BOM);

			for (InfoPropriedade infoPropriedade : Config.colunasParaArvore) {
				data.add(mapRuim.get(infoPropriedade.name()));
			}
			data.add(Config.RUIM);
			rows.add(data);

			if (mapBom != null) {
				data = new ArrayList<>();
				for (InfoPropriedade infoPropriedade : Config.colunasParaArvore) {
					data.add(mapBom.get(infoPropriedade.name()));
				}
				data.add(Config.BOM);
				rows.add(data);
			} else if (estatisticas != null) {
				AnaliseURL analiseURL = estatisticas.getUrlAnalise().get(k);
				if (analiseURL != null) {
					data = new ArrayList<>();
					mapBom = new HashMap<>();

					double valor = ((analiseURL.max - analiseURL.min) + (analiseURL.max - (analiseURL.duracao / analiseURL.quantidade))) * analiseURL.quantidade;
					double novoTempo = (analiseURL.duracao / analiseURL.quantidade) - Math.log(valor) - Math.log(analiseURL.quantidade);
					
					for (InfoPropriedade infoPropriedade : Config.colunasParaArvore) {
						if (infoPropriedade.name().equals(InfoPropriedade.TEMPO.name())) {
							mapBom.put(infoPropriedade.name(), String.valueOf(novoTempo > analiseURL.min ? (long) novoTempo: analiseURL.min));
						} else {
							mapBom.put(infoPropriedade.name(), mapRuim.get(infoPropriedade.name()));
						}
						
						data.add(mapBom.get(infoPropriedade.name()));
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