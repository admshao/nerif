package org.nerif;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.nerif.model.FormatoLog;
import org.nerif.model.Indicador;
import org.nerif.model.InfoPropriedade;
import org.nerif.model.Regra;
import org.nerif.model.ValidaIndicador;
import org.nerif.util.Config;

public class IndicadoresSimples {
	private static IndicadoresSimples instance = null;

	public static IndicadoresSimples getInstance() {
		if (instance == null) {
			instance = new IndicadoresSimples();
		}
		return instance;
	}

	private ValidaIndicador[] indicadoresBase = new ValidaIndicador[Config.indicadores.size()];
	private HashMap<InfoPropriedade, Boolean> verificaColuna = new HashMap<>();

	private IndicadoresSimples() {

	}

	public void init() {
		for (FormatoLog formato : Config.colunasLog) {
			verificaColuna.put(formato.getInfoPropriedade(), false);
		}
		for (int i = 0; i != Config.indicadores.size(); i++) {
			Indicador indicador = Config.indicadores.get(i);
			ValidaIndicador validaIndicaor = new ValidaIndicador();
			validaIndicaor.indicador = indicador;
			validaIndicaor.regras = new ArrayList<>();
			for (Regra regra : indicador.getRegras()) {
				validaIndicaor.regras.add(false);
				for (FormatoLog formato : Config.colunasLog) {
					verificaColuna.put(regra.getInfoPropriedade(), verificaColuna.get(regra.getInfoPropriedade())
							| regra.getInfoPropriedade().equals(formato.getInfoPropriedade()));
				}
			}
			indicadoresBase[i] = validaIndicaor;
		}
	}

	private void resetaVerificacao() {
		for (ValidaIndicador validaIndicador : indicadoresBase) {
			for (int i = 0; i != validaIndicador.regras.size(); i++) {
				validaIndicador.regras.set(i, false);
			}
		}
	}

	private void validaIndicadores(List<String> cols) {
		for (ValidaIndicador validaIndicador : indicadoresBase) {
			boolean ok = true;
			for (boolean b : validaIndicador.regras) {
				ok &= b;
			}
			if (ok) {
				Alertas.getInstance().notificaIndicatorAtivo(validaIndicador.indicador, cols);
			}
		}
	}

	public void processaLinha(List<String> cols) {
		resetaVerificacao();

		for (int i = 0; i != Config.indicadores.size(); i++) {
			Indicador indicador = Config.indicadores.get(i);
			for (int j = 0; j != indicador.getRegras().size(); j++) {
				Regra regra = indicador.getRegras().get(j);
				if (verificaColuna.get(regra.getInfoPropriedade())) {
					for (int k = 0; k != Config.colunasLog.size(); k++) {
						if (Config.colunasLog.get(k).getInfoPropriedade().equals(regra.getInfoPropriedade())) {
							switch (regra.getTipoValor()) {
							case NUMERICO:
								int valor1Cols = Integer.valueOf(cols.get(k));
								int valor1Regra = Integer.valueOf(regra.getValor1());
								switch (regra.getTipoComparacao()) {
								case DIFERENTE:
									if (valor1Cols != valor1Regra) {
										indicadoresBase[i].regras.set(j, true);
									}
									break;
								default:
									break;
								}
								break;
							default:
								break;
							}
						}
					}
				}
			}
		}

		validaIndicadores(cols);
	}
}
