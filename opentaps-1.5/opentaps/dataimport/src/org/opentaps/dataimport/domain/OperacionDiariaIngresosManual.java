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
import org.opentaps.base.entities.AcctgTransEntry;
import org.opentaps.common.util.UtilCommon;
import org.opentaps.common.util.UtilMessage;
import org.opentaps.foundation.action.ActionContext;


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
        List<AcctgTransEntry> entries = FastList.newInstance();
        
        Debug.logWarning("ENTRO AL SERVICIO PARA CREAR OPERACION DIARIA", MODULE);
        
        try {   
        	
	        String userLog = userLogin.getString("userLoginId");
	        String tipoDoc = (String) context.get("Tipo_Documento");
	        String tipoFis = (String) context.get("Tipo_Fiscal");
	        fecTrans = (Timestamp) UtilDateTime.stringToTimeStamp((String)context.get("Fecha_Transaccion"), dateFormat, timeZone, locale);
			fecContable = (Timestamp) UtilDateTime.stringToTimeStamp((String)context.get("Fecha_Contable"), dateFormat, timeZone, locale);
	        String refDoc = (String) context.get("Referencia_Documento");
	        String sec = (String) context.get("Secuencia");
	        String tipoCat = (String) context.get("Tipo_Catalogo");
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
	        String suFuente = (String) context.get("Sub_Fuente_Especifica");
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
	        Debug.logWarning("tipoCat "+tipoCat, MODULE);
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
	        Debug.logWarning("suFuente "+suFuente, MODULE);
	        Debug.logWarning("uniEjec "+uniEjec, MODULE);
	        Debug.logWarning("idPago "+idPago, MODULE);
	        Debug.logWarning("monto "+monto, MODULE);
	        
	        String descripcion = refDoc == null?" ":refDoc+" - "+tipoCat == null ?" ":tipoCat;
			GenericValue tipoDocumento = delegator.findByPrimaryKeyCache("TipoDocumento",UtilMisc.<String, Object>toMap("idTipoDoc", tipoDoc));
			String acctgTransTypeId = tipoDocumento.getString("acctgTransTypeId");
			Debug.logWarning("tipoDocumento  Encontrado "+tipoDocumento, MODULE);	  
	        
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
	        acctgtransPres.set("rubro", rubro);
	        acctgtransPres.set("tipo", tipo);
	        acctgtransPres.set("clase", clase);
	        acctgtransPres.set("conceptoRub", concepto);
	        acctgtransPres.set("nivel5", n5);
	        acctgtransPres.set("subFuenteEspecifica", suFuente);
	        acctgtransPres.set("entidadFederativa", entFed);
	        acctgtransPres.set("region", region);
	        acctgtransPres.set("municipio", muni);
	        acctgtransPres.set("localidad", local);
	        acctgtransPres.set("idTipoDoc", tipoDoc);
	        acctgtransPres.set("secuencia", sec);
	        acctgtransPres.create();


	        //Aqui se realiza la extraccion de las cuentas para la operacion 
	        
			//Se obtiene el tipo (CRI)
	    	String tipoCRI = new String();
	    	boolean encontro = false;
	    	if(n5 != null && !n5.isEmpty() && !encontro){
	    		tipoCRI = n5;
	    		encontro = true;
	    	} else if (concepto != null && !concepto.isEmpty() && !encontro){
	    		tipoCRI = concepto;
	    		encontro = true;
	    	} else if (clase != null && !clase.isEmpty() && !encontro){
	    		tipoCRI = clase;
	    		encontro = true;
	    	} else if (tipo != null && !tipo.isEmpty() && !encontro){
	    		tipoCRI = tipo;
	    		encontro = true;
	    	} else {
	    		tipoCRI = rubro;
	    		encontro = true;
	    	}
	    	
	    	Debug.logWarning("tipoCRI   "+tipoCRI, MODULE);	
	    	
	    	String currencyId = UtilCommon.getOrgBaseCurrency(organizationPartyId, delegator);
	    	
//	    	AcctgTransEntry transEntryPreC = new AcctgTransEntry();
//	    	AcctgTransEntry transEntryPreA = new AcctgTransEntry();
//	    	AcctgTransEntry transEntryConC = new AcctgTransEntry();
//	    	AcctgTransEntry transEntryConA = new AcctgTransEntry();
			
	        Map input = new HashMap(context);
	        input.put("acctgTransTypeId", acctgTransTypeId);
	        input.put("tipo", tipoCRI);
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
	        		
	        		//Generamos la transaccion para Cargo
//	        		transEntryPreC.setAcctgTransId(acctgTransId);
//	        		transEntryPreC.setAcctgTransEntrySeqId(String.format("%05d",1));
//	        		transEntryPreC.setAcctgTransEntryTypeId("_NA_");
//	        		transEntryPreC.setDescription("Operaci�n  diaria PRESUPUESTAL"+acctgTransId);
//	        		transEntryPreC.setGlAccountId(cargoPres);
//	        		transEntryPreC.setOrganizationPartyId(organizationPartyId);
//	        		transEntryPreC.setAmount(monto);
//	        		transEntryPreC.setCurrencyUomId(currencyId);
//	        		transEntryPreC.setDebitCreditFlag("D");
//	        		transEntryPreC.setReconcileStatusId("AES_NOT_RECONCILED");	
//	        		transEntryPreC.setPartyId(organizationPartyId);
//	        		
//	        		entries.add(transEntryPreC);
	        		
	        		GenericValue gTransEntryPreC = GenericValue.create(delegator.getModelEntity("AcctgTransEntry"));
	        		gTransEntryPreC.set("acctgTransId", acctgTransId);
	        		gTransEntryPreC.set("acctgTransEntrySeqId", String.format("%05d",1));
	        		gTransEntryPreC.set("acctgTransEntryTypeId", "_NA_");
	        		gTransEntryPreC.set("description", "Operaci�n  diaria PRESUPUESTAL Abono"+acctgTransId);
	        		gTransEntryPreC.set("glAccountId", cargoPres);
	        		gTransEntryPreC.set("organizationPartyId", organizationPartyId);
	        		gTransEntryPreC.set("amount", monto);
	        		gTransEntryPreC.set("currencyUomId", currencyId);
	        		gTransEntryPreC.set("debitCreditFlag", "D");
	        		gTransEntryPreC.set("reconcileStatusId", "AES_NOT_RECONCILED");
	        		gTransEntryPreC.set("partyId", organizationPartyId);
	        		gTransEntryPreC.create();
	        		
	        		//Generamos la transaccion para Abono
//	        		transEntryPreA.setAcctgTransId(acctgTransId);
//	        		transEntryPreA.setAcctgTransEntrySeqId(String.format("%05d",2));
//	        		transEntryPreA.setAcctgTransEntryTypeId("_NA_");
//	        		transEntryPreA.setDescription("Operaci�n  diaria PRESUPUESTAL Abono "+acctgTransId);
//	        		transEntryPreA.setGlAccountId(abonoPres);
//	        		transEntryPreA.setOrganizationPartyId(organizationPartyId);
//	        		transEntryPreA.setAmount(monto);
//	        		transEntryPreA.setCurrencyUomId(currencyId);
//	        		transEntryPreA.setDebitCreditFlag("C");
//	        		transEntryPreA.setReconcileStatusId("AES_NOT_RECONCILED");	
//	        		transEntryPreA.setPartyId(organizationPartyId);	        		
//	        		
//	        		entries.add(transEntryPreA);
	        		
	        		GenericValue gtransEntryPreA = GenericValue.create(delegator.getModelEntity("AcctgTransEntry"));
	        		gtransEntryPreA.set("acctgTransId", acctgTransId);
	        		gtransEntryPreA.set("acctgTransEntrySeqId", String.format("%05d",2));
	        		gtransEntryPreA.set("acctgTransEntryTypeId", "_NA_");
	        		gtransEntryPreA.set("description", "Operaci�n  diaria PRESUPUESTAL Abono "+acctgTransId);
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
	        		
	        		//Generamos la transaccion para Cargo
//	        		transEntryConC.setAcctgTransId(acctgTransId);
//	        		transEntryConC.setAcctgTransEntrySeqId(String.format("%05d",3));
//	        		transEntryConC.setAcctgTransEntryTypeId("_NA_");
//	        		transEntryConC.setDescription("Operaci�n  diaria Contable"+acctgTransId);
//	        		transEntryConC.setGlAccountId(cargoCont);
//	        		transEntryConC.setOrganizationPartyId(organizationPartyId);
//	        		transEntryConC.setAmount(monto);
//	        		transEntryConC.setCurrencyUomId(currencyId);
//	        		transEntryConC.setDebitCreditFlag("D");
//	        		transEntryConC.setReconcileStatusId("AES_NOT_RECONCILED");	
//	        		transEntryConC.setPartyId(organizationPartyId);
//	        		
//	        		entries.add(transEntryConC);
	        		
	        		GenericValue gTransEntryConC = GenericValue.create(delegator.getModelEntity("AcctgTransEntry"));
	        		gTransEntryConC.set("acctgTransId", acctgTransId);
	        		gTransEntryConC.set("acctgTransEntrySeqId", String.format("%05d",3));
	        		gTransEntryConC.set("acctgTransEntryTypeId", "_NA_");
	        		gTransEntryConC.set("description", "Operaci�n  diaria Contable Abono"+acctgTransId);
	        		gTransEntryConC.set("glAccountId", cargoCont);
	        		gTransEntryConC.set("organizationPartyId", organizationPartyId);
	        		gTransEntryConC.set("amount", monto);
	        		gTransEntryConC.set("currencyUomId", currencyId);
	        		gTransEntryConC.set("debitCreditFlag", "D");
	        		gTransEntryConC.set("reconcileStatusId", "AES_NOT_RECONCILED");
	        		gTransEntryConC.set("partyId", organizationPartyId);
	        		gTransEntryConC.create();
	        		
	        		//Generamos la transaccion para Abono
//	        		transEntryConA.setAcctgTransId(acctgTransId);
//	        		transEntryConA.setAcctgTransEntrySeqId(String.format("%05d",4));
//	        		transEntryConA.setAcctgTransEntryTypeId("_NA_");
//	        		transEntryConA.setDescription("Operaci�n  diaria Contable"+acctgTransId);
//	        		transEntryConA.setGlAccountId(abonoCont);
//	        		transEntryConA.setOrganizationPartyId(organizationPartyId);
//	        		transEntryConA.setAmount(monto);
//	        		transEntryConA.setCurrencyUomId(currencyId);
//	        		transEntryConA.setDebitCreditFlag("C");
//	        		transEntryConA.setReconcileStatusId("AES_NOT_RECONCILED");	
//	        		transEntryConA.setPartyId(organizationPartyId);	    
//	        		
//	        		entries.add(transEntryConA);
	        		
	        		GenericValue gTransEntryConA = GenericValue.create(delegator.getModelEntity("AcctgTransEntry"));
	        		gTransEntryConA.set("acctgTransId", acctgTransId);
	        		gTransEntryConA.set("acctgTransEntrySeqId", String.format("%05d",4));
	        		gTransEntryConA.set("acctgTransEntryTypeId", "_NA_");
	        		gTransEntryConA.set("description", "Operaci�n  diaria Contable Abono"+acctgTransId);
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
        results.put("acctgTransEntries", entries);
        return results;
    	
    }

    /**
     * Metodo para obtener las cuentas a registrar en una operacion diaria 
     * @param dctx
     * @param context
     * @return
     */
    public static Map obtenerCuentasIngresos(DispatchContext dctx, Map context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        String acctgTransTypeId = (String) context.get("acctgTransTypeId");
        String tipo = (String) context.get("tipo");
        
        
        Debug.logWarning("ENTRO A obtenerCuentasIngresos ", MODULE);
        Debug.logWarning("acctgTransTypeId "+acctgTransTypeId, MODULE);
		Map<String,String> cuentas = FastMap.newInstance();
		
        try {
        	
			GenericValue miniGuia = delegator.findByPrimaryKey("MiniGuiaContable", UtilMisc.toMap("acctgTransTypeId", acctgTransTypeId));

			
			Debug.logWarning("miniGuia     {obtenerCuentasIngresos}  : "+miniGuia, MODULE);
			
	    	cuentas.put("GlFiscalTypePresupuesto", miniGuia.getString("glFiscalTypeIdPres"));
	    	cuentas.put("GlFiscalTypeContable", miniGuia.getString("glFiscalTypeIdCont"));
	    	cuentas.put("Cuenta Cargo Presupuesto", miniGuia.getString("cuentaCargo"));
	    	cuentas.put("Cuenta Abono Presupuesto", miniGuia.getString("cuentaAbono"));
	    	
	    	String referencia = miniGuia.getString("referencia");
	    	String matrizId = miniGuia.getString("tipoMatriz");
	    	
	    	if(referencia.equalsIgnoreCase("M")){
	    		
		        EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
		                EntityCondition.makeCondition("cri", EntityOperator.EQUALS,tipo),
		                EntityCondition.makeCondition("matrizId", EntityOperator.EQUALS,matrizId));
		
				List<GenericValue> listMatriz = delegator.findByCondition("DataImportMatrizIng", conditions, null, null);
	    		
				Debug.logWarning("matriz     {obtenerCuentasIngresos}  : "+listMatriz, MODULE);
				
				if(listMatriz.isEmpty()){
					Debug.logError("Error, elemento en Matriz no existe",MODULE);
					return UtilMessage.createAndLogServiceError("Error, elemento en Matriz no existe", MODULE);
				} else {
					
					Debug.log("matriz     {obtenerCuentasIngresos} (0) : "+listMatriz.get(0), MODULE);
					GenericValue matriz = listMatriz.get(0);
					
					cuentas.put("Cuenta Cargo Contable",matriz.getString("cargo"));
					cuentas.put("Cuenta Abono Contable", matriz.getString("abono"));
					
				}
				
	    		
	    	}
    	
		} catch (GenericEntityException e) {
			return UtilMessage.createAndLogServiceError(e, MODULE);
		}
        
        Map results = ServiceUtil.returnSuccess();
        results.put("cuentas", cuentas);
        return results;
    }

}


