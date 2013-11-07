package org.opentaps.dataimport.domain;

public class ContenedorContable {
	private String cri;
	private String cog;
	private String tipoGasto;
	private String mensaje;
	
	public ContenedorContable(){
		
	}

	public String getCri() {
		return cri;
	}

	public void setCri(String cri) {
		this.cri = cri;
	}

	public String getCog() {
		return cog;
	}

	public void setCog(String cog) {
		this.cog = cog;
	}

	public String getTipoGasto() {
		return tipoGasto;
	}

	public void setTipoGasto(String tipoGasto) {
		this.tipoGasto = tipoGasto;
	}
	
	public String getMensaje() {
		return mensaje;
	}
	
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

}
