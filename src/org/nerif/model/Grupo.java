package org.nerif.model;

import java.util.HashSet;

public class Grupo {
	private int id;
	private String descricao;
	private HashSet<Usuario> usuarios;
	private HashSet<Indicador> indicadores;

	public Grupo(int id, String descricao, HashSet<Usuario> usuarios, HashSet<Indicador> indicadores) {
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

	public HashSet<Usuario> getUsuarios() {
		return usuarios;
	}

	public HashSet<Indicador> getIndicadores() {
		return indicadores;
	}

}
