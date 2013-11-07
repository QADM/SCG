package org.opentaps.dataimport.domain;

import java.util.List;

import org.opentaps.base.entities.Enumeration;
import org.opentaps.base.entities.Geo;
import org.opentaps.base.entities.Party;
import org.opentaps.base.entities.ProductCategory;
import org.opentaps.base.entities.WorkEffort;

public class ContenedorContable {
	private String mensaje;
	private Party party;
	private Geo geo;
	private WorkEffort we;
	private ProductCategory product;
	private List<Enumeration> enumeration;
	private String clavePresupuestal;
	
	public ContenedorContable(){
		
	}
	
	public String getClavePresupuestal() {
		return clavePresupuestal;
	}
	
	public void setClavePresupuestal(String clavePresupuestal) {
		this.clavePresupuestal = clavePresupuestal;
	}
	
	public String getMensaje() {
		return mensaje;
	}
	
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
	public List<Enumeration> getEnumeration() {
		return enumeration;
	}
	
	public Geo getGeo() {
		return geo;
	}
	
	public Party getParty() {
		return party;
	}
	
	public ProductCategory getProduct() {
		return product;
	}
	
	public WorkEffort getWe() {
		return we;
	}
	
	public void setEnumeration(List<Enumeration> enumeration) {
		this.enumeration = enumeration;
	}
	
	public void setGeo(Geo geo) {
		this.geo = geo;
	}
	
	public void setParty(Party party) {
		this.party = party;
	}
	
	public void setProduct(ProductCategory product) {
		this.product = product;
	}
	
	public void setWe(WorkEffort we) {
		this.we = we;
	}
}
