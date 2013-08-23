package org.opentaps.dataimport;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.opentaps.common.util.UtilMessage;

public class UtilOperacionDiariaServices {
	
	private static final String MODULE = UtilOperacionDiariaServices.class.getName();
	
   
    /**
     * Metodo utilizado para obtener el enumId padre a partir de uno dado
     * @param enumId
     * @return partyId(Padre)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map obtenEnumIdPadre(DispatchContext dctx, Map context) {
        Delegator delegator = dctx.getDelegator();
        String enumId = (String) context.get("enumId");
        
        String enumPadre = null;
      
		try {        
			
	    	EntityCondition condicionEnum = EntityCondition.makeCondition("enumId", EntityOperator.EQUALS, enumId);
	    	List<GenericValue> resultadoEnum = delegator.findByConditionCache("Enumeration", condicionEnum , UtilMisc.toList("enumId","parentEnumId"), null);
	    	
	    	if(resultadoEnum != null && !resultadoEnum.isEmpty()){
	    		
	    		enumPadre = resultadoEnum.get(0).getString("parentEnumId");
	    		
	    	}
			
		} catch (GenericEntityException e) {
			return UtilMessage.createAndLogServiceError(e, MODULE);
		}
        
        Map results = ServiceUtil.returnSuccess();
        results.put("enumIdPadre", enumPadre);
        return results;
    }	
    

    /**
     * Metodo utilizado para obtener el enumId padre a partir de uno dado
     * @param enumId
     * @return partyId(Padre)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map obtenPartyIdPadre(DispatchContext dctx, Map context) {
        Delegator delegator = dctx.getDelegator();
        String partyId = (String) context.get("partyId");
        
        String partyIdPadre = null;
      
		try {        
			
	    	EntityCondition condicionEnum = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
	    	List<GenericValue> resultadoEnum = delegator.findByConditionCache("PartyGroup", condicionEnum , UtilMisc.toList("partyId","Parent_id"), null);
	    	
	    	if(resultadoEnum != null && !resultadoEnum.isEmpty()){
	    		
	    		partyIdPadre = resultadoEnum.get(0).getString("Parent_id");
	    		
	    	}
			
		} catch (GenericEntityException e) {
			return UtilMessage.createAndLogServiceError(e, MODULE);
		}
        
        Map results = ServiceUtil.returnSuccess();
        results.put("partyIdPadre", partyIdPadre);
        return results;
    }	

}
