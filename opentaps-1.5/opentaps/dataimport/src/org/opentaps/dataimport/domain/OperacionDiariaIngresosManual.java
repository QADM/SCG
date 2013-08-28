package org.opentaps.dataimport.domain;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.opentaps.common.util.UtilMessage;
import org.opentaps.dataimport.UtilOperacionDiariaServices;
import org.opentaps.foundation.action.ActionContext;


public class OperacionDiariaIngresosManual {
	
	private static final String MODULE = OperacionDiariaIngresosManual.class.getName();
	
    /**
     * Metodo que se utiliza para registrar una operacion diaria de ingresos
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map createOperacionDiariaIngresos(DispatchContext dctx, Map context) {
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
        
        Debug.logWarning("ENTRO AL SERVICIO PARA CREAR OPERACION DIARIA INGRESOS", MODULE);
        
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
	        
	        //Buscamos el tipo documento seleccionado en pantalla para obtener el acctgTransTypeId
			GenericValue tipoDocumento = delegator.findByPrimaryKeyCache("TipoDocumento",UtilMisc.<String, Object>toMap("idTipoDoc", tipoDoc));
			String acctgTransTypeId = tipoDocumento.getString("acctgTransTypeId");
			Debug.logWarning("tipoDocumento  Encontrado "+tipoDocumento, MODULE);
			String docu = tipoDocumento.getString("descripcion");
			String descripcion = docu == null?" ":docu+" - "+refDoc == null ?" ":refDoc;
			
	        String ciclo = "";
	        if(fecContable != null)
		        ciclo = String.valueOf(UtilDateTime.getYear(fecContable, timeZone, locale)).substring(2);
	        
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
	        
	        acctgTransId = acctgtrans.getString("acctgTransId");
	        //Se registra en AcctTransPresupuestal
	        acctgtransPres = GenericValue.create(delegator.getModelEntity("AcctgTransPresupuestal"));
	        acctgtransPres.set("acctgTransId", acctgTransId);
	        acctgtransPres.set("ciclo", ciclo);
	        acctgtransPres.set("unidadOrganizacional", organizationPartyId);
	        acctgtransPres.set("unidadEjecutora", uniEjec);
	        String unidadOr = UtilOperacionDiariaServices.obtenPadrePartyId(dctx, dispatcher, uniEjec);
	        acctgtransPres.set("unidadOrganizacional", unidadOr);
	        String unidadRes = UtilOperacionDiariaServices.obtenPadrePartyId(dctx, dispatcher, unidadOr);
	        acctgtransPres.set("unidadResponsable", unidadRes);
	        acctgtransPres.set("clavePres", cvePrespues);
	        acctgtransPres.set("rubro", rubro);
	        acctgtransPres.set("tipo", tipo);
	        acctgtransPres.set("clase", clase);
	        acctgtransPres.set("conceptoRub", concepto);
	        acctgtransPres.set("nivel5", n5);
	        acctgtransPres.set("subFuenteEspecifica", suFuenteEsp);
	        String subfuente = UtilOperacionDiariaServices.obtenPadreEnumeration(dctx, dispatcher, suFuenteEsp);
	        acctgtransPres.set("subFuente",subfuente);
	        String fuente = UtilOperacionDiariaServices.obtenPadreEnumeration(dctx, dispatcher, subfuente);
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
	        UtilOperacionDiariaServices.registraEntries(dctx, dispatcher, context, organizationPartyId,
		        			acctgTransId, monto, fecContable,
		        			acctgTransTypeId, n5, tipoFis, 
		        			idProdAbono, idProdCargo, idPago);
	        
	        
		} catch (ParseException e) {
			return UtilMessage.createAndLogServiceError(e, MODULE);
		} catch (GenericServiceException e) {
			return UtilMessage.createAndLogServiceError(e, MODULE);
		} catch (GenericEntityException e) {
			return UtilMessage.createAndLogServiceError(e, MODULE);
		}
    	
        Map results = ServiceUtil.returnSuccess();
        results.put("acctgTrans",acctgtrans);
        results.put("acctgTransPres",acctgtransPres);
        results.put("acctgTransId",acctgTransId);
        return results;
    	
    }
	
}


