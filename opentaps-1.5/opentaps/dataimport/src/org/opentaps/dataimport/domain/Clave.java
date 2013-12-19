package org.opentaps.dataimport.domain;

import java.math.BigDecimal;

public class Clave {
	
	private String valor;
	private BigDecimal monto;
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public BigDecimal getMonto() {
		return monto;
	}
	public void setMonto(BigDecimal monto) {
		this.monto = monto;
	}
	public Clave(String valor, BigDecimal monto) {
		super();
		this.valor = valor;
		this.monto = monto;
	}
	
	

}
