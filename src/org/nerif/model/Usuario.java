package org.nerif.model;

public class Usuario {
	private String nome;
	private int id;
	private String telefone;
	private String email;
	
	public Usuario(int id, String nome, String telefone, String email) {
		this.id = id;
		this.nome = nome;
		this.telefone = telefone;
		this.email = email;
	}
	
	public String getNome() {
		return nome;
	}
	public int getId() {
		return id;
	}
	public String getTelefone() {
		return telefone;
	}
	public String getEmail() {
		return email;
	}
	
}
