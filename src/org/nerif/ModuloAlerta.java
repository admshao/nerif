package org.nerif;

import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.nerif.model.Indicador;
import org.nerif.util.Config;

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
	private HashMap<Indicador, Boolean> indicadorAlerta = new HashMap<>();

	private ModuloAlerta() {
	}

	public void iniciaAlertas() {
		for (Indicador indicador : Config.indicadores) {
			indicadorAlerta.put(indicador, true);
		}

	}

	public void notificaIndicadorAtivado(final Indicador indicador, final HashMap<String, String> cols) {
		// send mail
		// send sms
	}

	public void indicadorAtivado(final Indicador indicador, final HashMap<String, String> cols) {
		lock.lock();
		
		indicadorQuantidadeAtivacoes.compute(indicador, (k, v) -> v == null ? 1l : v + 1);

		if (indicadorAlerta.get(indicador)) {
			notificaIndicadorAtivado(indicador, cols);
			indicadorAlerta.put(indicador, false);
			Config.TIMER.schedule(new TimerTask() {
				@Override
				public void run() {
					indicadorAlerta.put(indicador, true);
				}
			}, 30 * 60 * 1000);
		}
		
		lock.unlock();
	}

	public void dump() {
		EstatisticaArquivo estatisticas = ModuloEstatistico.getInstance().getEstatisticaArquivo();
		final HashMap<String, Long> urlQuantidade = estatisticas.getUrlQuantidadeEstatistica();
		final HashMap<String, Long> urlDuracao = estatisticas.getUrlDuracaoEstatistica();
		
		String urlMax = urlQuantidade.keySet()
				.parallelStream()
	            .max((entry1, entry2) -> urlQuantidade.get(entry1) > urlQuantidade.get(entry2) ? 1 : -1)
	            .get();
		
		long totalRequest = urlQuantidade.values()
				.parallelStream()
				.reduce(0l, (a,b) -> a+b);

		
		long totalUrlRequest = urlQuantidade.get(urlMax);
		double urlTempoMedio = urlDuracao.get(urlMax) / totalUrlRequest;
		
		System.out.println("Numero total de requisicoes realizadas -> " + totalRequest);
		System.out.println("Url \"" + urlMax + "\" foi executada " + totalUrlRequest + " vezes.");
		System.out.println("Com tempo de resposta medio de -> " + urlTempoMedio + "ms");

		indicadorQuantidadeAtivacoes.forEach((chave, valor) -> {
			System.out.println("Indicador -> " + chave.getDescricao() + " foi ativado #: "
					+ indicadorQuantidadeAtivacoes.get(chave) + " vezes.");
		});
	}
}
