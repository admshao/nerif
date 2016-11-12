package org.nerif;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ModuloEstatistico {

	private EstatisticaArquivo estatisticaArquivo = new EstatisticaArquivo();

	private static ModuloEstatistico instance = null;

	public static ModuloEstatistico getInstance() {
		if (instance == null)
			instance = new ModuloEstatistico();
		return instance;
	}

	private final Lock lock = new ReentrantLock();

	private ModuloEstatistico() {
	}

	public EstatisticaArquivo getEstatisticaArquivo() {
		return estatisticaArquivo;
	}

	public void mesclaArquivo(final EstatisticaArquivo arquivo) {
		lock.lock();

		final HashMap<String, Long> getDataHoraQuantidadeEstatistica = estatisticaArquivo
				.getDataHoraQuantidadeEstatistica();
		arquivo.getDataHoraQuantidadeEstatistica().forEach((k, v) -> {
			getDataHoraQuantidadeEstatistica.compute(k, (key, value) -> value == null ? v : value + v);
		});

		final HashMap<String, Long> getUrlDuracaoEstatistica = estatisticaArquivo.getUrlDuracaoEstatistica();
		arquivo.getUrlDuracaoEstatistica().forEach((k, v) -> {
			getUrlDuracaoEstatistica.compute(k, (key, value) -> value == null ? v : value + v);
		});

		final HashMap<String, Long> getUrlMaxEstatistica = estatisticaArquivo.getUrlMaxEstatistica();
		arquivo.getUrlMaxEstatistica().forEach((k, v) -> {
			getUrlMaxEstatistica.compute(k, (key, value) -> value == null || v > value ? v : value);
		});

		final HashMap<String, Long> getUrlMinEstatistica = estatisticaArquivo.getUrlMinEstatistica();
		arquivo.getUrlMinEstatistica().forEach((k, v) -> {
			getUrlMinEstatistica.compute(k, (key, value) -> value == null || v < value ? v : value);
		});

		final HashMap<String, Long> getUrlQuantidadeEstatistica = estatisticaArquivo.getUrlQuantidadeEstatistica();
		arquivo.getUrlQuantidadeEstatistica().forEach((k, v) -> {
			getUrlQuantidadeEstatistica.compute(k, (key, value) -> value == null ? v : value + v);
		});

		lock.unlock();
	}

}