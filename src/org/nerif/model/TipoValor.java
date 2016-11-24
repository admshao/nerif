package org.nerif.model;

public enum TipoValor {
	NUMERICO("NUMERICO"), DATA("DATA"), HORA("HORA"), STRING("STRING"), BOOLEAN(
			"BOOLEAN");

	private final String tipoValor;

	TipoValor(String tipoValor) {
		this.tipoValor = tipoValor;
	}

	public String getTipoValor() {
		return tipoValor;
	}
}
