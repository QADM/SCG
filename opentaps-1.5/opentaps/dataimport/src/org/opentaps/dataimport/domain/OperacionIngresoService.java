package org.opentaps.dataimport.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
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

public class OperacionIngresoService extends DomainService  {

	public Map<String, Object> registraIngreso(DispatchContext d,Map<String, Object> context) throws ServiceException, GenericServiceException, GenericEntityException
	{
		Delegator del = d.getDelegator();
		Map<String,Object> output = new HashMap<String, Object>();
		String tipoDocumento= (String) context.get("tipoDocumento");
        String fechaRegistro= (String) context.get("fechaRegistro");
        String fechaContable= (String) context.get("fechaContable");
        BigDecimal monto= (BigDecimal) context.get("monto");
        String organizacionContable= (String) context.get("organizacionContable");
        String refDoc= (String) context.get("refDoc");
        String secuencia= (String) context.get("secuencia");
        String usuario= (String) context.get("usuario");
        //String lote= (String) context.get("lote");
        String idPago= (String) context.get("idPago");
        String idProductoD= (String) context.get("idProductoD");
        String idProductoH= (String) context.get("idProductoH");
        String ciclo= (String) context.get("ciclo");
        String ue_S= (String) context.get("ue");
        String n5_S= (String) context.get("n5");
        String sfe_S= (String) context.get("sfe");
        String loc_S= (String) context.get("loc");
        String concatenacion= (String) context.get("concatenacion");
        
        //Crea un registro de la entidad
        GenericValue dataImportIngresoDiario = GenericValue.create(del.getModelEntity("DataImportIngresoDiario"));
        dataImportIngresoDiario.set("idTipoDoc", tipoDocumento );
        dataImportIngresoDiario.set("fechaRegistro", getFechaHHMMSS(fechaRegistro));
        dataImportIngresoDiario.set("fechaContable", getFechaHHMMSS(fechaContable));
        dataImportIngresoDiario.set("monto", monto);
        dataImportIngresoDiario.set("organizationPartyId", organizacionContable);
        dataImportIngresoDiario.set("refDoc", refDoc);
        dataImportIngresoDiario.set("secuencia", secuencia);
        dataImportIngresoDiario.set("usuario", usuario );
        dataImportIngresoDiario.set("idPago", idPago );
        dataImportIngresoDiario.set("idProductoD", idProductoD );
        dataImportIngresoDiario.set("idProductoH", idProductoH );
        dataImportIngresoDiario.set("ciclo", ciclo);
        dataImportIngresoDiario.set("ue", ue_S );
        dataImportIngresoDiario.set("n5", n5_S);
        dataImportIngresoDiario.set("sfe", sfe_S);
        dataImportIngresoDiario.set("loc", loc_S );
        dataImportIngresoDiario.set("clavePres", concatenacion);
        dataImportIngresoDiario.create();
        
        
        //Invoca el servicio
        Map<String,Object> input = new HashMap<String,Object>();
        input.put("login.username", "admin");
        input.put("login.password", "opentaps");
        input.put("lote", "1");
        input = d.getModelService("importIngresoDiario").makeValid(input, ModelService.IN_PARAM);
        Map<String, Object> tmpResult = d.getDispatcher().runSync("importIngresoDiario", input);
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
        

