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
        String clasif1= (String) context.get("clasif1");
        String clasif2 = (String) context.get("clasif2");
        String clasif3 = (String) context.get("clasif3");
        String clasif4 = (String) context.get("clasif4");
        String clasif5 = (String) context.get("clasif5");
        String clasif6 = (String) context.get("clasif6");
        String clasif7 = (String) context.get("clasif7");
        String clasif8 = (String) context.get("clasif8");
        String clasif9 = (String) context.get("clasif9");
        String clasif10 = (String) context.get("clasif10");
        String clasif11 = (String) context.get("clasif11");
        String clasif12 = (String) context.get("clasif12");
        String clasif13 = (String) context.get("clasif13");
        String clasif14 = (String) context.get("clasif14");
        String clasif15 = (String) context.get("clasif15");
        
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
        dataImportEgresoDiario.set("clasificacion1", clasif1);
        dataImportEgresoDiario.set("clasificacion2", clasif2 );
        dataImportEgresoDiario.set("clasificacion3", clasif3);
        dataImportEgresoDiario.set("clasificacion4", clasif4);
        dataImportEgresoDiario.set("clasificacion5", clasif5 );
        dataImportEgresoDiario.set("clasificacion6", clasif6 );
        dataImportEgresoDiario.set("clasificacion7", clasif7 );
        dataImportEgresoDiario.set("clasificacion8", clasif8 );
        dataImportEgresoDiario.set("clasificacion9", clasif9 );
        dataImportEgresoDiario.set("clasificacion10", clasif10 );
        dataImportEgresoDiario.set("clasificacion11", clasif11 );
        dataImportEgresoDiario.set("clasificacion12", clasif12 );
        dataImportEgresoDiario.set("clasificacion13", clasif13 );
        dataImportEgresoDiario.set("clasificacion14", clasif14 );
        dataImportEgresoDiario.set("clasificacion15", clasif15 );
        //dataImportEgresoDiario.set("clavePres", concatenacion);
        dataImportEgresoDiario.create();
        
        
        //Invoca el servicio
        Map<String,Object> input = new HashMap<String,Object>();
        input.put("login.username", "admin");
        input.put("login.password", "opentaps");
        input.put("lote", "x");
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
