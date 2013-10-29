/*
 * Util creacion de clave presupuestal manual
 */

package com.opensourcestrategies.financials.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.opentaps.common.util.UtilCommon;

/**
 * UtilBudget - Utilities
 * 
 * @version $Rev: 1 $
 */
public final class UtilBudget {

	private UtilBudget() {
	}

	private static String MODULE = UtilBudget.class.getName();

	/*
	 * Generar automaticamente clave presupuestal
	 * @param Map 
	 * @param dispatcher
	 * return String clave
	 */
	public static String getClavePresupuestal(Map context,
			LocalDispatcher dispatcher) {

		String clavePresupuestal = null;
		
		
		try {
			Debug.log("Entro getClavePresupuestal ", MODULE);			
			
				String fechaContable = (String) context.get("fechaContable");
				
				String ciclo = String.valueOf((getDateTransaction(fechaContable)
						.getYear() + 1900)).substring(2);
				String UE = (String) context.get("unidadEjecutora");
				String nivel5 = (String) context.get("idN5");
				String SubFuenteEspecifica = (String) context.get("subFuenteEsp");
				String subFuncion = (String) context.get("subfuncion");
				String actividad = (String) context.get("actividad");
				String PE = (String) context.get("partidaEspecifica");
				String area = ((String) context.get("area"));
				String Localidad = (String) context.get("Localidad");
			
			if(subFuncion == null)
			{				
				clavePresupuestal = ciclo + UE + nivel5 + SubFuenteEspecifica
						+ Localidad;

			}
			else
			{
				clavePresupuestal = ciclo + UE + subFuncion + actividad + PE
						+ SubFuenteEspecifica + Localidad + area;
			}
			Debug.log("Entro getClavePresupuestal clave presupuestal" + clavePresupuestal, MODULE);
			
		} catch (Exception e) {
			
			Debug.log("Error al crear clave presupuestaria " + e, MODULE);			
		}
		return clavePresupuestal;
	}

	/*
	 * Fecha
	 */

	public static Date getDateTransaction(String Fecha) {

		// 21/08/13 12:49:00
		Debug.log(Fecha);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

		try {
			return sdf.parse(Fecha);
		} catch (ParseException e) {
			String msg = "Error al hacer el parse en la fecha " + e;
			Debug.log(msg);
			return null;
		}
	}

	/*
     * */

	public static String getParentProductCategory(String id,
			LocalDispatcher dispatcher) {
		String parentProduct = null;
		try {

			EntityCondition condicion = EntityCondition.makeCondition(
					"productCategoryId", id);
			List<GenericValue> workeffort = dispatcher.getDelegator()
					.findByCondition(
							"ProductCategory",
							condicion,
							UtilMisc.toList("productCategoryId",
									"primaryParentCategoryId"), null);

			for (GenericValue genericValue : workeffort) {
				if (genericValue.get("primaryParentCategoryId") != null
						|| !genericValue.get("primaryParentCategoryId")
								.toString().equals("")) {
					Debug.log("productCategoryId"
							+ genericValue.get("productCategoryId").toString());
					Debug.log("primaryParentCategoryId"
							+ genericValue.get("primaryParentCategoryId")
									.toString());

					parentProduct = genericValue.get("primaryParentCategoryId")
							.toString();
				}

			}

		} catch (Exception e) {
			Debug.log("Error al obtener Parent de ProductCategory Id ["
					+ parentProduct + "] " + e);
		}
		return parentProduct;
	}
	
	/*
	 * */
	
	public static String getParentEnumeration(String subFuenteEspecificaId,
			LocalDispatcher dispatcher) {
		String parentEnum = null;
		try {

			EntityCondition condicion = EntityCondition.makeCondition("enumId",
					subFuenteEspecificaId);
			List<GenericValue> enumeration = dispatcher.getDelegator()
					.findByCondition("Enumeration", condicion,
							UtilMisc.toList("enumId", "parentEnumId"), null);

			for (GenericValue genericValue : enumeration) {
				if(genericValue.get("parentEnumId") != null || !genericValue.get("parentEnumId").toString().equals(""))
				{
					Debug.log("enumId" + genericValue.get("enumId").toString());
					Debug.log("parentEnumId"
							+ genericValue.get("parentEnumId").toString());
					
					parentEnum = genericValue.get("parentEnumId").toString();
				}
				
			}
			
		} catch (Exception e) {
			Debug.log("Error al obtener Parent de Enumeration Id ["
					+ subFuenteEspecificaId + "] " + e);
		}
		return parentEnum;
	}
	
	/*
	 * */
	public static String getParentParty(String partyId,
			LocalDispatcher dispatcher) {
		String parentParty = null;
		try {

			EntityCondition condicion = EntityCondition.makeCondition(
					"partyId", partyId);
			List<GenericValue> partys = dispatcher.getDelegator()
					.findByCondition("PartyGroup", condicion,
							UtilMisc.toList("partyId", "Parent_id"), null);

			for (GenericValue genericValue : partys) {
				Debug.log("partyId" + genericValue.get("partyId").toString());
				Debug.log("parentId" + genericValue.get("Parent_id").toString());
				parentParty = genericValue.get("Parent_id").toString();
			}

		} catch (Exception e) {
			Debug.log("Error al obtener Parent de Party Id [" + partyId + "] "
					+ e);
		}
		return parentParty;
	}
	
	/*
	 * */
	
	public static String getParentWorkEffort(String id,
			LocalDispatcher dispatcher) {
		
		String parentwork = null;
		try {
			
			EntityCondition condicion = EntityCondition.makeCondition("workEffortId",
					id);
			List<GenericValue> workeffort = dispatcher.getDelegator()
					.findByCondition("WorkEffort", condicion,
							UtilMisc.toList("workEffortId", "workEffortParentId"), null);

			for (GenericValue genericValue : workeffort) {
				if(genericValue.get("workEffortParentId") != null || !genericValue.get("workEffortParentId").toString().equals(""))
				{
					Debug.log("workEffortId" + genericValue.get("workEffortId").toString());
					Debug.log("workEffortParentId"
							+ genericValue.get("workEffortParentId").toString());
					
					parentwork = genericValue.get("workEffortParentId").toString();
				}
				
			}
			
		} catch (Exception e) {
			Debug.log("Error al obtener Parent de WorkEffort Id ["
					+ parentwork + "] " + e);
		}
		return parentwork;

	}
}
