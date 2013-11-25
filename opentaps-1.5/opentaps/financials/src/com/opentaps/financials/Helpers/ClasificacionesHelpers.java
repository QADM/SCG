package com.opentaps.financials.Helpers;

import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.opentaps.base.entities.AcctgTrans;
import org.opentaps.base.entities.EstructuraClave;

public class ClasificacionesHelpers {
	// Atributos
	private String clasificacion;
	private String nombre;
	private String valor;
	private Delegator delegator;

	// Getters & Setters

	public String getClasificacion() {
		return clasificacion;
	}

	public void setClasificacion(String clasificacion) {
		this.clasificacion = clasificacion;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public Delegator getDelegator() {
		return delegator;
	}

	public void setDelegator(Delegator delegator) {
		this.delegator = delegator;
	}

	// Constructor
	public ClasificacionesHelpers() {

	}

	// Metodos

	public List<ClasificacionesHelpers> obtenClasificaciones(
			Delegator delegator, AcctgTrans acctgTrans)
			throws GenericEntityException {
		this.delegator = delegator;

		@SuppressWarnings("deprecation")
		GenericValue estructura = obtenEstructuraClave(
				Integer.toString(acctgTrans.getPostedDate().getYear()),
				obtenTipo(acctgTrans.getAcctgTransId()));

		GenericValue transaccion = delegator.findByPrimaryKey(
				"AcctgTransPresupuestal",
				UtilMisc.toMap("workEffortId", acctgTrans.getAcctgTransId()));

		List<ClasificacionesHelpers> clasificaciones = new ArrayList<ClasificacionesHelpers>();

		for (int i = 1; i < 16; i++) {
			ClasificacionesHelpers clasifH = new ClasificacionesHelpers();
			String campo = "clasificacion" + i;
			String clasificacion = estructura.getString(campo);
			if (clasificacion != null) {
				clasifH.setClasificacion(clasificacion);
				String tabla = delegator.findByPrimaryKey("ClasifPresupuestal",
						UtilMisc.toMap("clasificacionId", clasificacion))
						.getString("tablaRelacion");

				clasificaciones.addAll(obtenClasificacionConPadres(clasificacion,
						transaccion.getString("campo"), tabla));

			}
		}

		return null;
	}

	public String obtenTipo(String tipo) {
		boolean ingreso = tipo.contains("I");
		if (ingreso) {
			return "INGRESO";
		} else {
			return "EGRESO";
		}
	}

	public GenericValue obtenEstructuraClave(String anio, String tipo)
			throws GenericEntityException {
		List<GenericValue> estructuras = delegator.findByAnd("EstructuraClave",
				"ciclo", anio, "acctgTagUsageTypeId", tipo);
		for (GenericValue estructura : estructuras) {
			return estructura;
		}
		return null;
	}

	public List<ClasificacionesHelpers> obtenClasificacionConPadres(String clasificacion, String id,
			String tabla) throws GenericEntityException {

		if (tabla.equalsIgnoreCase("Party")) {
			return obtenParties(id, clasificacion);
		} else if (tabla.equalsIgnoreCase("ProductCategory")) {
			return obtenProducts(id, clasificacion);
		} else if (tabla.equalsIgnoreCase("Enumeration")) {
			return obtenEnums(id, clasificacion);
		} else if (tabla.equalsIgnoreCase("Geo")) {
			return obtenGeos(id, clasificacion);
		} else if (tabla.equalsIgnoreCase("WorkEffort")) {
			return 	obtenWorkEfforts(id, clasificacion);
		}
		return null;

	}

	public List<ClasificacionesHelpers> obtenParties(String id,
			String clasificacion) throws GenericEntityException {
		List<ClasificacionesHelpers> clasificaciones = new ArrayList<ClasificacionesHelpers>();
		ClasificacionesHelpers clasifH = new ClasificacionesHelpers();
		clasifH.setClasificacion(clasificacion);

		do {
			GenericValue generic = delegator.findByPrimaryKey("Party",
					UtilMisc.toMap("partyId", id));
			clasifH.setNombre("nivelId");

			generic = delegator.findByPrimaryKey("PartyGroup",
					UtilMisc.toMap("partyId", id));
			clasifH.setValor(generic.getString("groupName"));
			id = generic.getString("parentId");
			clasificaciones.add(clasifH);
		} while (id != null || !id.isEmpty());
		return clasificaciones;

	}

	public List<ClasificacionesHelpers> obtenProducts(String id,
			String clasificacion) throws GenericEntityException {
		List<ClasificacionesHelpers> clasificaciones = new ArrayList<ClasificacionesHelpers>();
		ClasificacionesHelpers clasifH = new ClasificacionesHelpers();
		clasifH.setClasificacion(clasificacion);

		do {
			GenericValue generic = delegator.findByPrimaryKey(
					"ProductCategory", UtilMisc.toMap("productCategoryId", id));
			clasifH.setNombre("productCategoryTypeId");
			clasifH.setValor(generic.getString("description"));
			id = generic.getString("primaryParentCategoryId");
			clasificaciones.add(clasifH);
		} while (id != null || !id.isEmpty());
		return clasificaciones;
	}

	public List<ClasificacionesHelpers> obtenEnums(String id,
			String clasificacion) throws GenericEntityException {
		List<ClasificacionesHelpers> clasificaciones = new ArrayList<ClasificacionesHelpers>();
		ClasificacionesHelpers clasifH = new ClasificacionesHelpers();
		clasifH.setClasificacion(clasificacion);

		do {
			GenericValue generic = delegator.findByPrimaryKey("Enumeration",
					UtilMisc.toMap("enumId", id));
			clasifH.setNombre("nivelId");
			clasifH.setValor(generic.getString("description"));
			id = generic.getString("parentEnumId");
			clasificaciones.add(clasifH);
		} while (id != null || !id.isEmpty());
		return clasificaciones;
	}

	public List<ClasificacionesHelpers> obtenGeos(String id,
			String clasificacion) throws GenericEntityException {
		List<ClasificacionesHelpers> clasificaciones = new ArrayList<ClasificacionesHelpers>();
		ClasificacionesHelpers clasifH = new ClasificacionesHelpers();
		clasifH.setClasificacion(clasificacion);

		do {
			GenericValue generic = delegator.findByPrimaryKey("Geo",
					UtilMisc.toMap("geoId", id));
			clasifH.setNombre("geoTypeId");
			clasifH.setValor(generic.getString("geoName"));
			id = generic.getString("geoCode");
			clasificaciones.add(clasifH);
		} while (id != null || !id.isEmpty());
		return clasificaciones;
	}

	public List<ClasificacionesHelpers> obtenWorkEfforts(String id,
			String clasificacion) throws GenericEntityException {
		List<ClasificacionesHelpers> clasificaciones = new ArrayList<ClasificacionesHelpers>();
		ClasificacionesHelpers clasifH = new ClasificacionesHelpers();
		clasifH.setClasificacion(clasificacion);

		do {
			GenericValue generic = delegator.findByPrimaryKey("WorkEffort",
					UtilMisc.toMap("workEffortId", id));
			clasifH.setNombre("nivelId");
			clasifH.setValor(generic.getString("description"));
			id = generic.getString("workEffortParentId");
			clasificaciones.add(clasifH);
		} while (id != null || !id.isEmpty());
		return clasificaciones;
	}

}
