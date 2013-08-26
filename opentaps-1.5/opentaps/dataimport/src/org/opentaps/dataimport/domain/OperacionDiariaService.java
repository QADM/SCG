package org.opentaps.dataimport.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;
import org.opentaps.domain.DomainService;
import org.opentaps.foundation.service.ServiceException;

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;

public class OperacionDiariaService extends DomainService{
	
	public Map<String, Object> registraDiario(DispatchContext d,Map<String, Object> context) throws ServiceException, GenericEntityException, GenericServiceException{
	
		Delegator del = d.getDelegator();
		Map<String,Object> output = new HashMap<String, Object>();
		//String tTrans_S= (String) context.get("tTrans");
		String fechaRegistro_S= (String) context.get("fechaRegistro");
		String fechaContable_S= (String) context.get("fechaContable");
		BigDecimal monto_S= (BigDecimal) context.get("monto");
		String organizacionContable_S= (String) context.get("organizacionContable");
		String organizacionEjecutora_S= (String) context.get("organizacionEjecutora");
		String tipoDocumento_S= (String) context.get("tipoDocumento");
		String refDoc_S= (String) context.get("refDoc");
		String secuencia_S= (String) context.get("secuencia");
		String usuario_S= (String) context.get("usuario");
		//String lote_S= (String) context.get("lote");
		String concepto_S= (String) context.get("concepto");
		String subConcepto_S= (String) context.get("subConcepto");
		String tipoCatalogoC_S= (String) context.get("idTipoCatalogoC");
		String idC_S= (String) context.get("idC");
		String tipoCatalogoD_S= (String) context.get("idTipoCatalogoD");
		String idD_S= (String) context.get("idD");
		
		 //Crea un registro de la entidad
        GenericValue dataImportOperacionDiaria = GenericValue.create(del.getModelEntity("DataImportOperacionDiaria"));
        dataImportOperacionDiaria.set("idTipoDoc", tipoDocumento_S);
        dataImportOperacionDiaria.set("fechaRegistro", getFechaHHMMSS(fechaRegistro_S));
        dataImportOperacionDiaria.set("fechaContable", getFechaHHMMSS(fechaContable_S));
        dataImportOperacionDiaria.set("monto", monto_S);
        dataImportOperacionDiaria.set("organizationPartyId", organizacionContable_S);
        dataImportOperacionDiaria.set("organizacionEjecutora", organizacionEjecutora_S);
        dataImportOperacionDiaria.set("refDoc", refDoc_S);
        dataImportOperacionDiaria.set("secuencia", secuencia_S);
        dataImportOperacionDiaria.set("usuario", usuario_S );
        dataImportOperacionDiaria.set("concepto", concepto_S );
        dataImportOperacionDiaria.set("subconcepto", subConcepto_S );
        dataImportOperacionDiaria.set("idTipoCatalogoC", tipoCatalogoC_S );
        dataImportOperacionDiaria.set("idC", idC_S);
        dataImportOperacionDiaria.set("idTipoCatalogoD", tipoCatalogoD_S );
        dataImportOperacionDiaria.set("idD", idD_S);
        dataImportOperacionDiaria.create();
        
        
        //Invoca el servicio
        Map<String,Object> input = new HashMap<String,Object>();
        input.put("login.username", "admin");
        input.put("login.password", "opentaps");
        input.put("lote", "1");
        input = d.getModelService("importOperacionDiaria").makeValid(input, ModelService.IN_PARAM);
        Map<String,Object> tmpResult = d.getDispatcher().runSync("importOperacionDiaria", input);
        output.put("messageOut", tmpResult.get("importedRecords"));
		return output;
}
	
	private Timestamp getFechaHHMMSS(String fecha) {

		SimpleDateFormat formatoDelTexto = new SimpleDateFormat(
				"dd-MM-yyyy hh:mm:ss");

		Calendar cal = null;

		try {
				String cadFecha = fecha.trim();
				cal = Calendar.getInstance();
				cal.setTime(formatoDelTexto.parse(cadFecha));
			
		} catch (Exception e) {
			Debug.log("No se pudo hacer el parser de la fecha: " + e);
		}

		return new Timestamp(cal.getTimeInMillis());

	}

}
