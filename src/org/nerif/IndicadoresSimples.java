package org.nerif;

import java.util.List;

public class IndicadoresSimples {
	private static IndicadoresSimples instance = null;

	public static IndicadoresSimples getInstance() {
		if (instance == null) {
			instance = new IndicadoresSimples();
		}
		return instance;
	}
	
	private IndicadoresSimples() {
		
	}
	
	public void init() {
		//
	}
	
	public void processaNovaLinha(List<String> coluns) {
		//System.out.println(coluns);
	}
}
