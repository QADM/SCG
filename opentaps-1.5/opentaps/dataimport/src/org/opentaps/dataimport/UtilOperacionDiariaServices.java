package org.opentaps.dataimport;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.opentaps.common.util.UtilMessage;

public class UtilOperacionDiariaServices {
	
	private static final String MODULE = UtilOperacionDiariaServices.class.getName();
	private static final BigDecimal ZERO = BigDecimal.ZERO;
   
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
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map getAuxiliarProd(DispatchContext dctx, Map context) throws GenericEntityException{
    	Delegator delegator = dctx.getDelegator();
    	String glAccountId = (String) context.get("glAccountId");
    	
    	EntityCondition condicionPrdCat = EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, glAccountId);
    	List<GenericValue> resultadoPrdCat = delegator.findByConditionCache("GlAccountCategoryRelation", condicionPrdCat , UtilMisc.toList("glAccountId","productCategoryId"), null);
    	
        Map results = ServiceUtil.returnSuccess();
        results.put("resultadoPrdCat", resultadoPrdCat);
        return results;
    }
    
    /**
     * Metodo que guarda los registros correspondientes en la tabla GL_ACCOUNT_HISTORY 
     * los realiza a partir de una lista de cuentas a guardar y una fecha contable
     * @param dctx
     * @param context
     * @return 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map guardaAccountHistory(DispatchContext dctx, Map context) {
    	Delegator delegator = dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Timestamp fechaContable = (Timestamp) context.get("fecContable");
    	String organizationPartyId = (String) context.get("organizationPartyId");
    	List<GenericValue> listAccounts = (List<GenericValue>) context.get("listCuentas");
    	List<GenericValue> listAccountsSaved = FastList.newInstance();
    	
    	try {
    	
	    	//Se obtienen los periodos en lo que se van a guardar la(s) cuenta(s)
	    	Map input = FastMap.newInstance();
	    	input.put("fecha", fechaContable);
	    	input.put("organizationPartyId", organizationPartyId);
	    	input = dctx.getModelService("obtenPeriodosFecha").makeValid(input, ModelService.IN_PARAM);
	    	Map tmpResult = dispatcher.runSync("obtenPeriodosFecha", input);
	    	List<GenericValue> listPeriods = (List<GenericValue>) tmpResult.get("listPeriods");
	    	
	    	for (GenericValue cuentas : listAccounts) {
	    		
	    		String glAccountId = cuentas.getString("glAccountId");
	    		BigDecimal monto = cuentas.getBigDecimal("amount");
	    		String tipoMonto = cuentas.getString("debitCreditFlag");
				
	    		for (GenericValue customPeriod : listPeriods) {
	    			
	    			String customTimePeriodId = customPeriod.getString("customTimePeriodId");
					
	    			GenericValue accountHistory = GenericValue.create(delegator.getModelEntity("GlAccountHistory"));
	    			accountHistory.set("glAccountId", glAccountId);
	    			accountHistory.set("organizationPartyId", organizationPartyId);
	    			accountHistory.set("customTimePeriodId", customTimePeriodId);
	    			if(tipoMonto.equalsIgnoreCase("D"))
	    				accountHistory.set("postedDebits", monto);
	    			else
	    				accountHistory.set("postedCredits", monto);
	    			
	    			accountHistory.create();
	    			
	    			listAccountsSaved.add(accountHistory);
	    			
				}
	    		
			}
    	
		} catch (GenericServiceException e) {
			return UtilMessage.createAndLogServiceError(e, MODULE);
		} catch (GenericEntityException e) {
			return UtilMessage.createAndLogServiceError(e, MODULE);
		}
    	
        Map results = ServiceUtil.returnSuccess();
        results.put("listAccountsSaved", listAccountsSaved);
        return results;
    }
    
    /**
     * Metodo que guarda los registros correspondientes en la tabla GL_ACCOUNT_ORGANIZATION 
     * los realiza a partir de una lista de cuentas , una organizacion y un monto 
     * @param dctx
     * @param context
     * @return 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map guardaAccountOrganization(DispatchContext dctx, Map context) {
    	Delegator delegator = dctx.getDelegator();
    	String organizationPartyId = (String) context.get("organizationPartyId");
    	Map<String,String> mapCuentas = (Map<String,String>) context.get("mapCuentas");
    	BigDecimal montoPrl = (BigDecimal) context.get("monto");
    	
    	List<String> listAccountId = FastList.newInstance();
    	Map<String,String> accountsNatu = FastMap.newInstance();
    	Map<String,BigDecimal> accountsOrga = FastMap.newInstance();
    	
    	List<GenericValue> listAccountsSaved = FastList.newInstance();
    	
    	for (String glAccountId : mapCuentas.values()) {
    		listAccountId.add(glAccountId);
    	}
    	
    	try {
    		
        	EntityCondition condicionAcc = EntityCondition.makeCondition("glAccountId", EntityOperator.IN, listAccountId);
        	List<GenericValue> resultadoAcc = delegator.findByConditionCache("GlAccount", condicionAcc , UtilMisc.toList("glAccountId","naturaleza"), null);
        	
        	for (GenericValue accounts : resultadoAcc) {
        		accountsNatu.put(accounts.getString("glAccountId"), accounts.getString("naturaleza"));
			}
        	
        	EntityCondition condicionAccOr = EntityCondition.makeCondition(EntityOperator.AND,
        			EntityCondition.makeCondition("glAccountId", EntityOperator.IN, listAccountId),
        			EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId));
        	List<GenericValue> resultadoAccOr = delegator.findByConditionCache("GlAccount", condicionAccOr , UtilMisc.toList("glAccountId","postedBalance"), null);
        	
        	for (GenericValue accoutOrg : resultadoAccOr) {
        		accountsOrga.put(accoutOrg.getString("glAccountId"), (accoutOrg.getBigDecimal("postedBalance") == null ? ZERO : accoutOrg.getBigDecimal("postedBalance")));
			}
        	
        	for (Map.Entry<String, String> cuenta : mapCuentas.entrySet())
        	{
        		
        		String glAccountId = cuenta.getValue();
        		
        		GenericValue accountOrgani = GenericValue.create(delegator.getModelEntity("GlAccountOrganization"));
        		accountOrgani.set("glAccountId", glAccountId);
        		accountOrgani.set("organizationPartyId", organizationPartyId);
        		
        		BigDecimal monto = ZERO;
        		String natu = null;
        		if(cuenta.getKey().contains("Cuenta_Cargo"))
        			natu = "D";
        		else
        			natu = "A";
        		
        		BigDecimal montoAux = accountsOrga.get(glAccountId);
        		monto = natu.equals(accountsNatu.get(glAccountId)) ? montoAux.add(montoPrl) : montoAux.subtract(montoPrl);
        		accountOrgani.set("postedBalance", monto);
        		accountOrgani.create();
        		
        		listAccountsSaved.add(accountOrgani);
        		
        	}        	
    		
    	} catch (GenericEntityException e) {
			return UtilMessage.createAndLogServiceError(e, MODULE);
		} 
    	
        Map results = ServiceUtil.returnSuccess();
        results.put("listAccountsSaved", listAccountsSaved);
        return results;
    }

}
