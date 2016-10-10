package org.nerif.model;

public class Regra {
	private String descricao;
	private InfoPropriedade infoPropriedade;
	private TipoComparacao tipoComparacao;
	private TipoValor tipoValor;
	private Object valor1;
	private Object valor2;

	public Regra(String descricao, InfoPropriedade infoPropriedade, TipoComparacao tipoComparacao, TipoValor tipoValor,
			String valor1, String valor2) {
		this.descricao = descricao;
		this.infoPropriedade = infoPropriedade;
		this.tipoComparacao = tipoComparacao;
		this.tipoValor = tipoValor;
		this.valor1 = valor1;
		this.valor2 = valor2;
	}

	public String getDescricao() {
		return descricao;
	}

	public InfoPropriedade getInfoPropriedade() {
		return infoPropriedade;
	}

	public TipoComparacao getTipoComparacao() {
		return tipoComparacao;
	}

	public TipoValor getTipoValor() {
		return tipoValor;
	}

	public Object getValor1() {
		return valor1;
	}

	public Object getValor2() {
		return valor2;
	}
}
