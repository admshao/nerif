package org.nerif.model;

import java.util.Date;

import org.nerif.util.Config;

public class Regra {
	private String descricao;
	private InfoPropriedade infoPropriedade;
	private TipoComparacao tipoComparacao;
	private TipoValor tipoValor;
	private String valorString1;
	private String valorString2;
	private Long valorLong1;
	private Long valorLong2;
	private Boolean valorBoolean1;
	private Boolean valorBoolean2;
	private Date valorData1;
	private Date valorData2;

	public Regra(String descricao, InfoPropriedade infoPropriedade, TipoComparacao tipoComparacao, TipoValor tipoValor,
			String valor1, String valor2) {
		this.descricao = descricao;
		this.infoPropriedade = infoPropriedade;
		this.tipoComparacao = tipoComparacao;
		this.tipoValor = tipoValor;
		switch (infoPropriedade) {
		case DATA:
			try {
				valorData1 = Config.dfData.convertStringToDate(valor1);
				if (valor2 != null) {
					valorData2 = Config.dfData.convertStringToDate(valor2);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case HORA:
			try {
				valorData1 = Config.dfHora.convertStringToDate(valor1);
				if (valor2 != null) {
					valorData2 = Config.dfHora.convertStringToDate(valor2);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case PORTA:
		case TAMANHO:
		case TEMPO:
			valorLong1 = Long.parseLong(valor1);
			if (valor2 != null) {
				valorLong2 = Long.parseLong(valor2);
			}
			break;
		case URL:
			valorString1 = valor1;
			valorString2 = valor2;
			break;
		default:
			break;
		}
	}

	public String getDescricao() {
		return descricao;
	}

	public InfoPropriedade getInfoPropriedade() {
		return infoPropriedade;
	}

	public TipoComparacao getTipoComparacao() {
		return tipoComparacao;
	}

	public TipoValor getTipoValor() {
		return tipoValor;
	}

	public String getValorString1() {
		return valorString1;
	}

	public String getValorString2() {
		return valorString2;
	}

	public Long getValorLong1() {
		return valorLong1;
	}

	public Long getValorLong2() {
		return valorLong2;
	}

	public Boolean getValorBoolean1() {
		return valorBoolean1;
	}

	public Boolean getValorBoolean2() {
		return valorBoolean2;
	}

	public Date getValorData1() {
		return valorData1;
	}

	public Date getValorData2() {
		return valorData2;
	}
}
