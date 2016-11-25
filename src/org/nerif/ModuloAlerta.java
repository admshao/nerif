package org.nerif;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.nerif.model.Alerta;
import org.nerif.model.Indicador;
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

	private HashMap<Indicador, Alerta> indicadorAlerta = new HashMap<>();

	private ModuloAlerta() {
	}

	public void init() {
		Config.indicadores.forEach((k, v) -> {
			indicadorAlerta.put(v, new Alerta());
		});

		if (Config.EXECUTA_MODULO_ESTATISTICO) {
			disparaTimerRelatorio();
		}
	}

	private void disparaTimerRelatorio() {
		Config.TIMER.schedule(new TimerTask() {
			@Override
			public void run() {
				disparaTimerRelatorio();
				gerarRelatorio();
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
		Config.lock.lock();
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
		Config.lock.unlock();
	}

	public void gerarRelatorio() {
		Config.lock.lock();

		/*
		 * StringBuffer sb = new StringBuffer();
		 * 
		 * EstatisticaArquivo estatisticas =
		 * ModuloEstatistico.getInstance().getEstatisticaArquivo(); final
		 * HashMap<String, Long> urlQuantidade =
		 * estatisticas.getUrlQuantidadeEstatistica(); final HashMap<String,
		 * Long> urlDuracao = estatisticas.getUrlDuracaoEstatistica();
		 * 
		 * if (!urlQuantidade.isEmpty()) { String urlMax =
		 * urlQuantidade.keySet().parallelStream() .max((entry1, entry2) ->
		 * urlQuantidade.get(entry1) > urlQuantidade.get(entry2) ? 1 :
		 * -1).get();
		 * 
		 * long totalUrlRequest = urlQuantidade.get(urlMax); sb.append("Url \""
		 * + urlMax + "\" foi executada " + totalUrlRequest + " vezes.\n"); if
		 * (urlDuracao.containsKey(urlMax)) { double urlTempoMedio =
		 * urlDuracao.get(urlMax) / totalUrlRequest;
		 * sb.append("Com tempo de resposta medio de -> " + urlTempoMedio +
		 * "ms\n"); }
		 * 
		 * long totalRequest =
		 * urlQuantidade.values().parallelStream().reduce(0l, (a, b) -> a + b);
		 * sb.append("Numero total de requisicoes realizadas -> " + totalRequest
		 * + "\n"); }
		 * 
		 * indicadorAlerta.forEach((indicador, alerta) -> {
		 * sb.append("Indicador -> " + indicador.getDescricao() +
		 * " foi ativado #: " + alerta.ativacoes + " vezes.\n"); });
		 */

		EstatisticaArquivo estatisticas = ModuloEstatistico.getInstance().getEstatisticaArquivo();

		estatisticas.getInfoPropriedadeMap().forEach((data, map) -> {
			try {
				Path p = Paths.get(new URL(Config.PATH_STATISTICS + data + ".json").toURI());
				p.toFile().getParentFile().mkdirs();
				Files.write(p, Config.GSON.toJson(map).getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		if (Config.EXECUTA_MODULO_ANALISE) {
			ModuloAnalise.getInstance().dump();
		}

		Config.lock.unlock();
	}
}
