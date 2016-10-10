package org.nerif.model;

public class FormatoLog {
	private InfoPropriedade infoPropriedade;
	private TipoValor tipoValor;

	public FormatoLog(InfoPropriedade infoPropriedade, TipoValor tipoValor) {
		this.infoPropriedade = infoPropriedade;
		this.tipoValor = tipoValor;
	}

	public InfoPropriedade getInfoPropriedade() {
		return infoPropriedade;
	}

	public TipoValor getTipoValor() {
		return tipoValor;
	}

}
