package org.nerif;

import java.util.List;

import org.nerif.model.Indicador;

public class Alertas {
	private static Alertas instance = null;

	public static Alertas getInstance() {
		if (instance == null) {
			instance = new Alertas();
		}
		return instance;
	}

	private Alertas() {

	}

	public void notificaIndicatorAtivo(Indicador indicador, List<String> cols) {
		System.out.println("Indicador ativado: " + indicador.getDescricao());
		System.out.println(cols);
	}
}
