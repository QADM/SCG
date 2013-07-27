package com.opensourcestrategies.financials.financials;

import java.io.Serializable;
import java.math.BigDecimal;

public class FlujoEfectivo implements Serializable,Comparable<FlujoEfectivo>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String grupoReport;
	private String idCuenta;
	private String nombreCta;
	private BigDecimal saldo;
	private BigDecimal saldoAnt;
	
	public FlujoEfectivo() {

		this.grupoReport = new String();
		this.idCuenta = new String();
		this.nombreCta = new String();
		this.saldo = BigDecimal.ZERO;
	
	}
	
	public String getGrupoReport() {
		return grupoReport;
	}
	
	public String getIdCuenta() {
		return idCuenta;
	}
	
	public String getNombreCta() {
		return nombreCta;
	}
	
	public BigDecimal getSaldo() {
		return saldo;
	}
	
	public BigDecimal getSaldoAnt() {
		return saldoAnt;
	}
	
	public void setGrupoReport(String grupoReport) {
		this.grupoReport = grupoReport;
	}
	
	public void setIdCuenta(String idCuenta) {
		this.idCuenta = idCuenta;
	}
	
	public void setNombreCta(String nombreCta) {
		this.nombreCta = nombreCta;
	}
	
	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}
	
	public void setSaldoAnt(BigDecimal saldoAnt) {
		this.saldoAnt = saldoAnt;
	}
	

	@Override
	public String toString() {
		return this.grupoReport+"|"+this.idCuenta+"|"+this.nombreCta+"|"+this.saldo+"|"+this.saldoAnt;
	}
	
	@Override
	public int compareTo(FlujoEfectivo other){
		return this.idCuenta.compareTo(other.getIdCuenta());
	}


}
