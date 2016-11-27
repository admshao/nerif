package org.nerif.estatistica;

import java.util.HashMap;

public class EstatisticasAnalise {
	private HashMap<String, Long> urlQuantidadeEstatistica = new HashMap<>();
	private HashMap<String, Long> urlDuracaoEstatistica = new HashMap<>();
	private HashMap<String, Long> urlMinEstatistica = new HashMap<>();
	private HashMap<String, Long> urlMaxEstatistica = new HashMap<>();

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
}
