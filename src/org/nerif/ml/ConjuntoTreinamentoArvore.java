package org.nerif.ml;

import java.util.HashMap;
import java.util.HashSet;

import org.nerif.model.InfoPropriedade;

public class ConjuntoTreinamentoArvore {
	public HashSet<InfoPropriedade> colunasParaArvore = new HashSet<>();
	public HashMap<String, HashMap<String, TipoTreinamento>> urlVerificada = new HashMap<>();
}
