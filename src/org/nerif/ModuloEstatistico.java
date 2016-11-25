package org.nerif;

import java.util.HashMap;

import org.nerif.util.Config;

public class ModuloEstatistico {

	private EstatisticaArquivo estatisticaArquivo = new EstatisticaArquivo();

	private static ModuloEstatistico instance = null;

	public static ModuloEstatistico getInstance() {
		if (instance == null)
			instance = new ModuloEstatistico();
		return instance;
	}

	private ModuloEstatistico() {
	}

	public EstatisticaArquivo getEstatisticaArquivo() {
		return estatisticaArquivo;
	}

	public void mesclaArquivo(final EstatisticaArquivo arquivo) {
		Config.lock.lock();

		arquivo.getInfoPropriedadeMap().forEach((data, horaMap) -> {
			horaMap.forEach((hora, map) -> {
				map.forEach((chave, v) -> {
					HashMap<String, HashMap<String, Long>> estatisticasDiaMap = estatisticaArquivo.getInfoPropriedadeMap().get(data);
					if (estatisticasDiaMap == null) {
						estatisticasDiaMap = new HashMap<>();
						estatisticaArquivo.getInfoPropriedadeMap().put(data, estatisticasDiaMap);
					}
					HashMap<String, Long> estatisticaMap = estatisticasDiaMap.get(hora);
					if (estatisticaMap == null) {
						estatisticaMap = new HashMap<>();
						estatisticasDiaMap.put(hora, estatisticaMap);
					}
					
					if (chave.endsWith(";min")) {
						estatisticaMap.compute(chave, (key, value) -> value == null || v < value ? v : value);
					} else if (chave.endsWith(";max")) {
						estatisticaMap.compute(chave, (key, value) -> value == null || v > value ? v : value);
					} else {
						estatisticaMap.compute(chave, (key, value) -> value == null ? v : value + v);
					}
				});
			});
		});

		Config.lock.unlock();
	}

}