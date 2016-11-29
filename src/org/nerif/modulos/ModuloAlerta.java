package org.nerif.modulos;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.nerif.estatistica.EstatisticaArquivo;
import org.nerif.estatistica.EstatisticasAnalise;
import org.nerif.ml.AnaliseURL;
import org.nerif.model.Alerta;
import org.nerif.model.FormatoLog;
import org.nerif.model.Indicador;
import org.nerif.model.InfoPropriedade;
import org.nerif.model.Usuario;
import org.nerif.util.Config;
import org.nerif.util.Email;
import org.nerif.util.SMS;

public class ModuloAlerta {
	private static ModuloAlerta instance = null;

	public static ModuloAlerta getInstance() {
		if (instance == null) {
			instance = new ModuloAlerta();
		}
		return instance;
	}

	public static final Lock lock = new ReentrantLock(true);
	private HashMap<Indicador, Alerta> indicadorAlerta = new HashMap<>();

	private ModuloAlerta() {
		Config.indicadores.forEach((k, v) -> {
			indicadorAlerta.put(v, new Alerta());
		});

		if (Config.EXECUTA_MODULO_ESTATISTICO) {
			disparaTimerRelatorio();
		}

		disparaTimerGeral(Config.horaRelatorioGeral);
	}

	private void disparaTimerGeral(long tempo) {
		Config.TIMER.schedule(new TimerTask() {
			@Override
			public void run() {
				disparaTimerGeral(Config.INTERVALO_RELATORIO_GERAL * 60 * 1000);
				gerarRelatorioGeral();
			}
		}, tempo);
	}

	private void disparaTimerRelatorio() {
		Config.TIMER.schedule(new TimerTask() {
			@Override
			public void run() {
				disparaTimerRelatorio();
				gerarRelatorioEstatistico();
			}
		}, Config.MIN_INTERVALO_RELATORIO * 60 * 1000);
	}

	public void notificaIndicadorAtivadoPorEmail(final List<String> to, final Indicador indicador,
			final String dataAgora, final String num) {
		Email.getInstance().sendFromGMail(to, dataAgora, indicador.getDescricao(), num);
	}

	public void notificaIndicadorAtivadoPorSMS(final List<String> to, final Indicador indicador, final String dataAgora,
			final String num) {
		SMS.getInstance().sendFromTwilio(to, dataAgora, indicador.getDescricao(), num);
	}

	public void indicadorAtivado(final Indicador indicador, final HashMap<String, String> cols) {
		lock.lock();
		final Alerta alerta = indicadorAlerta.get(indicador);
		alerta.ativacoes++;

		if (Config.EMAIL_ALERT || Config.SMS_ALERT) {
			if (alerta.ativo) {
				alerta.ativo = false;
				Config.TIMER.schedule(new TimerTask() {
					@Override
					public void run() {
						alerta.ativo = true;
					}
				}, 30 * 60 * 1000);

				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Stream<List<Usuario>> usuariosSet = Config.grupos.values().parallelStream()
									.filter(g -> g.getIndicadores().contains(indicador)).map(g -> g.getUsuarios()
											.parallelStream().map(u -> u).collect(Collectors.toList()));

							Set<String> setTelefone = new HashSet<String>();
							Set<String> setEmail = new HashSet<String>();
							usuariosSet.forEach(usuarios -> {
								usuarios.forEach(user -> {
									setTelefone.add(user.getTelefone());
									setEmail.add(user.getEmail());
								});
							});
							String dataAgora = Config.dfDataHora.convertDateToString(new Date());
							final String num = String.valueOf(alerta.ativacoes);
							if (Config.EMAIL_ALERT) {
								notificaIndicadorAtivadoPorEmail(setEmail.stream().collect(Collectors.toList()),
										indicador, dataAgora, num);
							}
							if (Config.SMS_ALERT) {
								notificaIndicadorAtivadoPorSMS(setTelefone.stream().collect(Collectors.toList()),
										indicador, dataAgora, num);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		}
		lock.unlock();
	}

	public void gerarRelatorioGeral() {
		Config.lock.lock();

		StringBuffer sb = new StringBuffer();

		final EstatisticasAnalise estatisticas = ModuloEstatistico.getInstance().getEstatisticasURL()
				.getEstatisticasAnalise();
		final HashMap<String, AnaliseURL> analiseURLMap = estatisticas.getUrlAnalise();

		if (!analiseURLMap.isEmpty()) {
			String urlMax = analiseURLMap.keySet().parallelStream().max((entry1,
					entry2) -> analiseURLMap.get(entry1).quantidade > analiseURLMap.get(entry2).quantidade ? 1 : -1)
					.get();

			AnaliseURL analiseURL = analiseURLMap.get(urlMax);
			sb.append("Url \"" + urlMax + "\" foi executada " + analiseURL.quantidade + " vezes.\n");
			double urlTempoMedio = analiseURL.duracao / analiseURL.quantidade;
			sb.append("Com tempo de resposta medio de -> " + urlTempoMedio + "ms\n");

			long totalRequest = analiseURLMap.values().parallelStream().map(analise -> analise.quantidade).reduce(0l,
					(a, b) -> a + b);
			sb.append("Numero total de requisicoes realizadas -> " + totalRequest + "\n");
		}

		indicadorAlerta.forEach((indicador, alerta) -> {
			sb.append("Indicador -> " + indicador.getDescricao() + " foi ativado #: " + alerta.ativacoes + " vezes.\n");
		});

		int total = 5;
		List<Entry<String, Long>> listUrlProblematicas = estatisticas.getUrlProblematicaQuantidade().entrySet().stream()
				.sorted(Map.Entry.comparingByValue(new Comparator<Long>() {
					@Override
					public int compare(Long o1, Long o2) {
						return (int) (o2 - o1);
					}
				})).collect(Collectors.toList());

		for (int i = 0; i != listUrlProblematicas.size() && --total >= 0; i++) {
			Entry<String, Long> e = listUrlProblematicas.get(i);
			sb.append(e.getKey() + " -> " + e.getValue() + "\n");
		}

		for (FormatoLog formatoLog : Config.colunasLog) {
			if (formatoLog.getInfoPropriedade().name().equals(InfoPropriedade.PORTA.name())) {
				final HashMap<String, Long> portaQuantidade = estatisticas.getPortaQuantidade();
				long totalPortas = estatisticas.getPortaQuantidade().values().stream().reduce(0l, (a, b) -> a + b);

				portaQuantidade.forEach((k, v) -> {
					double percent = (v * 100.0) / totalPortas;
					if (percent >= 95) {
						sb.append(percent + "% das requisicoes sao para a porta " + k
								+ ". Considerar adicionar um idicador. Ex (Porta DIFERENTE de " + k + ")\n");
					}

				});
			} else if (formatoLog.getInfoPropriedade().name().equals(InfoPropriedade.PROTOCOL_STATUS.name())) {
				final HashMap<String, HashSet<String>> urlStatusRuim = estatisticas.getUrlStatusRuim();

				urlStatusRuim.forEach((k, v) -> {
					sb.append("As seguintes urls tiveram o status da requisicao com codigo pertencente a classe " + k
							+ "00\n");
					v.forEach(url -> {
						sb.append("\t" + url + "\n");
					});
				});
			} else if (formatoLog.getInfoPropriedade().name().equals(InfoPropriedade.TAMANHO.name())) {
				long tamanhoTotal = analiseURLMap.values().parallelStream().map(analise -> analise.tamanho).reduce(0l,
						(a, b) -> a + b);
				if (tamanhoTotal > 0) {
					int subTotal = 5;
					List<Entry<String, AnaliseURL>> listURLPesadas = analiseURLMap.entrySet().stream()
							.sorted(Map.Entry.comparingByValue(new Comparator<AnaliseURL>() {
								@Override
								public int compare(AnaliseURL o1, AnaliseURL o2) {
									return (int) (o2.tamanho - o1.tamanho);
								}
							})).collect(Collectors.toList());

					sb.append("URLs com grande potencial de impacto no sistema: (Avaliar necessidade de todos os dados trafegados pelas requisicao)\n");
					for (int i = 0; i != listURLPesadas.size() && --subTotal >= 0; i++) {
						Entry<String, AnaliseURL> e = listURLPesadas.get(i);
						sb.append("\t" + e.getKey() + " trafegou com tamanho medio de " + e.getValue().tamanho + " bytes\n");
					}
				}
			} else if (formatoLog.getInfoPropriedade().name().equals(InfoPropriedade.TEMPO.name())) {
				int subTotal = 5;

				final HashMap<String, Long> urlProblematicas = estatisticas.getUrlProblematicaQuantidade();
				final HashMap<String, AnaliseURL> analiseURL = estatisticas.getUrlAnalise();
				final HashMap<String, Double> urlProblematicaComPesoMap = new HashMap<>();
				urlProblematicas.entrySet().stream().forEach(entry -> {
					AnaliseURL analise = analiseURL.get(entry.getKey());
					double valor = ((analise.max - analise.min) + (analise.max - (analise.duracao / analise.quantidade))) * analise.quantidade;
					urlProblematicaComPesoMap.put(entry.getKey(), valor);
				});

				List<Entry<String, Double>> listURLProblematicas = urlProblematicaComPesoMap.entrySet().stream()
						.sorted(Map.Entry.comparingByValue(Collections.reverseOrder())).collect(Collectors.toList());

				sb.append("As seguintes URLs foram consideradas como potenciais gargalos da aplicacao:\n");
				for (int i = 0; i != listURLProblematicas.size() && --subTotal >= 0; i++) {
					Entry<String, Double> e = listURLProblematicas.get(i);
					AnaliseURL analise = analiseURL.get(e.getKey());
					sb.append("\t" + e.getKey() + " executada " + analise.quantidade + " vezes com tempo medio de: " + (analise.duracao / analise.quantidade) + "ms\n");
				}

			} else if (formatoLog.getInfoPropriedade().name().equals(InfoPropriedade.URL.name())) {
				int subTotal = 5;
				List<Entry<String, Long>> listExtensaoPopulares = estatisticas.getExtensaoQuantidade().entrySet()
						.stream().sorted(Map.Entry.comparingByValue(new Comparator<Long>() {
							@Override
							public int compare(Long o1, Long o2) {
								return (int) (o2 - o1);
							}
						})).collect(Collectors.toList());

				for (int i = 0; i != listExtensaoPopulares.size() && --subTotal >= 0; i++) {
					Entry<String, Long> e = listExtensaoPopulares.get(i);

					if (Config.EXTENSAO_IMAGENS.contains(e.getKey())) {
						sb.append("Existem " + e.getValue() + " arquivos com a extensao " + e.getKey()
								+ ". Considerar comprimir as imagens em um unico arquivo.\n");
					} else if (Config.EXTENSAO_MINIMIFICAVEIS.contains(e.getKey())) {
						sb.append("Existem " + e.getValue() + " arquivos com a extensao " + e.getKey()
								+ ". Considerar minimificacao para um unico arquivo.\n");
					}

				}
			}
		}

		System.out.println(sb.toString());

		if (Config.EXECUTA_MODULO_ANALISE) {
			ModuloAnalise.getInstance().gerarRelatorio(estatisticas);
		}

		Config.lock.unlock();
	}

	public void gerarRelatorioEstatistico() {
		Config.lock.lock();

		HashMap<String, EstatisticaArquivo> estatisticas = ModuloEstatistico.getInstance().getEstatisticasHistoricas();
		estatisticas.forEach((data, map) -> {
			try {
				Path p = Paths.get(new URL(Config.PATH_STATISTICS + data + ".json").toURI());
				p.toFile().getParentFile().mkdirs();
				Files.write(p, Config.GSON.toJson(map.getEstatisticasDia().get(data)).getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		Config.lock.unlock();
	}
}
