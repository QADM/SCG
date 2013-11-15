package org.opentaps.dataimport.domain;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.opentaps.common.util.UtilMessage;
import org.opentaps.dataimport.UtilOperacionDiariaServices;
import org.opentaps.foundation.action.ActionContext;
import org.opentaps.foundation.service.ServiceException;

public class OperacionDiariaEgresosManual {
	
	private static final String MODULE = OperacionDiariaEgresosManual.class.getName();
	
	/**
	 * Metodo que crea la operacion diaria de Egresos 
	 * @param dctx
	 * @param context
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map createOperacionDiariaEgresos(DispatchContext dctx, Map context){
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
        
        GenericValue acctgtrans = null;
        GenericValue acctgtransPres = null;
        
        String acctgTransId = null;
        
        //Aqui se guardan las transacciones registradas
        List<String> listTransId = FastList.newInstance();
		
        Debug.logWarning("ENTRO AL SERVICIO PARA CREAR OPERACION DIARIA EGRESOS", MODULE);
        
        try{
        	Calendar c = new GregorianCalendar();
 			String anio = Integer.toString(c.get(Calendar.YEAR));
        	
	        String userLog = userLogin.getString("userLoginId");
	        String tipoDoc = (String) context.get("Tipo_Documento");
	        fecTrans = (Timestamp) context.get("Fecha_Transaccion");
			fecContable = (Timestamp) context.get("Fecha_Contable");
	        String refDoc = (String) context.get("Referencia_Documento");
	        String sec = (String) context.get("Secuencia");
	        String cvePrespues = UtilOperacionDiariaServices.getClavePresupuestal(context, dispatcher); 
	        String idProdAbono = (String) context.get("Id_Producto_Abono");
	        String idProdCargo = (String) context.get("Id_Producto_Cargo");
	        String entFed = (String) context.get("EntidadFederativa");	        
	        String idPago = (String) context.get("Id_RecaudadoH");
	        java.math.BigDecimal monto = java.math.BigDecimal.valueOf(Long.valueOf((String)context.get("Monto")));
	        
	        Debug.logWarning("userLog "+userLog, MODULE);
	        Debug.logWarning("tipoDoc "+tipoDoc, MODULE);
	        Debug.logWarning("fecTrans "+fecTrans, MODULE);
	        Debug.logWarning("fecContable "+fecContable, MODULE);
	        Debug.logWarning("refDoc "+refDoc, MODULE);
	        Debug.logWarning("sec "+sec, MODULE);
	        Debug.logWarning("cvePrespues "+cvePrespues, MODULE);
	        Debug.logWarning("idProdAbono "+idProdAbono, MODULE);
	        Debug.logWarning("idProdCargo "+idProdCargo, MODULE);
	        Debug.logWarning("entFed "+entFed, MODULE);	      
	        Debug.logWarning("idPago "+idPago, MODULE);
	        Debug.logWarning("monto "+monto, MODULE);	
	        
	        String clasif = UtilOperacionDiariaServices.getClasifNull(dispatcher,
					organizationPartyId, context, "EGRESO");
			
			if(clasif.equals("Nok"))
				throw new ServiceException(String.format("Deben de llenarse todas las clasificaciones"));
	       
			
	        String clasificacion = UtilOperacionDiariaServices.getClasificacionEconomica(dispatcher, "CL_COG", organizationPartyId, "EGRESO", anio);
	        
	        //Buscamos el tipo documento seleccionado en pantalla para obtener el acctgTransTypeId
			GenericValue tipoDocumento = delegator.findByPrimaryKeyCache("TipoDocumento",UtilMisc.<String, Object>toMap("idTipoDoc", tipoDoc));
			String acctgTransTypeId = tipoDocumento.getString("acctgTransTypeId");
			Debug.logWarning("tipoDocumento  Encontrado "+tipoDocumento, MODULE);
			String docu = tipoDocumento.getString("descripcion");
			String descripcion = (docu == null?" ":docu)+" - "+(refDoc == null ?" ":refDoc);
			
	        String ciclo = "";
	        if(fecContable != null)
		        ciclo = String.valueOf(UtilDateTime.getYear(fecContable, timeZone, locale)).substring(2);

	        //Obtener los tipos fiscales que se encuentran en la miniguia
	        List<String> tiposFiscales = UtilOperacionDiariaServices.obtenTiposFiscalDoc(dctx, dispatcher, tipoDoc);
	        
	        for (String tipoFis : tiposFiscales) {	        
	        
		        //Obtiene el mapa que se utiliza para guardar las cuentas correspondientes y para validaciones
		        Map<String,String> mapCuentas = UtilOperacionDiariaServices.regresaMapa(dctx, dispatcher, context, 
		        										monto, fecContable, acctgTransTypeId, (String) context.get(clasificacion), 
		        										tipoFis, idProdAbono, idProdCargo, idPago, "COG");
		        
		        String tipoAsiento = UtilOperacionDiariaServices.obtenTipoAsiento(mapCuentas);
		        
		        //Generamos la transaccion
		        acctgTransId = (refDoc == null ? "" :refDoc)+"-"+(sec == null ? "" :sec)+"-"+(tipoAsiento == null ? "" :tipoAsiento);
		        
		        listTransId.add(acctgTransId);
		        
		        acctgtrans = GenericValue.create(delegator.getModelEntity("AcctgTrans"));
	//	        acctgtrans.setNextSeqId();
		        acctgtrans.set("acctgTransId", acctgTransId);
		        acctgtrans.set("acctgTransTypeId", acctgTransTypeId);
		        acctgtrans.set("description", descripcion);
		        acctgtrans.set("transactionDate", fecTrans);
		        acctgtrans.set("isPosted", "Y");
		        acctgtrans.set("postedDate", fecContable);
		        acctgtrans.set("glFiscalTypeId", tipoFis);		       
		        acctgtrans.set("createdByUserLogin", userLog);
		        acctgtrans.set("postedAmount", monto);
		        acctgtrans.create();
		        
	//	        acctgTransId = acctgtrans.getString("acctgTransId");
		        //Se registra en AcctTransPresupuestal
		        acctgtransPres = GenericValue.create(delegator.getModelEntity("AcctgTransPresupuestal"));
		        acctgtransPres.set("acctgTransId", acctgTransId);		        
		        
		        for (int i = 1; i < 16; i++) {
		        	acctgtransPres.set("clasificacion" + i, (String) context.get("clasificacion" + i));
				}
		        acctgtransPres.set("clavePres", cvePrespues);
		        acctgtransPres.set("idTipoDoc", tipoDoc);
		        acctgtransPres.set("secuencia", sec);
		        acctgtransPres.set("idProductoD", idProdCargo);
		        acctgtransPres.set("idProductoH", idProdAbono);
		        acctgtransPres.set("idPago",idPago);
		        acctgtransPres.set("agrupador", refDoc);
		        acctgtransPres.create();
		        
		        Map<String,String> mapaAcctgEnums = FastMap.newInstance();
		        List<String> resultClasificaciones = new ArrayList<String>();		        
		        resultClasificaciones= UtilOperacionDiariaServices.getClasificacionEnumeration(dispatcher, "ACCOUNTING_TAG", organizationPartyId, "EnumerationType", "EGRESO", anio);		        
		        int tam = resultClasificaciones.size();
		        for(int i=0; i<tam; i++)		        
		        {	String id = "acctgTagEnumId"+String.valueOf(i+1);
		        	String idValue = resultClasificaciones.get(i);		        	
		        	mapaAcctgEnums.put(id,(String) context.get(idValue));		        	
		        }
		        Debug.log("Omar - mapaAcctgEnums: " + mapaAcctgEnums);
		       
		        //Se realiza el registro de trans entries
		        UtilOperacionDiariaServices.registraEntries(dctx, dispatcher, context,
						organizationPartyId, acctgTransId, monto,
						fecContable, acctgTransTypeId, mapaAcctgEnums, mapCuentas);
			
	        }
        
        } catch (GenericEntityException e) {
        	Debug.log( e.getMessage(), MODULE);
			Exception message = e;
			if(e.getMessage().contains("Violation of PRIMARY KEY constraint"))
			{
				 message = new Exception("Error: No se puede insertar registro duplicado");
			}
        	return UtilMessage.createAndLogServiceError(message, MODULE);
		} catch (GenericServiceException e) {
			return UtilMessage.createAndLogServiceError(e, MODULE);
		} catch (ServiceException e) {
			return UtilMessage.createAndLogServiceError(e, MODULE);
		}
		
        Map results = ServiceUtil.returnSuccess();
        results.put("listTransId", listTransId);
        return results;
	}


}
