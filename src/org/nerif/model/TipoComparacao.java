package org.nerif.model;

public enum TipoComparacao {
	IGUAL("IGUAL"), DIFERENTE("DIFERENTE"), MAIOR_QUE("MAIOR_QUE"), MENOR_QUE("MENOR_QUE"), NO_INTERVALO(
			"NO_INTERVALO"), FORA_DO_INTERVALO("FORA_DO_INTERVALO");

	private final String tipoComparacao;

	TipoComparacao(String tipoComparacao) {
		this.tipoComparacao = tipoComparacao;
	}

	public String getTipoComparacao() {
		return tipoComparacao;
	}
}
