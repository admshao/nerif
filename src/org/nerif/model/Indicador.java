package org.nerif.model;

import java.util.List;

public class Indicador {
	private int id;
	private String descricao;
	private List<Regra> regras;

	public Indicador(int id, String descricao, List<Regra> regras) {
		this.id = id;
		this.descricao = descricao;
		this.regras = regras;
	}

	public int getId() {
		return id;
	}

	public String getDescricao() {
		return descricao;
	}

	public List<Regra> getRegras() {
		return regras;
	}
}
