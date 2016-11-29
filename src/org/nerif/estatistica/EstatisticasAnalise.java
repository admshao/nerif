package org.nerif.estatistica;

import java.util.HashMap;
import java.util.HashSet;

import org.nerif.ml.AnaliseURL;

public class EstatisticasAnalise {
	private HashMap<String, AnaliseURL> urlAnalise = new HashMap<>();
	private HashMap<String, Long> portaQuantidade = new HashMap<>();
	private HashMap<String, Long> extensaoQuantidade = new HashMap<>();
	private HashMap<String, Long> urlProblematicaQuantidade = new HashMap<>();
	private HashMap<String, HashSet<String>> urlStatusRuim = new HashMap<>();

	public HashMap<String, HashSet<String>> getUrlStatusRuim() {
		return urlStatusRuim;
	}

	public HashMap<String, Long> getUrlProblematicaQuantidade() {
		return urlProblematicaQuantidade;
	}

	public HashMap<String, AnaliseURL> getUrlAnalise() {
		return urlAnalise;
	}

	public HashMap<String, Long> getPortaQuantidade() {
		return portaQuantidade;
	}

	public HashMap<String, Long> getExtensaoQuantidade() {
		return extensaoQuantidade;
	}
}
