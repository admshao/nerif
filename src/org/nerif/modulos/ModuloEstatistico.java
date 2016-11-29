package org.nerif.modulos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.nerif.estatistica.EstatisticaArquivo;
import org.nerif.ml.AnaliseURL;
import org.nerif.model.InfoPropriedade;
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

		final HashMap<String, AnaliseURL> getUrlAnalise = estatisticasURL.getEstatisticasAnalise().getUrlAnalise();
		arquivo.getEstatisticasAnalise().getUrlAnalise().forEach((k, v) -> {
			AnaliseURL analiseUrl = getUrlAnalise.get(k);
			if (analiseUrl == null) {
				analiseUrl = v;
				getUrlAnalise.put(k, analiseUrl);
			} else {
				analiseUrl.duracao += v.duracao;
				analiseUrl.max = analiseUrl.max > v.max ? analiseUrl.max : v.max;
				analiseUrl.min = analiseUrl.min < v.min ? analiseUrl.min : v.min;
				analiseUrl.quantidade += v.quantidade;
				analiseUrl.tamanho += v.tamanho;
			}
		});

		final HashMap<String, Long> getExtensaoQuantidade = estatisticasURL.getEstatisticasAnalise()
				.getExtensaoQuantidade();
		arquivo.getEstatisticasAnalise().getExtensaoQuantidade().forEach((k, v) -> {
			getExtensaoQuantidade.compute(k, (key, value) -> value == null ? v : value + v);
		});

		final HashMap<String, Long> getPortaQuantidade = estatisticasURL.getEstatisticasAnalise().getPortaQuantidade();
		arquivo.getEstatisticasAnalise().getPortaQuantidade().forEach((k, v) -> {
			getPortaQuantidade.compute(k, (key, value) -> value == null ? v : value + v);
		});

		final HashMap<String, HashSet<String>> getUrlStatusRuim = estatisticasURL.getEstatisticasAnalise()
				.getUrlStatusRuim();
		arquivo.getEstatisticasAnalise().getUrlStatusRuim().forEach((k, v) -> {
			HashSet<String> set = getUrlStatusRuim.get(k);
			if (set == null) {
				set = new HashSet<>();
				getUrlStatusRuim.put(k, set);
			}
			set.addAll(v);
		});

		Config.lock.unlock();
	}

	public void processaLinha(final HashMap<String, String> coluns) {
		Config.lock.lock();

		final String data = coluns.get(InfoPropriedade.DATA.name());

		if (data != null) {
			EstatisticaArquivo estatisticaArquivo = estatisticasHistoricas.get(data);
			if (estatisticaArquivo == null) {
				estatisticaArquivo = new EstatisticaArquivo();
				estatisticasHistoricas.put(data, estatisticaArquivo);
			}

			estatisticaArquivo.processaLinha(coluns);
		}
		Config.lock.unlock();
	}

	public void processaUltimasUrls(final List<String> ultimasUrls) {
		Config.lock.lock();

		ultimasUrls.forEach(ultimaUrl -> {
			estatisticasURL.getEstatisticasAnalise().getUrlProblematicaQuantidade().compute(ultimaUrl,
					(k, v) -> v == null ? 1l : v + 1);
		});

		Config.lock.unlock();
	}

}