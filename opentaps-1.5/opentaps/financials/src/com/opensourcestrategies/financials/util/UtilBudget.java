/*
 * Util creacion de clave presupuestal manual
 */

package com.opensourcestrategies.financials.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
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

		String clasificaciones = "", clavePresupuestal = "";
		
		try {
			Debug.log("Entro getClavePresupuestal ", MODULE);		
			
			for(int i=1; i<16; i++)
			{
				clasificaciones = (String) context.get("clasificacion"+i);
				if(clasificaciones != null)
					clavePresupuestal = clavePresupuestal + clasificaciones;
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
/*
	 * Verificar que las clasificaciones vienen completas o hacen falta
	 */
	
	public static String getClasifNull(LocalDispatcher dispatcher,
			String Organizacion, Map context, String Tipo) {

		String aviso = null;
		Calendar c = new GregorianCalendar();
		String anio = Integer.toString(c.get(Calendar.YEAR));
		Delegator delegator = dispatcher.getDelegator();

		try {

			EntityCondition condicion = EntityCondition.makeCondition(
					EntityOperator.AND, EntityCondition.makeCondition(
							"organizationPartyId", EntityOperator.EQUALS,
							Organizacion),
					EntityCondition.makeCondition("acctgTagUsageTypeId",
							EntityOperator.EQUALS, Tipo),
					EntityCondition.makeCondition("ciclo",
							EntityOperator.EQUALS, anio));

			List<GenericValue> resultado = delegator.findByCondition(
					"EstructuraClave", condicion,
					UtilMisc.toList(getListColumnas()), null);

			int tam = 0;

			// Se verifica cuantas clasificaciones tiene el Ingreso o Egreso
			// dentro de su clave
			if (!resultado.isEmpty()) {
				for (GenericValue genericValue : resultado) {
					for (int j = 1; j < 16; j++) {
						try {
							if ((genericValue.get("clasificacion"
									+ String.valueOf(j)) != null))
								tam++;
							else if (!(genericValue.get(
									"clasificacion" + String.valueOf(j))
									.toString().isEmpty()))
								tam++;

						} catch (NullPointerException e) {
							continue;
						}
					}

				}
			}

			int tam2 = 0;

			// Se valida que se llenen todas las clasificaciones
			if (tam != 0) {
				for (int j = 1; j <= tam; j++) {
					Debug.log("Clasificacion"
							+ context.get("clasificacion" + String.valueOf(j)));
					if (context.get("clasificacion" + String.valueOf(j)) != null)
						tam2++;
				}

			}

			if (tam == tam2)
				aviso = "ok";
			else
				aviso = "Nok";

		} catch (Exception e) {

			Debug.log("Error en clasificaciones obligatorias " + e, MODULE);
		}
		return aviso;
	}
	
	/**
	 * @return lista de columnas de la entidad EstructuraClave
	 */
	public static List<String> getListColumnas() {
		List<String> lista = new ArrayList<String>();
		lista.add("idSecuencia");
		for (int j = 1; j < 16; j++) {
			lista.add("clasificacion"+ String.valueOf(j));
		}
		return lista;
	}
	
	/*
	 * Obtener posicion de la clasificacion a consultar
	 * @param tipoClasificacion
	 * @param Organizacion
	 * @param Tipo
	 * @param ciclo
	 * @param dispatcher
	 * return idClasificacion
	 */
	
	public static String getClasificacion(LocalDispatcher dispatcher,
			String tipoClasificacion, String Organizacion, String Tipo) {		
		String tipoclasificacion = null; 
		Calendar c = new GregorianCalendar();
		String anio = Integer.toString(c.get(Calendar.YEAR));
		Delegator delegator = dispatcher.getDelegator();
		try {
			
			EntityCondition condicion = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, Organizacion),
					EntityCondition.makeCondition("acctgTagUsageTypeId", EntityOperator.EQUALS, Tipo),
					EntityCondition.makeCondition("ciclo", EntityOperator.EQUALS, anio));	
			
			List<GenericValue> resultado = delegator.findByCondition(
					"EstructuraClave", condicion, UtilMisc.toList(getListColumnas())
							, null);
			
			if (!resultado.isEmpty()) {
				for (GenericValue genericValue : resultado) {
					for (int j = 1; j < 16; j++) {

						try {

							if (!(genericValue.get("clasificacion"
									+ String.valueOf(j)).toString()).isEmpty()) {
								Debug.log("entro a validar clasificacion "
										+ genericValue.get(
												"clasificacion"
														+ String.valueOf(j))
												.toString());
								if (genericValue.get(
										"clasificacion" + String.valueOf(j))
										.equals(tipoClasificacion)
										|| genericValue
												.get("clasificacion"
														+ String.valueOf(j))
												.toString()
												.contains(tipoClasificacion)) {
									Debug.log("- Clasificacion"
											+ String.valueOf(j));

									tipoclasificacion = "clasificacion"
											+ String.valueOf(j);
									break;
								}
							}
						} catch (NullPointerException e) {
							continue;
						}

					}
				}
		}
			
		} catch (Exception e) {
			
			Debug.log("Error obtener clasificacion economica " + e, MODULE);			
		}
		return tipoclasificacion;
	}
	
	/*
     * Obtener posicion de la clasificacion a consultar
     * @param tipoClasificacion
     * @param Organizacion
     * @param Tipo
     * @param ciclo
     * @param dispatcher
     * return idClasificacion
     */
     
     public static String getClasificacionEconomica(LocalDispatcher dispatcher,
                 String tipoClasificacion, String Organizacion, String Tipo,
                 String ciclo) {         
           String tipoclasificacion = null; 
           Delegator delegator = dispatcher.getDelegator();
           try {
                 
                 EntityCondition condicion = EntityCondition.makeCondition(EntityOperator.AND,
                            EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, Organizacion),
                            EntityCondition.makeCondition("acctgTagUsageTypeId", EntityOperator.EQUALS, Tipo),
                            EntityCondition.makeCondition("ciclo", EntityOperator.EQUALS, ciclo));    
                 
                 List<GenericValue> resultado = delegator.findByCondition(
                            "EstructuraClave", condicion, UtilMisc.toList(getListColumnas())
                                        , null);
                 
                 if(!resultado.isEmpty()){
                       for (GenericValue genericValue : resultado) {
                            for (int j = 1; j < 16; j++) {
     
                                  if (!(genericValue.get("clasificacion" + String.valueOf(j))
                                              .toString()).isEmpty()) {
                                        Debug.log("entro a validar clasificacion "
                                                    + genericValue.get(
                                                                "clasificacion" + String.valueOf(j))
                                                                .toString());
                                        if (genericValue.get(
                                                                "clasificacion" + String.valueOf(j))
                                                                .equals(tipoClasificacion)
                                                    || genericValue
                                                                .get("clasificacion"
                                                                           + String.valueOf(j)).toString()
                                                                .contains(tipoClasificacion)) {
                                              Debug.log("Clasificacion Economica - Clasificacion"
                                                          + String.valueOf(j));
                                              
                                              tipoclasificacion = "clasificacion" + String.valueOf(j);
                                              break;
                                        }
                                  }
     
                            }                            
                 }
           }
                 
           } catch (Exception e) {
                 
                 Debug.log("Error obtener clasificacion economica " + e, MODULE);             
           }
           return tipoclasificacion;
     }
     
     /*
     * Obttiene la posicion de la clasificacion en EnumerationType y EstructuraClave
     * @param tipoClasificacion
     * @param EnumerationType
     * @param Tipo
     * @param ciclo
     * @param dispatcher
     * return idClasificacion
     */
     public static List<String> getClasificacionEnumeration(LocalDispatcher dispatcher, String tipoClasificacion, String Organizacion, String Entidad, String Tipo, String ciclo) 
     {     Delegator delegator = dispatcher.getDelegator();
           List<String> listaClasifEstrucResult = new ArrayList<String>();
           List<GenericValue> enumtype = null;      
           try 
           {     EntityCondition condicion = EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, tipoClasificacion);
                 enumtype = delegator.findByCondition(Entidad, condicion, UtilMisc.toList("clasificacionId"), null);                 
                 for(GenericValue genericValue : enumtype) 
                 {     String resultado = getClasificacionEconomica(dispatcher, genericValue.getString("clasificacionId"), Organizacion, Tipo, ciclo);                  
                       if(resultado != null)
                       {     listaClasifEstrucResult.add(resultado);                          
                       }
                 }
                 Debug.log("Omar - Lista listaClasifEstrucResult FIN: " + listaClasifEstrucResult);
           } catch (GenericEntityException e) 
           {     e.printStackTrace();
           }     
           return listaClasifEstrucResult;                
     }
}
	

