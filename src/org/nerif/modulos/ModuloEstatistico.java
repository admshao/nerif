package org.nerif.modulos;

import java.util.HashMap;

import org.nerif.estatistica.EstatisticaArquivo;
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

		arquivo.getEstatisticasDia().values().stream().forEach(v -> {
			estatisticasHistoricas.put(v.data, arquivo);
		});

		final HashMap<String, Long> getUrlDuracaoEstatistica = estatisticasURL.getEstatisticasAnalise().getUrlDuracaoEstatistica();
		arquivo.getEstatisticasAnalise().getUrlDuracaoEstatistica().forEach((k, v) -> {
			getUrlDuracaoEstatistica.compute(k, (key, value) -> value == null ? v : value + v);
		});

		final HashMap<String, Long> getUrlMaxEstatistica = estatisticasURL.getEstatisticasAnalise().getUrlMaxEstatistica();
		arquivo.getEstatisticasAnalise().getUrlMaxEstatistica().forEach((k, v) -> {
			getUrlMaxEstatistica.compute(k, (key, value) -> value == null || v > value ? v : value);
		});

		final HashMap<String, Long> getUrlMinEstatistica = estatisticasURL.getEstatisticasAnalise().getUrlMinEstatistica();
		arquivo.getEstatisticasAnalise().getUrlMinEstatistica().forEach((k, v) -> {
			getUrlMinEstatistica.compute(k, (key, value) -> value == null || v < value ? v : value);
		});

		final HashMap<String, Long> getUrlQuantidadeEstatistica = estatisticasURL.getEstatisticasAnalise().getUrlQuantidadeEstatistica();
		arquivo.getEstatisticasAnalise().getUrlQuantidadeEstatistica().forEach((k, v) -> {
			getUrlQuantidadeEstatistica.compute(k, (key, value) -> value == null ? v : value + v);
		});

		Config.lock.unlock();
	}

}