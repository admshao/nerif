package org.nerif.modulos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nerif.estatistica.EstatisticasAnalise;
import org.nerif.ml.AnaliseURL;
import org.nerif.ml.ConjuntoTreinamentoArvore;
import org.nerif.ml.DecisionTree;
import org.nerif.ml.TipoTreinamento;
import org.nerif.model.Indicador;
import org.nerif.model.InfoPropriedade;
import org.nerif.model.Regra;
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

	public void processaLinha(final HashMap<String, String> coluns, final List<Indicador> indicadoresAtivados) {
		boolean indicadorAtivado = !indicadoresAtivados.isEmpty();
		String url = coluns.get(InfoPropriedade.URL.name());
		int ultimaBarra = url.lastIndexOf("/");
		int ultimoPonto = url.lastIndexOf(".");
		if (ultimoPonto > ultimaBarra) {
			return;
		}

		if (indicadorAtivado) {
			if (!conjuntoTreinamento.urlVerificada.containsKey(url)) {
				boolean temPorta = false;
				boolean temTempo = false;
				for (Indicador indicador : indicadoresAtivados) {
					for (Regra regra : indicador.getRegras()) {
						temPorta |= regra.getInfoPropriedade().name().equals(InfoPropriedade.PORTA.name());
						temTempo |= regra.getInfoPropriedade().name().equals(InfoPropriedade.TEMPO.name());
					}
				}

				TipoTreinamento tt = new TipoTreinamento();
				if (temPorta) {
					tt.tipos.add(InfoPropriedade.PORTA);
				}
				if (temTempo) {
					tt.tipos.add(InfoPropriedade.TEMPO);
				}
				tt.tipoVerificado = coluns;

				HashMap<String, TipoTreinamento> mapTipoTreinamento = new HashMap<>();
				mapTipoTreinamento.put(Config.RUIM, tt);
				conjuntoTreinamento.urlVerificada.put(url, mapTipoTreinamento);
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
					for (InfoPropriedade infoPropriedade : Config.colunasParaArvore) {
						add(coluns.get(infoPropriedade.name()));
					}
				}
			});

			if (!(indicadorAtivado ^ resultado)) {
				if (resultado) {
					System.out.println(
							"Indicador classificou como linha ruim porem arvore classificou como boa (\"linha boa\") "
									+ coluns);
				}
			}
		}
	}

	public void gerarRelatorio(final EstatisticasAnalise estatisticasArquivo) {
		List<List<String>> rows = preparaLinhasArvore(estatisticasArquivo);

		/*
		 * try { Path p = Paths.get(Config.URI_TRAINING); Files.write(p,
		 * Config.GSON.toJson(conjuntoTreinamento).getBytes()); } catch
		 * (Exception e) { e.printStackTrace(); }
		 */

		System.out.println("Build da arvore em...");
		long start = System.nanoTime();
		tree = new DecisionTree(rows);
		long total = System.nanoTime() - start;
		System.out.println("segundos -> " + total / 1000000000d);
		tree.print();
	}

	private List<List<String>> preparaLinhasArvore(EstatisticasAnalise estatisticas) {
		List<List<String>> rows = new ArrayList<>();
		List<HashMap<String, String>> novos = new ArrayList<>();

		conjuntoTreinamento.urlVerificada.forEach((k, v) -> {
			TipoTreinamento treinoRuim = v.get(Config.RUIM);
			TipoTreinamento treinoBom = v.get(Config.BOM);

			boolean temPorta = treinoRuim.tipos.stream().anyMatch(p -> p.name().equals(InfoPropriedade.PORTA.name()));
			boolean temTempo = treinoRuim.tipos.stream().anyMatch(p -> p.name().equals(InfoPropriedade.TEMPO.name()));

			if (treinoBom != null) {
				List<String> data = new ArrayList<>();
				for (InfoPropriedade infoPropriedade : Config.colunasParaArvore) {
					data.add(treinoBom.tipoVerificado.get(infoPropriedade.name()));
				}
				data.add(Config.BOM);
				rows.add(data);
			} else if (estatisticas != null) {
				AnaliseURL analiseURL = estatisticas.getUrlAnalise().get(k);
				if (analiseURL != null) {
					List<String> data = new ArrayList<>();
					treinoBom = new TipoTreinamento();

					for (InfoPropriedade infoPropriedade : Config.colunasParaArvore) {
						if (infoPropriedade.name().equals(InfoPropriedade.TEMPO.name()) && temTempo) {
							double valor = ((analiseURL.max - analiseURL.min)
									+ (analiseURL.max - (analiseURL.duracao / analiseURL.quantidade)))
									* analiseURL.quantidade;
							double penalidade = Math.log(valor) - Math.log(analiseURL.quantidade);
							double novoTempo = (analiseURL.duracao / analiseURL.quantidade) - penalidade;
							treinoBom.tipoVerificado.put(infoPropriedade.name(),
									String.valueOf(novoTempo - penalidade > analiseURL.min
											? novoTempo - penalidade : analiseURL.min));
							treinoRuim.tipoVerificado.put(infoPropriedade.name(),
									String.valueOf(novoTempo > analiseURL.min ? novoTempo : analiseURL.min));
						} else if (infoPropriedade.name().equals(InfoPropriedade.PORTA.name()) && temPorta) {
							String portaMaisFrequente = estatisticas.getPortaQuantidade().entrySet().stream()
									.max(Map.Entry.comparingByValue()).get().getKey();
							treinoBom.tipoVerificado.put(infoPropriedade.name(), portaMaisFrequente);
						} else {
							treinoBom.tipoVerificado.put(infoPropriedade.name(),
									treinoRuim.tipoVerificado.get(infoPropriedade.name()));
						}

						data.add(treinoBom.tipoVerificado.get(infoPropriedade.name()));
					}

					novos.add(treinoBom.tipoVerificado);
					data.add(Config.BOM);
					rows.add(data);
				}
			}

			List<String> data = new ArrayList<>();
			for (InfoPropriedade infoPropriedade : Config.colunasParaArvore) {
				data.add(treinoRuim.tipoVerificado.get(infoPropriedade.name()));
			}
			data.add(Config.RUIM);
			rows.add(data);
		});

		novos.forEach(mapa -> {
			TipoTreinamento tt = new TipoTreinamento();
			tt.tipoVerificado = mapa;
			conjuntoTreinamento.urlVerificada.get(mapa.get(InfoPropriedade.URL.name())).put(Config.BOM, tt);
		});

		return rows;
	}
}