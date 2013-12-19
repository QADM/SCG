package org.opentaps.dataimport.domain;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.opentaps.domain.DomainService;
import org.opentaps.foundation.infrastructure.InfrastructureException;
import org.opentaps.foundation.service.ServiceException;

public class ValidacionSuficienciaPresupuestaria extends DomainService {

	public Map<String, Object> validaSuficiencia(DispatchContext d,Map<String, Object> context) throws ServiceException, GenericServiceException, GenericEntityException
	{
		Map<String,Object> output = new HashMap<String, Object>();
		String ur= (String) context.get("unidadResponsable");
		String clave= (String) context.get("clavePresupuestaria");
		BigDecimal monto= (BigDecimal) context.get("monto");
		String ciclo= (String) context.get("ciclo");
		String periodo= (String) context.get("periodo");
		String tipoDocumento= (String) context.get("tipoDocumento");
		String referencia= (String) context.get("referencia");
		String usuario= (String) context.get("usuario");
		String fechaSolicitud= (String) context.get("fechaSolicitud");
		String tipoOperacion = (String)context.get("tipoOperacion");
		
		try {
			String tipoCuenta = "";
			if(tipoOperacion.equals("Ingreso"))
			{
				tipoCuenta = "CARGO";
			}
			else
			{
				tipoCuenta = "ABONO";
			}
			List<BigDecimal> montos = new ArrayList<BigDecimal>();
			BigDecimal saldo = BigDecimal.ZERO;
            String url = "jdbc:sqlserver://OPENTAPSVM;databaseName=ofbiz;SelectMethod=cursor;";
            Connection conn = DriverManager.getConnection(url,"ofbiz","ofbiz");
            Statement stmt = conn.createStatement();
            ResultSet rs;
 
            rs = stmt.executeQuery("select distinct gla.gl_account_id CUENTA "+
                    ", AEN.amount MONTO "+
                    ", AEN.debit_credit_flag TIPO "+
                    ", GLA.naturaleza NATURALEZA "+
                    ", GLA.GL_ACCOUNT_CLASS_ID MOMENTO "+
                    ", AT.ACCTG_TRANS_ID TRANSACCION "+
                    ", CTP_M.PERIOD_NAME MES "+
               "from   ACCTG_TRANS_ENTRY        AEN "+
                    ", GL_ACCOUNT               GLA "+
                    ", ACCTG_TRANS              AT "+
                    ", ACCTG_TRANS_PRESUPUESTAL ATP "+
                    ", CUSTOM_TIME_PERIOD       CTP_M "+
                    ", MINI_GUIA_CONTABLE       M "+
                    ", TIPO_DOCUMENTO           T "+
               "where "+
                  "GLA.gl_account_id        = AEN.gl_account_id "+
               "and   AEN.party_id             = '"+ur+"' "+ //organizacion
               "and   aen.party_id             = CTP_M.ORGANIZATION_PARTY_ID "+
               "and   AT.POSTED_DATE          >= CTP_M.FROM_DATE "+
               "and   AT.POSTED_DATE           < CTP_M.THRU_DATE "+
               "and   CTP_M.PERIOD_TYPE_ID     = 'FISCAL_MONTH' "+
               "and   AEN.ACCTG_TRANS_ID       = AT.ACCTG_TRANS_ID "+
               "and   ATP.ACCTG_TRANS_ID       = AT.ACCTG_TRANS_ID "+
               "and   CTP_M.CUSTOM_TIME_PERIOD_ID  = '"+periodo+"' "+ //periodo
               "and   M.CUENTA_"+tipoCuenta+"           = AEN.GL_ACCOUNT_ID "+ //Tipo Cuenta
               "and   M.ACCTG_TRANS_TYPE_ID = T.ACCTG_TRANS_TYPE_ID "+
               "and   T.ID_TIPO_DOC           = '"+tipoDocumento+"' "+ //tipo documento
               "and   ATP.CLAVE_PRES = '"+clave+"'"); //Clave presupuestal

            while ( rs.next() ) {
                //Se invierten los montos cuya naturaleza sea diferente del tipo
                BigDecimal montoTrans = rs.getBigDecimal("MONTO");
                String tipo = rs.getString("TIPO");
                String naturaleza = rs.getString("NATURALEZA");
                if(naturaleza.equals("D") && tipo.equals("C"))
                {
                	montoTrans = montoTrans.multiply(new BigDecimal(-1));
                }
                if(naturaleza.equals("A") && tipo.equals("D"))
                {
                	montoTrans = montoTrans.multiply(new BigDecimal(-1));
                }
                montos.add(montoTrans);
            }
            
            //Se obtiene el saldo de la cuenta
            for(BigDecimal b : montos)
            {
            	saldo = saldo.add(b);
            }
            
            //Se verifica que el saldo sea mayor o igual al monto de la clave presupuestaria
            if(saldo.compareTo(monto) == 0 || saldo.compareTo(monto) == 1)
            {
            	output.put("existeSuficiencia", "Y");
            }
            else
            {
            	output.put("existeSuficiencia", "N");
            }
            
            //Se envian los datos restantes en el mensaje de salida del servicio
            output.put("usuario",usuario);
            output.put("fecha",fechaSolicitud);
            output.put("linea",clave);
            output.put("saldo", saldo.toString());
            conn.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
		return output;
	}
}
