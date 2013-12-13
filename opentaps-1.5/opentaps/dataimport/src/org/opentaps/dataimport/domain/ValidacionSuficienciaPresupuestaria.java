package org.opentaps.dataimport.domain;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.opentaps.domain.DomainService;
import org.opentaps.foundation.service.ServiceException;

public class ValidacionSuficienciaPresupuestaria extends DomainService {

	public Map<String, Object> validaSuficiencia(DispatchContext d,Map<String, Object> context) throws ServiceException, GenericServiceException, GenericEntityException
	{
		Map<String,Object> output = new HashMap<String, Object>();
		String organizacion= (String) context.get("organizacion");
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpGet post = new HttpGet("http://localhost:8081/SuficienciaPresupuestariaWS/Validacion/ValidaSuficienciaService?organizacion="+organizacion);

		try {
			HttpResponse resp = httpClient.execute(post);
			String respuesta = EntityUtils.toString(resp.getEntity());
			if(respuesta.equals("Y"))
			{
				output.put("messageOut", "S");
			}
			else
			{
				output.put("messageOut", "N");
			}
		}
		catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}
}
