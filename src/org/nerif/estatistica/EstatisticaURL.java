package org.nerif.estatistica;

import java.util.HashMap;

public class EstatisticaURL {
	public String extensao = "";
	public long quantidade = 0;
	public long tempoMin = Long.MAX_VALUE;
	public long tempoMax = Long.MIN_VALUE;
	public HashMap<String, EstatisticaHora> horarios = new HashMap<>();
}
