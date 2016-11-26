package org.nerif;

import java.util.HashMap;

import org.nerif.util.Config;

public class ModuloEstatistico {

	private HashMap<String, EstatisticaArquivo> estatisticasHistoricas = new HashMap<>();
	private EstatisticaArquivo estatisticasURL = new EstatisticaArquivo();
	
	private static ModuloEstatistico instance = null;

	public static ModuloEstatistico getInstance() {
		if (instance == null)
			instance = new ModuloEstatistico();
		return instance;
	}

	private ModuloEstatistico() {
	}

	public HashMap<String, EstatisticaArquivo> getEstatisticasHistoricas() {
		return estatisticasHistoricas;
	}
	
	public EstatisticaArquivo getEstatisticasURL() {
		return estatisticasURL;
	}

	public void mesclaArquivo(final EstatisticaArquivo arquivo) {
		Config.lock.lock();

		estatisticasHistoricas.put(arquivo.getDia(), arquivo);

		final HashMap<String, Long> getDataHoraQuantidadeEstatistica = estatisticasURL.getDataHoraQuantidadeEstatistica();
		arquivo.getDataHoraQuantidadeEstatistica().forEach((k, v) -> {
			getDataHoraQuantidadeEstatistica.compute(k, (key, value) -> value == null ? v : value + v);
		});

		final HashMap<String, Long> getUrlDuracaoEstatistica = estatisticasURL.getUrlDuracaoEstatistica();
		arquivo.getUrlDuracaoEstatistica().forEach((k, v) -> {
			getUrlDuracaoEstatistica.compute(k, (key, value) -> value == null ? v : value + v);
		});

		final HashMap<String, Long> getUrlMaxEstatistica = estatisticasURL.getUrlMaxEstatistica();
		arquivo.getUrlMaxEstatistica().forEach((k, v) -> {
			getUrlMaxEstatistica.compute(k, (key, value) -> value == null || v > value ? v : value);
		});

		final HashMap<String, Long> getUrlMinEstatistica = estatisticasURL.getUrlMinEstatistica();
		arquivo.getUrlMinEstatistica().forEach((k, v) -> {
			getUrlMinEstatistica.compute(k, (key, value) -> value == null || v < value ? v : value);
		});

		final HashMap<String, Long> getUrlQuantidadeEstatistica = estatisticasURL.getUrlQuantidadeEstatistica();
		arquivo.getUrlQuantidadeEstatistica().forEach((k, v) -> {
			getUrlQuantidadeEstatistica.compute(k, (key, value) -> value == null ? v : value + v);
		});

		Config.lock.unlock();
	}

}