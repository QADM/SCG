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

public class OperacionEgresoService extends DomainService{

	
	public Map<String, Object> registraEgreso(DispatchContext d,Map<String, Object> context) throws ServiceException, GenericEntityException, GenericServiceException
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
		//String tipoCatalogo= (String) context.get("tipoCatalogo");
		String idPago= (String) context.get("idPago");
		String idProductoD= (String) context.get("idProductoD");
		String idProductoH= (String) context.get("idProductoH");
		String ciclo= (String) context.get("ciclo");
	    String ue_S= (String) context.get("ue");
	    String subf_S= (String) context.get("subf");
	    String act_S= (String) context.get("act");
	    String tg_S= (String) context.get("tg");
	    String pe_S= (String) context.get("pe");
        String sfe_S= (String) context.get("sfe");
        String loc_S= (String) context.get("loc");
        String area_S= (String) context.get("area");
        String concatenacion= (String) context.get("concatenacion");
        
      //Crea un registro de la entidad
        GenericValue dataImportEgresoDiario = GenericValue.create(del.getModelEntity("DataImportEgresoDiario"));
        dataImportEgresoDiario.set("idTipoDoc", tipoDocumento );
        dataImportEgresoDiario.set("fechaRegistro", getFechaHHMMSS(fechaRegistro));
        dataImportEgresoDiario.set("fechaContable", getFechaHHMMSS(fechaContable));
        dataImportEgresoDiario.set("monto", monto);
        dataImportEgresoDiario.set("organizationPartyId", organizacionContable);
        dataImportEgresoDiario.set("refDoc", refDoc);
        dataImportEgresoDiario.set("secuencia", secuencia);
        dataImportEgresoDiario.set("usuario", usuario );
        dataImportEgresoDiario.set("idPago", idPago );
        dataImportEgresoDiario.set("idProductoD", idProductoD );
        dataImportEgresoDiario.set("idProductoH", idProductoH );
        dataImportEgresoDiario.set("ciclo", ciclo);
        dataImportEgresoDiario.set("ue", ue_S );
        dataImportEgresoDiario.set("subf", subf_S);
        dataImportEgresoDiario.set("act", act_S);
        dataImportEgresoDiario.set("tg", tg_S);
        dataImportEgresoDiario.set("pe", pe_S);
        dataImportEgresoDiario.set("sfe", sfe_S);
        dataImportEgresoDiario.set("loc", loc_S );
        dataImportEgresoDiario.set("area", area_S);
        dataImportEgresoDiario.set("clavePres", concatenacion);
        dataImportEgresoDiario.create();
        
        
        //Invoca el servicio
        Map<String,Object> input = new HashMap<String,Object>();
        input.put("login.username", "admin");
        input.put("login.password", "opentaps");
        input.put("lote", "1");
        input = d.getModelService("importEgresoDiario").makeValid(input, ModelService.IN_PARAM);
        Map<String, Object> tmpResult = d.getDispatcher().runSync("importEgresoDiario", input);
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
