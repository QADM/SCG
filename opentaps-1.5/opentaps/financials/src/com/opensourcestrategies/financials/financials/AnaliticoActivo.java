package com.opensourcestrategies.financials.financials;

import java.io.Serializable;
import java.math.BigDecimal;

import org.opentaps.foundation.entity.Entity;

public class AnaliticoActivo implements Serializable,Comparable<AnaliticoActivo>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String cuentaId;
	private String nombreCta;
	private BigDecimal saldoInicial;
	private BigDecimal cargo;
	private BigDecimal abono;
	private BigDecimal saldoFinal;
	private BigDecimal flujoEfectivo;
	
	public void setCuentaId(String cuentaId) {
		this.cuentaId = cuentaId;
	}
	
	public void setNombreCta(String nombreCta) {
		this.nombreCta = nombreCta;
	}
	
	public void setAbono(BigDecimal abono) {
		this.abono = abono;
	}
	
	public void setCargo(BigDecimal cargo) {
		this.cargo = cargo;
	}
	
	public void setSaldoInicial(BigDecimal saldoInicial) {
		this.saldoInicial = saldoInicial;
	}
	
	public void setSaldoFinal(BigDecimal saldoFinal) {
		this.saldoFinal = saldoFinal;
	}
	
	public void setFlujoEfectivo(BigDecimal flujoEfectivo) {
		this.flujoEfectivo = flujoEfectivo;
	}
	
	public String getCuentaId() {
		return cuentaId;
	}
	
	public String getNombreCta() {
		return nombreCta;
	}
	
	public BigDecimal getAbono() {
		return abono;
	}
	
	public BigDecimal getCargo() {
		return cargo;
	}
	
	public BigDecimal getSaldoInicial() {
		return saldoInicial;
	}
	
	public BigDecimal getSaldoFinal() {
		return saldoFinal;
	}
	
	public BigDecimal getFlujoEfectivo() {
		return flujoEfectivo;
	}
	
	@Override
	public String toString() {
		return this.cuentaId+"|"+this.nombreCta+"|"+this.saldoInicial+"|"+this.cargo+"|"+this.abono+"|"+this.saldoFinal+"|"+this.flujoEfectivo;
	}
	@Override
	public int compareTo(AnaliticoActivo other){
		return this.cuentaId.compareTo(other.getCuentaId());
	}		

}
