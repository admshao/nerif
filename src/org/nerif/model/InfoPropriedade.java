package org.nerif.model;

public enum InfoPropriedade {
	DATA("DATA"), HORA("HORA"), TEMPO("TEMPO"), URL("URL"), PORTA("PORTA"), TAMANHO("TAMANHO"), CLIENT_IP(
			"CLIENT_IP"), PROTOCOL_STATUS("PROTOCOL_STATUS");

	private final String infoPropriedade;

	InfoPropriedade(String infoPropriedade) {
		this.infoPropriedade = infoPropriedade;
	}

	public String getInfoPropriedade() {
		return infoPropriedade;
	}
}
