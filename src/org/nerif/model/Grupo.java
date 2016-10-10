package org.nerif.model;

import java.util.HashSet;

public class Grupo {
	private int id;
	private String descricao;
	private HashSet<Integer> usuarios;
	private HashSet<Integer> indicadores;

	public Grupo(int id, String descricao, HashSet<Integer> usuarios, HashSet<Integer> indicadores) {
		this.id = id;
		this.descricao = descricao;
		this.usuarios = usuarios;
		this.indicadores = indicadores;
	}

	public int getId() {
		return id;
	}

	public String getDescricao() {
		return descricao;
	}

	public HashSet<Integer> getUsuarios() {
		return usuarios;
	}

	public HashSet<Integer> getIndicadores() {
		return indicadores;
	}

}
