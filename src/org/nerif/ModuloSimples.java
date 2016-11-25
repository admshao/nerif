package org.nerif;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.nerif.model.ConcurrentDateFormat;
import org.nerif.model.ConcurrentTimeFormat;
import org.nerif.model.FormatoLog;
import org.nerif.model.Indicador;
import org.nerif.model.InfoPropriedade;
import org.nerif.model.Regra;
import org.nerif.model.ValidaIndicador;
import org.nerif.util.Config;

public class ModuloSimples {
	public final ConcurrentDateFormat dfData = new ConcurrentDateFormat();
	public final ConcurrentTimeFormat dfHora = new ConcurrentTimeFormat();
	private ValidaIndicador[] indicadoresBase = new ValidaIndicador[Config.indicadores.size()];
	private HashMap<InfoPropriedade, Boolean> verificaColuna = new HashMap<>();

	public ModuloSimples() {
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

	private void validaIndicadores(final HashMap<String, String> cols) {
		boolean indicadorAtivado = false;
		for (ValidaIndicador validaIndicador : indicadoresBase) {
			if (validaIndicador.regras.stream().reduce(true, (a, b) -> a & b)) {
				ModuloAlerta.getInstance().indicadorAtivado(validaIndicador.indicador, cols);
				indicadorAtivado = true;
			}
		}
		if (Config.EXECUTA_MODULO_ANALISE) {
			ModuloAnalise.getInstance().processaLinha(cols, indicadorAtivado);
		}
	}

	public void processaLinha(final HashMap<String, String> cols) {
		resetaVerificacao();

		for (int i = 0; i != Config.indicadores.size(); i++) {
			Indicador indicador = Config.indicadores.get(i);
			for (int j = 0; j != indicador.getRegras().size(); j++) {
				Regra regra = indicador.getRegras().get(j);
				if (verificaColuna.get(regra.getInfoPropriedade())) {
					String s = cols.get(regra.getInfoPropriedade().getInfoPropriedade());
					switch (regra.getTipoValor()) {
					case NUMERICO:
						double valor1IntCol = Double.valueOf(s);
						switch (regra.getTipoComparacao()) {
						case IGUAL:
							if (valor1IntCol == regra.getValorLong1()) {
								indicadoresBase[i].regras.set(j, true);
							}
							break;
						case DIFERENTE:
							if (valor1IntCol != regra.getValorLong1()) {
								indicadoresBase[i].regras.set(j, true);
							}
							break;
						case MAIOR_QUE:
							if (valor1IntCol > regra.getValorLong1()) {
								indicadoresBase[i].regras.set(j, true);
							}
							break;
						case MENOR_QUE:
							if (valor1IntCol < regra.getValorLong1()) {
								indicadoresBase[i].regras.set(j, true);
							}
							break;
						case NO_INTERVALO:
							if (valor1IntCol >= regra.getValorLong1() && valor1IntCol <= regra.getValorLong2()) {
								indicadoresBase[i].regras.set(j, true);
							}
							break;
						case FORA_DO_INTERVALO:
							if (valor1IntCol <= regra.getValorLong1() || valor1IntCol >= regra.getValorLong2()) {
								indicadoresBase[i].regras.set(j, true);
							}
							break;
						default:
							break;
						}
						break;
					case BOOLEAN:
						boolean valor1BooleanCols = Boolean.valueOf(s);
						switch (regra.getTipoComparacao()) {
						case IGUAL:
							if (valor1BooleanCols == regra.getValorBoolean1()) {
								indicadoresBase[i].regras.set(j, true);
							}
							break;
						case DIFERENTE:
							if (valor1BooleanCols != regra.getValorBoolean1()) {
								indicadoresBase[i].regras.set(j, true);
							}
							break;
						default:
							break;
						}
						break;
					case DATA:
						try {
							Date result = dfData.convertStringToDate(s);
							switch (regra.getTipoComparacao()) {
							case IGUAL:
								if (result.compareTo(regra.getValorData1()) == 0) {
									indicadoresBase[i].regras.set(j, true);
								}
								break;
							case DIFERENTE:
								if (result.compareTo(regra.getValorData1()) != 0) {
									indicadoresBase[i].regras.set(j, true);
								}
								break;
							case MAIOR_QUE:
								if (result.compareTo(regra.getValorData1()) > 0) {
									indicadoresBase[i].regras.set(j, true);
								}
								break;
							case MENOR_QUE:
								if (result.compareTo(regra.getValorData1()) < 0) {
									indicadoresBase[i].regras.set(j, true);
								}
								break;
							case NO_INTERVALO:
								if (result.compareTo(regra.getValorData1()) >= 0
										&& result.compareTo(regra.getValorData2()) <= 0) {
									indicadoresBase[i].regras.set(j, true);
								}
								break;
							case FORA_DO_INTERVALO:
								if (result.compareTo(regra.getValorData1()) < 0
										|| result.compareTo(regra.getValorData2()) > 0) {
									indicadoresBase[i].regras.set(j, true);
								}
								break;
							default:
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case HORA:
						try {
							Date result = dfHora.convertStringToDate(s);
							switch (regra.getTipoComparacao()) {
							case IGUAL:
								if (result.compareTo(regra.getValorData1()) == 0) {
									indicadoresBase[i].regras.set(j, true);
								}
								break;
							case DIFERENTE:
								if (result.compareTo(regra.getValorData1()) != 0) {
									indicadoresBase[i].regras.set(j, true);
								}
								break;
							case MAIOR_QUE:
								if (result.compareTo(regra.getValorData1()) > 0) {
									indicadoresBase[i].regras.set(j, true);
								}
								break;
							case MENOR_QUE:
								if (result.compareTo(regra.getValorData1()) < 0) {
									indicadoresBase[i].regras.set(j, true);
								}
								break;
							case NO_INTERVALO:
								if (result.compareTo(regra.getValorData1()) >= 0
										&& result.compareTo(regra.getValorData2()) <= 0) {
									indicadoresBase[i].regras.set(j, true);
								}
								break;
							case FORA_DO_INTERVALO:
								if (result.compareTo(regra.getValorData1()) < 0
										|| result.compareTo(regra.getValorData2()) > 0) {
									indicadoresBase[i].regras.set(j, true);
								}
								break;
							default:
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case STRING:
						String valor1StringCols = s;
						switch (regra.getTipoComparacao()) {
						case IGUAL:
							if (valor1StringCols.equalsIgnoreCase(regra.getValorString1())) {
								indicadoresBase[i].regras.set(j, true);
							}
							break;
						case DIFERENTE:
							if (!valor1StringCols.equalsIgnoreCase(regra.getValorString1())) {
								indicadoresBase[i].regras.set(j, true);
							}
							break;
						case CONTENHA:
							if (valor1StringCols.contains(regra.getValorString1())) {
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

		validaIndicadores(cols);
	}
}