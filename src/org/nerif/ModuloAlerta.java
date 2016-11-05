package org.nerif;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.nerif.model.Indicador;

public class ModuloAlerta {
	private static ModuloAlerta instance = null;

	public static ModuloAlerta getInstance() {
		if (instance == null) {
			instance = new ModuloAlerta();
		}
		return instance;
	}

	private final Lock lock = new ReentrantLock();	
	private ModuloAlerta() {
	}

	public void notificaIndicatorAtivo(Indicador indicador, List<String> cols) {
		lock.lock();
		System.out.println("Indicador ativado: " + indicador.getDescricao());
		System.out.println(cols);
		lock.unlock();
	}
}
