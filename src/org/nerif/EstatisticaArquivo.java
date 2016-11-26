package org.nerif;

import java.util.HashMap;

import org.nerif.model.InfoPropriedade;
import org.nerif.util.Config;

public class EstatisticaArquivo {

	private String dia;
	private HashMap<String, HashMap<String, HashMap<String, Long>>> infoPropriedadeMap = new HashMap<>();

	private HashMap<String, Long> dataHoraQuantidadeEstatistica = new HashMap<>();
	private HashMap<String, Long> urlQuantidadeEstatistica = new HashMap<>();
	private HashMap<String, Long> urlDuracaoEstatistica = new HashMap<>();
	private HashMap<String, Long> urlMinEstatistica = new HashMap<>();
	private HashMap<String, Long> urlMaxEstatistica = new HashMap<>();

	public HashMap<String, Long> getDataHoraQuantidadeEstatistica() {
		return dataHoraQuantidadeEstatistica;
	}
	
	public HashMap<String, HashMap<String, HashMap<String, Long>>> getInfoPropriedadeMap() {
		return infoPropriedadeMap;
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

	public String getDia() {
		return dia;
	}

	public void processaLinha(final HashMap<String, String> coluns) {
		final String data = coluns.get(InfoPropriedade.DATA.name());
		final String hora = coluns.get(InfoPropriedade.HORA.name());
		final String url = coluns.get(InfoPropriedade.URL.name());
		final String tempo = coluns.get(InfoPropriedade.TEMPO.name());
		final String tamanho = coluns.get(InfoPropriedade.TAMANHO.name());
		final String clientIp = coluns.get(InfoPropriedade.CLIENT_IP.name());
		final String protocolStatus = coluns.get(InfoPropriedade.PROTOCOL_STATUS.name());

		if (data != null) {
			dia = data;

			if (hora != null) {
				HashMap<String, HashMap<String, Long>> estatisticasHoraMap = infoPropriedadeMap.get(hora);
				if (estatisticasHoraMap == null) {
					estatisticasHoraMap = new HashMap<>();
					infoPropriedadeMap.put(hora, estatisticasHoraMap);
				}
				HashMap<String, Long> estatisticasQuantidadeMap = estatisticasHoraMap.get(Config.QUANTIDADE);
				if (estatisticasQuantidadeMap == null) {
					estatisticasQuantidadeMap = new HashMap<>();
					estatisticasHoraMap.put(Config.QUANTIDADE, estatisticasQuantidadeMap);
				}
				estatisticasQuantidadeMap.compute(Config.QUANTIDADE, (k, v) -> v == null ? 1l : v + 1);
				dataHoraQuantidadeEstatistica.compute(data + hora, (k, v) -> v == null ? 1l : v + 1);

				if (tamanho != null) {
					Long tamanhoL = Long.valueOf(tamanho);
					HashMap<String, Long> estatisticasTamanhoMap = estatisticasHoraMap
							.get(InfoPropriedade.TAMANHO.name());
					if (estatisticasTamanhoMap == null) {
						estatisticasTamanhoMap = new HashMap<>();
						estatisticasHoraMap.put(InfoPropriedade.TAMANHO.name(), estatisticasTamanhoMap);
					}
					estatisticasTamanhoMap.compute(InfoPropriedade.TAMANHO.name(),
							(k, v) -> v == null ? tamanhoL : v + tamanhoL);
					if (url != null) {
						estatisticasTamanhoMap.compute(url + ";" + InfoPropriedade.TAMANHO.name(),
								(k, v) -> v == null ? tamanhoL : v + tamanhoL);
					}
				}
				if (tempo != null) {
					Long tempoL = Long.valueOf(tempo);
					HashMap<String, Long> estatisticasTempoMap = estatisticasHoraMap.get(InfoPropriedade.TEMPO.name());
					if (estatisticasTempoMap == null) {
						estatisticasTempoMap = new HashMap<>();
						estatisticasHoraMap.put(InfoPropriedade.TEMPO.name(), estatisticasTempoMap);
					}
					estatisticasTempoMap.compute(InfoPropriedade.TEMPO.name(),
							(k, v) -> v == null ? tempoL : v + tempoL);
					if (url != null) {
						String urltempo = url + ";" + InfoPropriedade.TEMPO.name();
						estatisticasTempoMap.compute(urltempo, (k, v) -> v == null ? tempoL : v + tempoL);
						estatisticasTempoMap.compute(urltempo + ";" + Config.MIN, (k, v) -> v == null || tempoL < v ? tempoL : v);
						estatisticasTempoMap.compute(urltempo + ";" + Config.MAX, (k, v) -> v == null || tempoL > v ? tempoL : v);

						urlQuantidadeEstatistica.compute(url, (k, v) -> v == null ? 1l : v + 1);
						urlDuracaoEstatistica.compute(url, (k, v) -> v == null ? tempoL : v + tempoL);
						urlMinEstatistica.compute(url, (k, v) -> v == null || tempoL < v ? tempoL : v);
						urlMaxEstatistica.compute(url, (k, v) -> v == null || tempoL > v ? tempoL : v);
					}
				}
				if (clientIp != null) {
					HashMap<String, Long> estatisticasIpMap = estatisticasHoraMap.get(InfoPropriedade.CLIENT_IP.name());
					if (estatisticasIpMap == null) {
						estatisticasIpMap = new HashMap<>();
						estatisticasHoraMap.put(InfoPropriedade.CLIENT_IP.name(), estatisticasIpMap);
					}
					estatisticasIpMap.compute(clientIp, (k, v) -> v == null ? 1l : v + 1);
					if (url != null) {
						estatisticasIpMap.compute(url + ";" + clientIp, (k, v) -> v == null ? 1l : v + 1);
					}
				}
				if (url != null) {
					HashMap<String, Long> estatisticasUrlMap = estatisticasHoraMap.get(InfoPropriedade.URL.name());
					if (estatisticasUrlMap == null) {
						estatisticasUrlMap = new HashMap<>();
						estatisticasHoraMap.put(InfoPropriedade.URL.name(), estatisticasUrlMap);
					}
					estatisticasUrlMap.compute(url, (k, v) -> v == null ? 1l : v + 1);
					int ultimaBarra = url.lastIndexOf("/");
					int ultimoPonto = url.lastIndexOf(".");
					if (ultimoPonto > ultimaBarra) {
						estatisticasUrlMap.compute(url.substring(ultimoPonto + 1), (k, v) -> v == null ? 1l : v + 1);
					}
					if (protocolStatus != null) {
						estatisticasUrlMap.compute(protocolStatus + ";" + url, (k, v) -> v == null ? 1l : v + 1);
					}
				}
			}
		}
	}

}