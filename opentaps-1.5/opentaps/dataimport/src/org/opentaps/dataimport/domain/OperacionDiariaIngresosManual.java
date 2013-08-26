package org.opentaps.dataimport.domain;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.opentaps.common.util.UtilCommon;
import org.opentaps.common.util.UtilMessage;
import org.opentaps.foundation.action.ActionContext;
import org.opentaps.foundation.service.ServiceException;


public class OperacionDiariaIngresosManual {
	
	private static final String MODULE = OperacionDiariaIngresosManual.class.getName();
	
    /**
     * Metodo que se utiliza para registrar una operacion diaria de ingresos
     * @param dctx
     * @param context
     * @return
     * @throws GenericEntityException 
     * @throws GenericServiceException 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map createOperacionDiariaIngresos(DispatchContext dctx, Map context) throws GenericEntityException, GenericServiceException {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String organizationPartyId = (String) context.get("organizationPartyId");
        
        final ActionContext ac = new ActionContext(context);
        final Locale locale = ac.getLocale();
        final TimeZone timeZone = ac.getTimeZone();
        String dateFormat = UtilDateTime.getDateFormat(locale);
        
        Timestamp fecContable = null;
        Timestamp fecTrans = null; 
        
        GenericValue acctgtrans;
        GenericValue acctgtransPres;
        
        String acctgTransId;
        
        Debug.logWarning("ENTRO AL SERVICIO PARA CREAR OPERACION DIARIA", MODULE);
        
        try {
        	
	        String userLog = userLogin.getString("userLoginId");
	        String tipoDoc = (String) context.get("Tipo_Documento");
	        String tipoFis = (String) context.get("Tipo_Fiscal");
	        fecTrans = (Timestamp) UtilDateTime.stringToTimeStamp((String)context.get("Fecha_Transaccion"), dateFormat, timeZone, locale);
			fecContable = (Timestamp) UtilDateTime.stringToTimeStamp((String)context.get("Fecha_Contable"), dateFormat, timeZone, locale);
	        String refDoc = (String) context.get("Referencia_Documento");
	        String sec = (String) context.get("Secuencia");
	        String cvePrespues = (String) context.get("Cve_Presupuestal");
	        String idProdAbono = (String) context.get("Id_Producto_Abono");
	        String idProdCargo = (String) context.get("Id_Producto_Cargo");
	        String rubro = (String) context.get("Rubro");
	        String tipo = (String) context.get("Tipo");
	        String clase = (String) context.get("Clase");
	        String concepto = (String) context.get("Concepto");
	        String n5 = (String) context.get("N5");
	        String entFed = (String) context.get("EntidadFederativa");
	        String region = (String) context.get("Region");
	        String muni = (String) context.get("Municipio");
	        String local = (String) context.get("Localidad");
	        String suFuenteEsp = (String) context.get("Sub_Fuente_Especifica");
	        String uniEjec = (String) context.get("Unidad_Ejecutora");
	        String idPago = (String) context.get("Id_Pago");
	        java.math.BigDecimal monto = java.math.BigDecimal.valueOf(Long.valueOf((String)context.get("Monto")));
	        
	        Debug.logWarning("userLog "+userLog, MODULE);
	        Debug.logWarning("tipoDoc "+tipoDoc, MODULE);
	        Debug.logWarning("tipoFis "+tipoFis, MODULE);
	        Debug.logWarning("fecTrans "+fecTrans, MODULE);
	        Debug.logWarning("fecContable "+fecContable, MODULE);
	        Debug.logWarning("refDoc "+refDoc, MODULE);
	        Debug.logWarning("sec "+sec, MODULE);
	        Debug.logWarning("cvePrespues "+cvePrespues, MODULE);
	        Debug.logWarning("idProdAbono "+idProdAbono, MODULE);
	        Debug.logWarning("idProdCargo "+idProdCargo, MODULE);
	        Debug.logWarning("rubro "+rubro, MODULE);
	        Debug.logWarning("tipo "+tipo, MODULE);
	        Debug.logWarning("clase "+clase, MODULE);
	        Debug.logWarning("concepto "+concepto, MODULE);
	        Debug.logWarning("n5 "+n5, MODULE);
	        Debug.logWarning("entFed "+entFed, MODULE);
	        Debug.logWarning("region "+region, MODULE);
	        Debug.logWarning("muni "+muni, MODULE);
	        Debug.logWarning("local "+local, MODULE);
	        Debug.logWarning("suFuente "+suFuenteEsp, MODULE);
	        Debug.logWarning("uniEjec "+uniEjec, MODULE);
	        Debug.logWarning("idPago "+idPago, MODULE);
	        Debug.logWarning("monto "+monto, MODULE);
	        
	        
			GenericValue tipoDocumento = delegator.findByPrimaryKeyCache("TipoDocumento",UtilMisc.<String, Object>toMap("idTipoDoc", tipoDoc));
			String acctgTransTypeId = tipoDocumento.getString("acctgTransTypeId");
			Debug.logWarning("tipoDocumento  Encontrado "+tipoDocumento, MODULE);
			String docu = tipoDocumento.getString("descripcion");
			String descripcion = docu == null?" ":docu+" - "+refDoc == null ?" ":refDoc;
	        
	        acctgtrans = GenericValue.create(delegator.getModelEntity("AcctgTrans"));
	        acctgtrans.setNextSeqId();
	        acctgtrans.set("acctgTransTypeId", acctgTransTypeId);
	        acctgtrans.set("description", descripcion);
	        acctgtrans.set("transactionDate", fecTrans);
	        acctgtrans.set("isPosted", "Y");
	        acctgtrans.set("postedDate", fecContable);
	        acctgtrans.set("glFiscalTypeId", tipoFis);
	        acctgtrans.set("partyId", uniEjec);
	        acctgtrans.set("createdByUserLogin", userLog);
	        acctgtrans.set("postedAmount", monto);
	        acctgtrans.create();
	        
	        
	        String ciclo = "";
	        if(fecContable != null){
		        ciclo = String.valueOf(UtilDateTime.getYear(fecContable, timeZone, locale)).substring(2);
		        Debug.logWarning("CLICLO STRING +++ "+ciclo, MODULE);
	        }

	        acctgTransId = acctgtrans.getString("acctgTransId");
	        //Se registra en AcctTransPresupuestal
	        acctgtransPres = GenericValue.create(delegator.getModelEntity("AcctgTransPresupuestal"));
	        acctgtransPres.set("acctgTransId", acctgTransId);
	        acctgtransPres.set("ciclo", ciclo);
	        acctgtransPres.set("unidadOrganizacional", organizationPartyId);
	        acctgtransPres.set("unidadEjecutora", uniEjec);
	        String unidadOr = obtenPadrePartyId(dctx, dispatcher, uniEjec);
	        acctgtransPres.set("unidadOrganizacional", unidadOr);
	        String unidadRes = obtenPadrePartyId(dctx, dispatcher, unidadOr);
	        acctgtransPres.set("unidadResponsable", unidadRes);
	        acctgtransPres.set("clavePres", cvePrespues);
	        acctgtransPres.set("rubro", rubro);
	        acctgtransPres.set("tipo", tipo);
	        acctgtransPres.set("clase", clase);
	        acctgtransPres.set("conceptoRub", concepto);
	        acctgtransPres.set("nivel5", n5);
	        acctgtransPres.set("subFuenteEspecifica", suFuenteEsp);
	        String subfuente = obtenPadresSubfuenteEspecifica(dctx, dispatcher, suFuenteEsp);
	        acctgtransPres.set("subFuente",subfuente);
	        String fuente = obtenPadresSubfuenteEspecifica(dctx, dispatcher, subfuente);
	        acctgtransPres.set("subFuente",fuente);	        
	        acctgtransPres.set("entidadFederativa", entFed);
	        acctgtransPres.set("region", region);
	        acctgtransPres.set("municipio", muni);
	        acctgtransPres.set("localidad", local);
	        acctgtransPres.set("idTipoDoc", tipoDoc);
	        acctgtransPres.set("secuencia", sec);
	        acctgtransPres.set("idProductoD", idProdCargo);
	        acctgtransPres.set("idProductoH", idProdAbono);
	        acctgtransPres.create();


	        //Aqui se realiza la extraccion de las cuentas para la operacion 
	    	
	    	String currencyId = UtilCommon.getOrgBaseCurrency(organizationPartyId, delegator);
		
	        Map input = new HashMap(context);
	        input.put("acctgTransTypeId", acctgTransTypeId);
	        input.put("cri", n5);
	        input.put("tipoFis", tipoFis);
	        input.put("idProdAbono", idProdAbono);
	        input.put("idProdCargo", idProdCargo);
	        input = dctx.getModelService("obtenerCuentasIngresos").makeValid(input, ModelService.IN_PARAM);
	        Map tmpResult = dispatcher.runSync("obtenerCuentasIngresos", input);
	        Map<String,String> cuentas = (Map<String, String>) tmpResult.get("cuentas");
        
	        Debug.logWarning("cuentas   Regresadas :  "+cuentas, MODULE);
        
	        if(cuentas != null && !cuentas.isEmpty()){
	        	
	        	String cargoPres = cuentas.get("Cuenta Cargo Presupuesto");
	        	String abonoPres = cuentas.get("Cuenta Abono Presupuesto");
	        	String cargoCont = cuentas.get("Cuenta Cargo Contable");
	        	String abonoCont = cuentas.get("Cuenta Abono Contable");
	        	
	        	if(cargoPres != null && !cargoPres.isEmpty() && abonoPres != null && !abonoPres.isEmpty()){
	        		
	        		GenericValue gTransEntryPreC = GenericValue.create(delegator.getModelEntity("AcctgTransEntry"));
	        		gTransEntryPreC.set("acctgTransId", acctgTransId);
	        		gTransEntryPreC.set("acctgTransEntrySeqId", String.format("%05d",1));
	        		gTransEntryPreC.set("acctgTransEntryTypeId", "_NA_");
	        		gTransEntryPreC.set("description", "Operación  diaria PRESUPUESTAL Abono"+acctgTransId);
	        		gTransEntryPreC.set("glAccountId", cargoPres);
	        		gTransEntryPreC.set("organizationPartyId", organizationPartyId);
	        		gTransEntryPreC.set("amount", monto);
	        		gTransEntryPreC.set("currencyUomId", currencyId);
	        		gTransEntryPreC.set("debitCreditFlag", "D");
	        		gTransEntryPreC.set("reconcileStatusId", "AES_NOT_RECONCILED");
	        		gTransEntryPreC.set("partyId", organizationPartyId);
	        		gTransEntryPreC.create();
	        		
	        		GenericValue gtransEntryPreA = GenericValue.create(delegator.getModelEntity("AcctgTransEntry"));
	        		gtransEntryPreA.set("acctgTransId", acctgTransId);
	        		gtransEntryPreA.set("acctgTransEntrySeqId", String.format("%05d",2));
	        		gtransEntryPreA.set("acctgTransEntryTypeId", "_NA_");
	        		gtransEntryPreA.set("description", "Operación  diaria PRESUPUESTAL Abono "+acctgTransId);
	        		gtransEntryPreA.set("glAccountId", abonoPres);
	        		gtransEntryPreA.set("organizationPartyId", organizationPartyId);
	        		gtransEntryPreA.set("amount", monto);
	        		gtransEntryPreA.set("currencyUomId", currencyId);
	        		gtransEntryPreA.set("debitCreditFlag", "C");
	        		gtransEntryPreA.set("reconcileStatusId", "AES_NOT_RECONCILED");
	        		gtransEntryPreA.set("partyId", organizationPartyId);	 
	        		gtransEntryPreA.create();
	        		
	        	}
	        	
	        	if(cargoCont != null && !cargoCont.isEmpty() && abonoCont != null && !abonoCont.isEmpty()){
	        		
	        		GenericValue gTransEntryConC = GenericValue.create(delegator.getModelEntity("AcctgTransEntry"));
	        		gTransEntryConC.set("acctgTransId", acctgTransId);
	        		gTransEntryConC.set("acctgTransEntrySeqId", String.format("%05d",3));
	        		gTransEntryConC.set("acctgTransEntryTypeId", "_NA_");
	        		gTransEntryConC.set("description", "Operación  diaria Contable Abono"+acctgTransId);
	        		gTransEntryConC.set("glAccountId", cargoCont);
	        		gTransEntryConC.set("organizationPartyId", organizationPartyId);
	        		gTransEntryConC.set("amount", monto);
	        		gTransEntryConC.set("currencyUomId", currencyId);
	        		gTransEntryConC.set("debitCreditFlag", "D");
	        		gTransEntryConC.set("reconcileStatusId", "AES_NOT_RECONCILED");
	        		gTransEntryConC.set("partyId", organizationPartyId);
	        		gTransEntryConC.create();
	        		
	        		GenericValue gTransEntryConA = GenericValue.create(delegator.getModelEntity("AcctgTransEntry"));
	        		gTransEntryConA.set("acctgTransId", acctgTransId);
	        		gTransEntryConA.set("acctgTransEntrySeqId", String.format("%05d",4));
	        		gTransEntryConA.set("acctgTransEntryTypeId", "_NA_");
	        		gTransEntryConA.set("description", "Operación  diaria Contable Abono"+acctgTransId);
	        		gTransEntryConA.set("glAccountId", abonoCont);
	        		gTransEntryConA.set("organizationPartyId", organizationPartyId);
	        		gTransEntryConA.set("amount", monto);
	        		gTransEntryConA.set("currencyUomId", currencyId);
	        		gTransEntryConA.set("debitCreditFlag", "C");
	        		gTransEntryConA.set("reconcileStatusId", "AES_NOT_RECONCILED");
	        		gTransEntryConA.set("partyId", organizationPartyId);
	        		gTransEntryConA.create();	        		
	        		
	        	}
	        	
	        }	        

		} catch (ParseException e) {
			return UtilMessage.createAndLogServiceError(e, MODULE);
		}
    	
        Map results = ServiceUtil.returnSuccess();
        results.put("acctgTrans",acctgtrans);
        results.put("acctgTransPres",acctgtransPres);
        results.put("acctgTransId",acctgTransId);
        return results;
    	
    }

    /**
     * Metodo para obtener las cuentas a registrar en una operacion diaria 
     * @param dctx
     * @param context
     * @return
     * @throws GenericServiceException 
     * @throws ServiceException 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map obtenerCuentasIngresos(DispatchContext dctx, Map context) throws ServiceException, GenericServiceException{
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String acctgTransTypeId = (String) context.get("acctgTransTypeId");
        String tipoFis = (String) context.get("tipoFis");
        String cri = (String) context.get("cri");
        String idProdAbono = (String) context.get("idProdAbono");
        String idProdCargo = (String) context.get("idProdCargo");
        
        Debug.logWarning("ENTRO A obtenerCuentasIngresos ", MODULE);
        Debug.logWarning("acctgTransTypeId "+acctgTransTypeId, MODULE);
		Map<String,String> cuentas = FastMap.newInstance();
		
        try {
        	
			GenericValue miniGuia = delegator.findByPrimaryKey("MiniGuiaContable", UtilMisc.toMap("acctgTransTypeId", acctgTransTypeId));

			
			Debug.logWarning("miniGuia     {obtenerCuentasIngresos}  : "+miniGuia, MODULE);
			
			String glFiscalTypeIdPres = miniGuia.getString("glFiscalTypeIdPres");
			String glFiscalTypeIdCont = miniGuia.getString("glFiscalTypeIdCont");
			
	    	cuentas.put("GlFiscalTypePresupuesto", glFiscalTypeIdPres);
	    	cuentas.put("GlFiscalTypeContable", glFiscalTypeIdCont);
			
			//Si el tipo fiscal es de presupuesto se obtienen las cuentas
			if(tipoFis.equalsIgnoreCase(glFiscalTypeIdPres)){
				
		    	cuentas.put("Cuenta_Cargo_Presupuesto", miniGuia.getString("cuentaCargo"));
		    	cuentas.put("Cuenta_Abono_Presupuesto", miniGuia.getString("cuentaAbono"));
		    	
			} 
			
			if(tipoFis.equalsIgnoreCase(glFiscalTypeIdCont)){
				
		    	String referencia = miniGuia.getString("referencia");
		    	String matrizId = miniGuia.getString("tipoMatriz");
		    	
		    	if(referencia.equalsIgnoreCase("M")){
		    		
			        EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
			                EntityCondition.makeCondition("cri", EntityOperator.EQUALS,cri),
			                EntityCondition.makeCondition("matrizId", EntityOperator.EQUALS,matrizId));
			
					List<GenericValue> listMatriz = delegator.findByCondition("DataImportMatrizIng", conditions, null, null);
		    		
					Debug.logWarning("matriz     {obtenerCuentasIngresos}  : "+listMatriz, MODULE);
					
					if(listMatriz.isEmpty()){
						Debug.logError("Error, elemento en Matriz no existe",MODULE);
						return UtilMessage.createAndLogServiceError("Error, elemento en Matriz no existe", MODULE);
					} else {
						
						Debug.log("matriz     {obtenerCuentasIngresos} (0) : "+listMatriz.get(0), MODULE);
						GenericValue matriz = listMatriz.get(0);
						
						String cuentaCargo = matriz.getString("cargo");
						String cuentaAbono = matriz.getString("cargo");
						
						if(matrizId.equalsIgnoreCase("B.1")){
							
							cuentaCargo = verificarAuxiliarProducto(dctx, dispatcher, cuentaCargo, idProdCargo);
							cuentaAbono = verificarAuxiliarProducto(dctx, dispatcher, cuentaAbono, idProdAbono);
							
						} else if(matrizId.equalsIgnoreCase("B.2")){
							
							cuentaAbono = verificarAuxiliarProducto(dctx, dispatcher, cuentaAbono, idProdAbono);
						}
						
						cuentas.put("Cuenta Cargo Contable",cuentaCargo);
						cuentas.put("Cuenta Abono Contable", cuentaAbono);
						
					}
					
		    		
		    	}				
				
			}
    	
		} catch (GenericEntityException e) {
			return UtilMessage.createAndLogServiceError(e, MODULE);
		}
        
        Map results = ServiceUtil.returnSuccess();
        results.put("cuentas", cuentas);
        return results;
    }
    
    /**
     * Obtiene el padre de un enumId 
     * @param enumId (Subfuente Especifica)
     * @return
     * @throws GenericServiceException 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static String obtenPadresSubfuenteEspecifica(DispatchContext dctx,LocalDispatcher dispatcher,String enumId) throws GenericServiceException{
    	
    	String padreEnumId = null;
    	
    	if(enumId != null && !enumId.isEmpty()){

        	Map input = FastMap.newInstance();
        	input.put("enumId", enumId);
        	input = dctx.getModelService("obtenEnumIdPadre").makeValid(input, ModelService.IN_PARAM);
        	Map tmpResult = dispatcher.runSync("obtenEnumIdPadre", input);
            padreEnumId = (String) tmpResult.get("enumIdPadre");
            
            Debug.logWarning("ENUM ID "+enumId+"   PADRE  "+padreEnumId, MODULE);
    		
    	}
    	
    	return padreEnumId;
    	
    }
    
    /**
     * Obtiene el padre de un partyId
     * @param dctx
     * @param dispatcher
     * @param partyId
     * @return
     * @throws GenericServiceException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static String obtenPadrePartyId(DispatchContext dctx,LocalDispatcher dispatcher,String partyId) throws GenericServiceException{
    	
    	String partyIdPadre = null;
    	
    	if(partyId != null && !partyId.isEmpty()){
    		
        	Map input = FastMap.newInstance();
        	input.put("partyId", partyId);
        	input = dctx.getModelService("obtenPartyIdPadre").makeValid(input, ModelService.IN_PARAM);
        	Map tmpResult = dispatcher.runSync("obtenPartyIdPadre", input);
        	partyIdPadre = (String) tmpResult.get("partyIdPadre");
            
            Debug.logWarning("PARTY ID "+partyId+"   PADRE  "+partyIdPadre, MODULE);
            
            
    	}
    	
    	return partyIdPadre;
    	
    }   
    
    /**
     * Metodo que valida las cuentas auxiliares de los productos a partir de una cuenta dada y regresa 
     * la cuenta correspondiente al catálogo auxiliar si se encuentra ahí , si no regresa la misma cuenta
     * @param dctx
     * @param dispatcher
     * @param glAccountId
     * @param productId
     * @return
     * @throws ServiceException
     * @throws GenericServiceException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static String verificarAuxiliarProducto(DispatchContext dctx,LocalDispatcher dispatcher,String glAccountId,String productId) throws ServiceException, GenericServiceException{
    	
    	String cuentaRegresa = glAccountId;
    	
    		if(glAccountId != null && !glAccountId.isEmpty()){
    			
            	Map input = FastMap.newInstance();
            	input.put("glAccountId", glAccountId);
            	input = dctx.getModelService("getAuxiliarProd").makeValid(input, ModelService.IN_PARAM);
            	Map tmpResult = dispatcher.runSync("getAuxiliarProd", input);
            	List<GenericValue> resultados = (List<GenericValue>) tmpResult.get("resultadoPrdCat");
            	
            	if(resultados != null && !resultados.isEmpty()){
            		
            		if(productId != null && !productId.isEmpty()){
            			
            			resultados.contains(glAccountId);
            			
            			//Iteramos ProductCategory
            			for (GenericValue genericValue : resultados) {
            				if(genericValue.getString("productCategoryId").equalsIgnoreCase(productId))
            					cuentaRegresa = genericValue.getString("glAccountId");
						}
            			
            		} else {
    					Debug.logError("Debe de proporcionar el Producto",MODULE);
    					throw new ServiceException(String.format("Debe de proporcionar el Producto"));
            		}
            	} else {
					Debug.logError("No existe catalogo auxiliar asociado a la cuenta",MODULE);
					throw new ServiceException(String.format("No existe catalogo auxiliar asociado a la cuenta : ["+glAccountId+"]"));
            	}
                
                Debug.logWarning("Cuenta Entrada {{ "+glAccountId+"   cuenta Salida ]{ "+cuentaRegresa, MODULE);
    			
    		}
    	
    	return cuentaRegresa;
    	
    }
    
}


