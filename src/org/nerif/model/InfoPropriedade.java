package org.nerif.model;

public enum InfoPropriedade {
	DATA("DATA"), HORA("HORA"), TEMPO("TEMPO"), URL("URL"), PORTA("PORTA"), TAMANHO(
			"TAMANHO"), MEDIA("MEDIA");

	private final String infoPropriedade;

	InfoPropriedade(String infoPropriedade) {
		this.infoPropriedade = infoPropriedade;
	}

	public String getInfoPropriedade() {
		return infoPropriedade;
	}
}
