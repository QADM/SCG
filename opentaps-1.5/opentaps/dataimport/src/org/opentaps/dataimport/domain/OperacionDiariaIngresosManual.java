package org.opentaps.dataimport.domain;

import java.math.BigDecimal;
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
	        String idPago = (String) context.get("Id_RecaudadoH");
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
	        
	        
			GenericValue tipoDocumento = delegator.findByPrimaryKey("TipoDocumento",UtilMisc.<String, Object>toMap("idTipoDoc", tipoDoc));
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
	        acctgtransPres.set("idPago",idPago);
	        acctgtransPres.create();


	        //Se realiza el registro de trans entries
	    	
	        registraEntries(dctx, dispatcher, context, organizationPartyId,
		        			acctgTransId, monto, fecContable,
		        			acctgTransTypeId, n5, tipoFis, 
		        			idProdAbono, idProdCargo, idPago);
	        
	        
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
        String idPago = (String) context.get("idPago");
        
        Debug.logWarning("ENTRO A obtenerCuentasIngresos ", MODULE);
        Debug.logWarning("acctgTransTypeId "+acctgTransTypeId, MODULE);
		Map<String,String> mapCuentas = FastMap.newInstance();
		
        try {
        	
			GenericValue miniGuia = delegator.findByPrimaryKey("MiniGuiaContable", UtilMisc.toMap("acctgTransTypeId", acctgTransTypeId));

			
			Debug.logWarning("miniGuia     {obtenerCuentasIngresos}  : "+miniGuia, MODULE);
			
			String glFiscalTypeIdPres = miniGuia.getString("glFiscalTypeIdPres");
			String glFiscalTypeIdCont = miniGuia.getString("glFiscalTypeIdCont");
			
	    	mapCuentas.put("GlFiscalTypePresupuesto", glFiscalTypeIdPres);
	    	mapCuentas.put("GlFiscalTypeContable", glFiscalTypeIdCont);
			
			//Si el tipo fiscal es de presupuesto se obtienen las cuentas
			if(tipoFis.equalsIgnoreCase(glFiscalTypeIdPres)){
				
		    	mapCuentas.put("Cuenta_Cargo_Presupuesto", miniGuia.getString("cuentaCargo"));
		    	mapCuentas.put("Cuenta_Abono_Presupuesto", miniGuia.getString("cuentaAbono"));
		    	
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
							
							cuentaCargo = verificarBancos(dctx, dispatcher, cuentaCargo, idPago);
							cuentaAbono = verificarAuxiliarProducto(dctx, dispatcher, cuentaAbono, idProdAbono);
						}
						
						mapCuentas.put("Cuenta_Cargo_Contable",cuentaCargo);
						mapCuentas.put("Cuenta_Abono_Contable", cuentaAbono);
						
					}
					
		    		
		    	}				
				
			}
    	
		} catch (GenericEntityException e) {
			return UtilMessage.createAndLogServiceError(e, MODULE);
		}
        
        Debug.logWarning("CUENTAS REGRESADAS [obtenerCuentasIngresos] "+mapCuentas, MODULE);
        
        Map results = ServiceUtil.returnSuccess();
        results.put("mapCuentas", mapCuentas);
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
            	
            	Debug.logWarning("glAccountId  "+glAccountId+"  getAuxiliarProd   *[ "+resultados+"*]", MODULE);
            	
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
            	} 
                
                Debug.logWarning("Cuenta Entrada {{ "+glAccountId+"   cuenta Salida ]{ "+cuentaRegresa, MODULE);
    			
    		}
    	
    	return cuentaRegresa;
    	
    }
    
    /**
     * Metodo que valida las cuentas auxiliares de los productos a partir de una cuenta dada y regresa 
     * la cuenta correspondiente al catálogo auxiliar si se encuentra ahí , si no regresa la misma cuenta
     * @param dctx
     * @param dispatcher
     * @param glAccountId
     * @param productId
     * @return string
     * @throws ServiceException
     * @throws GenericServiceException
     */
	public static String verificarBancos(DispatchContext dctx,LocalDispatcher dispatcher,String glAccountId,String recaudadoD) throws ServiceException, GenericEntityException{
		Delegator delegator = dctx.getDelegator();
		
    	String cuentaRegresa = glAccountId;
    	
    	if(glAccountId != null && !glAccountId.isEmpty()){
    		
    		GenericValue paymenthMet = delegator.findByPrimaryKey("PaymentMethod", UtilMisc.toMap("paymentMethodId",recaudadoD));
    		if(paymenthMet != null && !paymenthMet.isEmpty())
    			cuentaRegresa = paymenthMet.getString("glAccountId");
    		
    	} 
    	
    	return cuentaRegresa;
    	
    }
	
	/**
	 * Metodo que se utiliza para registrar los entries relacionados a una operacion de ingresos
	 * @param dctx
	 * @param dispatcher
	 * @param context
	 * @return
	 * @throws GenericEntityException 
	 * @throws GenericServiceException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<GenericValue> registraEntries(DispatchContext dctx,LocalDispatcher dispatcher,Map context,
						String organizationPartyId,String acctgTransId,BigDecimal monto,Timestamp fecContable,
						String acctgTransTypeId, String CRI , String tipoFiscal, 
						String idProdAbono, String idProdCargo, String idPago) throws GenericEntityException, GenericServiceException{
		
		Delegator delegator = dctx.getDelegator();
		
    	String currencyId = UtilCommon.getOrgBaseCurrency(organizationPartyId, delegator);
		
        Map input = new HashMap(context);
        input.put("acctgTransTypeId", acctgTransTypeId);
        input.put("cri", CRI);
        input.put("tipoFis", tipoFiscal);
        input.put("idProdAbono", idProdAbono);
        input.put("idProdCargo", idProdCargo);
        input.put("idPago",idPago);
        input = dctx.getModelService("obtenerCuentasIngresos").makeValid(input, ModelService.IN_PARAM);
        Map tmpResult = dispatcher.runSync("obtenerCuentasIngresos", input);
        Map<String,String> mapCuentas = (Map<String, String>) tmpResult.get("mapCuentas");
        List<GenericValue> listCuentas = FastList.newInstance();
    
        if(mapCuentas != null && !mapCuentas.isEmpty()){
        	
        	String cargoPres = mapCuentas.get("Cuenta_Cargo_Presupuesto");
        	String abonoPres = mapCuentas.get("Cuenta_Abono_Presupuesto");
        	String cargoCont = mapCuentas.get("Cuenta_Cargo_Contable");
        	String abonoCont = mapCuentas.get("Cuenta_Abono_Contable");
        	
        	Debug.logWarning(" cargoPres "+cargoPres, MODULE);
        	Debug.logWarning(" abonoPres "+abonoPres, MODULE);
        	Debug.logWarning(" cargoCont "+cargoCont, MODULE);
        	Debug.logWarning(" abonoCont "+abonoCont, MODULE);
        	
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
        		
        		listCuentas.add(gTransEntryPreC);
        		listCuentas.add(gtransEntryPreA);
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
        		
        		listCuentas.add(gTransEntryConC);
        		listCuentas.add(gTransEntryConA);
        		
        	}
        	
        }
        
        Debug.logWarning("LISTA DE CUENTAS REGISTRADAS  } "+listCuentas, MODULE);
        
        //Aqui se guardan los datos correspondientes en la tabla AccountHistory
        
        List<GenericValue> listHistory = registrarAcctHistory(dctx, dispatcher, context, 
        			fecContable, organizationPartyId, listCuentas);
        
        Debug.logWarning("LISTA DE CUENTAS HISTORY REGISTRADAS  } "+listHistory, MODULE);
        
        List<GenericValue> listOrganization = registrarAcctOrganization(dctx, dispatcher, context,
        			organizationPartyId,monto, mapCuentas);
        
        Debug.logWarning("LISTA DE CUENTAS ORGANIZATION REGISTRADAS  } "+listOrganization, MODULE);
        
		
        return listCuentas;
	}
	
	/**
	 * Metodo que registra el GL_ACCOUNT_HISTORY
	 * @param fechaContable
	 * @param organizationPartyId
	 * @param listCuentas
	 * @throws GenericServiceException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<GenericValue> registrarAcctHistory(DispatchContext dctx,LocalDispatcher dispatcher,
					Map context,Timestamp fechaContable, 
					String organizationPartyId, List<GenericValue> listCuentas) throws GenericServiceException{
		
        Map input = new HashMap(context);
        input.put("fecContable", fechaContable);
        input.put("organizationPartyId", organizationPartyId);
        input.put("listCuentas", listCuentas);
        input = dctx.getModelService("guardaAccountHistory").makeValid(input, ModelService.IN_PARAM);
        Map tmpResult = dispatcher.runSync("guardaAccountHistory", input);

        List<GenericValue> listSaved = (List<GenericValue>) tmpResult.get("listAccountsSaved");
		
		return listSaved;
	}
	
	/**
	 * Metodo que registra el GL_ACCOUNT_ORGANIZATION
	 * @param fechaContable
	 * @param organizationPartyId
	 * @param listCuentas
	 * @throws GenericServiceException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<GenericValue> registrarAcctOrganization(DispatchContext dctx,LocalDispatcher dispatcher,Map context,
					String organizationPartyId,BigDecimal monto, Map<String,String> mapCuentas) throws GenericServiceException{
		
        Map input = new HashMap(context);
        input.put("organizationPartyId", organizationPartyId);
        input.put("mapCuentas", mapCuentas);
        input.put("monto", monto);
        input = dctx.getModelService("guardaAccountOrganization").makeValid(input, ModelService.IN_PARAM);
        Map tmpResult = dispatcher.runSync("guardaAccountOrganization", input);

        List<GenericValue> listSaved = (List<GenericValue>) tmpResult.get("listAccountsSaved");
		
		return listSaved;
	}	
	
}


