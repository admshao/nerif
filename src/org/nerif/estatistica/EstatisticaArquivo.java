package org.nerif.estatistica;

import java.util.HashMap;
import java.util.HashSet;

import org.nerif.ml.AnaliseURL;
import org.nerif.model.InfoPropriedade;
import org.nerif.util.Config;

public class EstatisticaArquivo {

	private HashMap<String, EstatisticaDia> estatisticasDia = new HashMap<>();
	private EstatisticasAnalise estatisticasAnalise = new EstatisticasAnalise();

	public EstatisticasAnalise getEstatisticasAnalise() {
		return estatisticasAnalise;
	}

	public HashMap<String, EstatisticaDia> getEstatisticasDia() {
		return estatisticasDia;
	}

	public void processaLinhaAnalise(final HashMap<String, String> coluns) {
		final String url = coluns.get(InfoPropriedade.URL.name());
		final String tempo = coluns.get(InfoPropriedade.TEMPO.name());
		final String tamanho = coluns.get(InfoPropriedade.TAMANHO.name());
		final String porta = coluns.get(InfoPropriedade.PORTA.name());
		final String protocolStatus = coluns.get(InfoPropriedade.PROTOCOL_STATUS.name());

		if (url != null) {
			AnaliseURL analiseURL = estatisticasAnalise.getUrlAnalise().get(url);
			if (analiseURL == null) {
				analiseURL = new AnaliseURL();
				estatisticasAnalise.getUrlAnalise().put(url, analiseURL);
			}
			analiseURL.quantidade++;
			
			int ultimaBarra = url.lastIndexOf("/");
			int ultimoPonto = url.lastIndexOf(".");
			if (ultimoPonto > ultimaBarra) {
				estatisticasAnalise.getExtensaoQuantidade().compute(url.substring(ultimoPonto + 1),
						(k, v) -> v == null ? 1l : v + 1);
			}

			if (tempo != null) {
				Long tempoL = Long.valueOf(tempo);
				analiseURL.duracao += tempoL;
				analiseURL.min = analiseURL.min < tempoL ? analiseURL.min : tempoL;
				analiseURL.max = analiseURL.max > tempoL ? analiseURL.max : tempoL;
			}

			if (tamanho != null) {
				Long tamanhoL = Long.valueOf(tamanho);
				analiseURL.tamanho += tamanhoL;
			}

			if (porta != null) {
				estatisticasAnalise.getPortaQuantidade().compute(porta, (k, v) -> v == null ? 1l : v + 1);
			}
			
			if (protocolStatus != null) {
				if (protocolStatus.startsWith("4")) {
					HashSet<String> statusRuins = estatisticasAnalise.getUrlStatusRuim().get("4");
					if (statusRuins == null) {
						statusRuins = new HashSet<>();
						estatisticasAnalise.getUrlStatusRuim().put("4", statusRuins);
					}
					statusRuins.add(url);
				} else if (protocolStatus.startsWith("5")) {
					HashSet<String> statusRuins = estatisticasAnalise.getUrlStatusRuim().get("5");
					if (statusRuins == null) {
						statusRuins = new HashSet<>();
						estatisticasAnalise.getUrlStatusRuim().put("5", statusRuins);
					}
					statusRuins.add(url);
				}
			}
		}
	}

	public void processaLinha(final HashMap<String, String> coluns) {
		final String data = coluns.get(InfoPropriedade.DATA.name());
		final String hora = coluns.get(InfoPropriedade.HORA.name());
		final String url = coluns.get(InfoPropriedade.URL.name());
		final String tempo = coluns.get(InfoPropriedade.TEMPO.name());
		final String tamanho = coluns.get(InfoPropriedade.TAMANHO.name());
		final String clientIp = coluns.get(InfoPropriedade.CLIENT_IP.name());
		final String protocolStatus = coluns.get(InfoPropriedade.PROTOCOL_STATUS.name());
		final String porta = coluns.get(InfoPropriedade.PORTA.name());

		if (data != null) {
			EstatisticaDia estatisticaDia = estatisticasDia.get(data);
			if (estatisticaDia == null) {
				estatisticaDia = new EstatisticaDia();
				estatisticaDia.data = data;
				estatisticasDia.put(data, estatisticaDia);
			}

			if (url != null) {
				EstatisticaURL estatisticaURL = estatisticaDia.requisicoes.get(url);
				if (estatisticaURL == null) {
					estatisticaURL = new EstatisticaURL();
					estatisticaDia.requisicoes.put(url, estatisticaURL);
				}

				int ultimaBarra = url.lastIndexOf("/");
				int ultimoPonto = url.lastIndexOf(".");
				if (ultimoPonto > ultimaBarra) {
					estatisticaURL.extensao = url.substring(ultimoPonto + 1);
				}

				estatisticaURL.quantidade++;
				if (tempo != null) {
					Long tempoL = Long.valueOf(tempo);
					if (estatisticaURL.tempoMax < tempoL) {
						estatisticaURL.tempoMax = tempoL;
					}
					if (estatisticaURL.tempoMin > tempoL) {
						estatisticaURL.tempoMin = tempoL;
					}

					if (hora != null) {
						EstatisticaHora estatisticaHora = estatisticaURL.horarios.get(hora);
						if (estatisticaHora == null) {
							estatisticaHora = new EstatisticaHora();
							estatisticaURL.horarios.put(hora, estatisticaHora);
						}

						LinhaGeral linhaGeral = new LinhaGeral();
						linhaGeral.duracao = tempoL;
						if (clientIp != null) {
							linhaGeral.ipOrigem = clientIp;
						}
						if (protocolStatus != null) {
							linhaGeral.status = protocolStatus;
						}
						if (porta != null) {
							linhaGeral.porta = porta;
						}
						if (tamanho != null) {
							Long tamanhoL = Long.valueOf(tamanho);
							linhaGeral.tamanho = tamanhoL;
						}
						estatisticaHora.linhas.add(linhaGeral);
					}
				}
			}

			if (Config.EXECUTA_MODULO_ANALISE) {
				processaLinhaAnalise(coluns);
			}
		}
	}

}