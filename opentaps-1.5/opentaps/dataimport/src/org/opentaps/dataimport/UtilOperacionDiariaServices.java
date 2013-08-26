package org.opentaps.dataimport;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

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
    
    /**
     * Obtiene los periodos custom que coinciden en una fecha y una organizacion
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map obtenPeriodosFecha(DispatchContext dctx, Map context){
    	Delegator delegator = dctx.getDelegator();
    	String organizationPartyId = (String) context.get("organizationPartyId");
    	Timestamp fecha = (Timestamp) context.get("fecha");
    	
    	List<GenericValue> listPeriods = FastList.newInstance();
    	
        EntityCondition conditionsPeriods = EntityCondition.makeCondition(EntityOperator.AND,
                EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS,organizationPartyId),
                EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,fecha),
                EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO,fecha));

		try {
			
			listPeriods = delegator.findByCondition("CustomTimePeriod", conditionsPeriods, null, UtilMisc.toList("customTimePeriodId"));
			
		} catch (GenericEntityException e) {
			return UtilMessage.createAndLogServiceError(e, MODULE);
		}
    	
    	
        Map results = ServiceUtil.returnSuccess();
        results.put("listPeriods", listPeriods);
        return results;
    	
    }
    
    /**
     * Obtiene los productos asociados a una cuenta  
     * @param dctx
     * @param context
     * @return
     * @throws GenericEntityException 
     */
    public static Map getAuxiliarProd(DispatchContext dctx, Map context) throws GenericEntityException{
    	Delegator delegator = dctx.getDelegator();
    	String glAccountId = (String) context.get("glAccountId");
    	
    	EntityCondition condicionPrdCat = EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, glAccountId);
    	List<GenericValue> resultadoPrdCat = delegator.findByConditionCache("GlAccountCategoryRelation", condicionPrdCat , UtilMisc.toList("glAccountId","productCategoryId"), null);
    	
        Map results = ServiceUtil.returnSuccess();
        results.put("resultadoPrdCat", resultadoPrdCat);
        return results;
    }

}
