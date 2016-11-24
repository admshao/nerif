package org.nerif;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	private final Lock lock = new ReentrantLock(true);

	private HashMap<Indicador, Long> indicadorQuantidadeAtivacoes = new HashMap<>();
	private HashMap<Indicador, Boolean> alertarUsuario = new HashMap<>();

	private ModuloAlerta() {
	}

	public void init() {
		Config.indicadores.forEach((k, v) -> {
			alertarUsuario.put(v, true);
		});
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
		indicadorQuantidadeAtivacoes.compute(indicador, (k, v) -> v == null ? 1l : v + 1);

		if (Config.EMAIL_ALERT || Config.SMS_ALERT) {
			if (alertarUsuario.get(indicador)) {
				alertarUsuario.put(indicador, false);
				Config.TIMER.schedule(new TimerTask() {
					@Override
					public void run() {
						alertarUsuario.put(indicador, true);
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
							final String num = String.valueOf(indicadorQuantidadeAtivacoes.get(indicador));
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

	public void gerarRelatorio() {
		if (Config.EXECUTA_MODULO_ESTATISTICO) {
			EstatisticaArquivo estatisticas = ModuloEstatistico.getInstance().getEstatisticaArquivo();
			final HashMap<String, Long> urlQuantidade = estatisticas.getUrlQuantidadeEstatistica();
			final HashMap<String, Long> urlDuracao = estatisticas.getUrlDuracaoEstatistica();

			String urlMax = urlQuantidade.keySet().parallelStream()
					.max((entry1, entry2) -> urlQuantidade.get(entry1) > urlQuantidade.get(entry2) ? 1 : -1).get();

			long totalRequest = urlQuantidade.values().parallelStream().reduce(0l, (a, b) -> a + b);

			long totalUrlRequest = urlQuantidade.get(urlMax);
			double urlTempoMedio = urlDuracao.get(urlMax) / totalUrlRequest;

			System.out.println("Numero total de requisicoes realizadas -> " + totalRequest);
			System.out.println("Url \"" + urlMax + "\" foi executada " + totalUrlRequest + " vezes.");
			System.out.println("Com tempo de resposta medio de -> " + urlTempoMedio + "ms");
		}

		indicadorQuantidadeAtivacoes.forEach((chave, valor) -> {
			System.out.println("Indicador -> " + chave.getDescricao() + " foi ativado #: "
					+ indicadorQuantidadeAtivacoes.get(chave) + " vezes.");
		});
		
		if (Config.EXECUTA_MODULO_ANALISE) {
			ModuloAnalise.getInstance().dump();
		}
	}
}
