package org.nerif.model;

public class FormatoLog {
	private String chave;
	private InfoPropriedade infoPropriedade;
	private TipoValor tipoValor;

	public FormatoLog(String chave, InfoPropriedade infoPropriedade, TipoValor tipoValor) {
		this.chave = chave;
		this.infoPropriedade = infoPropriedade;
		this.tipoValor = tipoValor;
	}

	public String getChave() {
		return chave;
	}

	public InfoPropriedade getInfoPropriedade() {
		return infoPropriedade;
	}

	public TipoValor getTipoValor() {
		return tipoValor;
	}

}
