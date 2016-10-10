package org.nerif.model;

public enum TipoValor {
	NUMERICO("NUMERICO"), PERCENTUAL("PERCENTUAL"), DATA("DATA"), DATA_HORA("DATA_HORA"), STRING("STRING"), BOOLEAN(
			"BOOLEAN");

	private final String tipoValor;

	TipoValor(String tipoValor) {
		this.tipoValor = tipoValor;
	}

	public String getTipoValor() {
		return tipoValor;
	}
}
