package org.nerif;

import java.util.HashMap;

import org.nerif.model.InfoPropriedade;
import org.nerif.util.Config;

public class EstatisticaArquivo {

	private HashMap<String, HashMap<String, HashMap<String, Long>>> infoPropriedadeMap = new HashMap<>();

	public HashMap<String, HashMap<String, HashMap<String, Long>>> getInfoPropriedadeMap() {
		return infoPropriedadeMap;
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
			HashMap<String, HashMap<String, Long>> estatisticasDiaMap = infoPropriedadeMap.get(data);
			if (estatisticasDiaMap == null) {
				estatisticasDiaMap = new HashMap<>();
				infoPropriedadeMap.put(data, estatisticasDiaMap);
			}

			if (hora != null) {
				HashMap<String, Long> estatisticasHoraMap = estatisticasDiaMap.get(hora);
				if (estatisticasHoraMap == null) {
					estatisticasHoraMap = new HashMap<>();
					estatisticasDiaMap.put(hora, estatisticasHoraMap);
				}
				estatisticasHoraMap.compute(Config.QUANTIDADE, (k, v) -> v == null ? 1l : v + 1);
				if (tamanho != null) {
					Long tamanhoL = Long.valueOf(tamanho);
					estatisticasHoraMap.compute(InfoPropriedade.TAMANHO.name(),
							(k, v) -> v == null ? tamanhoL : v + tamanhoL);
					if (url != null) {
						estatisticasHoraMap.compute(url + ";" + InfoPropriedade.TAMANHO.name(),
								(k, v) -> v == null ? tamanhoL : v + tamanhoL);
					}
				}
				if (tempo != null) {
					Long tempoL = Long.valueOf(tempo);
					if (url != null) {
						String urltempo = url + ";" + InfoPropriedade.TEMPO.name();
						estatisticasHoraMap.compute(urltempo, (k, v) -> v == null ? tempoL : v + tempoL);
						estatisticasHoraMap.compute(urltempo+";"+Config.MIN, (k, v) -> v == null || tempoL < v ? tempoL : v);
						estatisticasHoraMap.compute(urltempo+";"+Config.MAX, (k, v) -> v == null || tempoL > v ? tempoL : v);
					}
				}
				if (clientIp != null) {
					estatisticasHoraMap.compute(clientIp, (k, v) -> v == null ? 1l : v + 1);
					if (url != null) {
						estatisticasHoraMap.compute(url + ";" + clientIp, (k, v) -> v == null ? 1l : v + 1);
					}
				}
				if (url != null) {
					estatisticasHoraMap.compute(url, (k, v) -> v == null ? 1l : v + 1);
					int ultimaBarra = url.lastIndexOf("/");
					int ultimoPonto = url.lastIndexOf(".");
					if (ultimoPonto > ultimaBarra) {
						estatisticasHoraMap.compute(url.substring(ultimoPonto+1), (k, v) -> v == null ? 1l : v + 1);
					}
					if (protocolStatus != null) {
						estatisticasHoraMap.compute(url + ";" + protocolStatus, (k, v) -> v == null ? 1l : v + 1);
					}
				}
			}
		}
	}

}