package org.nerif;

import java.util.HashMap;

import org.nerif.model.InfoPropriedade;

public class EstatisticaArquivo {
	private HashMap<String, Long> dataHoraQuantidadeEstatistica = new HashMap<>();
	private HashMap<String, Long> urlQuantidadeEstatistica = new HashMap<>();
	private HashMap<String, Long> urlDuracaoEstatistica = new HashMap<>();
	private HashMap<String, Long> urlMinEstatistica = new HashMap<>();
	private HashMap<String, Long> urlMaxEstatistica = new HashMap<>();

	public HashMap<String, Long> getDataHoraQuantidadeEstatistica() {
		return dataHoraQuantidadeEstatistica;
	}

	public HashMap<String, Long> getUrlQuantidadeEstatistica() {
		return urlQuantidadeEstatistica;
	}

	public HashMap<String, Long> getUrlDuracaoEstatistica() {
		return urlDuracaoEstatistica;
	}

	public HashMap<String, Long> getUrlMinEstatistica() {
		return urlMinEstatistica;
	}

	public HashMap<String, Long> getUrlMaxEstatistica() {
		return urlMaxEstatistica;
	}

	public void processaLinha(final HashMap<String, String> coluns) {
		String data = coluns.get(InfoPropriedade.DATA.name());
		String hora = coluns.get(InfoPropriedade.HORA.name());
		String url = coluns.get(InfoPropriedade.URL.name());
		String tempo = coluns.get(InfoPropriedade.TEMPO.name());

		if (data != null && hora != null) {
			dataHoraQuantidadeEstatistica.compute(data + hora, (k, v) -> v == null ? 1l : v + 1);
		}

		if (url != null && tempo != null) {
			Long tempoL = Long.valueOf(tempo);
			urlQuantidadeEstatistica.compute(url, (k, v) -> v == null ? 1l : v + 1);
			urlDuracaoEstatistica.compute(url, (k, v) -> v == null ? tempoL : v + tempoL);
			urlMinEstatistica.compute(url, (k, v) -> v == null || tempoL < v ? tempoL : v);
			urlMaxEstatistica.compute(url, (k, v) -> v == null || tempoL > v ? tempoL : v);
		}
	}

}