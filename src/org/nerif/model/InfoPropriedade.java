package org.nerif.model;

public enum InfoPropriedade {
	DATA_HORA("DATA_HORA"), TEMPO_REQUISICAO("TEMPO_REQUISICAO"), URL("URL"), PORTA("PORTA"), TAMANHO("TAMANHO"), MEDIA(
			"MEDIA");

	private final String infoPropriedade;

	InfoPropriedade(String infoPropriedade) {
		this.infoPropriedade = infoPropriedade;
	}

	public String getInfoPropriedade() {
		return infoPropriedade;
	}
}
