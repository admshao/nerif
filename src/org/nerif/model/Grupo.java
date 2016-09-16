package org.nerif.model;

import java.util.List;

public class Grupo {
	private int id;
	private String nome;
	private List<Usuario> usuarios;
	private List<Indicador> indicadores;
	
	public Grupo(int id, String nome, List<Usuario> usuarios, List<Indicador> indicadores) {
		this.id = id;
		this.nome = nome;
		this.usuarios = usuarios;
		this.indicadores = indicadores;
	}
	
	public boolean removeUsuario(Usuario usuario) {
		return usuarios.remove(usuario);
	}
	
	public boolean addUsuario(Usuario usuario) {
		if (!usuarios.contains(usuario)) {
			usuarios.add(usuario);
			return true;
		}
		return false;
	}
	
	public int getId() {
		return id;
	}
	public String getNome() {
		return nome;
	}
	public List<Usuario> getUsuarios() {
		return usuarios;
	}
	public List<Indicador> getIndicadores() {
		return indicadores;
	}
	
	
}
