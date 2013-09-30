/*
 * Copyright (c) Open Source Strategies, Inc.
 *
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.opensourcestrategies.financials.reports;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import org.hibernate.ScrollableResults;
import org.hibernate.Transaction;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilTimer;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.ResourceBundleMapWrapper;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.model.ModelViewEntity.ComplexAlias;
import org.ofbiz.entity.model.ModelViewEntity.ComplexAliasField;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.opentaps.base.constants.EnumerationConstants;
import org.opentaps.base.constants.PaymentMethodTypeConstants;
import org.opentaps.base.constants.StatusItemConstants;
import org.opentaps.base.entities.SalesInvoiceItemFact;
import org.opentaps.base.entities.TaxInvoiceItemFact;
import org.opentaps.common.jndi.DataSourceImpl;
import org.opentaps.common.reporting.etl.UtilEtl;
import org.opentaps.common.util.UtilAccountingTags;
import org.opentaps.common.util.UtilCommon;
import org.opentaps.common.util.UtilDate;
import org.opentaps.common.util.UtilMessage;
import org.opentaps.domain.billing.invoice.Invoice;
import org.opentaps.foundation.entity.hibernate.Query;
import org.opentaps.foundation.entity.hibernate.Session;
import org.opentaps.foundation.infrastructure.Infrastructure;
import org.opentaps.foundation.infrastructure.InfrastructureException;
import org.opentaps.foundation.repository.RepositoryException;

import com.opensourcestrategies.financials.accounts.AccountsHelper;
import com.opensourcestrategies.financials.financials.FinancialServices;
import com.opensourcestrategies.financials.util.UtilFinancial;

/**
 * Events preparing data passed to Jasper reports.
 */
public final class FinancialReports {

    private static final String MODULE = FinancialReports.class.getName();
    /**
     * Variable Bigdecimal utiles para operaciones
     */
    private static final BigDecimal ZERO = BigDecimal.ZERO;
	private static final BigDecimal MIL = new BigDecimal(1000);
	private static final BigDecimal UNOMs = new BigDecimal(-1);

    private FinancialReports() { }

    /**
     * Prepare query parameters for standard financial report.
     * @param request a <code>HttpServletRequest</code> value
     * @return query parameter map
     * @throws GenericEntityException if error occur
     */
    public static Map<String, Object> prepareFinancialReportParameters(HttpServletRequest request) throws GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilCommon.getTimeZone(request);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String organizationPartyId = UtilCommon.getOrganizationPartyId(request);
        String glFiscalTypeId = UtilCommon.getParameter(request, "glFiscalTypeId");
        String dateOption = UtilCommon.getParameter(request, "reportDateOption");
        String fromDateText = UtilCommon.getParameter(request, "fromDate");
        String thruDateText = UtilCommon.getParameter(request, "thruDate");
        String reportFormType = UtilCommon.getParameter(request, "reportFormType");
        Timestamp fromDate = null;
        Timestamp thruDate = null;

        Timestamp defaultAsOfDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), timeZone, locale);
        Timestamp asOfDate = null;
        Map<String, Object> ctxt = FastMap.newInstance();
        ctxt.put("userLogin", userLogin);
        ctxt.put("organizationPartyId", organizationPartyId);
        ctxt.put("glFiscalTypeId", glFiscalTypeId);

        // determine the period
        if (dateOption == null) {
            // use the default asOfDate and run the report
            ctxt.put("asOfDate", defaultAsOfDate);
        } else if ("byDate".equals(dateOption)) {
            if ("state".equals(reportFormType)) {
                String asOfDateText = UtilCommon.getParameter(request, "asOfDate");
                asOfDate = UtilDateTime.getDayEnd(UtilDate.toTimestamp(asOfDateText, timeZone, locale), timeZone, locale);
                // use current date
                if (asOfDate == null) {
                    asOfDate = defaultAsOfDate;
                }
                ctxt.put("asOfDate", asOfDate);
            } else if ("flow".equals(reportFormType)) {
                fromDateText = UtilCommon.getParameter(request, "fromDate");
                thruDateText = UtilCommon.getParameter(request, "thruDate");
                if (UtilValidate.isNotEmpty(fromDateText)) {
                    fromDate = UtilDateTime.getDayStart(UtilDate.toTimestamp(fromDateText, timeZone, locale), timeZone, locale);
                }
                if (UtilValidate.isNotEmpty(thruDateText)) {
                    thruDate = UtilDateTime.getDayEnd(UtilDate.toTimestamp(thruDateText, timeZone, locale), timeZone, locale);
                }
                ctxt.put("fromDate", fromDate);
                ctxt.put("thruDate", thruDate);
            }
        } else if ("byTimePeriod".equals(dateOption)) {
            if ("state".equals(reportFormType) || "flow".equals(reportFormType)) {
                String customTimePeriodId = UtilCommon.getParameter(request, "customTimePeriodId");
                ctxt.put("customTimePeriodId", customTimePeriodId);
                GenericValue timePeriod = delegator.findByPrimaryKeyCache("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId));
                fromDate = UtilDateTime.getDayStart(UtilDateTime.getTimestamp(((Date) timePeriod.get("fromDate")).getTime()), timeZone, locale);
                thruDate = UtilDateTime.adjustTimestamp(UtilDateTime.getTimestamp(((Date) timePeriod.get("thruDate")).getTime()), Calendar.MILLISECOND, -1, timeZone, locale);
                ctxt.put("fromDate", fromDate);
                ctxt.put("thruDate", thruDate);
                asOfDate = thruDate;
                ctxt.put("asOfDate", asOfDate);
            }
        }

        return ctxt;
    }

    /**
     * Prepare query parameters for comparative flow report.
     * @param request a <code>HttpServletRequest</code> value
     * @return query parameter map
     * @throws GenericEntityException if error occur
     */
    public static Map<String, Object> prepareComparativeFlowReportParameters(HttpServletRequest request) throws GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String organizationPartyId = UtilCommon.getOrganizationPartyId(request);
        String glFiscalTypeId1 = UtilCommon.getParameter(request, "glFiscalTypeId1");
        String glFiscalTypeId2 = UtilCommon.getParameter(request, "glFiscalTypeId2");
        String fromDateText1 = null;
        String thruDateText1 = null;
        String fromDateText2 = null;
        String thruDateText2 = null;
        Timestamp fromDate1 = null;
        Timestamp thruDate1 = null;
        Timestamp fromDate2 = null;
        Timestamp thruDate2 = null;
        String dateOption = UtilCommon.getParameter(request, "reportDateOption");

        Map<String, Object> ctxt = FastMap.newInstance();
        ctxt.put("userLogin", userLogin);
        ctxt.put("organizationPartyId", organizationPartyId);
        ctxt.put("glFiscalTypeId1", glFiscalTypeId1);
        ctxt.put("glFiscalTypeId2", glFiscalTypeId2);
        // determine the period
        if (dateOption != null) {
            if (dateOption.equals("byDate")) {
                fromDateText1 = UtilCommon.getParameter(request, "fromDate1");
                thruDateText1 = UtilCommon.getParameter(request, "thruDate1");
                fromDateText2 = UtilCommon.getParameter(request, "fromDate2");
                thruDateText2 = UtilCommon.getParameter(request, "thruDate2");
                fromDate1 = UtilDateTime.getDayStart(UtilDate.toTimestamp(fromDateText1, timeZone, locale), timeZone, locale);
                thruDate1 = UtilDateTime.getDayEnd(UtilDate.toTimestamp(thruDateText1, timeZone, locale), timeZone, locale);
                fromDate2 = UtilDateTime.getDayStart(UtilDate.toTimestamp(fromDateText2, timeZone, locale), timeZone, locale);
                thruDate2 = UtilDateTime.getDayEnd(UtilDate.toTimestamp(thruDateText2, timeZone, locale), timeZone, locale);
                ctxt.put("fromDate1", fromDate1);
                ctxt.put("thruDate1", thruDate1);
                ctxt.put("fromDate2", fromDate2);
                ctxt.put("thruDate2", thruDate2);
            } else if (dateOption.equals("byTimePeriod")) {
                String fromCustomTimePeriodId = UtilCommon.getParameter(request, "fromCustomTimePeriodId");
                String thruCustomTimePeriodId = UtilCommon.getParameter(request, "thruCustomTimePeriodId");
                ctxt.put("fromCustomTimePeriodId", fromCustomTimePeriodId);
                ctxt.put("thruCustomTimePeriodId", thruCustomTimePeriodId);
                GenericValue fromTimePeriod = delegator.findByPrimaryKeyCache("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", fromCustomTimePeriodId));
                GenericValue thruTimePeriod = delegator.findByPrimaryKeyCache("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", thruCustomTimePeriodId));
                fromDate1 = UtilDateTime.getDayStart(UtilDateTime.getTimestamp(((Date) fromTimePeriod.get("fromDate")).getTime()), timeZone, locale);
                thruDate1 = UtilDateTime.adjustTimestamp(UtilDateTime.getTimestamp(((Date) fromTimePeriod.get("thruDate")).getTime()), Calendar.MILLISECOND, -1, timeZone, locale);
                fromDate2 = UtilDateTime.getDayStart(UtilDateTime.getTimestamp(((Date) thruTimePeriod.get("fromDate")).getTime()), timeZone, locale);
                thruDate2 = UtilDateTime.adjustTimestamp(UtilDateTime.getTimestamp(((Date) thruTimePeriod.get("thruDate")).getTime()), Calendar.MILLISECOND, -1, timeZone, locale);
                ctxt.put("fromDate1", fromDate1);
                ctxt.put("thruDate1", thruDate1);
                ctxt.put("fromDate2", fromDate2);
                ctxt.put("thruDate2", thruDate2);
            }
        }
        return ctxt;
    }

    /**
     * Prepare data source and parameters for comparative variacion de la hacienda publica / patrimonio report.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the event response <code>String</code> value
     */
    @SuppressWarnings("unchecked")
    public static String prepareComparativeVariacionPatrimonio(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        ResourceBundleMapWrapper uiLabelMap = UtilMessage.getUiLabels(locale);
        String reportType = UtilCommon.getParameter(request, "type");
        TimeZone timeZone = UtilHttp.getTimeZone(request);

        try {
        	
        	Map<String,Object> mapDatos = preparaDatosVariacion(request);
        	
        	if(mapDatos.get("asOfDateTimeLast") == null && mapDatos.get("customTimePeriodId") == null){
        		return UtilMessage.createAndLogEventError(request, "FinancialsError_asOfDateOrPeriod", locale, MODULE);
        	}
        	
            Map<String, Object> results = dispatcher.runSync("getComparativeVariacionPatrimonio", dispatcher.getDispatchContext().makeValidContext("getComparativeVariacionPatrimonio", ModelService.IN_PARAM, mapDatos));

            Map<String, Object> secondLastDateResults = (Map<String, Object>) results.get("secondLastDateResults");
            Map<String, Object> nextLastDateResults = (Map<String, Object>) results.get("nextLastDateResults");
            Map<String, Object> lastDateResults = (Map<String, Object>) results.get("lastDateResults");
            
            Debug.logWarning(" secondLastDateResults "+secondLastDateResults, MODULE);
            Debug.logWarning(" nextLastDateResults "+nextLastDateResults, MODULE);
            Debug.logWarning(" lastDateResults "+lastDateResults, MODULE);
            
            Timestamp fromDate = (Timestamp) mapDatos.get("fromDate");
            int anioIni = UtilDateTime.getYear(fromDate, timeZone, locale);
            anioIni = anioIni - 2;
            
            List<Map<String, Object>> listDatesAll = new ArrayList<Map<String, Object>>();
            listDatesAll.add(secondLastDateResults);
            listDatesAll.add(nextLastDateResults);
            listDatesAll.add(lastDateResults);
            
          //Lista principal de datos a enviar
            List<Map<String, Object>> rows = generaReporteVariacion(listDatesAll,uiLabelMap,anioIni);
            
            request.setAttribute("jrDataSource", new JRMapCollectionDataSource(rows));

            // prepare report parameters
            Map<String, Object> jrParameters = FastMap.newInstance();
            jrParameters.put("fromDate", mapDatos.get("fromDate"));
            jrParameters.put("thruDate", mapDatos.get("thruDate"));
            jrParameters.put("fiscalType", mapDatos.get("glFiscalTypeId"));
            jrParameters.put("organizationPartyId", mapDatos.get("organizationPartyId"));
            jrParameters.put("organizationName", PartyHelper.getPartyName(delegator, (String) mapDatos.get("organizationPartyId"), false));
            jrParameters.put("accountingTags", UtilAccountingTags.formatTagsAsString(request, UtilAccountingTags.FINANCIALS_REPORTS_TAG, delegator));
            
            Debug.logWarning("accountingTags  [  "+UtilAccountingTags.formatTagsAsString(request, UtilAccountingTags.FINANCIALS_REPORTS_TAG, delegator)+" ]", MODULE);
            
            
            request.setAttribute("jrParameters", jrParameters);

        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (RepositoryException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }

        return reportType;
    } 
    
    /**
     * Genera el reporte y regresa una lista con mapas con los datos correspondientes
     * @param listDatesAll
     * @param uiLabelMap
     * @param anioIni
     * @return
     */
    private static List<Map<String, Object>> generaReporteVariacion(
			List<Map<String, Object>> listDatesAll, ResourceBundleMapWrapper uiLabelMap, int anioIni) {
    	
    	List<Map<String, Object>> rows = FastList.newInstance();

        List<Map<String, Object>> rowsInicialEjercicio = FastList.newInstance();

        BigDecimal montoTraspaso = ZERO;
        
      	//Se arma completamente el reporte
        int i = 0;
      	for (Map<String, Object> mapResults : listDatesAll) {
      		
      		Debug.logWarning("listDates  + "+i+" "+mapResults, MODULE);
      		
        	Map<GenericValue,BigDecimal> cuentasPatContribuido = (Map<GenericValue, BigDecimal>) mapResults.get("cuentasPatContribuido");
        	List<GenericValue> cuentasPatContribuidoList = EntityUtil.orderBy(((Map<GenericValue, BigDecimal>) mapResults.get("cuentasPatContribuido")).keySet(), UtilMisc.toList("glAccountId"));
        	Map<GenericValue,BigDecimal> cuentasPatGenAnterior = (Map<GenericValue, BigDecimal>) mapResults.get("cuentasPatGenAnterior");
        	List<GenericValue> cuentasPatGenAnteriorList = EntityUtil.orderBy(((Map<GenericValue, BigDecimal>) mapResults.get("cuentasPatGenAnterior")).keySet(), UtilMisc.toList("glAccountId"));        	
        	Map<GenericValue,BigDecimal> cuentasPatGenerado = (Map<GenericValue, BigDecimal>) mapResults.get("cuentasPatGenerado");
        	List<GenericValue> cuentasPatGeneradoList = EntityUtil.orderBy(((Map<GenericValue, BigDecimal>) mapResults.get("cuentasPatGenerado")).keySet(), UtilMisc.toList("glAccountId"));        	
        	Map<GenericValue,BigDecimal> cuentasPatrimonio = (Map<GenericValue, BigDecimal>) mapResults.get("cuentasPatrimonio");
        	List<GenericValue> cuentasPatrimonioList = EntityUtil.orderBy(((Map<GenericValue, BigDecimal>) mapResults.get("cuentasPatrimonio")).keySet(), UtilMisc.toList("glAccountId"));
        	
        	if(i>0){
            	//Agregamos las filas de ejecicios anteriores
            	armaVariacionPatri(rows, cuentasPatGenAnteriorList, null, cuentasPatGenAnterior, null, null, Integer.valueOf(i),uiLabelMap,montoTraspaso);
            	//Se agrega el patrimonio inicial del ejercicio
            	armaVariacionInicial(rows,rowsInicialEjercicio.get(i-1),cuentasPatGenAnteriorList);
            	//Agregamos las filas de patrimonio contribuido
            	armaVariacionPatri(rows, cuentasPatContribuidoList, cuentasPatContribuido, null, null, null, Integer.valueOf(i),uiLabelMap,montoTraspaso);
            	//Agregamos las filas de patrimonio generado en el ejercicio
            	armaVariacionPatri(rows, cuentasPatGeneradoList, null, null, cuentasPatGenerado, null, Integer.valueOf(i),uiLabelMap,montoTraspaso);
            	//Agregamos las filas de patrimonio generado en el ejercicio
            	armaVariacionPatri(rows, cuentasPatrimonioList, null, null, null, cuentasPatrimonio, Integer.valueOf(i),uiLabelMap,montoTraspaso);
        	}
        	
            //Patrimonio al final del ejercicicio
        	montoTraspaso = armaVariacionFinal(rows,rowsInicialEjercicio, mapResults,uiLabelMap,anioIni,i);
        	
      		i++;
      		anioIni++;
      	}
      	
        // sort records by account code
        Collections.sort(rows, new Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Integer accountTypeSeqNum1 = (Integer) o1.get("accountTypeSeqNum");
                Integer accountTypeSeqNum2 = (Integer) o2.get("accountTypeSeqNum");
                int c = accountTypeSeqNum1.compareTo(accountTypeSeqNum2);
                return c;
            }
        });
        
        return rows;
		
	}

	/**
     * Arma el renglon de patrimonio inicial
     * @param rows
     * @param rowsInicialEjercicio
     * @param cuentasPatGenAnteriorList
     */
    private static void armaVariacionInicial(List<Map<String, Object>> rows,
			Map<String, Object> rowsInicialEjercicio, List<GenericValue> cuentasPatGenAnteriorList) {
    	
    	BigDecimal montoEjerAnt = ZERO;
    	String idCuenta = "";
    	
    	for (GenericValue genericValue : cuentasPatGenAnteriorList) {
    		idCuenta = genericValue.getString("glAccountId");
			if(idCuenta.equalsIgnoreCase("3.2.5")){
				montoEjerAnt = genericValue.getBigDecimal("postedBalance");
				montoEjerAnt = montoEjerAnt.divide(MIL);
				break;
			}
		}
    	
    	BigDecimal montoAnterior = (BigDecimal) rowsInicialEjercicio.get("anteriores");
    	BigDecimal montoAntTotal = montoEjerAnt.add(montoAnterior);
    	
    	rowsInicialEjercicio.put("anteriores", montoAntTotal);
    	//Se recalcula el total de patrimonio inicial ajustado del ejercicio
    	BigDecimal total = ZERO;
    	total = total.add((BigDecimal) rowsInicialEjercicio.get("contribuido"));
    	total = total.add(montoAntTotal);
    	total = total.add((BigDecimal) rowsInicialEjercicio.get("ejercicio"));
    	total = total.add((BigDecimal) rowsInicialEjercicio.get("ajustes"));
    	rowsInicialEjercicio.put("total", total);
    	rows.add(rowsInicialEjercicio);
	}

	/**
     * Arma el renglon con los datos finales del ejercicio (anioIni)
     * @param listaPrl
     * @param rowsInicialEjercicio 
     * @param listDatesAll
     * @param uiLabelMap
     * @param anioIni
     * @return
     */
    @SuppressWarnings("unchecked")
	public static BigDecimal armaVariacionFinal(List<Map<String, Object>> listaPrl,
			List<Map<String, Object>> rowsInicialEjercicio, Map<String, Object> mapResults,
			ResourceBundleMapWrapper uiLabelMap, int anioIni,int i){            
    
	    BigDecimal montoTrans = ZERO;
	    	
	    Map<String, Object> row = FastMap.newInstance();
	    //Se separan los encabezados para meterlos, posteriormente para mostrarlos
      	List<GenericValue> cuentasPatContribuidoList = EntityUtil.orderBy(((Map<GenericValue, BigDecimal>) mapResults.get("cuentasPatContribuido")).keySet(), UtilMisc.toList("glAccountId"));
      	List<GenericValue> cuentasPatGenAnteriorList = EntityUtil.orderBy(((Map<GenericValue, BigDecimal>) mapResults.get("cuentasPatGenAnterior")).keySet(), UtilMisc.toList("glAccountId"));
      	List<GenericValue> cuentasPatGeneradoList = EntityUtil.orderBy(((Map<GenericValue, BigDecimal>) mapResults.get("cuentasPatGenerado")).keySet(), UtilMisc.toList("glAccountId"));
      	List<GenericValue> cuentasPatrimonioList = EntityUtil.orderBy(((Map<GenericValue, BigDecimal>) mapResults.get("cuentasPatrimonio")).keySet(), UtilMisc.toList("glAccountId"));
      	
      	Debug.logWarning("cuentasPatContribuidoList "+cuentasPatContribuidoList, MODULE);
      	Debug.logWarning("cuentasPatGenAnteriorList "+cuentasPatGenAnteriorList, MODULE);
      	Debug.logWarning("cuentasPatGeneradoList "+cuentasPatGeneradoList, MODULE);
      	Debug.logWarning("cuentasPatrimonioList "+cuentasPatrimonioList, MODULE);
      	
      //Primera etapa (encabezado)
      	BigDecimal monto = ZERO;
      	BigDecimal totalMonto = ZERO;
      	String acctId = "";
      	row.put("accountName", uiLabelMap.get("PatrimonioNetoAnterior")+" "+(anioIni));
      	for (GenericValue patContr : cuentasPatContribuidoList) {
      		acctId = patContr.getString("glAccountId");
				if(acctId.equalsIgnoreCase("3.1")){
					//Se agrega el monto del patrimonio contribuido
//					monto = cuentasPatContribuido.get(patContr);
					monto = patContr.getBigDecimal("postedBalance");
					monto = (monto == null ? ZERO : monto);
					Debug.logWarning("Monto 3.1 "+monto,MODULE);
					break;
				}
			}
      	row.put("contribuido",monto.divide(MIL));
      	totalMonto = totalMonto.add(monto.divide(MIL));
      	monto = ZERO;
      	for (GenericValue patGenAnt : cuentasPatGenAnteriorList) {
      		acctId = patGenAnt.getString("glAccountId");
				if(acctId.equalsIgnoreCase("3.2.2") || acctId.equalsIgnoreCase("3.2.5")){
//					BigDecimal auxMonto = cuentasPatGenAnterior.get(patGenAnt);
					BigDecimal auxMonto = patGenAnt.getBigDecimal("postedBalance");
					monto = monto.add(auxMonto == null ? ZERO : auxMonto);
					Debug.logWarning("Monto 3.2.2 || 3.2.5 "+monto,MODULE);
				}
			}
			//Se agrega el monto del patrimonio de ejercicios anteriores
      	row.put("anteriores",monto.divide(MIL));
      	totalMonto = totalMonto.add(monto.divide(MIL));
      	monto = ZERO;
      	for (GenericValue patGen : cuentasPatGeneradoList) {		
      		acctId = patGen.getString("glAccountId");
				if(acctId.equalsIgnoreCase("3.2.1") || acctId.equalsIgnoreCase("3.2.3")){
//					BigDecimal auxMonto = cuentasPatGenerado.get(patGen);
					BigDecimal auxMonto = patGen.getBigDecimal("postedBalance");
					monto = monto.add(auxMonto == null ? ZERO : auxMonto);
					Debug.logWarning("Monto 3.2.1 || 3.2.3 "+monto,MODULE);
				}
			}
			//Se agrega el monto del patrimonio de ejercicios
      	row.put("ejercicio",monto.divide(MIL));    
      	totalMonto = totalMonto.add(monto.divide(MIL));
      	monto = ZERO;
      	for (GenericValue patrimonio : cuentasPatrimonioList) {
      		acctId = patrimonio.getString("glAccountId");
				if(acctId.equalsIgnoreCase("3.3")){
//					BigDecimal auxMonto = cuentasPatrimonio.get(patrimonio);
          		BigDecimal auxMonto = patrimonio.getBigDecimal("postedBalance");
					monto = monto.add(auxMonto == null ? ZERO : auxMonto);
					Debug.logWarning("Monto patrimonio "+monto,MODULE);
				}
			}
			//Se agrega el monto del patrimonio de ejercicios
      	row.put("ajustes",monto.divide(MIL));    
      	totalMonto = totalMonto.add(monto.divide(MIL));            	
      	//Se agreaga la suma total del renglon
      	row.put("total",totalMonto);
      	row.put("accountTypeSeqNum", Integer.valueOf(i));
      	//Primera etapa (encabezado)            	
    	
    	listaPrl.add(row);
    	
    	Map<String, Object> rowCopy = new HashMap<String, Object>();
    	rowCopy.putAll(row);
    	rowCopy.put("accountName", uiLabelMap.get("PatrimonioNetoAjustado"));
    	rowCopy.put("accountTypeSeqNum", Integer.valueOf(i+1));
    	if(rowsInicialEjercicio != null && rowsInicialEjercicio.size()>0){
    		Map<String, Object> rowPrev = rowsInicialEjercicio.get(i-1);
    		BigDecimal contribuido = (BigDecimal) rowPrev.get("contribuido");
    		BigDecimal anteriores = (BigDecimal) rowPrev.get("anteriores");
    		BigDecimal ejercicio = (BigDecimal) rowPrev.get("ejercicio");
    		BigDecimal ajustes = (BigDecimal) rowPrev.get("ajustes");
    		rowCopy.put("contribuido", contribuido.add((BigDecimal) rowCopy.get("contribuido")));
    		rowCopy.put("anteriores", anteriores.add((BigDecimal) rowCopy.get("anteriores")));
    		rowCopy.put("ejercicio", ejercicio.add((BigDecimal) rowCopy.get("ejercicio")));
    		rowCopy.put("ajustes", ajustes.add((BigDecimal) rowCopy.get("ajustes")));
    	}
    	rowsInicialEjercicio.add(rowCopy);	
    	
    	montoTrans = (BigDecimal) row.get("ejercicio");
    	return montoTrans;
    }
    
    /**
     * Metodo que se utiliza para armar el reporte variaciones del patrimonio
     * @param listaPrl
     * @param listaItera
     * @param contribuido
     * @param ejercicio
     * @param anteriores
     * @param ajustes
     * @param secuencia
     */
    public static void armaVariacionPatri(List<Map<String, Object>> listaPrl, 
    					List<GenericValue> listaItera , Map<GenericValue, BigDecimal> contribuido ,
    					Map<GenericValue, BigDecimal> anteriores, Map<GenericValue, BigDecimal> ejercicio , 
    					Map<GenericValue, BigDecimal> ajustes ,Integer secuencia,
    					ResourceBundleMapWrapper uiLabelMap,BigDecimal montoTraspaso){
    	
   	 	String acctId = new String();
   	 	Map<String,BigDecimal> idMapContribuido = FastMap.newInstance();
   	 	if(contribuido != null)
   	 	{
	 		for (Map.Entry<GenericValue, BigDecimal> entry : contribuido.entrySet())
	 		{
		   	    acctId = entry.getKey().getString("glAccountId");
				idMapContribuido.put(acctId, entry.getValue());
	 		}
   	 	}
   	 	Map<String,BigDecimal> idMapEjercicio = FastMap.newInstance();
   	 	if(ejercicio != null)
   	 	{
	 		for (Map.Entry<GenericValue, BigDecimal> entry : ejercicio.entrySet())
	 		{
		   	    acctId = entry.getKey().getString("glAccountId");
		   	    idMapEjercicio.put(acctId, entry.getValue());
	 		}
   	 	}
   	 	Map<String,BigDecimal> idMapAnteriores = FastMap.newInstance();
   	 	if(anteriores != null)
   	 	{
   	 		for (Map.Entry<GenericValue, BigDecimal> entry : anteriores.entrySet())
   	 		{
   		   	    acctId = entry.getKey().getString("glAccountId");
	   		   	idMapAnteriores.put(acctId, entry.getValue());
   	 		}
   	 	}

   	 	Map<String,BigDecimal> idMapAjustes = FastMap.newInstance();
   	 	if(ajustes != null)
   	 	{
   	 		for (Map.Entry<GenericValue, BigDecimal> entry : ajustes.entrySet())
   	 		{
   		   	    acctId = entry.getKey().getString("glAccountId");
   		   	    idMapAjustes.put(acctId, entry.getValue());
   	 		} 		
   	 	}
   	 	
    	if(ejercicio != null){
    		
    		Map<String,Object> row = FastMap.newInstance();
	        	row.put("accountName", uiLabelMap.get("TraspasoResultados"));
	        	row.put("contribuido", ZERO );
	        	row.put("anteriores", montoTraspaso);
	        	row.put("ejercicio", montoTraspaso.multiply(UNOMs));
	        	row.put("ajustes", ZERO);
	        	row.put("total", ZERO);
	        	row.put("accountTypeSeqNum", secuencia);
	        	listaPrl.add(row);
    	}
   	 	
    	BigDecimal total = ZERO;
    	
    	for (GenericValue account : listaItera) {
    		
    		String idCuenta = account.getString("glAccountId");
    		
    		if(!idCuenta.equalsIgnoreCase("3") && !idCuenta.equalsIgnoreCase("3.1") && !idCuenta.equalsIgnoreCase("3.2")){
    			
	        	total = (contribuido == null || idMapContribuido.get(idCuenta) == null ? ZERO : idMapContribuido.get(idCuenta)).add(
	        			(ejercicio == null || idMapEjercicio.get(idCuenta) == null ? ZERO : idMapEjercicio.get(idCuenta))).add(
	        			(anteriores == null || idMapAnteriores.get(idCuenta) == null ? ZERO : idMapAnteriores.get(idCuenta))).add(
	        			(ajustes == null || idMapAjustes.get(idCuenta) == null ? ZERO : idMapAjustes.get(idCuenta)));
	        	
	    		Map<String,Object> row = FastMap.newInstance();
	        	row.put("accountName", account.getString("accountName"));
	        	row.put("contribuido", contribuido == null || idMapContribuido.get(idCuenta) == null ? ZERO : idMapContribuido.get(idCuenta).divide(MIL));
	        	row.put("anteriores", anteriores == null || idMapAnteriores.get(idCuenta) == null ? ZERO : idMapAnteriores.get(idCuenta).divide(MIL));
	        	row.put("ejercicio", ejercicio == null || idMapEjercicio.get(idCuenta) == null ? ZERO : idMapEjercicio.get(idCuenta).divide(MIL));
	        	row.put("ajustes", ajustes == null || idMapAjustes.get(idCuenta) == null ? ZERO : idMapAjustes.get(idCuenta).divide(MIL));
	        	row.put("total", total.divide(MIL));
	        	row.put("accountTypeSeqNum", secuencia);
	        	
	        	listaPrl.add(row);
	        	
	        	Debug.logWarning("ROW   "+row, MODULE);
	        	
	        	Debug.logWarning("idMapContribuido.get(idCuenta)   "+idMapContribuido.get(idCuenta), MODULE);
        	
    		}
        	
    	}
    	
    }
    
    /**
     * Prepare query parameters for comparative state report.
     * @param request a <code>HttpServletRequest</code> value
     * @return query parameter map
     * @throws GenericEntityException if error occur
     */
    public static Map<String, Object> prepareComparativeStateReportParameters(HttpServletRequest request) throws GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String organizationPartyId = UtilCommon.getOrganizationPartyId(request);
        String glFiscalTypeId1 = UtilCommon.getParameter(request, "glFiscalTypeId1");
        String glFiscalTypeId2 = UtilCommon.getParameter(request, "glFiscalTypeId2");
        String glFiscalTypeId = UtilCommon.getParameter(request, "glFiscalTypeId");//Para reporte de Analitico del activo
        String fromDateText = null;
        String thruDateText = null;
        Timestamp fromDate = null;
        Timestamp thruDate = null;
        String dateOption = UtilCommon.getParameter(request, "reportDateOption");

        Map<String, Object> ctxt = FastMap.newInstance();
        ctxt.put("userLogin", userLogin);
        ctxt.put("organizationPartyId", organizationPartyId);
        ctxt.put("glFiscalTypeId1", glFiscalTypeId1);
        ctxt.put("glFiscalTypeId2", glFiscalTypeId2);
        ctxt.put("glFiscalTypeId", glFiscalTypeId);//Para reporte de Analitico del activo

        // determine the period
        if (dateOption != null) {
            // do nothing
            if (dateOption.equals("byDate")) {
                fromDateText = UtilCommon.getParameter(request, "fromDate");
                if(fromDateText == null)//Para reporte de Analitico del activo
                	fromDateText = request.getAttribute("fromDate").toString();//Para reporte de Analitico del activo                
                thruDateText = UtilCommon.getParameter(request, "thruDate");
                if(thruDateText == null)//Para reporte de Analitico del activo
                	thruDateText = request.getAttribute("thruDate").toString();//Para reporte de Analitico del activo                  
                fromDate = UtilDateTime.getDayEnd(UtilDate.toTimestamp(fromDateText, timeZone, locale), timeZone, locale);
                thruDate = UtilDateTime.getDayEnd(UtilDate.toTimestamp(thruDateText, timeZone, locale), timeZone, locale);
                ctxt.put("fromDate", fromDate);
                ctxt.put("thruDate", thruDate);
            } else if (dateOption.equals("byTimePeriod")) {
                String fromCustomTimePeriodId = UtilCommon.getParameter(request, "fromCustomTimePeriodId");
                if(fromCustomTimePeriodId == null)//Para reporte de Analitico del activo
                	fromCustomTimePeriodId = request.getAttribute("fromCustomTimePeriodId").toString();//Para reporte de Analitico del activo
                String thruCustomTimePeriodId = UtilCommon.getParameter(request, "thruCustomTimePeriodId");
                if(thruCustomTimePeriodId == null)//Para reporte de Analitico del activo
                	thruCustomTimePeriodId = request.getAttribute("thruCustomTimePeriodId").toString();//Para reporte de Analitico del activo
                ctxt.put("fromCustomTimePeriodId", fromCustomTimePeriodId);
                ctxt.put("thruCustomTimePeriodId", thruCustomTimePeriodId);
                GenericValue fromTimePeriod = delegator.findByPrimaryKeyCache("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", fromCustomTimePeriodId));
                GenericValue thruTimePeriod = delegator.findByPrimaryKeyCache("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", thruCustomTimePeriodId));
                
                Debug.logWarning("fromTimePeriod  === "+fromTimePeriod,MODULE);
                Debug.logWarning("thruTimePeriod  === "+thruTimePeriod,MODULE);
                
                //  the time periods end at the beginning of the thruDate,  end we want to adjust it so that the report ends at the end of day before the thruDate
                if(fromTimePeriod != null)
                	fromDate = UtilDateTime.adjustTimestamp(UtilDateTime.getTimestamp(((Date) fromTimePeriod.get("thruDate")).getTime()), Calendar.MILLISECOND, -1, timeZone, locale);
                if(thruTimePeriod != null)
                	thruDate = UtilDateTime.adjustTimestamp(UtilDateTime.getTimestamp(((Date) thruTimePeriod.get("thruDate")).getTime()), Calendar.MILLISECOND, -1, timeZone, locale);
                Debug.logWarning("fromDate  === "+fromDate,MODULE);
                Debug.logWarning("thruDate  === "+thruDate,MODULE);
                ctxt.put("fromDate", fromDate);
                ctxt.put("thruDate", thruDate);
            }
        }
        return ctxt;
    }

    /**
     * Prepare data source and parameters for income statement report.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the event response <code>String</code> value
     */
    @SuppressWarnings("unchecked")
    public static String prepareIncomeStatementReport(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        ResourceBundleMapWrapper uiLabelMap = UtilMessage.getUiLabels(locale);
        Map<String, String> groupMarkers = UtilMisc.<String, String>toMap(
                "REVENUE", uiLabelMap.get("FinancialsGrossProfit"), "COGS", uiLabelMap.get("FinancialsGrossProfit"),
               "OPERATING_EXPENSE", uiLabelMap.get("FinancialsOperatingIncome"),
                "OTHER_EXPENSE", uiLabelMap.get("FinancialsPretaxIncome"), "OTHER_INCOME", uiLabelMap.get("FinancialsPretaxIncome"),
                "TAX_EXPENSE", uiLabelMap.get("AccountingNetIncome")
        );
        String reportType = UtilCommon.getParameter(request, "type");
        try {
            Map ctxt = prepareFinancialReportParameters(request);
            UtilAccountingTags.addTagParameters(request, ctxt);
            Map<String, ?> results = dispatcher.runSync("getIncomeStatementByDates", dispatcher.getDispatchContext().makeValidContext("getIncomeStatementByDates", ModelService.IN_PARAM, ctxt));
            Map<String, Object> glAccountSums = (Map<String, Object>) results.get("glAccountSums");
            BigDecimal grossProfit = (BigDecimal) results.get("grossProfit");
            BigDecimal operatingIncome = (BigDecimal) results.get("operatingIncome");
            BigDecimal pretaxIncome = (BigDecimal) results.get("pretaxIncome");
            BigDecimal netIncome = (BigDecimal) results.get("netIncome");
            Map<String, BigDecimal> glAccountGroupSums = (Map) results.get("glAccountGroupSums");

            List<Map<String, Object>> rows = FastList.newInstance();

            Integer i = 1;
            for (String glAccountTypeId : FinancialServices.INCOME_STATEMENT_TYPES) {
                List<Map<String, Object>> accounts = (List<Map<String, Object>>) glAccountSums.get(glAccountTypeId);

                // find account type
                GenericValue glAccountType = delegator.findByPrimaryKey("GlAccountType", UtilMisc.toMap("glAccountTypeId", glAccountTypeId));

                // create records for report
                if (UtilValidate.isNotEmpty(accounts)) {
                    for (Map<String, Object> account : accounts) {
                        Map<String, Object> row = FastMap.newInstance();
                        row.put("typeSeqNum", i);
                        row.put("accountCode", account.get("accountCode"));
                        row.put("accountName", account.get("accountName"));
                        row.put("glAccountTypeDescription", glAccountType.getString("description"));
                        row.put("glAccountTypeAmount", glAccountGroupSums.get(glAccountTypeId));
                        row.put("accountSum", account.get("accountSum"));
                        row.put("cumulativeIncomeName", groupMarkers.get(glAccountTypeId));
                        if ("REVENUE".equals(glAccountTypeId) || "COGS".equals(glAccountTypeId)) {
                            row.put("cumulativeIncomeAmount", grossProfit);
                        } else if ("OPERATING_EXPENSE".equals(glAccountTypeId)) {
                            row.put("cumulativeIncomeAmount", operatingIncome);
                        } else if ("OTHER_EXPENSE".equals(glAccountTypeId) || "OTHER_INCOME".equals(glAccountTypeId)) {
                            row.put("cumulativeIncomeAmount", pretaxIncome);
                        } else if ("TAX_EXPENSE".equals(glAccountTypeId)) {
                            row.put("cumulativeIncomeAmount", netIncome);
                        } else {
                            row.put("cumulativeIncomeAmount", null);
                        }
                        rows.add(row);
                    }
                } else {
                    // put line w/ empty detail fields to display grouping and totals in any case
                    Map<String, Object> row = FastMap.newInstance();
                    row.put("typeSeqNum", i);
                    row.put("glAccountTypeDescription", glAccountType.getString("description"));
                    row.put("glAccountTypeAmount", glAccountGroupSums.get(glAccountTypeId));
                    row.put("cumulativeIncomeName", groupMarkers.get(glAccountTypeId));
                    if ("REVENUE".equals(glAccountTypeId) || "COGS".equals(glAccountTypeId)) {
                        row.put("cumulativeIncomeAmount", grossProfit);
                    } else if ("OPERATING_EXPENSE".equals(glAccountTypeId)) {
                        row.put("cumulativeIncomeAmount", operatingIncome);
                    } else if ("OTHER_EXPENSE".equals(glAccountTypeId) || "OTHER_INCOME".equals(glAccountTypeId)) {
                        row.put("cumulativeIncomeAmount", pretaxIncome);
                    } else if ("TAX_EXPENSE".equals(glAccountTypeId)) {
                        row.put("cumulativeIncomeAmount", netIncome);
                    } else {
                        row.put("cumulativeIncomeAmount", null);
                    }
                    rows.add(row);
                }
                i++;
            }

            // sort records by account code
            Collections.sort(rows, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    Integer seqNum1 = (Integer) o1.get("typeSeqNum");
                    Integer seqNum2 = (Integer) o2.get("typeSeqNum");
                    int isSeqNumEq = seqNum1.compareTo(seqNum2);
                    if (isSeqNumEq != 0) {
                        return isSeqNumEq;
                    }
                    String accountCode1 = (String) o1.get("accountCode");
                    String accountCode2 = (String) o2.get("accountCode");
                    if (accountCode1 == null && accountCode2 == null) {
                        return 0;
                    }
                    if (accountCode1 == null && accountCode2 != null) {
                        return -1;
                    }
                    if (accountCode1 != null && accountCode2 == null) {
                        return 1;
                    }
                    return accountCode1.compareTo(accountCode2);
                }
            });
            request.setAttribute("jrDataSource", new JRMapCollectionDataSource(rows));

            // prepare report parameters
            Map<String, Object> jrParameters = FastMap.newInstance();
            jrParameters.put("glFiscalTypeId", ctxt.get("glFiscalTypeId"));
            jrParameters.put("fromDate", ctxt.get("fromDate"));
            jrParameters.put("thruDate", ctxt.get("thruDate"));
            jrParameters.put("organizationPartyId", ctxt.get("organizationPartyId"));
            jrParameters.put("organizationName", PartyHelper.getPartyName(delegator, (String) ctxt.get("organizationPartyId"), false));
            jrParameters.put("accountingTags", UtilAccountingTags.formatTagsAsString(request, UtilAccountingTags.FINANCIALS_REPORTS_TAG, delegator));
            request.setAttribute("jrParameters", jrParameters);

        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (RepositoryException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }

        return reportType;
    }

    /**
     * Prepare data source and parameters for Estado de Actividades report.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the event response <code>String</code> value
     */
    @SuppressWarnings("unchecked")
    public static String prepareComparativeEstadoActividades(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        ResourceBundleMapWrapper uiLabelMap = UtilMessage.getUiLabels(locale);

        String reportType = UtilCommon.getParameter(request, "type");

        try {

            Map ctxt = prepareComparativeFlowReportParameters(request);
            // validate parameters
            if (ctxt.get("fromDate1") == null || ctxt.get("thruDate1") == null || ctxt.get("fromDate2") == null || ctxt.get("thruDate2") == null) {
                return UtilMessage.createAndLogEventError(request, "FinancialsError_DateRangeMissing", locale, MODULE);
            }

            UtilAccountingTags.addTagParameters(request, ctxt);
            Map<String, Object> results = dispatcher.runSync("getComparativeIncomeStatement", dispatcher.getDispatchContext().makeValidContext("getComparativeIncomeStatement", ModelService.IN_PARAM, ctxt));

            Map<String, Object> set1IncomeStatement = (Map<String, Object>) results.get("set1IncomeStatement");
            Map<String, Object> set2IncomeStatement = (Map<String, Object>) results.get("set2IncomeStatement");
            BigDecimal netIncome1 = (BigDecimal) set1IncomeStatement.get("netIncome");
            Debug.logWarning("netIncome1.- "+netIncome1, MODULE);
            BigDecimal netIncome2 = (BigDecimal) set2IncomeStatement.get("netIncome");
            Debug.logWarning("netIncome2.- "+netIncome2, MODULE);
            Map<GenericValue, BigDecimal> accountBalances = (Map<GenericValue, BigDecimal>) results.get("accountBalances");
            Map<GenericValue, BigDecimal> set1Accounts = (Map<GenericValue, BigDecimal>) set1IncomeStatement.get("glAccountSumsFlat");
            Map<GenericValue, BigDecimal> set2Accounts = (Map<GenericValue, BigDecimal>) set2IncomeStatement.get("glAccountSumsFlat");
            GenericValue glFiscalType1 = delegator.findByPrimaryKey("GlFiscalType", UtilMisc.toMap("glFiscalTypeId", ctxt.get("glFiscalTypeId1")));
            GenericValue glFiscalType2 = delegator.findByPrimaryKey("GlFiscalType", UtilMisc.toMap("glFiscalTypeId", ctxt.get("glFiscalTypeId2")));

            List<Map<String, Object>> rows = FastList.newInstance();
            
            BigDecimal mil = new BigDecimal(1000);
            BigDecimal totFec1 = ZERO;
            BigDecimal totFec2 = ZERO;
            BigDecimal totFecTot = ZERO;

            // create records for report
            Set<GenericValue> accounts = accountBalances.keySet();
            for (GenericValue account : accounts) {
                Map<String, Object> row = FastMap.newInstance();
                BigDecimal montoLeft = (set1Accounts.get(account) == null ? ZERO : set1Accounts.get(account));
                BigDecimal montoRight = (set2Accounts.get(account) == null ? ZERO : set2Accounts.get(account));
                BigDecimal montoDiff = (accountBalances.get(account) == null ? ZERO : accountBalances.get(account));
                row.put("accountClass", account.get("glAccountClassId"));
                row.put("accountCode", account.get("accountCode"));
                row.put("accountName", account.get("accountName"));
                row.put("accountSumLeft", montoLeft.divide(mil));
                row.put("cumulativeIncomeName", obtenNombreCta(account.getString("glAccountClassId"),locale));
                row.put("accountSumRight", montoRight.divide(mil));
                row.put("accountSumDiff", montoDiff.divide(mil));
                rows.add(row);
                
                totFec1 = totFec1.add(montoLeft.divide(mil));
                totFec2 = totFec2.add(montoRight.divide(mil));
                totFecTot = totFecTot.add(montoDiff.divide(mil));
            }

            // sort records by account code
            Collections.sort(rows, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    String accountCode1 = (String) o1.get("accountCode");
                    String accountCode2 = (String) o2.get("accountCode");
                    if (accountCode1 == null && accountCode2 == null) {
                        return 0;
                    }
                    if (accountCode1 == null && accountCode2 != null) {
                        return -1;
                    }
                    if (accountCode1 != null && accountCode2 == null) {
                        return 1;
                    }
                    return accountCode1.compareTo(accountCode2);
                }
            });
            request.setAttribute("jrDataSource", new JRMapCollectionDataSource(rows));

            // prepare report parameters
            Map<String, Object> jrParameters = FastMap.newInstance();
            jrParameters.put(JRParameter.REPORT_LOCALE, locale);
            jrParameters.put("fromDateLeft", ctxt.get("fromDate1"));
            jrParameters.put("thruDateLeft", ctxt.get("thruDate1"));
            jrParameters.put("fromDateRight", ctxt.get("fromDate2"));
            jrParameters.put("thruDateRight", ctxt.get("thruDate2"));
            jrParameters.put("netIncomeLeft", netIncome1);
            jrParameters.put("netIncomeRight", netIncome2);
            jrParameters.put("fiscalTypeLeft", glFiscalType1.get("description", locale));
            jrParameters.put("fiscalTypeRight", glFiscalType2.get("description", locale));
            jrParameters.put("organizationPartyId", ctxt.get("organizationPartyId"));
            jrParameters.put("organizationName", PartyHelper.getPartyName(delegator, (String) ctxt.get("organizationPartyId"), false));
            jrParameters.put("accountingTags", UtilAccountingTags.formatTagsAsString(request, UtilAccountingTags.FINANCIALS_REPORTS_TAG, delegator));
            
            //Se agregan algunos totales 
            jrParameters.put("totFec1", totFec1);
            jrParameters.put("totFec2", totFec2);
            jrParameters.put("totFecTot", totFecTot);
            
            request.setAttribute("jrParameters", jrParameters);

        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (RepositoryException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }

        return reportType;
    
    	
    }
    
    /**
     * Prepare data source and parameters for comparative income statement report.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the event response <code>String</code> value
     */
    @SuppressWarnings("unchecked")
    public static String prepareComparativeIncomeStatementReport(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        ResourceBundleMapWrapper uiLabelMap = UtilMessage.getUiLabels(locale);

        String reportType = UtilCommon.getParameter(request, "type");

        try {

            Map ctxt = prepareComparativeFlowReportParameters(request);
            // validate parameters
            if (ctxt.get("fromDate1") == null || ctxt.get("thruDate1") == null || ctxt.get("fromDate2") == null || ctxt.get("thruDate2") == null) {
                return UtilMessage.createAndLogEventError(request, "FinancialsError_DateRangeMissing", locale, MODULE);
            }

            UtilAccountingTags.addTagParameters(request, ctxt);
            Map<String, Object> results = dispatcher.runSync("getComparativeIncomeStatement", dispatcher.getDispatchContext().makeValidContext("getComparativeIncomeStatement", ModelService.IN_PARAM, ctxt));

            Map<String, Object> set1IncomeStatement = (Map<String, Object>) results.get("set1IncomeStatement");
            Map<String, Object> set2IncomeStatement = (Map<String, Object>) results.get("set2IncomeStatement");
            BigDecimal netIncome1 = (BigDecimal) set1IncomeStatement.get("netIncome");
            BigDecimal netIncome2 = (BigDecimal) set2IncomeStatement.get("netIncome");
            Map<GenericValue, BigDecimal> accountBalances = (Map<GenericValue, BigDecimal>) results.get("accountBalances");
            Map<GenericValue, BigDecimal> set1Accounts = (Map<GenericValue, BigDecimal>) set1IncomeStatement.get("glAccountSumsFlat");
            Map<GenericValue, BigDecimal> set2Accounts = (Map<GenericValue, BigDecimal>) set2IncomeStatement.get("glAccountSumsFlat");
            GenericValue glFiscalType1 = delegator.findByPrimaryKey("GlFiscalType", UtilMisc.toMap("glFiscalTypeId", ctxt.get("glFiscalTypeId1")));
            GenericValue glFiscalType2 = delegator.findByPrimaryKey("GlFiscalType", UtilMisc.toMap("glFiscalTypeId", ctxt.get("glFiscalTypeId2")));

            List<Map<String, Object>> rows = FastList.newInstance();

            // create records for report
            Set<GenericValue> accounts = accountBalances.keySet();
            for (GenericValue account : accounts) {
                Map<String, Object> row = FastMap.newInstance();
                row.put("accountCode", account.get("accountCode"));
                row.put("accountName", account.get("accountName"));
                row.put("accountSumLeft", set1Accounts.get(account));
                row.put("cumulativeIncomeName", uiLabelMap.get("AccountingNetIncome"));
                row.put("accountSumRight", set2Accounts.get(account));
                row.put("accountSumDiff", accountBalances.get(account));
                rows.add(row);
            }

            // sort records by account code
            Collections.sort(rows, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    String accountCode1 = (String) o1.get("accountCode");
                    String accountCode2 = (String) o2.get("accountCode");
                    if (accountCode1 == null && accountCode2 == null) {
                        return 0;
                    }
                    if (accountCode1 == null && accountCode2 != null) {
                        return -1;
                    }
                    if (accountCode1 != null && accountCode2 == null) {
                        return 1;
                    }
                    return accountCode1.compareTo(accountCode2);
                }
            });
            request.setAttribute("jrDataSource", new JRMapCollectionDataSource(rows));

            // prepare report parameters
            Map<String, Object> jrParameters = FastMap.newInstance();
            jrParameters.put("fromDateLeft", ctxt.get("fromDate1"));
            jrParameters.put("thruDateLeft", ctxt.get("thruDate1"));
            jrParameters.put("fromDateRight", ctxt.get("fromDate2"));
            jrParameters.put("thruDateRight", ctxt.get("thruDate2"));
            jrParameters.put("netIncomeLeft", netIncome1);
            jrParameters.put("netIncomeRight", netIncome2);
            jrParameters.put("fiscalTypeLeft", glFiscalType1.get("description", locale));
            jrParameters.put("fiscalTypeRight", glFiscalType2.get("description", locale));
            jrParameters.put("organizationPartyId", ctxt.get("organizationPartyId"));
            jrParameters.put("organizationName", PartyHelper.getPartyName(delegator, (String) ctxt.get("organizationPartyId"), false));
            jrParameters.put("accountingTags", UtilAccountingTags.formatTagsAsString(request, UtilAccountingTags.FINANCIALS_REPORTS_TAG, delegator));
            request.setAttribute("jrParameters", jrParameters);

        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (RepositoryException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }

        return reportType;
    }
    
    /**
     * Obtiene el nombre de los grupos de cuentas de Ingresos y Egresos
     * @param nombreClase
     * @param locale
     * @return
     */
    private static String obtenNombreCta(String nombreClase,Locale locale){
    	
    	ResourceBundleMapWrapper uiLabelMap = UtilMessage.getUiLabels(locale);
    	String nombre = new String();
    	
    	//Ingresos
    	if(nombreClase.equals("REVENUE") || nombreClase.equals("INCOME")){
    		
    		nombre = uiLabelMap.get("AccountingRevenue").toString();
    		
    	} else{
    		
    		nombre = uiLabelMap.get("AccountingExpenses").toString();
    		
    	}
    	
    	return nombre;
    }

    /**
     * Prepare data source and parameters for trial balance report.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the event response <code>String</code> value
     */
    @SuppressWarnings("unchecked")
    public static String prepareTrialBalanceReport(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        ResourceBundleMapWrapper uiLabelMap = UtilMessage.getUiLabels(locale);

        String reportType = UtilCommon.getParameter(request, "type");

        try {

            // retrieve financial data
            Map ctxt = prepareFinancialReportParameters(request);
            UtilAccountingTags.addTagParameters(request, ctxt);
            Map<String, Object> results = dispatcher.runSync("getTrialBalanceForDate", dispatcher.getDispatchContext().makeValidContext("getTrialBalanceForDate", ModelService.IN_PARAM, ctxt));
            List<Map<String, Object>> rows = FastList.newInstance();

            // the account groups that will be included in the report, in the same order
            Map<String, String> accountLabels = new LinkedHashMap<String, String>();
            accountLabels.put("asset", "AccountingAssets");
            accountLabels.put("liability", "AccountingLiabilities");
            accountLabels.put("equity", "AccountingEquities");
            accountLabels.put("revenue", "FinancialsRevenue");
            accountLabels.put("expense", "FinancialsExpense");
            accountLabels.put("income", "FinancialsIncome");
            accountLabels.put("other", "CommonOther");

            for (String pre : accountLabels.keySet()) {
                Object label = uiLabelMap.get(accountLabels.get(pre));
                // for the given group, list the entries ordered by account code
                List<GenericValue> accounts = EntityUtil.orderBy(((Map) results.get(pre + "AccountBalances")).keySet(), UtilMisc.toList("accountCode"));
                if (UtilValidate.isNotEmpty(accounts)) {
                    Map<GenericValue, Double> accountBalances = (Map) results.get(pre + "AccountBalances");
                    Map<GenericValue, Double> accountCredits = (Map) results.get(pre + "AccountCredits");
                    Map<GenericValue, Double> accountDebits = (Map) results.get(pre + "AccountDebits");
                    for (GenericValue account : accounts) {
                        Map<String, Object> row = FastMap.newInstance();
                        row.put("accountCode", account.get("accountCode"));
                        row.put("accountName", account.get("accountName"));
                        row.put("accountBalance", accountBalances.get(account));
                        row.put("accountCredit", accountCredits.get(account));
                        row.put("accountDebit", accountDebits.get(account));
                        row.put("accountType", label);
                        row.put("accountCreditDebitFlag", UtilAccounting.isDebitAccount(account) ? "D" : "C");
                        rows.add(row);
                    }
                } else {
                    // for empty groups
                    rows.add(UtilMisc.toMap("accountType", label));
                }
            }

            request.setAttribute("jrDataSource", new JRMapCollectionDataSource(rows));

            // prepare report parameters
            Map<String, Object> jrParameters = FastMap.newInstance();
            jrParameters.put("asOfDate", ctxt.get("asOfDate"));
            jrParameters.put("glFiscalTypeId", ctxt.get("glFiscalTypeId"));
            jrParameters.put("organizationPartyId", ctxt.get("organizationPartyId"));
            jrParameters.put("organizationName", PartyHelper.getPartyName(delegator, (String) ctxt.get("organizationPartyId"), false));
            jrParameters.put("totalDebits", results.get("totalDebits"));
            jrParameters.put("totalCredits", results.get("totalCredits"));
            jrParameters.put("accountingTags", UtilAccountingTags.formatTagsAsString(request, UtilAccountingTags.FINANCIALS_REPORTS_TAG, delegator));
            request.setAttribute("jrParameters", jrParameters);

        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (RepositoryException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }

        return reportType;

    }

    
    /**
     * Prepare data source and parameters for estado de situacion financiera report.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the event response <code>String</code> value
     */
    @SuppressWarnings("unchecked")
    public static String prepareEdoSituacionFinancieraReport(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        ResourceBundleMapWrapper uiLabelMap = UtilMessage.getUiLabels(locale);
        String reportType = UtilCommon.getParameter(request, "type");

        try {
            // retrieve financial data
            Map ctxt = prepareComparativeStateReportParameters(request);
            if (ctxt.get("fromDate") == null || ctxt.get("thruDate") == null) {
                return UtilMessage.createAndLogEventError(request, "FinancialsError_FromOrThruDateMissing", locale, MODULE);
            }

            UtilAccountingTags.addTagParameters(request, ctxt);
            Map<String, Object> results = dispatcher.runSync("getComparativeSituacionFinanciera", dispatcher.getDispatchContext().makeValidContext("getComparativeSituacionFinanciera", ModelService.IN_PARAM, ctxt));

            List<GenericValue> assetAccounts = EntityUtil.orderBy(((Map<GenericValue, BigDecimal>) results.get("assetAccountBalances")).keySet(), UtilMisc.toList("glAccountId"));
            Map<GenericValue, BigDecimal> assetAccountBalances = (Map<GenericValue, BigDecimal>) results.get("assetAccountBalances");
            List<GenericValue> liabilityAccounts = EntityUtil.orderBy(((Map<GenericValue, BigDecimal>) results.get("liabilityAccountBalances")).keySet(), UtilMisc.toList("glAccountId"));
            Map<GenericValue, BigDecimal> liabilityAccountBalances = (Map<GenericValue, BigDecimal>) results.get("liabilityAccountBalances");
            List<GenericValue> equityAccounts = EntityUtil.orderBy(((Map<GenericValue, BigDecimal>) results.get("equityAccountBalances")).keySet(), UtilMisc.toList("glAccountId"));
            Map<GenericValue, BigDecimal> equityAccountBalances = (Map<GenericValue, BigDecimal>) results.get("equityAccountBalances");

            Map<String, Object> fromDateAccountBalances = (Map<String, Object>) results.get("fromDateAccountBalances");
            Map<GenericValue, BigDecimal> fromAssetAccountBalances = (Map<GenericValue, BigDecimal>) fromDateAccountBalances.get("assetAccountBalances");
            Map<GenericValue, BigDecimal> fromLiabilityAccountBalances = (Map<GenericValue, BigDecimal>) fromDateAccountBalances.get("liabilityAccountBalances");
            Map<GenericValue, BigDecimal> fromEquityAccountBalances = (Map<GenericValue, BigDecimal>) fromDateAccountBalances.get("equityAccountBalances");

            Map<String, Object> thruDateAccountBalances = (Map<String, Object>) results.get("thruDateAccountBalances");
            Map<GenericValue, BigDecimal> toAssetAccountBalances = (Map<GenericValue, BigDecimal>) thruDateAccountBalances.get("assetAccountBalances");
            Map<GenericValue, BigDecimal> toLiabilityAccountBalances = (Map<GenericValue, BigDecimal>) thruDateAccountBalances.get("liabilityAccountBalances");
            Map<GenericValue, BigDecimal> toEquityAccountBalances = (Map<GenericValue, BigDecimal>) thruDateAccountBalances.get("equityAccountBalances");

            GenericValue fromGlFiscalType = delegator.findByPrimaryKey("GlFiscalType", UtilMisc.toMap("glFiscalTypeId", ctxt.get("glFiscalTypeId1")));
            GenericValue toGlFiscalType = delegator.findByPrimaryKey("GlFiscalType", UtilMisc.toMap("glFiscalTypeId", ctxt.get("glFiscalTypeId2")));

            List<Map<String, Object>> rows = FastList.newInstance();
            
            //Se agregan estos campos para el calculo de los activos y los pasivos con patrimonio
            BigDecimal totalActivosTo = ZERO;
            BigDecimal totalActivosFrom = ZERO;
            BigDecimal totalActivosComp = ZERO;
            BigDecimal totalNoActivosTo = ZERO;
            BigDecimal totalNoActivosFrom = ZERO;
            BigDecimal totalNoActivosComp = ZERO;
            
            BigDecimal mil = new BigDecimal(1000);

            // create record set for report
            if (UtilValidate.isNotEmpty(assetAccounts)) {
                for (GenericValue account : assetAccounts) {
                	BigDecimal montoComp = (assetAccountBalances.get(account) == null ? ZERO : assetAccountBalances.get(account));
                	BigDecimal montoFrom = (fromAssetAccountBalances.get(account) == null ? ZERO : fromAssetAccountBalances.get(account));
                	BigDecimal montoTo = (toAssetAccountBalances.get(account) == null ? ZERO : toAssetAccountBalances.get(account));
                    Map<String, Object> row = FastMap.newInstance();
                    row.put("accountCode", account.get("accountCode"));
                    row.put("accountName", account.get("accountName"));
                    row.put("accountBalance", (montoComp == null ? ZERO : montoComp.divide(mil)));
                    row.put("accountBalanceLeft", (montoFrom == null ? ZERO : montoFrom.divide(mil)));
                    row.put("accountBalanceRight", (montoTo == null ? ZERO : montoTo.divide(mil)));
                    row.put("accountType", uiLabelMap.get("AccountingAssets"));
                    row.put("accountTypeSeqNum", Integer.valueOf(1));
                    rows.add(row);
                    
                    //Aqui se inician los calculos CUSTOM
                    totalActivosTo = totalActivosTo.add(montoTo.divide(mil));
                    totalActivosFrom = totalActivosFrom.add(montoFrom.divide(mil));
                    totalActivosComp = totalActivosComp.add(montoComp.divide(mil));
                }
            } else {
                rows.add(UtilMisc.toMap("accountType", uiLabelMap.get("AccountingAssets"), "accountTypeSeqNum", Integer.valueOf(1)));
            }

            if (UtilValidate.isNotEmpty(liabilityAccounts)) {
                for (GenericValue account : liabilityAccounts) {
                	BigDecimal montoComp = (liabilityAccountBalances.get(account) == null ? ZERO : liabilityAccountBalances.get(account));
                	BigDecimal montoFrom = (fromLiabilityAccountBalances.get(account) == null ? ZERO : fromLiabilityAccountBalances.get(account));
                	BigDecimal montoTo = (toLiabilityAccountBalances.get(account) == null ? ZERO : toLiabilityAccountBalances.get(account));
                    Map<String, Object> row = FastMap.newInstance();
                    row.put("accountCode", account.get("accountCode"));
                    row.put("accountName", account.get("accountName"));
                    row.put("accountBalance",(montoComp == null ? ZERO : montoComp.divide(mil)));
                    row.put("accountBalanceLeft", (montoFrom == null ? ZERO : montoFrom.divide(mil)));
                    row.put("accountBalanceRight", (montoTo == null ? ZERO : montoTo.divide(mil)));
                    row.put("accountType", uiLabelMap.get("AccountingLiabilities"));
                    row.put("accountTypeSeqNum", Integer.valueOf(2));
                    rows.add(row);
                    
                    //Aqui se inician los calculos CUSTOM
                    totalNoActivosTo = totalNoActivosTo.add(montoTo.divide(mil));
                    totalNoActivosFrom = totalNoActivosFrom.add(montoFrom.divide(mil));
                    totalNoActivosComp = totalNoActivosComp.add(montoComp.divide(mil));
                }
            } else {
                rows.add(UtilMisc.toMap("accountType", uiLabelMap.get("AccountingLiabilities"), "accountTypeSeqNum", Integer.valueOf(2)));
            }

            if (UtilValidate.isNotEmpty(equityAccounts)) {
                for (GenericValue account : equityAccounts) {
                	BigDecimal montoComp = (equityAccountBalances.get(account) == null ? ZERO : equityAccountBalances.get(account));
                	BigDecimal montoFrom = (fromEquityAccountBalances.get(account) == null ? ZERO : fromEquityAccountBalances.get(account));
                	BigDecimal montoTo = (toEquityAccountBalances.get(account) == null ? ZERO : toEquityAccountBalances.get(account));
                    Map<String, Object> row = FastMap.newInstance();
                    row.put("accountCode", account.get("accountCode"));
                    row.put("accountName", account.get("accountName"));
                    row.put("accountBalance", (montoComp == null ? ZERO : montoComp.divide(mil)));
                    row.put("accountBalanceLeft", (montoFrom == null ? ZERO : montoFrom.divide(mil)));
                    row.put("accountBalanceRight", (montoTo == null ? ZERO : montoTo.divide(mil)));
                    row.put("accountType", uiLabelMap.get("AccountingEquities"));
                    row.put("accountTypeSeqNum", Integer.valueOf(3));
                    rows.add(row);
                    
                    //Aqui se inician los calculos CUSTOM
                    totalNoActivosTo = totalNoActivosTo.add(montoTo.divide(mil));
                    totalNoActivosFrom = totalNoActivosFrom.add(montoFrom.divide(mil));
                    totalNoActivosComp = totalNoActivosComp.add(montoComp.divide(mil));
                }
            } else {
                rows.add(UtilMisc.toMap("accountType", uiLabelMap.get("AccountingEquities"), "accountTypeSeqNum", Integer.valueOf(3)));
            }

            // sort records by account code
            Collections.sort(rows, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    Integer accountTypeSeqNum1 = (Integer) o1.get("accountTypeSeqNum");
                    Integer accountTypeSeqNum2 = (Integer) o2.get("accountTypeSeqNum");
                    int c = accountTypeSeqNum1.compareTo(accountTypeSeqNum2);
                    if (c == 0) {
                        String accountCode1 = (String) o1.get("accountCode");
                        String accountCode2 = (String) o2.get("accountCode");
                        if (accountCode1 == null && accountCode2 == null) {
                            return 0;
                        }
                        if (accountCode1 == null && accountCode2 != null) {
                            return -1;
                        }
                        if (accountCode1 != null && accountCode2 == null) {
                            return 1;
                        }
                        return accountCode1.compareTo(accountCode2);
                    }
                    return c;
                }
            });
            request.setAttribute("jrDataSource", new JRMapCollectionDataSource(rows));

            // prepare report parameters
            Map<String, Object> jrParameters = FastMap.newInstance();
            jrParameters.put("fromDate", ctxt.get("fromDate"));
            jrParameters.put("thruDate", ctxt.get("thruDate"));
            jrParameters.put("fiscalTypeLeft", fromGlFiscalType.get("description", locale));
            jrParameters.put("fiscalTypeRight", toGlFiscalType.get("description", locale));
            jrParameters.put("organizationPartyId", ctxt.get("organizationPartyId"));
            jrParameters.put("organizationName", PartyHelper.getPartyName(delegator, (String) ctxt.get("organizationPartyId"), false));
            jrParameters.put("accountingTags", UtilAccountingTags.formatTagsAsString(request, UtilAccountingTags.FINANCIALS_REPORTS_TAG, delegator));
            
            //Datos extras
            jrParameters.put("totalActivosTo", totalActivosTo);
            jrParameters.put("totalActivosFrom", totalActivosFrom);
            jrParameters.put("totalActivosComp", totalActivosComp);
            jrParameters.put("totalNoActivosTo", totalNoActivosTo);
            jrParameters.put("totalNoActivosFrom", totalNoActivosFrom);
            jrParameters.put("totalNoActivosComp", totalNoActivosComp);
            
            request.setAttribute("jrParameters", jrParameters);

        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (RepositoryException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }

        return reportType;
    }    

    /**
     * Busca el monto en un map de la cuenta solicitada 
     * @param account
     * @param mapa
     * @return
     */
    public static BigDecimal buscaMonto(GenericValue account,Map<GenericValue, BigDecimal> mapa){
    	
    	BigDecimal monto = ZERO;
    	
    	Debug.logWarning("ENTRO A BUSCAR MONTO "+mapa.toString(),MODULE);
    	
		Iterator<Entry<GenericValue, BigDecimal>> it = mapa.entrySet().iterator();
		
    	while (it.hasNext()) {
    		Map.Entry e = (Map.Entry)it.next();
    		Debug.logWarning("Llave == "+e.getKey()+" Valor === "+e.getValue() ,MODULE);
    		
    		GenericValue gAcc = (GenericValue) e.getKey();
    		
    		if((gAcc.get("accountCode").toString()).equals((account).get("accountCode").toString()))
    		{
    			Debug.logWarning("Cuenta : "+gAcc.get("accountCode")+" Monto ===> "+mapa.get(gAcc) ,MODULE);
    			return mapa.get(gAcc);
    		}
    		
    	}
    	
    	return monto;
    }    
    /**
     * Prepare data source and parameters for balance sheet report.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the event response <code>String</code> value
     */
    @SuppressWarnings("unchecked")
    public static String prepareBalanceSheetReport(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        ResourceBundleMapWrapper uiLabelMap = UtilMessage.getUiLabels(locale);
        String reportType = UtilCommon.getParameter(request, "type");

        try {

            // retrieve financial data
            Map ctxt = prepareFinancialReportParameters(request);
            UtilAccountingTags.addTagParameters(request, ctxt);
            Map<String, Object> results = dispatcher.runSync("getBalanceSheetForDate", dispatcher.getDispatchContext().makeValidContext("getBalanceSheetForDate", ModelService.IN_PARAM, ctxt));

            List<GenericValue> assetAccounts = EntityUtil.orderBy(((Map) results.get("assetAccountBalances")).keySet(), UtilMisc.toList("glAccountId"));
            Map<GenericValue, BigDecimal> assetAccountBalances = (Map) results.get("assetAccountBalances");
            List<GenericValue> liabilityAccounts = EntityUtil.orderBy(((Map) results.get("liabilityAccountBalances")).keySet(), UtilMisc.toList("glAccountId"));
            Map<GenericValue, BigDecimal> liabilityAccountBalances = (Map) results.get("liabilityAccountBalances");
            List<GenericValue> equityAccounts = EntityUtil.orderBy(((Map) results.get("equityAccountBalances")).keySet(), UtilMisc.toList("glAccountId"));
            Map<GenericValue, BigDecimal> equityAccountBalances = (Map) results.get("equityAccountBalances");

            List<Map<String, Object>> rows = FastList.newInstance();

            // create record set for report
            if (UtilValidate.isNotEmpty(assetAccounts)) {
                for (GenericValue account : assetAccounts) {
                    Map<String, Object> row = FastMap.newInstance();
                    row.put("accountCode", account.get("accountCode"));
                    row.put("accountName", account.get("accountName"));
                    row.put("accountBalance", assetAccountBalances.get(account));
                    row.put("accountType", uiLabelMap.get("AccountingAssets"));
                    row.put("accountClass", account.get("glAccountClassId"));
                    row.put("accountTypeSeqNum", Integer.valueOf(1));
                    rows.add(row);
                }
            } else {
                rows.add(UtilMisc.toMap("accountType", uiLabelMap.get("AccountingAssets"), "accountTypeSeqNum", Integer.valueOf(1)));
            }

            if (UtilValidate.isNotEmpty(liabilityAccounts)) {
                for (GenericValue account : liabilityAccounts) {
                    Map<String, Object> row = FastMap.newInstance();
                    row.put("accountCode", account.get("accountCode"));
                    row.put("accountName", account.get("accountName"));
                    row.put("accountBalance", liabilityAccountBalances.get(account));
                    row.put("accountType", uiLabelMap.get("AccountingLiabilities"));
                    row.put("accountClass", account.get("glAccountClassId"));
                    row.put("accountTypeSeqNum", Integer.valueOf(2));
                    rows.add(row);
                }
            } else {
                rows.add(UtilMisc.toMap("accountType", uiLabelMap.get("AccountingLiabilities"), "accountTypeSeqNum", Integer.valueOf(2)));
            }

            if (UtilValidate.isNotEmpty(equityAccounts)) {
                for (GenericValue account : equityAccounts) {
                    Map<String, Object> row = FastMap.newInstance();
                    row.put("accountCode", account.get("accountCode"));
                    row.put("accountName", account.get("accountName"));
                    row.put("accountBalance", equityAccountBalances.get(account));
                    row.put("accountType", uiLabelMap.get("AccountingEquities"));
                    row.put("accountClass", account.get("glAccountClassId"));
                    row.put("accountTypeSeqNum", Integer.valueOf(3));
                    rows.add(row);
                }
            } else {
                rows.add(UtilMisc.toMap("accountType", uiLabelMap.get("AccountingEquities"), "accountTypeSeqNum", Integer.valueOf(3)));
            }


            // sort records by account code
            Collections.sort(rows, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    Integer accountTypeSeqNum1 = (Integer) o1.get("accountTypeSeqNum");
                    Integer accountTypeSeqNum2 = (Integer) o2.get("accountTypeSeqNum");
                    int c = accountTypeSeqNum1.compareTo(accountTypeSeqNum2);
                    if (c == 0) {
                        String accountCode1 = (String) o1.get("accountCode");
                        String accountCode2 = (String) o2.get("accountCode");
                        if (accountCode1 == null && accountCode2 == null) {
                            return 0;
                        }
                        if (accountCode1 == null && accountCode2 != null) {
                            return -1;
                        }
                        if (accountCode1 != null && accountCode2 == null) {
                            return 1;
                        }
                        return accountCode1.compareTo(accountCode2);
                    }
                    return c;
                }
            });
            request.setAttribute("jrDataSource", new JRMapCollectionDataSource(rows));

            // prepare report parameters
            Map<String, Object> jrParameters = FastMap.newInstance();
            jrParameters.put("glFiscalTypeId", ctxt.get("glFiscalTypeId"));
            jrParameters.put("asOfDate", ctxt.get("asOfDate"));
            jrParameters.put("organizationPartyId", ctxt.get("organizationPartyId"));
            jrParameters.put("organizationName", PartyHelper.getPartyName(delegator, (String) ctxt.get("organizationPartyId"), false));
            jrParameters.put("accountingTags", UtilAccountingTags.formatTagsAsString(request, UtilAccountingTags.FINANCIALS_REPORTS_TAG, delegator));
            request.setAttribute("jrParameters", jrParameters);

        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (RepositoryException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }

        return reportType;
    }

    /**
     * Prepare data source and parameters for comparative balance sheet report.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the event response <code>String</code> value
     */
    @SuppressWarnings("unchecked")
    public static String prepareComparativeBalanceReport(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        ResourceBundleMapWrapper uiLabelMap = UtilMessage.getUiLabels(locale);
        String reportType = UtilCommon.getParameter(request, "type");

        try {
            // retrieve financial data
            Map ctxt = prepareComparativeStateReportParameters(request);
            if (ctxt.get("fromDate") == null || ctxt.get("thruDate") == null) {
                return UtilMessage.createAndLogEventError(request, "FinancialsError_FromOrThruDateMissing", locale, MODULE);
            }

            UtilAccountingTags.addTagParameters(request, ctxt);
            Map<String, Object> results = dispatcher.runSync("getComparativeBalanceSheet", dispatcher.getDispatchContext().makeValidContext("getComparativeBalanceSheet", ModelService.IN_PARAM, ctxt));

            List<GenericValue> assetAccounts = EntityUtil.orderBy(((Map<GenericValue, BigDecimal>) results.get("assetAccountBalances")).keySet(), UtilMisc.toList("glAccountId"));
            Map<GenericValue, BigDecimal> assetAccountBalances = (Map<GenericValue, BigDecimal>) results.get("assetAccountBalances");
            List<GenericValue> liabilityAccounts = EntityUtil.orderBy(((Map<GenericValue, BigDecimal>) results.get("liabilityAccountBalances")).keySet(), UtilMisc.toList("glAccountId"));
            Map<GenericValue, BigDecimal> liabilityAccountBalances = (Map<GenericValue, BigDecimal>) results.get("liabilityAccountBalances");
            List<GenericValue> equityAccounts = EntityUtil.orderBy(((Map<GenericValue, BigDecimal>) results.get("equityAccountBalances")).keySet(), UtilMisc.toList("glAccountId"));
            Map<GenericValue, BigDecimal> equityAccountBalances = (Map<GenericValue, BigDecimal>) results.get("equityAccountBalances");

            Map<String, Object> fromDateAccountBalances = (Map<String, Object>) results.get("fromDateAccountBalances");
            Map<GenericValue, BigDecimal> fromAssetAccountBalances = (Map<GenericValue, BigDecimal>) fromDateAccountBalances.get("assetAccountBalances");
            Map<GenericValue, BigDecimal> fromLiabilityAccountBalances = (Map<GenericValue, BigDecimal>) fromDateAccountBalances.get("liabilityAccountBalances");
            Map<GenericValue, BigDecimal> fromEquityAccountBalances = (Map<GenericValue, BigDecimal>) fromDateAccountBalances.get("equityAccountBalances");

            Map<String, Object> thruDateAccountBalances = (Map<String, Object>) results.get("thruDateAccountBalances");
            Map<GenericValue, BigDecimal> toAssetAccountBalances = (Map<GenericValue, BigDecimal>) thruDateAccountBalances.get("assetAccountBalances");
            Map<GenericValue, BigDecimal> toLiabilityAccountBalances = (Map<GenericValue, BigDecimal>) thruDateAccountBalances.get("liabilityAccountBalances");
            Map<GenericValue, BigDecimal> toEquityAccountBalances = (Map<GenericValue, BigDecimal>) thruDateAccountBalances.get("equityAccountBalances");

            GenericValue fromGlFiscalType = delegator.findByPrimaryKey("GlFiscalType", UtilMisc.toMap("glFiscalTypeId", ctxt.get("glFiscalTypeId1")));
            GenericValue toGlFiscalType = delegator.findByPrimaryKey("GlFiscalType", UtilMisc.toMap("glFiscalTypeId", ctxt.get("glFiscalTypeId2")));

            List<Map<String, Object>> rows = FastList.newInstance();

            // create record set for report
            if (UtilValidate.isNotEmpty(assetAccounts)) {
                for (GenericValue account : assetAccounts) {
                    Map<String, Object> row = FastMap.newInstance();
                    row.put("accountCode", account.get("accountCode"));
                    row.put("accountName", account.get("accountName"));
                    row.put("accountBalance", assetAccountBalances.get(account));
                    row.put("accountBalanceLeft", fromAssetAccountBalances.get(account));
                    row.put("accountBalanceRight", toAssetAccountBalances.get(account));
                    row.put("accountType", uiLabelMap.get("AccountingAssets"));
                    row.put("accountTypeSeqNum", Integer.valueOf(1));
                    rows.add(row);
                }
            } else {
                rows.add(UtilMisc.toMap("accountType", uiLabelMap.get("AccountingAssets"), "accountTypeSeqNum", Integer.valueOf(1)));
            }

            if (UtilValidate.isNotEmpty(liabilityAccounts)) {
                for (GenericValue account : liabilityAccounts) {
                    Map<String, Object> row = FastMap.newInstance();
                    row.put("accountCode", account.get("accountCode"));
                    row.put("accountName", account.get("accountName"));
                    row.put("accountBalance", liabilityAccountBalances.get(account));
                    row.put("accountBalanceLeft", fromLiabilityAccountBalances.get(account));
                    row.put("accountBalanceRight", toLiabilityAccountBalances.get(account));
                    row.put("accountType", uiLabelMap.get("AccountingLiabilities"));
                    row.put("accountTypeSeqNum", Integer.valueOf(2));
                    rows.add(row);
                }
            } else {
                rows.add(UtilMisc.toMap("accountType", uiLabelMap.get("AccountingLiabilities"), "accountTypeSeqNum", Integer.valueOf(2)));
            }

            if (UtilValidate.isNotEmpty(equityAccounts)) {
                for (GenericValue account : equityAccounts) {
                    Map<String, Object> row = FastMap.newInstance();
                    row.put("accountCode", account.get("accountCode"));
                    row.put("accountName", account.get("accountName"));
                    row.put("accountBalance", equityAccountBalances.get(account));
                    row.put("accountBalanceLeft", fromEquityAccountBalances.get(account));
                    row.put("accountBalanceRight", toEquityAccountBalances.get(account));
                    row.put("accountType", uiLabelMap.get("AccountingEquities"));
                    row.put("accountTypeSeqNum", Integer.valueOf(3));
                    rows.add(row);
                }
            } else {
                rows.add(UtilMisc.toMap("accountType", uiLabelMap.get("AccountingEquities"), "accountTypeSeqNum", Integer.valueOf(3)));
            }

            // sort records by account code
            Collections.sort(rows, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    Integer accountTypeSeqNum1 = (Integer) o1.get("accountTypeSeqNum");
                    Integer accountTypeSeqNum2 = (Integer) o2.get("accountTypeSeqNum");
                    int c = accountTypeSeqNum1.compareTo(accountTypeSeqNum2);
                    if (c == 0) {
                        String accountCode1 = (String) o1.get("accountCode");
                        String accountCode2 = (String) o2.get("accountCode");
                        if (accountCode1 == null && accountCode2 == null) {
                            return 0;
                        }
                        if (accountCode1 == null && accountCode2 != null) {
                            return -1;
                        }
                        if (accountCode1 != null && accountCode2 == null) {
                            return 1;
                        }
                        return accountCode1.compareTo(accountCode2);
                    }
                    return c;
                }
            });
            request.setAttribute("jrDataSource", new JRMapCollectionDataSource(rows));

            // prepare report parameters
            Map<String, Object> jrParameters = FastMap.newInstance();
            jrParameters.put("fromDate", ctxt.get("fromDate"));
            jrParameters.put("thruDate", ctxt.get("thruDate"));
            jrParameters.put("fiscalTypeLeft", fromGlFiscalType.get("description", locale));
            jrParameters.put("fiscalTypeRight", toGlFiscalType.get("description", locale));
            jrParameters.put("organizationPartyId", ctxt.get("organizationPartyId"));
            jrParameters.put("organizationName", PartyHelper.getPartyName(delegator, (String) ctxt.get("organizationPartyId"), false));
            jrParameters.put("accountingTags", UtilAccountingTags.formatTagsAsString(request, UtilAccountingTags.FINANCIALS_REPORTS_TAG, delegator));
            request.setAttribute("jrParameters", jrParameters);

        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (RepositoryException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }

        return reportType;
    }
    
    /**
     * Prepare data source and parameters for comparative balance sheet report.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the event response <code>String</code> value
     */
    @SuppressWarnings("unchecked")
    public static String prepareComparativeActivoAnalitico(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        ResourceBundleMapWrapper uiLabelMap = UtilMessage.getUiLabels(locale);
        String reportType = UtilCommon.getParameter(request, "type");
        
        
        try {
        	
        	Map<String,Object> mapDatos = preparaDatosPreCompara(request);
        	
            // retrieve financial data
            Map<String, Object> ctxt = prepareComparativeStateReportParameters(request);
            ctxt.put("opcionBusca", "BUSCAACTIVO");//Se agrega este parametro para el reporte del activo
            ctxt.put("asOfDate", ctxt.get("thruDate"));
            
            if (ctxt.get("fromDate") == null){
            	ctxt.put("fromDate", UtilDateTime.toTimestamp(1, 1, 1980, 0, 0, 0));//Se coloca una fecha antigua para no obtener resultados
            }
            if (ctxt.get("thruDate") == null) {
                return UtilMessage.createAndLogEventError(request, "FinancialsError_FromOrThruDateMissing", locale, MODULE);
            }
            
            

            UtilAccountingTags.addTagParameters(request, ctxt);
            Map<String, Object> results = dispatcher.runSync("getComparativeSituacionFinanciera", dispatcher.getDispatchContext().makeValidContext("getComparativeSituacionFinanciera", ModelService.IN_PARAM, ctxt));

            List<GenericValue> assetAccounts = EntityUtil.orderBy(((Map<GenericValue, BigDecimal>) results.get("assetAccountBalances")).keySet(), UtilMisc.toList("glAccountId"));
            Map<GenericValue, BigDecimal> assetAccountBalances = (Map<GenericValue, BigDecimal>) results.get("assetAccountBalances");

            Map<String, Object> fromDateAccountBalances = (Map<String, Object>) results.get("fromDateAccountBalances");
            Map<GenericValue, BigDecimal> fromAssetAccountBalances = (Map<GenericValue, BigDecimal>) fromDateAccountBalances.get("assetAccountBalances");

            GenericValue GlFiscalType = delegator.findByPrimaryKey("GlFiscalType", UtilMisc.toMap("glFiscalTypeId", ctxt.get("glFiscalTypeId")));
            
            Debug.logWarning("GlFiscalType  [  "+GlFiscalType+" ]", MODULE);
            
            //Se agrega la lista para sacar las transacciones
            mapDatos.put("assetAccountBalances", assetAccountBalances);
            
            Map<String, Object> resultsTrans = dispatcher.runSync("agregarTransacciones", dispatcher.getDispatchContext().makeValidContext("agregarTransacciones", ModelService.IN_PARAM, mapDatos));
            Map<String, BigDecimal> acctDebito = (Map<String, BigDecimal>) resultsTrans.get("cuentasDebito");
            Debug.logWarning("acctDebito  [  "+acctDebito+" ]", MODULE);
            Map<String, BigDecimal> accCredito = (Map<String, BigDecimal>) resultsTrans.get("cuentasCredito");
            Debug.logWarning("accCredito  [  "+accCredito+" ]", MODULE);

            List<Map<String, Object>> rows = FastList.newInstance(); 
            
            // create record set for report
            if (UtilValidate.isNotEmpty(assetAccounts)) {
                for (GenericValue account : assetAccounts) {
                	String acctId = account.getString("glAccountId");
                	BigDecimal montoIni = (fromAssetAccountBalances.get(account) == null ? ZERO : fromAssetAccountBalances.get(account));
                	BigDecimal montoCargo = (acctDebito.get(acctId) == null ? ZERO : acctDebito.get(acctId));
                	BigDecimal montoAbono = (accCredito.get(acctId) == null ? ZERO : accCredito.get(acctId));
                	BigDecimal montoFinal = montoIni.add(montoCargo).subtract(montoAbono);
                	BigDecimal montoFlujo = montoIni.subtract(montoFinal);
                    Map<String, Object> row = FastMap.newInstance();
                    row.put("accountCode", account.get("accountCode"));
                    row.put("accountName", account.get("accountName"));
                    row.put("accountInicial", (montoIni == null ? ZERO : montoIni.divide(MIL)));
                    row.put("accountCargo", (montoCargo == null ? ZERO : montoCargo.divide(MIL)));
                    row.put("accountAbono", (montoAbono == null ? ZERO : montoAbono.divide(MIL)));
                    row.put("accountFinal", (montoFinal == null ? ZERO : montoFinal.divide(MIL)));
                    row.put("flujo", (montoFlujo == null ? ZERO : montoFlujo.divide(MIL)));
                    row.put("accountType", uiLabelMap.get("AccountingAssets"));
                    row.put("accountTypeSeqNum", Integer.valueOf(1));
                    rows.add(row);

                }
            } else {
                rows.add(UtilMisc.toMap("accountType", uiLabelMap.get("AccountingAssets"), "accountTypeSeqNum", Integer.valueOf(1)));
            }

            // sort records by account code
            Collections.sort(rows, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    Integer accountTypeSeqNum1 = (Integer) o1.get("accountTypeSeqNum");
                    Integer accountTypeSeqNum2 = (Integer) o2.get("accountTypeSeqNum");
                    int c = accountTypeSeqNum1.compareTo(accountTypeSeqNum2);
                    if (c == 0) {
                        String accountCode1 = (String) o1.get("accountCode");
                        String accountCode2 = (String) o2.get("accountCode");
                        if (accountCode1 == null && accountCode2 == null) {
                            return 0;
                        }
                        if (accountCode1 == null && accountCode2 != null) {
                            return -1;
                        }
                        if (accountCode1 != null && accountCode2 == null) {
                            return 1;
                        }
                        return accountCode1.compareTo(accountCode2);
                    }
                    return c;
                }
            });
            request.setAttribute("jrDataSource", new JRMapCollectionDataSource(rows));

            // prepare report parameters
            Map<String, Object> jrParameters = FastMap.newInstance();
            jrParameters.put("fromDate", ctxt.get("fromDate"));
            jrParameters.put("thruDate", ctxt.get("thruDate"));
            jrParameters.put("fiscalType", GlFiscalType.get("description", locale));
            jrParameters.put("organizationPartyId", ctxt.get("organizationPartyId"));
            jrParameters.put("organizationName", PartyHelper.getPartyName(delegator, (String) ctxt.get("organizationPartyId"), false));
            jrParameters.put("accountingTags", UtilAccountingTags.formatTagsAsString(request, UtilAccountingTags.FINANCIALS_REPORTS_TAG, delegator));
            
            Debug.logWarning("accountingTags  [  "+UtilAccountingTags.formatTagsAsString(request, UtilAccountingTags.FINANCIALS_REPORTS_TAG, delegator)+" ]", MODULE);
            
            
            request.setAttribute("jrParameters", jrParameters);

        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (RepositoryException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }

        return reportType;
    }
    
    public static Map<String,Object> preparaDatosVariacion(HttpServletRequest request) throws GenericServiceException, GenericEntityException{
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
    	String dateOption = UtilCommon.getParameter(request, "reportDateOption");
    	Locale locale = UtilHttp.getLocale(request);
    	TimeZone timeZone = UtilHttp.getTimeZone(request);
    	String organizationPartyId = UtilCommon.getOrganizationPartyId(request);
        String glFiscalTypeId = UtilCommon.getParameter(request, "glFiscalTypeId");
        String customTimePeriodId = UtilCommon.getParameter(request, "customTimePeriodId");
        
        EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
                EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId),
                EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
        
        GenericValue customPeriod = EntityUtil.getFirst(delegator.findByCondition("CustomTimePeriod", conditions, null, null));
        
        Map<String, Object> datos = FastMap.newInstance();
        
        if (dateOption != null) {
            // do nothing
            if (dateOption.equals("byDate")) {
            	
            	String asOfDate = UtilCommon.getParameter(request, "asOfDate");
            	Timestamp asOfDateTimeLast = UtilDateTime.getDayEnd(UtilDate.toTimestamp(asOfDate, timeZone, locale), timeZone, locale);
            	//Obtenemos los datos de la fecha final
            	int year = UtilDateTime.getYear(asOfDateTimeLast, timeZone, locale);
            	int month = UtilDateTime.getMonth(asOfDateTimeLast, timeZone, locale);
            	int day = UtilDateTime.getDayOfMonth(asOfDateTimeLast, timeZone, locale);
            	int hour = UtilDateTime.getHour(asOfDateTimeLast, timeZone, locale);
            	int minute = UtilDateTime.getMinute(asOfDateTimeLast, timeZone, locale);
            	int second = UtilDateTime.getSecond(asOfDateTimeLast, timeZone, locale);
            	Timestamp asOfDateTimeNextLast = UtilDateTime.toTimestamp(month, day, (year-1), hour, minute, second);
            	Timestamp asOfDateTime2ndLast = UtilDateTime.toTimestamp(month, day, (year-2), hour, minute, second);
            	
            	datos.put("asOfDateTimeLast", asOfDateTimeLast);
            	datos.put("asOfDateTimeNextLast", asOfDateTimeNextLast);
            	datos.put("asOfDateTime2ndLast", asOfDateTime2ndLast);
            	
            	datos.put("fromDate", UtilDateTime.getDayStart(UtilDateTime.getYearStart(asOfDateTimeLast)));
            	datos.put("thruDate", asOfDateTimeLast);
            	
            	Debug.logWarning("asOfDateTimeLast   "+asOfDateTimeLast, MODULE);
            	Debug.logWarning("asOfDateTimeNextLast   "+asOfDateTimeNextLast, MODULE);
            	Debug.logWarning("asOfDateTime2ndLast   "+asOfDateTime2ndLast, MODULE);
            	Debug.logWarning("fromDate   "+UtilDateTime.getDayStart(UtilDateTime.getYearStart(asOfDateTimeLast)), MODULE);
            	Debug.logWarning("thruDate   "+asOfDateTimeLast, MODULE);
            	
            } else if (dateOption.equals("byTimePeriod")) {
            	
            	String customTimePeriodId2ndLast = "";
            	
            	String customTimePeriodIdNextLast = FinancialServices.getPreviousCustomPeriodId(dispatcher, organizationPartyId, customTimePeriodId, userLogin);
            	if(customTimePeriodIdNextLast != null && !customTimePeriodIdNextLast.isEmpty())
            		customTimePeriodId2ndLast = FinancialServices.getPreviousCustomPeriodId(dispatcher, organizationPartyId, customTimePeriodIdNextLast, userLogin);
            	
            	datos.put("customTimePeriodIdNextLast", customTimePeriodIdNextLast);
            	datos.put("customTimePeriodId2ndLast", customTimePeriodId2ndLast);
            	
            	datos.put("fromDate", UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customPeriod.getDate("fromDate"))));
            	datos.put("thruDate", UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customPeriod.getDate("thruDate"))));
            	
            	Debug.logWarning("customTimePeriodId   "+customTimePeriodId, MODULE);
            	Debug.logWarning("customTimePeriodIdNextLast   "+customTimePeriodIdNextLast, MODULE);
            	Debug.logWarning("customTimePeriodId2ndLast   "+customTimePeriodId2ndLast, MODULE);
            	Debug.logWarning("fromDate   "+UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customPeriod.getDate("fromDate"))), MODULE);
            	Debug.logWarning("thruDate   "+UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customPeriod.getDate("thruDate"))), MODULE);
            	
            }
            
        }
    	
    	datos.put("customTimePeriodId", customTimePeriodId);
    	datos.put("organizationPartyId",organizationPartyId);
    	datos.put("userLogin", userLogin);
    	datos.put("glFiscalTypeId", glFiscalTypeId);
    	return datos;
    	
    }
    
    /**
     * Prepara los datos para utilizar el metodo de comparacion con una interfaz sin comparacion
     * @param request
     * @return
     * @throws GenericServiceException 
     * @throws GenericEntityException 
     */
    public static Map<String, Object> preparaDatosPreCompara(HttpServletRequest request) throws GenericServiceException, GenericEntityException {
        
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
    	String dateOption = UtilCommon.getParameter(request, "reportDateOption");
    	Locale locale = UtilHttp.getLocale(request);
    	TimeZone timeZone = UtilHttp.getTimeZone(request);
    	String organizationPartyId = UtilCommon.getOrganizationPartyId(request);
    	String customTimePeriodId = UtilCommon.getParameter(request, "customTimePeriodId");
        String glFiscalTypeId = UtilCommon.getParameter(request, "glFiscalTypeId");
    	
        Debug.logWarning("organizationPartyId    [  "+organizationPartyId+" ]", MODULE);
        Debug.logWarning("glFiscalTypeId    [  "+glFiscalTypeId+" ]", MODULE);
        
    	Map<String, Object> ctxt = FastMap.newInstance();
    	ctxt.put("customTimePeriodId", customTimePeriodId);
    	ctxt.put("organizationPartyId",organizationPartyId);
    	ctxt.put("userLogin", userLogin);
    	ctxt.put("glFiscalTypeId", glFiscalTypeId);
    	ctxt.put("glFiscalTypeId1", glFiscalTypeId);
    	ctxt.put("glFiscalTypeId2", glFiscalTypeId);
    	
	        if (dateOption != null) {
	            // do nothing
	            if (dateOption.equals("byDate")) {
	            	
	                String asOfDate = UtilCommon.getParameter(request, "asOfDate");
	                Timestamp asOfDateTime = UtilDateTime.getDayEnd(UtilDate.toTimestamp(asOfDate, timeZone, locale), timeZone, locale);
	                Timestamp asOfDateBeforeTime = UtilDateTime.addDaysToTimestamp(asOfDateTime, -365);
	                asOfDateBeforeTime = UtilDateTime.getYearEnd(asOfDateBeforeTime, timeZone, locale);
	                request.setAttribute("fromDate", asOfDateBeforeTime);
	                request.setAttribute("thruDate", asOfDateTime);        
	            	
	                Debug.logWarning("fromDate   [ " + asOfDateBeforeTime + "] ", MODULE);
	                Debug.logWarning("thruDate   [ " + asOfDateTime + "] ", MODULE);
	                
	                /**
	                 * Estos datos se enviaran al servicio qeu busca las transacciones (debito y credito) de cada cuenta
	                 */
	                Timestamp asOfDateInicioAnio = UtilDateTime.getYearStart(UtilDate.toTimestamp(asOfDate, timeZone, locale), timeZone, locale);
	                Debug.logWarning("asOfDateInicioAnio   [ " + asOfDateInicioAnio + "] ", MODULE);
	                ctxt.put("fecIni", asOfDateBeforeTime);
	                ctxt.put("fecFin", asOfDateTime);
	                
	                ctxt.put("fromDate", asOfDateBeforeTime);
	                ctxt.put("thruDate", asOfDateTime);	                
	            	
	            } else if (dateOption.equals("byTimePeriod")) {
	            	
	            	request.setAttribute("thruCustomTimePeriodId", customTimePeriodId);
	                Debug.logWarning("customTimePeriodId    [  "+customTimePeriodId+" ]", MODULE);
	                
	    	    	Map<String, Object> resultPeriod = dispatcher.runSync("obtenPeriodoAnterior", dispatcher.getDispatchContext().makeValidContext("obtenPeriodoAnterior", ModelService.IN_PARAM, ctxt));
	    	    	
	    	    	Debug.logWarning("resultPeriod   [ " + resultPeriod + "] ", MODULE);
	    	    		
	    	    	request.setAttribute("fromCustomTimePeriodId", resultPeriod.get("customPeriodFromId")); 
	    	    	
	    	    	ctxt.put("fromCustomTimePeriodId", resultPeriod.get("customPeriodFromId"));
	    	    	
	    	    	GenericValue periodoFinal = delegator.findByPrimaryKey("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId));
	    	    	 
	    	    	 Debug.logWarning("periodoFinal   [ " + periodoFinal + "] ", MODULE);
	    	    	 
	    	    	 java.sql.Date fecIni = periodoFinal.getDate("fromDate");
	    	    	 java.sql.Date fecFin = periodoFinal.getDate("thruDate");
	    	    	 
	                ctxt.put("fecIni", UtilDateTime.getTimestamp(fecIni.getTime()));
	                ctxt.put("fecFin", UtilDateTime.getTimestamp(fecFin.getTime()));
	                
	                Debug.logWarning("fecIni   [ " + ctxt.get("fecIni") + "] ", MODULE);
	                Debug.logWarning("fecFin   [ " + ctxt.get("fecFin") + "] ", MODULE);
	                
	                //Se setean las fechas 
	                request.setAttribute("fromDate", UtilDateTime.getTimestamp(fecIni.getTime()));
	                request.setAttribute("thruDate", UtilDateTime.getTimestamp(fecFin.getTime())); 
	                
	                ctxt.put("fromDate", UtilDateTime.getTimestamp(fecIni.getTime()));
	                ctxt.put("thruDate", UtilDateTime.getTimestamp(fecFin.getTime()));
	                ctxt.put("fromCustomTimePeriodId", resultPeriod.get("customPeriodFromId"));
	                ctxt.put("thruCustomTimePeriodId", customTimePeriodId);
	    	    	 
	            }
	            
	        	}
        
    	return ctxt;
    }

    /**
     * Prepare data source and parameters for cash flow report.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the event response <code>String</code> value
     */
    @SuppressWarnings("unchecked")
    public static String prepareCashFlowStatementReport(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        ResourceBundleMapWrapper uiLabelMap = UtilMessage.getUiLabels(locale);

        String reportType = UtilCommon.getParameter(request, "type");

        try {
            // retrieve financial data
            Map ctxt = prepareFinancialReportParameters(request);
            if (ctxt.get("fromDate") == null || ctxt.get("thruDate") == null) {
                return UtilMessage.createAndLogEventError(request, "FinancialsError_FromOrThruDateMissing", locale, MODULE);
            }

            UtilAccountingTags.addTagParameters(request, ctxt);
            Map<String, Object> results = dispatcher.runSync("getCashFlowStatementForDates", dispatcher.getDispatchContext().makeValidContext("getCashFlowStatementForDates", ModelService.IN_PARAM, ctxt));

            BigDecimal beginningCashAmount = (BigDecimal) results.get("beginningCashAmount");
            BigDecimal endingCashAmount = (BigDecimal) results.get("endingCashAmount");
            BigDecimal operatingCashFlow = (BigDecimal) results.get("operatingCashFlow");
            BigDecimal investingCashFlow = (BigDecimal) results.get("investingCashFlow");
            BigDecimal financingCashFlow = (BigDecimal) results.get("financingCashFlow");
            BigDecimal netCashFlow = (BigDecimal) results.get("netCashFlow");
            BigDecimal netIncome = (BigDecimal) results.get("netIncome");
            Map<GenericValue, BigDecimal> operatingCashFlowAccountBalances = (Map<GenericValue, BigDecimal>) results.get("operatingCashFlowAccountBalances");
            Map<GenericValue, BigDecimal> investingCashFlowAccountBalances = (Map<GenericValue, BigDecimal>) results.get("investingCashFlowAccountBalances");
            Map<GenericValue, BigDecimal> financingCashFlowAccountBalances = (Map<GenericValue, BigDecimal>) results.get("financingCashFlowAccountBalances");

            List<GenericValue> operatingAccounts = EntityUtil.orderBy(operatingCashFlowAccountBalances.keySet(), UtilMisc.toList("glAccountId"));
            List<GenericValue> investingAccounts = EntityUtil.orderBy(investingCashFlowAccountBalances.keySet(), UtilMisc.toList("glAccountId"));
            List<GenericValue> financingAccounts = EntityUtil.orderBy(financingCashFlowAccountBalances.keySet(), UtilMisc.toList("glAccountId"));

            List<Map<String, Object>> rows = FastList.newInstance();

            // create record set for report
            for (GenericValue account : operatingAccounts) {
                Map<String, Object> row = FastMap.newInstance();
                row.put("accountCode", account.get("accountCode"));
                row.put("accountName", account.get("accountName"));
                row.put("accountSum", operatingCashFlowAccountBalances.get(account));
                row.put("cashFlowType", uiLabelMap.get("FinancialsOperatingCashFlowAccounts"));
                row.put("cashFlowTypeTotal", uiLabelMap.get("FinancialsTotalOperatingCashFlow"));
                row.put("cashFlowTypeTotalAmount", operatingCashFlow);
                row.put("netCashFlow", uiLabelMap.get("FinancialsTotalNetCashFlow"));
                row.put("endingCashFlow", uiLabelMap.get("FinancialsEndingCashBalance"));
                rows.add(row);
            }

            for (GenericValue account : investingAccounts) {
                Map<String, Object> row = FastMap.newInstance();
                row.put("accountCode", account.get("accountCode"));
                row.put("accountName", account.get("accountName"));
                row.put("accountSum", investingCashFlowAccountBalances.get(account));
                row.put("cashFlowType", uiLabelMap.get("FinancialsInvestingCashFlowAccounts"));
                row.put("cashFlowTypeTotal", uiLabelMap.get("FinancialsTotalInvestingCashFlow"));
                row.put("cashFlowTypeTotalAmount", investingCashFlow);
                row.put("netCashFlow", uiLabelMap.get("FinancialsTotalNetCashFlow"));
                row.put("endingCashFlow", uiLabelMap.get("FinancialsEndingCashBalance"));
                rows.add(row);
            }

            for (GenericValue account : financingAccounts) {
                Map<String, Object> row = FastMap.newInstance();
                row.put("accountCode", account.get("accountCode"));
                row.put("accountName", account.get("accountName"));
                row.put("accountSum", financingCashFlowAccountBalances.get(account));
                row.put("cashFlowType", uiLabelMap.get("FinancialsFinancingCashFlowAccounts"));
                row.put("cashFlowTypeTotal", uiLabelMap.get("FinancialsTotalFinancingCashFlow"));
                row.put("cashFlowTypeTotalAmount", financingCashFlow);
                row.put("netCashFlow", uiLabelMap.get("FinancialsTotalNetCashFlow"));
                row.put("endingCashFlow", uiLabelMap.get("FinancialsEndingCashBalance"));
                rows.add(row);
            }

            request.setAttribute("jrDataSource", new JRMapCollectionDataSource(rows));

            // prepare report parameters
            Map<String, Object> jrParameters = FastMap.newInstance();
            jrParameters.put("glFiscalTypeId", ctxt.get("glFiscalTypeId"));
            jrParameters.put("fromDate", ctxt.get("fromDate"));
            jrParameters.put("thruDate", ctxt.get("thruDate"));
            jrParameters.put("organizationPartyId", ctxt.get("organizationPartyId"));
            jrParameters.put("organizationName", PartyHelper.getPartyName(delegator, (String) ctxt.get("organizationPartyId"), false));
            jrParameters.put("beginningCashAmount", beginningCashAmount);
            jrParameters.put("endingCashAmount", endingCashAmount);
            jrParameters.put("netCashFlow", netCashFlow);
            jrParameters.put("netIncome", netIncome);
            jrParameters.put("accountingTags", UtilAccountingTags.formatTagsAsString(request, UtilAccountingTags.FINANCIALS_REPORTS_TAG, delegator));
            request.setAttribute("jrParameters", jrParameters);

        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (RepositoryException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }

        return reportType;
    }

    /**
     * Prepare data source and parameters for comparative cash flow report.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the event response <code>String</code> value
     */
    @SuppressWarnings("unchecked")
    public static String prepareComparativeCashFlowStatementReport(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        ResourceBundleMapWrapper uiLabelMap = UtilMessage.getUiLabels(locale);

        String reportType = UtilCommon.getParameter(request, "type");

        try {

            // retrieve financial data
            Map ctxt = prepareComparativeFlowReportParameters(request);
            if (ctxt.get("fromDate1") == null || ctxt.get("thruDate1") == null || ctxt.get("fromDate2") == null || ctxt.get("thruDate2") == null) {
                return UtilMessage.createAndLogEventError(request, "FinancialsError_DateRangeMissing", locale, MODULE);
            }

            UtilAccountingTags.addTagParameters(request, ctxt);
            Map<String, Object> results = dispatcher.runSync("getComparativeCashFlowStatement", dispatcher.getDispatchContext().makeValidContext("getComparativeCashFlowStatement", ModelService.IN_PARAM, ctxt));

            Map<String, Object> set1CashFlowStatement = (Map<String, Object>) results.get("set1CashFlowStatement");
            Map<String, Object> set2CashFlowStatement = (Map<String, Object>) results.get("set2CashFlowStatement");

            BigDecimal beginningCashAmount1 = (BigDecimal) set1CashFlowStatement.get("beginningCashAmount");
            BigDecimal beginningCashAmount2 = (BigDecimal) set2CashFlowStatement.get("beginningCashAmount");
            BigDecimal endingCashAmount1 = (BigDecimal) set1CashFlowStatement.get("endingCashAmount");
            BigDecimal endingCashAmount2 = (BigDecimal) set2CashFlowStatement.get("endingCashAmount");
            BigDecimal operatingCashFlow1 = (BigDecimal) set1CashFlowStatement.get("operatingCashFlow");
            BigDecimal operatingCashFlow2 = (BigDecimal) set2CashFlowStatement.get("operatingCashFlow");
            BigDecimal investingCashFlow1 = (BigDecimal) set1CashFlowStatement.get("investingCashFlow");
            BigDecimal investingCashFlow2 = (BigDecimal) set2CashFlowStatement.get("investingCashFlow");
            BigDecimal financingCashFlow1 = (BigDecimal) set1CashFlowStatement.get("financingCashFlow");
            BigDecimal financingCashFlow2 = (BigDecimal) set2CashFlowStatement.get("financingCashFlow");
            BigDecimal netCashFlow1 = (BigDecimal) set1CashFlowStatement.get("netCashFlow");
            BigDecimal netCashFlow2 = (BigDecimal) set2CashFlowStatement.get("netCashFlow");
            BigDecimal netIncome1 = (BigDecimal) set1CashFlowStatement.get("netIncome");
            BigDecimal netIncome2 = (BigDecimal) set2CashFlowStatement.get("netIncome");

            Map<GenericValue, BigDecimal> operatingCashFlowAccounts1 = (Map<GenericValue, BigDecimal>) set1CashFlowStatement.get("operatingCashFlowAccountBalances");
            Map<GenericValue, BigDecimal> investingCashFlowAccounts1 = (Map<GenericValue, BigDecimal>) set1CashFlowStatement.get("investingCashFlowAccountBalances");
            Map<GenericValue, BigDecimal> financingCashFlowAccounts1 = (Map<GenericValue, BigDecimal>) set1CashFlowStatement.get("financingCashFlowAccountBalances");
            Map<GenericValue, BigDecimal> operatingCashFlowAccounts2 = (Map<GenericValue, BigDecimal>) set2CashFlowStatement.get("operatingCashFlowAccountBalances");
            Map<GenericValue, BigDecimal> investingCashFlowAccounts2 = (Map<GenericValue, BigDecimal>) set2CashFlowStatement.get("investingCashFlowAccountBalances");
            Map<GenericValue, BigDecimal> financingCashFlowAccounts2 = (Map<GenericValue, BigDecimal>) set2CashFlowStatement.get("financingCashFlowAccountBalances");

            Map<GenericValue, BigDecimal> operatingCashFlowAccountBalances = (Map<GenericValue, BigDecimal>) results.get("operatingCashFlowAccountBalances");
            Map<GenericValue, BigDecimal> investingCashFlowAccountBalances = (Map<GenericValue, BigDecimal>) results.get("investingCashFlowAccountBalances");
            Map<GenericValue, BigDecimal> financingCashFlowAccountBalances = (Map<GenericValue, BigDecimal>) results.get("financingCashFlowAccountBalances");

            List<GenericValue> operatingAccounts = EntityUtil.orderBy(operatingCashFlowAccountBalances.keySet(), UtilMisc.toList("glAccountId"));
            List<GenericValue> investingAccounts = EntityUtil.orderBy(investingCashFlowAccountBalances.keySet(), UtilMisc.toList("glAccountId"));
            List<GenericValue> financingAccounts = EntityUtil.orderBy(financingCashFlowAccountBalances.keySet(), UtilMisc.toList("glAccountId"));

            List<Map<String, Object>> rows = FastList.newInstance();

            // create record set for report
            for (GenericValue account : operatingAccounts) {
                Map<String, Object> row = FastMap.newInstance();
                row.put("accountCode", account.get("accountCode"));
                row.put("accountName", account.get("accountName"));
                row.put("accountSumLeft", operatingCashFlowAccounts1.get(account));
                row.put("accountSumRight", operatingCashFlowAccounts2.get(account));
                row.put("accountSumDiff", operatingCashFlowAccountBalances.get(account));
                row.put("cashFlowType", uiLabelMap.get("FinancialsOperatingCashFlowAccounts"));
                row.put("cashFlowTypeTotal", uiLabelMap.get("FinancialsTotalOperatingCashFlow"));
                row.put("cashFlowTypeTotalAmountLeft", operatingCashFlow1);
                row.put("cashFlowTypeTotalAmountRight", operatingCashFlow2);
                row.put("netCashFlow", uiLabelMap.get("FinancialsTotalNetCashFlow"));
                row.put("endingCashFlow", uiLabelMap.get("FinancialsEndingCashBalance"));
                rows.add(row);
            }

            for (GenericValue account : investingAccounts) {
                Map<String, Object> row = FastMap.newInstance();
                row.put("accountCode", account.get("accountCode"));
                row.put("accountName", account.get("accountName"));
                row.put("accountSumLeft", investingCashFlowAccounts1.get(account));
                row.put("accountSumRight", investingCashFlowAccounts2.get(account));
                row.put("accountSumDiff", investingCashFlowAccountBalances.get(account));
                row.put("cashFlowType", uiLabelMap.get("FinancialsInvestingCashFlowAccounts"));
                row.put("cashFlowTypeTotal", uiLabelMap.get("FinancialsTotalInvestingCashFlow"));
                row.put("cashFlowTypeTotalAmountLeft", investingCashFlow1);
                row.put("cashFlowTypeTotalAmountRight", investingCashFlow2);
                row.put("netCashFlow", uiLabelMap.get("FinancialsTotalNetCashFlow"));
                row.put("endingCashFlow", uiLabelMap.get("FinancialsEndingCashBalance"));
                rows.add(row);
            }

            for (GenericValue account : financingAccounts) {
                Map<String, Object> row = FastMap.newInstance();
                row.put("accountCode", account.get("accountCode"));
                row.put("accountName", account.get("accountName"));
                row.put("accountSumLeft", financingCashFlowAccounts1.get(account));
                row.put("accountSumRight", financingCashFlowAccounts2.get(account));
                row.put("accountSumDiff", financingCashFlowAccountBalances.get(account));
                row.put("cashFlowType", uiLabelMap.get("FinancialsFinancingCashFlowAccounts"));
                row.put("cashFlowTypeTotal", uiLabelMap.get("FinancialsTotalFinancingCashFlow"));
                row.put("cashFlowTypeTotalAmountLeft", financingCashFlow1);
                row.put("cashFlowTypeTotalAmountRight", financingCashFlow2);
                row.put("netCashFlow", uiLabelMap.get("FinancialsTotalNetCashFlow"));
                row.put("endingCashFlow", uiLabelMap.get("FinancialsEndingCashBalance"));
                rows.add(row);
            }

            // sort records by account code
            Collections.sort(rows, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    String accountCode1 = (String) o1.get("accountCode");
                    String accountCode2 = (String) o2.get("accountCode");
                    if (accountCode1 == null && accountCode2 == null) {
                        return 0;
                    }
                    if (accountCode1 == null && accountCode2 != null) {
                        return -1;
                    }
                    if (accountCode1 != null && accountCode2 == null) {
                        return 1;
                    }
                    return accountCode1.compareTo(accountCode2);
                }
            });
            request.setAttribute("jrDataSource", new JRMapCollectionDataSource(rows));

            GenericValue glFiscalType1 = delegator.findByPrimaryKey("GlFiscalType", UtilMisc.toMap("glFiscalTypeId", ctxt.get("glFiscalTypeId1")));
            GenericValue glFiscalType2 = delegator.findByPrimaryKey("GlFiscalType", UtilMisc.toMap("glFiscalTypeId", ctxt.get("glFiscalTypeId2")));

            // prepare report parameters
            Map<String, Object> jrParameters = FastMap.newInstance();
            jrParameters.put("fromDateLeft", ctxt.get("fromDate1"));
            jrParameters.put("thruDateLeft", ctxt.get("thruDate1"));
            jrParameters.put("fromDateRight", ctxt.get("fromDate2"));
            jrParameters.put("thruDateRight", ctxt.get("thruDate2"));
            jrParameters.put("organizationPartyId", ctxt.get("organizationPartyId"));
            jrParameters.put("organizationName", PartyHelper.getPartyName(delegator, (String) ctxt.get("organizationPartyId"), false));
            jrParameters.put("fiscalTypeLeft", glFiscalType1.get("description", locale));
            jrParameters.put("fiscalTypeRight", glFiscalType2.get("description", locale));
            jrParameters.put("beginningCashAmountLeft", beginningCashAmount1);
            jrParameters.put("beginningCashAmountRight", beginningCashAmount2);
            jrParameters.put("endingCashAmountLeft", endingCashAmount1);
            jrParameters.put("endingCashAmountRight", endingCashAmount2);
            jrParameters.put("netCashFlowLeft", netCashFlow1);
            jrParameters.put("netCashFlowRight", netCashFlow2);
            jrParameters.put("netIncomeLeft", netIncome1);
            jrParameters.put("netIncomeRight", netIncome2);
            jrParameters.put("accountingTags", UtilAccountingTags.formatTagsAsString(request, UtilAccountingTags.FINANCIALS_REPORTS_TAG, delegator));
            request.setAttribute("jrParameters", jrParameters);

        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (RepositoryException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }

        return reportType;
    }
    
    /**
     * Prepare data source and parameters for comparative cash flow report.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the event response <code>String</code> value
     */
    @SuppressWarnings("unchecked")
    public static String prepareComparativeFlujodeEfectivo(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);

        String reportType = UtilCommon.getParameter(request, "type");

        try {

            // retrieve financial data
            Map ctxt = prepareComparativeFlowReportParameters(request);
            if (ctxt.get("fromDate1") == null || ctxt.get("thruDate1") == null || ctxt.get("fromDate2") == null || ctxt.get("thruDate2") == null) {
                return UtilMessage.createAndLogEventError(request, "FinancialsError_DateRangeMissing", locale, MODULE);
            }

            UtilAccountingTags.addTagParameters(request, ctxt);
            Map<String, Object> results = dispatcher.runSync("getComparativeEstadoFlujoEfectivo", dispatcher.getDispatchContext().makeValidContext("getComparativeEstadoFlujoEfectivo", ModelService.IN_PARAM, ctxt));

            Debug.logWarning("results getComparativeEstadoFlujoEfectivo   ___   "+results.toString(), MODULE);
            
            Map<String, Object> set1FlujoDeEfectivo = (Map<String, Object>) results.get("set1FlujoDeEfectivo");
            Map<String, Object> set2FlujoDeEfectivo = (Map<String, Object>) results.get("set2FlujoDeEfectivo");
            
            Debug.logWarning("set1FlujoDeEfectivo getComparativeEstadoFlujoEfectivo   ___   "+set1FlujoDeEfectivo.toString(), MODULE);
            Debug.logWarning("set2FlujoDeEfectivo getComparativeEstadoFlujoEfectivo   ___   "+set2FlujoDeEfectivo.toString(), MODULE);

            //Maps con datos de la fecha inicial de busqueda (From)
            Map<GenericValue, BigDecimal> cuentasImpuestoFr = (Map<GenericValue, BigDecimal>) set1FlujoDeEfectivo.get("cuentasImpuesto");
            Map<GenericValue, BigDecimal> cuentasContribuFr = (Map<GenericValue, BigDecimal>) set1FlujoDeEfectivo.get("cuentasContribu");
            Map<GenericValue, BigDecimal> cuentasParticipaFr = (Map<GenericValue, BigDecimal>) set1FlujoDeEfectivo.get("cuentasParticipa");
            Map<GenericValue, BigDecimal> cuentasTransfeFr = (Map<GenericValue, BigDecimal>) set1FlujoDeEfectivo.get("cuentasTransfe");
            Map<GenericValue, BigDecimal> cuentasOtrosIngFr = (Map<GenericValue, BigDecimal>) set1FlujoDeEfectivo.get("cuentasOtrosIng");
            Map<GenericValue, BigDecimal> cuentasIcreVariaFr = (Map<GenericValue, BigDecimal>) set1FlujoDeEfectivo.get("cuentasIcreVaria");
            Map<GenericValue, BigDecimal> cuentasGastosFunFr = (Map<GenericValue, BigDecimal>) set1FlujoDeEfectivo.get("cuentasGastosFun");
            Map<GenericValue, BigDecimal> cuentasTransGastosFr = (Map<GenericValue, BigDecimal>) set1FlujoDeEfectivo.get("cuentasTransGastos");
            Map<GenericValue, BigDecimal> cuentaPartiAportaFr = (Map<GenericValue, BigDecimal>) set1FlujoDeEfectivo.get("cuentaPartiAporta");
            Map<GenericValue, BigDecimal> cuentaInteComisFr = (Map<GenericValue, BigDecimal>) set1FlujoDeEfectivo.get("cuentaInteComis");
            Map<GenericValue, BigDecimal> cuentasOtrosGastosFr = (Map<GenericValue, BigDecimal>) set1FlujoDeEfectivo.get("cuentasOtrosGastos");
            
            //Listas y Mapas con datos de la fecha final de busqueda (Thru)
            Map<GenericValue, BigDecimal> cuentasImpuestoTr = (Map<GenericValue, BigDecimal>) set2FlujoDeEfectivo.get("cuentasImpuesto");
            Map<GenericValue, BigDecimal> cuentasContribuTr = (Map<GenericValue, BigDecimal>) set2FlujoDeEfectivo.get("cuentasContribu");
            Map<GenericValue, BigDecimal> cuentasParticipaTr = (Map<GenericValue, BigDecimal>) set2FlujoDeEfectivo.get("cuentasParticipa");
            Map<GenericValue, BigDecimal> cuentasTransfeTr = (Map<GenericValue, BigDecimal>) set2FlujoDeEfectivo.get("cuentasTransfe");
            Map<GenericValue, BigDecimal> cuentasOtrosIngTr = (Map<GenericValue, BigDecimal>) set2FlujoDeEfectivo.get("cuentasOtrosIng");
            Map<GenericValue, BigDecimal> cuentasIcreVariaTr = (Map<GenericValue, BigDecimal>) set2FlujoDeEfectivo.get("cuentasIcreVaria");
            Map<GenericValue, BigDecimal> cuentasGastosFunTr = (Map<GenericValue, BigDecimal>) set2FlujoDeEfectivo.get("cuentasGastosFun");
            Map<GenericValue, BigDecimal> cuentasTransGastosTr = (Map<GenericValue, BigDecimal>) set2FlujoDeEfectivo.get("cuentasTransGastos");
            Map<GenericValue, BigDecimal> cuentaPartiAportaTr = (Map<GenericValue, BigDecimal>) set2FlujoDeEfectivo.get("cuentaPartiAporta");
            Map<GenericValue, BigDecimal> cuentaInteComisTr = (Map<GenericValue, BigDecimal>) set2FlujoDeEfectivo.get("cuentaInteComis");
            Map<GenericValue, BigDecimal> cuentasOtrosGastosTr = (Map<GenericValue, BigDecimal>) set2FlujoDeEfectivo.get("cuentasOtrosGastos");
            
           //Maps con datos comparativos entre las dos fechas
            Map<GenericValue, BigDecimal> difCuentasImpuesto = (Map<GenericValue, BigDecimal>) results.get("difCuentasImpuesto");
            Map<GenericValue, BigDecimal> difCuentasContribu = (Map<GenericValue, BigDecimal>) results.get("difCuentasContribu");
            Map<GenericValue, BigDecimal> difCuentasParticipa = (Map<GenericValue, BigDecimal>) results.get("difCuentasParticipa");
            Map<GenericValue, BigDecimal> difCuentasTransfe = (Map<GenericValue, BigDecimal>) results.get("difCuentasTransfe");
            Map<GenericValue, BigDecimal> difCuentasOtrosIng = (Map<GenericValue, BigDecimal>) results.get("difCuentasOtrosIng");
            Map<GenericValue, BigDecimal> difCuentasIcreVaria = (Map<GenericValue, BigDecimal>) results.get("difCuentasIcreVaria");
            Map<GenericValue, BigDecimal> difCuentasGastosFun = (Map<GenericValue, BigDecimal>) results.get("difCuentasGastosFun");
            Map<GenericValue, BigDecimal> difCuentasTransGastos = (Map<GenericValue, BigDecimal>) results.get("difCuentasTransGastos");
            Map<GenericValue, BigDecimal> difCuentaPartiAporta = (Map<GenericValue, BigDecimal>) results.get("difCuentaPartiAporta");
            Map<GenericValue, BigDecimal> difCuentaInteComis = (Map<GenericValue, BigDecimal>) results.get("difCuentaInteComis");
            Map<GenericValue, BigDecimal> difCuentasOtrosGastos = (Map<GenericValue, BigDecimal>) results.get("difCuentasOtrosGastos");
            
            /*
             * Aqui se crean las listas para iterar las cuentas de cada grupo de datos , 
             * se agregan los datos de las dos listas con el metodo putAll que sobreescribe
             * los valores que ya existen , se ordenan
             */
            Map cuentasImpuestoAgre = FastMap.newInstance(); 
            cuentasImpuestoAgre.putAll(cuentasImpuestoFr);
            cuentasImpuestoAgre.putAll(cuentasImpuestoTr); 
            List<GenericValue> cuentasImpuestoList = EntityUtil.orderBy(cuentasImpuestoAgre.keySet(), UtilMisc.toList("glAccountId"));
            Map cuentasContribuAgre = FastMap.newInstance(); 
            cuentasContribuAgre.putAll(cuentasContribuFr);
            cuentasContribuAgre.putAll(cuentasContribuTr); 
            List<GenericValue> cuentasContribuList = EntityUtil.orderBy(cuentasContribuAgre.keySet(), UtilMisc.toList("glAccountId"));  
            Map cuentasParticipaAgre = FastMap.newInstance(); 
            cuentasParticipaAgre.putAll(cuentasParticipaFr);
            cuentasParticipaAgre.putAll(cuentasParticipaTr); 
            List<GenericValue> cuentasParticipaList = EntityUtil.orderBy(cuentasParticipaAgre.keySet(), UtilMisc.toList("glAccountId"));  
            Map cuentasTransfeAgre = FastMap.newInstance(); 
            cuentasTransfeAgre.putAll(cuentasTransfeFr);
            cuentasTransfeAgre.putAll(cuentasTransfeTr); 
            List<GenericValue> cuentasTransfeList = EntityUtil.orderBy(cuentasTransfeAgre.keySet(), UtilMisc.toList("glAccountId"));          
            Map cuentasOtrosIngAgre = FastMap.newInstance(); 
            cuentasOtrosIngAgre.putAll(cuentasOtrosIngFr);
            cuentasOtrosIngAgre.putAll(cuentasOtrosIngTr); 
            List<GenericValue> cuentasOtrosIngList = EntityUtil.orderBy(cuentasOtrosIngAgre.keySet(), UtilMisc.toList("glAccountId"));    
            Map cuentasIcreVariaAgre = FastMap.newInstance(); 
            cuentasIcreVariaAgre.putAll(cuentasIcreVariaFr);
            cuentasIcreVariaAgre.putAll(cuentasIcreVariaTr); 
            List<GenericValue> cuentasIcreVariaList = EntityUtil.orderBy(cuentasIcreVariaAgre.keySet(), UtilMisc.toList("glAccountId"));       
            Map cuentasGastosFunAgre = FastMap.newInstance(); 
            cuentasGastosFunAgre.putAll(cuentasGastosFunFr);
            cuentasGastosFunAgre.putAll(cuentasGastosFunTr); 
            List<GenericValue> cuentasGastosFunList = EntityUtil.orderBy(cuentasGastosFunAgre.keySet(), UtilMisc.toList("glAccountId"));       
            Map cuentasTransGastosAgre = FastMap.newInstance(); 
            cuentasTransGastosAgre.putAll(cuentasTransGastosFr);
            cuentasTransGastosAgre.putAll(cuentasTransGastosTr); 
            List<GenericValue> cuentasTransGastosList = EntityUtil.orderBy(cuentasTransGastosAgre.keySet(), UtilMisc.toList("glAccountId"));        
            Map cuentaPartiAportaAgre = FastMap.newInstance(); 
            cuentaPartiAportaAgre.putAll(cuentaPartiAportaFr);
            cuentaPartiAportaAgre.putAll(cuentaPartiAportaTr); 
            List<GenericValue> cuentaPartiAportaList = EntityUtil.orderBy(cuentaPartiAportaAgre.keySet(), UtilMisc.toList("glAccountId"));    
            Map cuentaInteComisAgre = FastMap.newInstance(); 
            cuentaInteComisAgre.putAll(cuentaInteComisFr);
            cuentaInteComisAgre.putAll(cuentaInteComisTr); 
            List<GenericValue> cuentaInteComisList = EntityUtil.orderBy(cuentaInteComisAgre.keySet(), UtilMisc.toList("glAccountId"));    
            Map cuentasOtrosGastosAgre = FastMap.newInstance(); 
            cuentasOtrosGastosAgre.putAll(cuentasOtrosGastosFr);
            cuentasOtrosGastosAgre.putAll(cuentasOtrosGastosTr); 
            List<GenericValue> cuentasOtrosGastosList = EntityUtil.orderBy(cuentasOtrosGastosAgre.keySet(), UtilMisc.toList("glAccountId"));                    

            List<Map<String, Object>> rows = FastList.newInstance();
            
            //Se agregan las cuentas de IMPUESTOS
            llenaMapaReporteComparativo(rows, cuentasImpuestoList, cuentasImpuestoFr, cuentasImpuestoTr, difCuentasImpuesto);
            //Se agregan las cuentas de Contribuciones
            llenaMapaReporteComparativo(rows, cuentasContribuList, cuentasContribuFr, cuentasContribuTr, difCuentasContribu);            
            //Se agregan las cuentas de Participaciones
            llenaMapaReporteComparativo(rows, cuentasParticipaList, cuentasParticipaFr, cuentasParticipaTr, difCuentasParticipa);    
            //Se agregan las cuentas de Transferencias
            llenaMapaReporteComparativo(rows, cuentasTransfeList, cuentasTransfeFr, cuentasTransfeTr, difCuentasTransfe);    
            //Se agregan las cuentas de Otros Ingresos y Beneficios
            llenaMapaReporteComparativo(rows, cuentasOtrosIngList, cuentasOtrosIngFr, cuentasOtrosIngTr, difCuentasOtrosIng);      
            //Se agregan las cuentas de INCREMENTO POR VARIACION DE INVENTARIOS
            llenaMapaReporteComparativo(rows, cuentasIcreVariaList, cuentasIcreVariaFr, cuentasIcreVariaTr, difCuentasIcreVaria);                
            //Se agregan las cuentas de GASTO DE FUNCIONAMIENTO
            llenaMapaReporteComparativo(rows, cuentasGastosFunList, cuentasGastosFunFr, cuentasGastosFunTr, difCuentasGastosFun);                          
            //Se agregan las cuentas de TRANSFERENCIAS, ASIGNACIONES, SUBSIDIOS Y OTRAS AYUDAS DE GASTOS
            llenaMapaReporteComparativo(rows, cuentasTransGastosList, cuentasTransGastosFr, cuentasTransGastosTr, difCuentasTransGastos);            
            //Se agregan las cuentas de PARTICIPACIONES Y APORTACIONES DE GASTOS
            llenaMapaReporteComparativo(rows, cuentaPartiAportaList, cuentaPartiAportaFr, cuentaPartiAportaTr, difCuentaPartiAporta);            
            //Se agregan las cuentas de INTERESES, COMISIONES Y OTROS GASTOS DE LA DEUDA PUBLICA
            llenaMapaReporteComparativo(rows, cuentaInteComisList, cuentasOtrosGastosFr, cuentasOtrosGastosTr, difCuentaInteComis);            
            //Se agregan las cuentas de OTROS GASTOS Y PERDIDAS EXTRAORDINARIAS
            llenaMapaReporteComparativo(rows, cuentasOtrosGastosList, cuentaInteComisFr, cuentaInteComisTr, difCuentasOtrosGastos);            
            
            
            Debug.logWarning("CUENTAS PARA REPORTE   :::%% "+rows.toArray().toString(), MODULE);

            request.setAttribute("jrDataSource", new JRMapCollectionDataSource(rows));

            GenericValue glFiscalType1 = delegator.findByPrimaryKey("GlFiscalType", UtilMisc.toMap("glFiscalTypeId", ctxt.get("glFiscalTypeId1")));
            GenericValue glFiscalType2 = delegator.findByPrimaryKey("GlFiscalType", UtilMisc.toMap("glFiscalTypeId", ctxt.get("glFiscalTypeId2")));

            // prepare report parameters
            Map<String, Object> jrParameters = FastMap.newInstance();
            jrParameters.put("fromDateLeft", ctxt.get("fromDate1"));
            jrParameters.put("thruDateLeft", ctxt.get("thruDate1"));
            jrParameters.put("fromDateRight", ctxt.get("fromDate2"));
            jrParameters.put("thruDateRight", ctxt.get("thruDate2"));
            jrParameters.put("organizationPartyId", ctxt.get("organizationPartyId"));
            jrParameters.put("organizationName", PartyHelper.getPartyName(delegator, (String) ctxt.get("organizationPartyId"), false));
            jrParameters.put("fiscalTypeLeft", glFiscalType1.get("description", locale));
            jrParameters.put("fiscalTypeRight", glFiscalType2.get("description", locale));
            jrParameters.put("accountingTags", UtilAccountingTags.formatTagsAsString(request, UtilAccountingTags.FINANCIALS_REPORTS_TAG, delegator));
            request.setAttribute("jrParameters", jrParameters);

        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (RepositoryException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }

        return reportType;
    }    
    
    /**
     * Metodo que sirve para llenar un mapa para los datos de un reporte 
     * @param mapaALlenar
     * @param listaCuentas
     * @param mapaLeft
     * @param mapaRight
     * @param mapaComp
     * @return
     */
    private static void llenaMapaReporteComparativo(List<Map<String, Object>> mapaALlenar,
    								List<GenericValue> listaCuentas,Map<GenericValue, BigDecimal> mapaLeft, 
    								Map<GenericValue, BigDecimal> mapaRight,Map<GenericValue, BigDecimal> mapaComp ){
    	
        for (GenericValue account : listaCuentas) {
        	Map<String, Object> row = FastMap.newInstance();
        	BigDecimal montoLeft = (mapaLeft.get(account) == null ? ZERO :mapaLeft.get(account));
        	BigDecimal montoRight = (mapaRight.get(account) == null ? ZERO :mapaRight.get(account));
        	BigDecimal montoCompara = (mapaComp.get(account) == null ? ZERO :mapaComp.get(account));             	
        	row.put("accountCode", account.get("accountCode"));
        	row.put("accountName", account.get("accountName"));
        	row.put("accountLeft", montoLeft.divide(MIL));
        	row.put("accountRight", montoRight.divide(MIL));
        	row.put("accountCompar", montoCompara.divide(MIL));
        	row.put("accountType", getNombreGrupoEfectivo(account.getString("glAccountClassId")));
        	Debug.logWarning("Renglon disponible para agregar al reporte :   "+row.toString(), MODULE);
        	if(!montoLeft.equals(ZERO)||!montoRight.equals(ZERO)||!montoCompara.equals(ZERO)){
        		mapaALlenar.add(row);
        	}
		}
        
        Debug.logWarning("CUENTAS AGREGADAS   :::� "+listaCuentas.toString(), MODULE);
    	
    }
    
    /**
     * Metodo que obtiene el nombre del grupo para el reporte de Flujo de Efectivo
     * @param glAccountClassId
     * @return
     */
    private static String getNombreGrupoEfectivo(String glAccountClassId){
    	
    	String regresa = "";
    	
    	if (glAccountClassId.equals("IMPUESTOS")){
    		regresa = "Impuestos";
    	} else if (glAccountClassId.equals("CONTRIBUCIONES M")){
    		regresa = "Contribuciones de Mejoras ";
    	} else if (glAccountClassId.equals("PARTICIPACIONESyA")){
    		regresa = "Participaciones y Aportaciones";
    	} else if (glAccountClassId.equals("TASyOA")){
    		regresa = "Transferencias, Asignaciones, Subsidios y Otras ";
    	} else if (glAccountClassId.equals("OTROS IyB")){
    		regresa = "Otros Ingresos y Beneficios";
    	} else if (glAccountClassId.equals("IVI")){
    		regresa = "Incremento por Variaci�n de Inventarios ";
    	} else if (glAccountClassId.equals("GASTO DE FUNCION")){
    		regresa = "Gastos de Funcionamiento";
    	} else if (glAccountClassId.equals("TASyOA DE GASTOS")){
    		regresa = "Transferencias, Asignaciones, Subsidios y Otras Ayudas";
    	} else if (glAccountClassId.equals("PARTICIPyA DE GASTOS")){
    		regresa = "Participaciones y Aportaciones";
    	} else if (glAccountClassId.equals("ICyOGDP")){
    		regresa = "Intereses, Comisiones y Otros Gastos De La Deuda P�blica";
    	} else if (glAccountClassId.equals("OGPE")){
    		regresa = "Otros Gastos y P�rdidas Extraordinarias";
    	} 
    	
    	return regresa;
    }

    /**
     * Prepare data source and parameters for accounts receivables aging report.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the event response, either "pdf" or "xls" string to select report type, or "error".
     */
    public static String prepareReceivablesAgingReport(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);

        String reportType = UtilCommon.getParameter(request, "type");
        String partyId = UtilCommon.getParameter(request, "partyId");

        String organizationPartyId = null;
        Timestamp asOfDate = null;

        try {

            // retrieve financial data
            Map<String, Object> ctxt = prepareFinancialReportParameters(request);
            UtilAccountingTags.addTagParameters(request, ctxt);
            asOfDate = (Timestamp) ctxt.get("asOfDate");
            organizationPartyId = (String) ctxt.get("organizationPartyId");

            List<Integer> daysOutstandingBreakPoints = UtilMisc.<Integer>toList(30, 60, 90);
            // this is a hack to get the the invoices over the last break point (ie, 90+ invoices)
            Integer maximumDSOBreakPoint = 9999;
            List<Integer> breakPointParams = FastList.<Integer>newInstance();
            breakPointParams.addAll(daysOutstandingBreakPoints);
            breakPointParams.add(maximumDSOBreakPoint);

            Map<Integer, List<Invoice>> invoicesByDSO = null;
            if (UtilValidate.isEmpty(partyId)) {
                invoicesByDSO = AccountsHelper.getUnpaidInvoicesForCustomers(organizationPartyId, breakPointParams, asOfDate, UtilMisc.toList("INVOICE_READY"), delegator, timeZone, locale);
            } else {
                invoicesByDSO = AccountsHelper.getUnpaidInvoicesForCustomer(organizationPartyId, partyId, breakPointParams, asOfDate, UtilMisc.toList("INVOICE_READY"), delegator, timeZone, locale);
            }

            List<Map<String, Object>> plainList = FastList.<Map<String, Object>>newInstance();
            Integer prevDCOBreakPoint = 0;
            for (Integer breakPoint : invoicesByDSO.keySet()) {
                List<Invoice> invoicesForBreakPoint = invoicesByDSO.get(breakPoint);
                if (UtilValidate.isEmpty(invoicesForBreakPoint)) {
                    plainList.add(UtilMisc.<String, Object>toMap("DCOBreakPoint", breakPoint, "prevDCOBreakPoint", prevDCOBreakPoint, "isEmpty", Boolean.TRUE));
                }
                for (Invoice invoice : invoicesForBreakPoint) {
                    FastMap<String, Object> reportLine = FastMap.<String, Object>newInstance();
                    reportLine.put("prevDCOBreakPoint", prevDCOBreakPoint);
                    reportLine.put("DCOBreakPoint", breakPoint);
                    reportLine.put("isEmpty", Boolean.FALSE);
                    reportLine.put("invoiceDate", invoice.getInvoiceDate());
                    reportLine.put("invoiceId", invoice.getInvoiceId());
                    reportLine.put("invoiceTotal", invoice.getInvoiceTotal());
                    reportLine.put("openAmount", invoice.getOpenAmount());
                    reportLine.put("partyId", invoice.getPartyId());
                    reportLine.put("partyName", PartyHelper.getPartyName(delegator, invoice.getPartyId(), false));
                    plainList.add(reportLine);
                }
                prevDCOBreakPoint = breakPoint;
            }

            request.setAttribute("jrDataSource", new JRMapCollectionDataSource(plainList));

            Map<String, Object> jrParameters = FastMap.newInstance();
            jrParameters.putAll(ctxt);
            jrParameters.put("organizationName", PartyHelper.getPartyName(delegator, (String) ctxt.get("organizationPartyId"), false));
            jrParameters.put("isReceivables", Boolean.TRUE);
            request.setAttribute("jrParameters", jrParameters);

        } catch (GenericEntityException e) {
            UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (RepositoryException e) {
            UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }

        return reportType;
    }

    /**
     * Prepare data source and parameters for accounts payables aging report.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the event response, either "pdf" or "xls" string to select report type, or "error".
     */
    public static String preparePayablesAgingReport(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);

        String reportType = UtilCommon.getParameter(request, "type");
        String partyId = UtilCommon.getParameter(request, "partyId");

        String organizationPartyId = null;
        Timestamp asOfDate = null;

        try {

            // retrieve financial data
            Map<String, Object> ctxt = prepareFinancialReportParameters(request);
            UtilAccountingTags.addTagParameters(request, ctxt);
            asOfDate = (Timestamp) ctxt.get("asOfDate");
            organizationPartyId = (String) ctxt.get("organizationPartyId");

            List<Integer> daysOutstandingBreakPoints = UtilMisc.<Integer>toList(30, 60, 90);
            // this is a hack to get the the invoices over the last break point (ie, 90+ invoices)
            Integer maximumDSOBreakPoint = 9999;
            List<Integer> breakPointParams = FastList.<Integer>newInstance();
            breakPointParams.addAll(daysOutstandingBreakPoints);
            breakPointParams.add(maximumDSOBreakPoint);

            Map<Integer, List<Invoice>> invoicesByDSO = null;
            if (UtilValidate.isEmpty(partyId)) {
                invoicesByDSO = AccountsHelper.getUnpaidInvoicesForVendors(organizationPartyId, breakPointParams, asOfDate, UtilMisc.toList("INVOICE_READY"), delegator, timeZone, locale);
            } else {
                invoicesByDSO = AccountsHelper.getUnpaidInvoicesForVendor(organizationPartyId, partyId, breakPointParams, asOfDate, UtilMisc.toList("INVOICE_READY"), delegator, timeZone, locale);
            }

            List<Map<String, Object>> plainList = FastList.<Map<String, Object>>newInstance();
            Integer prevDCOBreakPoint = 0;
            for (Integer breakPoint : invoicesByDSO.keySet()) {
                List<Invoice> invoicesForBreakPoint = invoicesByDSO.get(breakPoint);
                if (UtilValidate.isEmpty(invoicesForBreakPoint)) {
                    plainList.add(UtilMisc.<String, Object>toMap("DCOBreakPoint", breakPoint, "prevDCOBreakPoint", prevDCOBreakPoint, "isEmpty", Boolean.TRUE));
                }
                for (Invoice invoice : invoicesForBreakPoint) {
                    FastMap<String, Object> reportLine = FastMap.<String, Object>newInstance();
                    reportLine.put("prevDCOBreakPoint", prevDCOBreakPoint);
                    reportLine.put("DCOBreakPoint", breakPoint);
                    reportLine.put("isEmpty", Boolean.FALSE);
                    reportLine.put("invoiceDate", invoice.getInvoiceDate());
                    reportLine.put("invoiceId", invoice.getInvoiceId());
                    reportLine.put("invoiceTotal", invoice.getInvoiceTotal());
                    reportLine.put("partyId", invoice.getPartyIdFrom());
                    reportLine.put("partyName", PartyHelper.getPartyName(delegator, invoice.getPartyIdFrom(), false));
                    plainList.add(reportLine);
                }
                prevDCOBreakPoint = breakPoint;
            }

            request.setAttribute("jrDataSource", new JRMapCollectionDataSource(plainList));

            Map<String, Object> jrParameters = FastMap.newInstance();
            jrParameters.putAll(ctxt);
            jrParameters.put("organizationName", PartyHelper.getPartyName(delegator, (String) ctxt.get("organizationPartyId"), false));
            jrParameters.put("accountingTags", UtilAccountingTags.formatTagsAsString(request, UtilAccountingTags.FINANCIALS_REPORTS_TAG, delegator));
            jrParameters.put("isReceivables", Boolean.FALSE);
            request.setAttribute("jrParameters", jrParameters);

        } catch (GenericEntityException e) {
            UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (RepositoryException e) {
            UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }

        return reportType;
    }

    /**
     * Prepare data source and parameters for average DSO receivables report.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the event response, either "pdf" or "xls" string to select report type, or "error".
     */
    public static String prepareAverageDSOReportReceivables(HttpServletRequest request, HttpServletResponse response) {
        return prepareAverageDSOReport("SALES_INVOICE", request);
    }

    /**
     * Prepare data source and parameters for average DSO payables report.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the event response, either "pdf" or "xls" string to select report type, or "error".
     */
    public static String prepareAverageDSOReportPayables(HttpServletRequest request, HttpServletResponse response) {
        return prepareAverageDSOReport("PURCHASE_INVOICE", request);
    }

    /**
     * Implements common logic for average DSO reports.
     * @param invoiceTypeId report analyzes invoices of this type
     * @param request a <code>HttpServletRequest</code> value
     * @return the event response, either "pdf" or "xls" string to select report type, or "error".
     */
    @SuppressWarnings("unchecked")
    private static String prepareAverageDSOReport(String invoiceTypeId, HttpServletRequest request) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        TimeZone timeZone = UtilCommon.getTimeZone(request);
        Locale locale = UtilCommon.getLocale(request);

        String reportType = UtilCommon.getParameter(request, "type");

        try {

            // parse user's input
            Map<String, Object> ctxt = prepareFinancialReportParameters(request);

            Map<String, Object> jrParameters = FastMap.<String, Object>newInstance();

            // get the from and thru date
            Timestamp fromDate = (Timestamp) ctxt.get("fromDate");
            Timestamp thruDate = (Timestamp) ctxt.get("thruDate");

            // get the invoice type of the report
            Boolean isReceivables =
                "SALES_INVOICE".equals(invoiceTypeId) ? Boolean.TRUE : "PURCHASE_INVOICE".equals(invoiceTypeId) ? Boolean.FALSE : null;

            // don't do anything if invoiceTypeId is invalid
            if (isReceivables == null) {
                return "error";
            }

            jrParameters.put("isReceivables", isReceivables);

            // the date of the report is either now or the thruDate of user's input, whichever is earlier
            Timestamp now = UtilDateTime.nowTimestamp();
            Timestamp reportDate = (thruDate != null && thruDate.before(now) ? thruDate : now); 
            jrParameters.put("reportDate", reportDate);

            // report should display period, pass dates to parameters
            jrParameters.put("fromDate", fromDate);
            jrParameters.put("thruDate", reportDate);

            // the partyId field we want to use for grouping the report fields is partyIdFrom for receivables or
            // partyId for payables
            String partyIdField = (isReceivables ? "partyId" : "partyIdFrom");

            // constants
            EntityFindOptions options = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
            String organizationPartyId = UtilCommon.getOrganizationPartyId(request);
            jrParameters.put("organizationPartyId", organizationPartyId);
            jrParameters.put("organizationName", PartyHelper.getPartyName(delegator, (String) ctxt.get("organizationPartyId"), false));

            // get the invoices as a list iterator.  Pending and canceled invoices should not be considered.
            List<EntityCondition> conditionList = FastList.<EntityCondition>newInstance();
            conditionList.add(EntityCondition.makeCondition("invoiceTypeId", invoiceTypeId));
            conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_IN_PROCESS"));
            conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_WRITEOFF"));
            conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
            conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_VOIDED"));
            conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, reportDate));
            // use the other partyId field to restrict invoices to those just for the current organization 
            if (isReceivables) {
                conditionList.add(EntityCondition.makeCondition("partyIdFrom", organizationPartyId)); 
            } else {
                conditionList.add(EntityCondition.makeCondition("partyId", organizationPartyId));
            }

            if (fromDate != null) {
                conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate) );
            }
            EntityConditionList<EntityCondition> conditions = EntityCondition.makeCondition(conditionList);

            TransactionUtil.begin();
            EntityListIterator iterator =
                delegator.findListIteratorByCondition("Invoice", conditions, null, null, UtilMisc.toList(partyIdField), options);

            // compose the report by keeping each row of data in a report map keyed to partyId
            Map<String, Object> reportData = FastMap.<String, Object>newInstance();

            GenericValue invoice = null;
            while ((invoice = iterator.next()) != null) {

                Timestamp invoiceDate = invoice.getTimestamp("invoiceDate");
                if (invoiceDate == null) {
                    Debug.logWarning("No invoice date for invoice [" + invoice.get("invoiceId") + "], skipping it", MODULE);
                    continue;
                }

                String partyId = invoice.getString(partyIdField);

                Map<String, Object> reportLine = (Map<String, Object>) reportData.get(partyId);
                // if no row yet, create a new row and add party data for display
                if (reportLine == null) {
                    reportLine = FastMap.<String, Object>newInstance();
                    reportLine.put("partyId", partyId);
                    reportLine.put("partyName", PartyHelper.getPartyName(delegator, partyId, false));
                }

                // keep running total of invoice
                BigDecimal invoiceTotal = InvoiceWorker.getInvoiceTotal(invoice);

                // convert to the currency exchange rate at the time of the invoiceDate
                BigDecimal invoiceSum = UtilFinancial.determineUomConversionFactor(delegator, dispatcher, organizationPartyId, invoice.getString("currencyUomId"), invoice.getTimestamp("invoiceDate")).multiply(invoiceTotal);
                if (reportLine.get("invoiceSum") != null) {
                    invoiceSum = invoiceSum.add((BigDecimal) reportLine.get("invoiceSum")).setScale(2, BigDecimal.ROUND_HALF_UP);
                }
                reportLine.put("invoiceSum", invoiceSum);

                // compute DSO, number of days outstanding for invoice

                // if the invoice is PAID, then the paid date from the invoice is used as paid date
                // if there is no paidDate, then it is set to invoiceDate -- ie, DSO of 0, because in older versions of ofbiz
                // most orders are captured & paid when they are shipped and no paidDate was set
                Timestamp dsoDate = reportDate; 
                if ("INVOICE_PAID".equals(invoice.getString("statusId"))) {
                    dsoDate = (invoice.get("paidDate") != null ? invoice.getTimestamp("paidDate") : invoiceDate);
                }
                Calendar fromCal = UtilDate.toCalendar(invoiceDate, timeZone, locale);
                Calendar thruCal = UtilDate.toCalendar(dsoDate, timeZone, locale);
                BigDecimal dso = BigDecimal.valueOf((thruCal.getTimeInMillis() - fromCal.getTimeInMillis()) / (UtilDate.MS_IN_A_DAY));

                // keep running sum of DSO
                BigDecimal dsoSum = dso;
                if (reportLine.get("dsoSum") != null) {
                    dsoSum = dsoSum.add((BigDecimal) reportLine.get("dsoSum"));
                }
                reportLine.put("dsoSum", dsoSum);

                // keep running DSO*invoiceTotal sum
                BigDecimal dsoValueSum = dso.multiply(invoiceTotal);
                if (reportLine.get("dsoValueSum") != null) {
                    dsoValueSum = dsoValueSum.add((BigDecimal) reportLine.get("dsoValueSum"));
                }
                reportLine.put("dsoValueSum", dsoValueSum);

                // update number of invoices
                int numberOfInvoices = 1;
                if (reportLine.get("numberOfInvoices") != null) {
                    numberOfInvoices += ((Integer) reportLine.get("numberOfInvoices")).intValue();
                }
                reportLine.put("numberOfInvoices", numberOfInvoices);

                // update avg DSO
                reportLine.put("dsoAvg", dsoSum.divide(BigDecimal.valueOf(numberOfInvoices), 0, BigDecimal.ROUND_HALF_UP));

                // update weighted DSO
                reportLine.put("dsoWeighted", invoiceSum.signum() != 0 ? dsoValueSum.divide(invoiceSum, 0, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);

                reportData.put(partyId, reportLine);
            }
            iterator.close();
            TransactionUtil.commit();

            Collection<Object> report = reportData.values();
            request.setAttribute("jrDataSource", new JRMapCollectionDataSource(report));

            // go through report once more and compute totals row
            BigDecimal invoiceSum = BigDecimal.ZERO;
            BigDecimal dsoSum = BigDecimal.ZERO;
            BigDecimal dsoValueSum = BigDecimal.ZERO;
            int numberOfInvoices = 0;

            for (Object row : reportData.values()) {
                Map<String, Object> reportLine = (Map<String, Object>) row;
                invoiceSum = invoiceSum.add((BigDecimal) reportLine.get("invoiceSum"));
                dsoSum = dsoSum.add((BigDecimal) reportLine.get("dsoSum"));
                dsoValueSum = dsoValueSum.add((BigDecimal) reportLine.get("dsoValueSum"));
                numberOfInvoices += ((Integer) reportLine.get("numberOfInvoices")).intValue();
            }

            jrParameters.put("invoiceSum", invoiceSum);
            jrParameters.put("dsoSum", dsoSum);
            jrParameters.put("dsoValueSum", dsoValueSum);
            jrParameters.put("numberOfInvoices", new Integer(numberOfInvoices));
            if (numberOfInvoices > 0) {
                jrParameters.put("dsoAvg", dsoSum.divide(BigDecimal.valueOf(numberOfInvoices), 0, BigDecimal.ROUND_HALF_UP));
            };
            if (invoiceSum.compareTo(BigDecimal.ZERO) > 0) {
                jrParameters.put("dsoWeighted", dsoValueSum.divide(invoiceSum, 0, BigDecimal.ROUND_HALF_UP));
            }

            request.setAttribute("jrParameters", jrParameters);

        } catch (GenericEntityException e) {
            try {
                if (TransactionUtil.isTransactionInPlace()) {
                    TransactionUtil.rollback();
                }
            } catch (GenericTransactionException e1) {
                return UtilMessage.createAndLogEventError(request, e1, locale, MODULE);
            }
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }

        return reportType;
    }

    /**
     * Prepare data source and parameters for credit card settlements report.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the event response, either "pdf" or "xls" string to select report type, or "error".
     */
    public static String prepareCreditCardReport(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        TimeZone timeZone = UtilCommon.getTimeZone(request);
        Locale locale = UtilCommon.getLocale(request);
        Map<String, Object> jrParameters = FastMap.<String, Object>newInstance();

        String reportType = UtilCommon.getParameter(request, "type");

        try {

            String dateTimeFormat = UtilDateTime.getDateTimeFormat(locale);

            // get the from and thru date timestamps
            String fromDateString = UtilHttp.makeParamValueFromComposite(request, "fromDate", locale);
            String thruDateString = UtilHttp.makeParamValueFromComposite(request, "thruDate", locale);

            // don't do anything if dates invalid
            if (fromDateString == null || thruDateString == null) {
                return "error";
            }

            Timestamp fromDate = null;
            Timestamp thruDate = null;
            try {
                fromDate = UtilDateTime.stringToTimeStamp(fromDateString.trim(), dateTimeFormat, timeZone, locale);
                thruDate = UtilDateTime.stringToTimeStamp(thruDateString.trim(), dateTimeFormat, timeZone, locale);
            } catch (ParseException e) {
                return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
            }

            if (thruDate.before(fromDate)) {
                return UtilMessage.createAndLogEventError(request, "FinancialsError_FromDateAfterThruDate", locale, MODULE);
            }

            // fields to select and order by
            List<String> orderByForSum = UtilMisc.toList("currencyUomId"); // this is not really important, but helps speed up conversion
            List<String> orderByForDetail = UtilMisc.toList("transactionDate DESC");
            List<String> fieldsToSelectForSum = UtilMisc.toList("paymentMethodId", "currencyUomId", "amount", "effectiveDate");
            List<String> fieldsToSelectForDetail = null; // all fields

            // since these are all receipts, we need to constrain to Payment.partyIdTo = organizationPartyId
            String organizationPartyId = UtilCommon.getOrganizationPartyId(request);

            // conditions for detail report
            EntityConditionList<EntityCondition> commonConditions =
                EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
                        EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
                        EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate),
                        EntityCondition.makeCondition("transCodeEnumId", EnumerationConstants.PgtCode.PGT_CAPTURE),
                        EntityCondition.makeCondition("partyIdTo", organizationPartyId)
                ));

            // conditions for sum report
            EntityConditionList<EntityCondition> sumConditions =
                EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
                        EntityCondition.makeCondition("statusId", StatusItemConstants.PmntStatus.PMNT_RECEIVED),
                        EntityCondition.makeCondition("partyIdTo", organizationPartyId),
                        EntityCondition.makeCondition("paymentMethodTypeId", PaymentMethodTypeConstants.CREDIT_CARD),
                        EntityCondition.makeCondition("effectiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
                        EntityCondition.makeCondition("effectiveDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate)
                ));

            List<GenericValue> sumResults =
                delegator.findByCondition("Payment", sumConditions, fieldsToSelectForSum, orderByForSum);
            List<GenericValue> detailResults =
                delegator.findByCondition("CreditCardTrans", commonConditions, fieldsToSelectForDetail, orderByForDetail);

            // sum report keyed to credit card type
            Map<String, Map<String, Object>> sumReport = new TreeMap<String, Map<String, Object>>();
            String unknownType = UtilMessage.expandLabel("OpentapsUnknown", locale);
            for (GenericValue sum : sumResults) {
                GenericValue creditCard = sum.getRelatedOneCache("CreditCard");
                String creditCardType = (creditCard == null ? unknownType : creditCard.getString("cardType"));
                BigDecimal previousAmount = BigDecimal.ZERO;

                // get the last row keyed to the cardType, and its amount (which we will merge)
                Map<String, Object> reportLine = sumReport.get(creditCardType);
                if (reportLine == null) {
                    reportLine = FastMap.<String, Object>newInstance();
                    reportLine.putAll(sum.getAllFields());
                    reportLine.put("cardType", creditCardType);
                } else {
                    previousAmount = (BigDecimal) reportLine.get("amount");
                }

                // convert current results
                BigDecimal amount = sum.getBigDecimal("amount");
                if (amount == null) {
                    amount = BigDecimal.ZERO;
                }
                String currencyUomId = sum.getString("currencyUomId");
                Timestamp asOf = sum.getTimestamp("effectiveDate");
                BigDecimal amountConverted;
                amountConverted = amount.multiply(UtilFinancial.determineUomConversionFactor(delegator, dispatcher, organizationPartyId, currencyUomId, asOf));

                // store the merged results
                reportLine.put("amount", amountConverted.add(previousAmount));
                sumReport.put(creditCardType, reportLine);
            }

            // the detail report needs to use getRelatedOne in the ftl to get the CreditCard details, especially cardNumber since it cannot be decrypted in a view entity
            List<Map<String, Object>> details = FastList.<Map<String, Object>>newInstance();
            for (GenericValue detailValue : detailResults) {
                Map<String, Object> detailsLine = FastMap.<String, Object>newInstance();
                detailsLine.putAll(detailValue.getAllFields());
                if (UtilValidate.isNotEmpty(detailValue.get("paymentMethodId"))) {
                    GenericValue cc = detailValue.getRelatedOne("CreditCard");
                    if (cc != null) {
                        detailsLine.put("cardNumber", cc.get("cardNumber"));
                        detailsLine.put("cardType", cc.get("cardType"));
                        detailsLine.put("expireDate", cc.get("expireDate"));
                    }
                }

                details.add(detailsLine);
            }
            request.setAttribute("jrDataSource", new JRMapCollectionDataSource(details));

            // store the cardType-sorted sum report results 
            jrParameters.put("summaryReport", new JRMapCollectionDataSource(sumReport.values()));
            jrParameters.put("fromDate", fromDate);
            jrParameters.put("thruDate", thruDate);
            jrParameters.put("organizationPartyId", organizationPartyId);
            jrParameters.put("organizationName", PartyHelper.getPartyName(delegator, organizationPartyId, false));
            request.setAttribute("jrParameters", jrParameters);

        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }

        return reportType;
    }

    /**
     * Prepare data source and parameters for payment receipts detail report.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the event response, either "pdf" or "xls" string to select report type, or "error".
     */
    public static String preparePaymentReceiptsDetailReport(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        TimeZone timeZone = UtilCommon.getTimeZone(request);
        Locale locale = UtilCommon.getLocale(request);
        Map<String, Object> jrParameters = FastMap.<String, Object>newInstance();

        String reportType = UtilCommon.getParameter(request, "type");

        try {

            String dateTimeFormat = UtilDateTime.getDateTimeFormat(locale);

            // get the from and thru date timestamps
            String fromDateString = UtilHttp.makeParamValueFromComposite(request, "fromDate", locale);
            String thruDateString = UtilHttp.makeParamValueFromComposite(request, "thruDate", locale);

            // don't do anything if dates invalid
            if (fromDateString == null || thruDateString == null) {
                return UtilMessage.createAndLogEventError(request, "Either From Date or Thru Date is empty.", MODULE);
            }
            Timestamp fromDate = null;
            Timestamp thruDate = null;
            try {
                fromDate = UtilDateTime.stringToTimeStamp(fromDateString.trim(), dateTimeFormat, timeZone, locale);
                thruDate = UtilDateTime.stringToTimeStamp(thruDateString.trim(), dateTimeFormat, timeZone, locale);
            } catch (ParseException e) {
                return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
            }

            if (thruDate.before(fromDate)) {
                return UtilMessage.createAndLogEventError(request, "FinancialsError_FromDateAfterThruDate", locale, MODULE);
            }

            // the glAccountId to select for
            String glAccountId = UtilCommon.getParameter(request, "glAccountId");
            if (UtilValidate.isEmpty(glAccountId)) {
                return UtilMessage.createAndLogEventError(request, "Please specify a GL account.", MODULE);
            }

            GenericValue glAccount = delegator.findByPrimaryKey("GlAccount", UtilMisc.toMap("glAccountId", glAccountId));
            String glAccountName = String.format("%1$s: %2$s", glAccountId, glAccount.getString("accountName"));
            
            boolean showInvoiceLevelDetail = 
                "Y".equals(UtilCommon.getParameter(request, "showInvoiceLevelDetail")) ? true : false;

            // Since these are receipts, the partyIdTo of the payment should be the organization
            String organizationPartyId = UtilCommon.getOrganizationPartyId(request);

            BigDecimal totalCash = BigDecimal.ZERO;
            BigDecimal total = BigDecimal.ZERO;
            
            List<EntityCondition> conditionList = UtilMisc.<EntityCondition>toList(
                    EntityCondition.makeCondition("glAccountId", glAccountId),
                    EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
                    EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate),
                    EntityCondition.makeCondition("debitCreditFlag", "D"), // note that only ATE debits must be selected
                    EntityCondition.makeCondition("partyIdTo", organizationPartyId)
            );

            GenericValue paymentMethodType = null;
            String paymentMethodTypeId = UtilCommon.getParameter(request, "paymentMethodTypeId");
            if( UtilValidate.isNotEmpty(paymentMethodTypeId)) {
                conditionList.add(EntityCondition.makeCondition("paymentMethodTypeId", paymentMethodTypeId));
                paymentMethodType = delegator.findByPrimaryKey("PaymentMethodType", UtilMisc.toMap("paymentMethodTypeId", paymentMethodTypeId));
            }

            // determine whether the invoice level needs to be shown.  A different view entity is used if the invoice level detail is required
            List<String> fieldsToSelect =
                UtilMisc.<String>toList("transactionDate", "paymentId", "paymentMethodTypeId", "partyIdFrom", "amount", "currencyUomId");
            fieldsToSelect.add("paymentRefNum");
            String entityName = "PaymentReceiptsDetail";
            if (showInvoiceLevelDetail) {
                fieldsToSelect.add("invoiceId");
                fieldsToSelect.add("amountApplied");
                entityName = "PaymentReceiptsDetailWithApplication";
            }

            List<GenericValue> PaymentReceiptsDetail = 
                delegator.findByCondition(entityName, EntityCondition.makeCondition(conditionList), fieldsToSelect, UtilMisc.toList("transactionDate DESC"));
            // add extra row data
            List<Map<String, Object>> reportData = FastList.<Map<String, Object>>newInstance();
            for (GenericValue value : PaymentReceiptsDetail) {

                BigDecimal conversion = BigDecimal.ONE;
                String currencyUomId = value.getString("currencyUomId");
                if (UtilValidate.isNotEmpty(currencyUomId)) {
                    conversion = UtilFinancial.determineUomConversionFactor(delegator, dispatcher, organizationPartyId, currencyUomId);
                }

                Map<String, Object> reportLine = FastMap.<String, Object>newInstance();
                reportLine.putAll(value.getAllFields());

                String payerId = value.getString("partyIdFrom");
                reportLine.put("partyId", payerId);
                reportLine.put("partyName", PartyHelper.getPartyName(delegator, payerId, false));

                reportLine.put("isCash", PaymentMethodTypeConstants.CASH.equals(value.getString("paymentMethodTypeId")) ? Boolean.TRUE : Boolean.FALSE);

                BigDecimal amount = null;
                if (showInvoiceLevelDetail) {
                    amount = value.getBigDecimal("amountApplied");
                    if (amount == null) {
                        amount = value.getBigDecimal("amount");
                    }
                } else {
                    amount = value.getBigDecimal("amount");
                }
                BigDecimal amountConverted = amount.multiply(conversion);
                reportLine.put("amount", amount);

                // adjust totals
                if (PaymentMethodTypeConstants.CASH.equals(paymentMethodTypeId)) {
                    totalCash = totalCash.add(amountConverted);
                } else {
                    total = total.add(amountConverted);
                }

                reportData.add(reportLine);
            }

            request.setAttribute("jrDataSource", new JRMapCollectionDataSource(reportData));

            jrParameters.put("fromDate", fromDate);
            jrParameters.put("thruDate", thruDate);
            jrParameters.put("glAccountId", glAccountId);
            jrParameters.put("glAccount", glAccountName);
            jrParameters.put("organizationPartyId", organizationPartyId);
            jrParameters.put("organizationName", PartyHelper.getPartyName(delegator, organizationPartyId, false));
            if (paymentMethodType != null) {
                jrParameters.put("paymentMethod", paymentMethodType.getString("description"));
            }
            jrParameters.put("totalCashAmount", totalCash);
            jrParameters.put("totalNonCashAmount", total);
            jrParameters.put("showInvoiceLevelDetail", showInvoiceLevelDetail);
            request.setAttribute("jrParameters", jrParameters);

        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }

        return reportType;
    }

    /**
     * Prepare data source and parameters for transaction summary report.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the event response, either "pdf" or "xls" string to select report type, or "error".
     */
    public static String prepareTransactionSummaryReport(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        TimeZone timeZone = UtilCommon.getTimeZone(request);
        Locale locale = UtilCommon.getLocale(request);
        Map<String, Object> jrParameters = FastMap.<String, Object>newInstance();

        String reportType = UtilCommon.getParameter(request, "type");

        String dateTimeFormat = UtilDateTime.getDateTimeFormat(locale);
        String organizationPartyId = UtilCommon.getOrganizationPartyId(request);

        try {

            // get the from and thru date timestamps
            String fromDateString = UtilHttp.makeParamValueFromComposite(request, "fromDate", locale);
            String thruDateString = UtilHttp.makeParamValueFromComposite(request, "thruDate", locale);

            // don't do anything if dates invalid
            if (fromDateString == null || thruDateString == null) {
                return UtilMessage.createAndLogEventError(request, "Both From Date and Thru Date are required", MODULE);
            }

            Timestamp fromDate = null;
            Timestamp thruDate = null;
            try {
                fromDate = UtilDateTime.stringToTimeStamp(fromDateString.trim(), dateTimeFormat, timeZone, locale);
            } catch (ParseException e) {
                UtilMessage.addFieldError(request, "fromDate", "FinancialsError_IllegalDateFieldFormat", UtilMisc.toMap("date", fromDateString) );
            }
            try {
                thruDate = UtilDateTime.stringToTimeStamp(thruDateString.trim(), dateTimeFormat, timeZone, locale);
            } catch (ParseException e) {
                UtilMessage.addFieldError(request, "thruDate", "FinancialsError_IllegalDateFieldFormat", UtilMisc.toMap("date", thruDateString) );
            }
            if (fromDate != null) {
                jrParameters.put("fromDate", fromDate);
            }
            if (thruDate != null) {
                jrParameters.put("thruDate", thruDate);
            }
            if (fromDate == null || thruDate == null) {
                return UtilMessage.createAndLogEventError(request, "Both From Date and Thru Date are required", MODULE);
            }
            if (thruDate.before(fromDate)) {
                return UtilMessage.createAndLogEventError(request, "FinancialsError_FromDateAfterThruDate", locale, MODULE);
            }

            String glFiscalTypeId = UtilCommon.getParameter(request, "glFiscalTypeId");
            if (glFiscalTypeId == null) {
                glFiscalTypeId = "ACTUAL";
            }
            jrParameters.put("glFiscalTypeId", glFiscalTypeId);

            String isPosted = UtilCommon.getParameter(request, "isPosted");
            if (isPosted == null) {
                isPosted = "Y";
            }
    
            List<EntityCondition> commonConditionLists = UtilMisc.<EntityCondition>toList(
                    EntityCondition.makeCondition("organizationPartyId", organizationPartyId),
                    EntityCondition.makeCondition("glFiscalTypeId", glFiscalTypeId),
                    EntityCondition.makeCondition("isPosted", isPosted),
                    EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
                    EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate)
            );
            commonConditionLists.addAll(UtilAccountingTags.buildTagConditions(organizationPartyId, UtilAccountingTags.FINANCIALS_REPORTS_TAG, delegator, request));

            EntityConditionList<EntityCondition> commonConditions = EntityCondition.makeCondition(commonConditionLists);

            EntityConditionList<EntityCondition> creditConditions =
                EntityCondition.makeCondition(UtilMisc.toList(commonConditions, EntityCondition.makeCondition("debitCreditFlag", "C")), EntityOperator.AND);
            EntityConditionList<EntityCondition> debitConditions =
                EntityCondition.makeCondition(UtilMisc.toList(commonConditions, EntityCondition.makeCondition("debitCreditFlag", "D")), EntityOperator.AND);

            List<String> fieldsToSelect =
                UtilMisc.<String>toList("glAccountId", "accountCode", "accountName", "glAccountClassId", "amount");
            List<String> orderBy = UtilMisc.<String>toList("glAccountId");

            // find the debits and credits, summed up.  Note that with the introduction of accounting tags, there may still be multiple rows for each gl Account now.  
            List<GenericValue> credits =
                delegator.findByCondition("AcctgTransEntryAccountSum", creditConditions, fieldsToSelect, orderBy);
            List<GenericValue> debits =
                delegator.findByCondition("AcctgTransEntryAccountSum", debitConditions, fieldsToSelect, orderBy);

            // go through credits and build a report row keyed to glAccountId with the total credit amounts 
            Map<String, Map<String, Object>> reportMap = new TreeMap<String, Map<String, Object>>();
            for (GenericValue credit : credits) {
                // get the row for the GL account already in this Map, or create it if it's not there
                Map<String, Object> row = (Map<String, Object>) reportMap.remove(credit.get("glAccountId")); 
                if (row == null) {
                    row = UtilMisc.<String, Object>toMap(
                            "glAccountId", credit.get("glAccountId"),
                            "accountCode", credit.get("accountCode"),
                            "accountName", credit.get("accountName")
                    );
                }

                // calculate the creditSum for this row, adding it to the existing creditSum for the GL account if it's there 
                BigDecimal creditSum = null;
                if (row.get("creditSum") == null) {
                    creditSum = credit.getBigDecimal("amount");
                } else {
                    // credit.amount should never be null, but let's be careful
                    creditSum = ((BigDecimal) row.get("creditSum"));
                    if (credit.get("amount") != null) {
                        creditSum = creditSum.add(credit.getBigDecimal("amount"));
                    }
                }

                // skip this row if credit sum is null or zero  
                if (creditSum == null) {
                    continue;
                }
                creditSum = creditSum.setScale(UtilFinancial.decimals, UtilFinancial.rounding);
                if (creditSum.signum() == 0) {
                    continue;
                }
                row.put("creditSum", creditSum);

                reportMap.put(credit.getString("glAccountId"), row);
            }

            // go through debits and add the debit data to each row, or build new rows if no credits were reported for an account
            for (GenericValue debit : debits) {
                // it's possible that there were no credits, so a row for the GL account does not exist from above.  If so, add it.
                Map<String, Object> row = (Map<String, Object>) reportMap.remove(debit.get("glAccountId")); // note that row is removed and must be put back
                if (row == null) {
                    row = UtilMisc.toMap("glAccountId", debit.get("glAccountId"), "accountCode", debit.get("accountCode"), "accountName", debit.get("accountName"));
                }

                // similar strategy to creditSum from above
                BigDecimal debitSum = null;
                if (row.get("debitSum") == null) {
                    debitSum = debit.getBigDecimal("amount");
                } else {
                    debitSum = ((BigDecimal) row.get("debitSum"));
                    if (debit.get("amount") != null) {
                        debitSum = debitSum.add(debit.getBigDecimal("amount"));
                    }
                }

                if (debitSum == null) {
                    debitSum = BigDecimal.ZERO;
                }
                debitSum = debitSum.setScale(UtilFinancial.decimals, UtilFinancial.rounding);
                row.put("debitSum", debitSum);

                BigDecimal creditSum = (BigDecimal) row.get("creditSum");
                if (creditSum == null) {
                    creditSum = BigDecimal.ZERO;
                }

                // if both credit and debit sums are ZERO, then continue to next row (this row is removed)
                if (debitSum.signum() == 0 && creditSum.signum() == 0) {
                    continue;
                }

                reportMap.put(debit.getString("glAccountId"), row);
            }

            // calculate net debit and credit
            for (Map<String, Object> reportLine : reportMap.values()) {
                BigDecimal creditSum = (BigDecimal) reportLine.get("creditSum");
                if (creditSum == null) {
                    creditSum = BigDecimal.ZERO;
                }
                BigDecimal debitSum = (BigDecimal) reportLine.get("debitSum");
                if (debitSum == null) {
                    debitSum = BigDecimal.ZERO;
                }
                if (debitSum.compareTo(creditSum) > 0) {
                    reportLine.put("netDebit", debitSum.subtract(creditSum));
                } else {
                    reportLine.put("netCredit", creditSum.subtract(debitSum));
                }
            }

            // put the key-sorted values into the context as our report data
            request.setAttribute("jrDataSource", new JRMapCollectionDataSource(reportMap.values()));

            jrParameters.put("organizationPartyId", organizationPartyId);
            jrParameters.put("organizationName", PartyHelper.getPartyName(delegator, organizationPartyId, false));
            jrParameters.put("accountingTags", UtilAccountingTags.formatTagsAsString(request, UtilAccountingTags.FINANCIALS_REPORTS_TAG, delegator));
            request.setAttribute("jrParameters", jrParameters);

        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (RepositoryException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }

        return reportType;
    }

    /**
     * Call a service to update GlAccountTransEntryFact entity. This intermediate data are used by budgeting reports.
     *
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the event response <code>String</code> value
     */
    public static String createGlAccountTransEntryFacts(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String organizationPartyId = UtilCommon.getOrganizationPartyId(request);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");

        Map<String, Object> context = FastMap.<String, Object>newInstance();
        context.put("organizationPartyId", organizationPartyId);
        context.put("userLogin", userLogin);
        try {
            dispatcher.runSync("financials.collectEncumbranceAndTransEntryFacts", context);
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogEventError(request, e, UtilHttp.getLocale(request), MODULE);
        }

        return "success";
    }

    /**
     * <p>Load data to TaxInvoiceItemFact entity.</p>
     *
     * <p>TODO: It would be great to rework this service and use Hibernate API. This might make
     * code more understandable and independent of underlying database.<br>
     * Make sense also rid of sales invoice item fact Kettle transformation and fill out both fact tables
     * in one procedure as they very similar.</p>
     *
     * @param dctx a <code>DispatchContext</code> instance
     * @param context the service context <code>Map</code>
     * @return the service response <code>Map</code>
     */
    public static Map<String, Object> loadTaxInvoiceItemFact(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        Map<String, Object> results = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");
        if (locale == null) {
            locale = Locale.getDefault();
        }
        TimeZone timeZone = (TimeZone) context.get("timeZone");
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }

        // collection of unique invoice id
        Set<String> uniqueInvoices = FastSet.<String>newInstance();
        Long sequenceId = 0L;

        try {

            // find all invoice items (as join Invoice and InvoiceItem) from involved invoices
            // and which aren't sales tax, promotion or shipping charges
            DynamicViewEntity salesInvoiceItems = new DynamicViewEntity();
            salesInvoiceItems.addMemberEntity("I", "Invoice");
            salesInvoiceItems.addMemberEntity("II", "InvoiceItem");
            salesInvoiceItems.addViewLink("I", "II", false, ModelKeyMap.makeKeyMapList("invoiceId"));
            salesInvoiceItems.addAlias("I", "invoiceDate");
            salesInvoiceItems.addAlias("I", "currencyUomId");
            salesInvoiceItems.addAlias("I", "invoiceTypeId");
            salesInvoiceItems.addAlias("I", "statusId");
            salesInvoiceItems.addAlias("I", "partyIdFrom");
            salesInvoiceItems.addAlias("II", "invoiceId");
            salesInvoiceItems.addAlias("II", "invoiceItemSeqId");
            salesInvoiceItems.addAlias("II", "invoiceItemTypeId");
            salesInvoiceItems.addAlias("II", "quantity");
            salesInvoiceItems.addAlias("II", "amount");

            List<String> selectList = UtilMisc.toList("invoiceId", "invoiceItemSeqId", "invoiceDate", "currencyUomId", "quantity", "amount");
            selectList.add("partyIdFrom");

            DateFormat dayOfMonthFmt = new SimpleDateFormat("dd");
            DateFormat monthOfYearFmt = new SimpleDateFormat("MM");
            DateFormat yearNumberFmt = new SimpleDateFormat("yyyy");

            EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("invoiceTypeId", "SALES_INVOICE"),
                    EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, Arrays.asList("INVOICE_IN_PROCESS", "INVOICE_CANCELLED", "INVOICE_VOIDED", "INVOICE_WRITEOFF")),
                    EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN, Arrays.asList("ITM_SALES_TAX", "INV_SALES_TAX", "ITM_PROMOTION_ADJ", "ITM_SHIPPING_CHARGES"))
            );
            EntityListIterator itemsIterator = delegator.findListIteratorByCondition(salesInvoiceItems, conditions, null, selectList, UtilMisc.toList("invoiceId", "invoiceItemSeqId"), null);
            GenericValue invoiceItem = null;

            // iterate over found items and build fact item for each
            while ((invoiceItem = itemsIterator.next()) != null) {
                GenericValue taxInvItemFact = delegator.makeValue("TaxInvoiceItemFact");

                // set item ids
                String invoiceId = invoiceItem.getString("invoiceId");
                uniqueInvoices.add(invoiceId);
                String invoiceItemSeqId = invoiceItem.getString("invoiceItemSeqId");
                taxInvItemFact.put("invoiceId", invoiceId);
                taxInvItemFact.put("invoiceItemSeqId", invoiceItemSeqId);

                // store quantity and amount in variables
                BigDecimal quantity = invoiceItem.getBigDecimal("quantity");
                if (quantity == null) {
                    quantity = BigDecimal.ONE;
                }

                BigDecimal amount = invoiceItem.getBigDecimal("amount");
                if (amount == null) {
                    amount = BigDecimal.ZERO;
                }

                // set gross amount
                BigDecimal grossAmount = quantity.multiply(amount);
                taxInvItemFact.set("grossAmount", grossAmount != null ? grossAmount : null);

                // set total promotions amount
                BigDecimal totalPromotions = getTotalPromoAmount(invoiceId, invoiceItemSeqId, delegator);
                taxInvItemFact.set("discounts", totalPromotions != null ? totalPromotions : null);

                // set total refunds
                BigDecimal totalRefunds = getTotalRefundAmount(invoiceId, invoiceItemSeqId, delegator);
                taxInvItemFact.set("refunds", totalRefunds != null ? totalRefunds : null);

                // set net amount
                // net amount is total sales minus returns and plus adjustments
                taxInvItemFact.set("netAmount", grossAmount.subtract(totalRefunds).add(totalPromotions));

                // set tax due amount
                List<Map<String, Object>> taxes = getTaxDueAmount(invoiceId, invoiceItemSeqId, delegator);

                // lookup and set date dimension id
                // TODO: date dimension lookup deserves a separate method accepting only argument of Timestamp
                Timestamp invoiceDate = invoiceItem.getTimestamp("invoiceDate");
                String dayOfMonth = dayOfMonthFmt.format(invoiceDate);
                String monthOfYear = monthOfYearFmt.format(invoiceDate);
                String yearNumber = yearNumberFmt.format(invoiceDate);

                EntityCondition dateDimConditions = EntityCondition.makeCondition(EntityOperator.AND,
                        EntityCondition.makeCondition("dayOfMonth", dayOfMonth),
                        EntityCondition.makeCondition("monthOfYear", monthOfYear),
                        EntityCondition.makeCondition("yearNumber", yearNumber));
                taxInvItemFact.set("dateDimId", UtilEtl.lookupDimension("DateDim", "dateDimId", dateDimConditions, delegator));

                // lookup and set store dimension id
                taxInvItemFact.set("storeDimId", lookupStoreDim(invoiceId, invoiceItemSeqId, delegator));

                // lookup and set currency dimension id
                taxInvItemFact.set("currencyDimId", UtilEtl.lookupDimension("CurrencyDim", "currencyDimId", EntityCondition.makeCondition("uomId", EntityOperator.EQUALS, invoiceItem.getString("currencyUomId")), delegator)
                );

                // lookup and set organization dimension id
                taxInvItemFact.set("organizationDimId", UtilEtl.lookupDimension("OrganizationDim", "organizationDimId", EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, invoiceItem.getString("partyIdFrom")), delegator)
                );

                // store collected records
                if (UtilValidate.isNotEmpty(taxes)) {
                    for (Map<String, Object> taxInfo : taxes) {
                        // add a record for each tax authority party & geo
                        EntityCondition taxAuthCondList = EntityCondition.makeCondition(EntityOperator.AND,
                                        EntityCondition.makeCondition("taxAuthPartyId", EntityOperator.EQUALS, taxInfo.get("taxAuthPartyId")),
                                        EntityCondition.makeCondition("taxAuthGeoId", EntityOperator.EQUALS, taxInfo.get("taxAuthGeoId")));
                        taxInvItemFact.set("taxAuthorityDimId", UtilEtl.lookupDimension("TaxAuthorityDim", "taxAuthorityDimId", taxAuthCondList, delegator));
                        BigDecimal taxable = (BigDecimal) taxInfo.get("taxable");
                        if (taxable != null) {
                            taxable = taxable.subtract(totalRefunds);
                        }
                        taxInvItemFact.set("taxable", taxable);
                        BigDecimal taxDue = (BigDecimal) taxInfo.get("taxDue");
                        taxInvItemFact.set("taxDue", (taxDue != null && taxable.compareTo(BigDecimal.ZERO) > 0) ? taxDue : BigDecimal.ZERO);
                        sequenceId++;
                        taxInvItemFact.set("taxInvItemFactId", sequenceId);
                        taxInvItemFact.create();
                    }
                } else {
                    taxInvItemFact.set("taxAuthorityDimId", Long.valueOf(0));
                    taxInvItemFact.set("taxDue", BigDecimal.ZERO);
                    taxInvItemFact.set("taxable", BigDecimal.ZERO);
                    sequenceId++;
                    taxInvItemFact.set("taxInvItemFactId", sequenceId);
                    taxInvItemFact.create();
                }

            }

            itemsIterator.close();

            // following code retrieve all sales tax invoice items that have no parent invoice item
            // hence can't be linked to any product invoice item and described as separate fact row.
            DynamicViewEntity dv = new DynamicViewEntity();
            dv.addMemberEntity("I", "Invoice");
            dv.addMemberEntity("II", "InvoiceItem");
            dv.addViewLink("I", "II", false, ModelKeyMap.makeKeyMapList("invoiceId"));
            dv.addAlias("I", "invoiceDate");
            dv.addAlias("I", "currencyUomId");
            dv.addAlias("I", "invoiceId");
            dv.addAlias("I", "partyIdFrom");
            dv.addAlias("II", "invoiceItemSeqId");
            dv.addAlias("II", "invoiceItemTypeId");
            dv.addAlias("II", "parentInvoiceId");
            dv.addAlias("II", "parentInvoiceItemSeqId");
            dv.addAlias("II", "quantity");
            dv.addAlias("II", "amount");
            dv.addAlias("II", "taxAuthPartyId");
            dv.addAlias("II", "taxAuthGeoId");
            ComplexAlias totalAlias = new ComplexAlias("*");
            totalAlias.addComplexAliasMember(new ComplexAliasField("II", "quantity", "1.0", null));
            totalAlias.addComplexAliasMember(new ComplexAliasField("II", "amount", "0.0", null));
            dv.addAlias("II", "totalAmount", null, null, null, null, null, totalAlias);

            selectList = UtilMisc.toList("invoiceId", "invoiceItemSeqId", "totalAmount", "taxAuthPartyId", "taxAuthGeoId", "currencyUomId");
            selectList.add("partyIdFrom");
            selectList.add("invoiceDate");

            EntityCondition invoiceLevelTaxConditions = EntityCondition.makeCondition(EntityOperator.AND,
                            EntityCondition.makeCondition("invoiceItemTypeId", "ITM_SALES_TAX"),
                            EntityCondition.makeCondition("invoiceId", EntityOperator.IN, uniqueInvoices),
                            EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, null),
                            EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS, null));

            EntityListIterator iter2 = delegator.findListIteratorByCondition(dv, invoiceLevelTaxConditions, null, selectList, null, null);
            GenericValue tax = null;

            // iterate over found records and create fact row for each
            // every such fact looks like ordinary one but has only valued amount taxDue, all other equals to zero
            while ((tax = iter2.next()) != null) {
                BigDecimal amount = tax.getBigDecimal("totalAmount");
                if (amount != null) {

                    // date lookup conditions
                    Timestamp invoiceDate = tax.getTimestamp("invoiceDate");
                    String dayOfMonth = dayOfMonthFmt.format(invoiceDate);
                    String monthOfYear = monthOfYearFmt.format(invoiceDate);
                    String yearNumber = yearNumberFmt.format(invoiceDate);
                    EntityCondition dateDimConditions = EntityCondition.makeCondition(EntityOperator.AND,
                            EntityCondition.makeCondition("dayOfMonth", dayOfMonth),
                            EntityCondition.makeCondition("monthOfYear", monthOfYear),
                            EntityCondition.makeCondition("yearNumber", yearNumber));

                    // tax authority lookup conditions
                    EntityCondition taxAuthCondList = EntityCondition.makeCondition(EntityOperator.AND,
                            EntityCondition.makeCondition("taxAuthPartyId", tax.get("taxAuthPartyId")),
                            EntityCondition.makeCondition("taxAuthGeoId", tax.get("taxAuthGeoId")));

                    GenericValue taxInvItemFact = delegator.makeValue("TaxInvoiceItemFact");

                    taxInvItemFact.set("dateDimId", UtilEtl.lookupDimension("DateDim", "dateDimId", dateDimConditions, delegator));
                    taxInvItemFact.set("storeDimId", lookupStoreDim(tax.getString("invoiceId"), tax.getString("invoiceItemSeqId"), delegator));
                    taxInvItemFact.set("taxAuthorityDimId", UtilEtl.lookupDimension("TaxAuthorityDim", "taxAuthorityDimId", taxAuthCondList, delegator));
                    taxInvItemFact.set("currencyDimId", UtilEtl.lookupDimension("CurrencyDim", "currencyDimId", EntityCondition.makeCondition("uomId", EntityOperator.EQUALS, tax.getString("currencyUomId")), delegator));
                    taxInvItemFact.set("organizationDimId", UtilEtl.lookupDimension("OrganizationDim", "organizationDimId", EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, tax.getString("partyIdFrom")), delegator));
                    taxInvItemFact.set("invoiceId", tax.getString("invoiceId"));
                    taxInvItemFact.set("invoiceItemSeqId", tax.getString("invoiceItemSeqId"));
                    taxInvItemFact.set("grossAmount", BigDecimal.ZERO);
                    taxInvItemFact.set("discounts", BigDecimal.ZERO);
                    taxInvItemFact.set("refunds", BigDecimal.ZERO);
                    taxInvItemFact.set("netAmount", BigDecimal.ZERO);
                    taxInvItemFact.set("taxable", BigDecimal.ZERO);
                    BigDecimal totalAmount = tax.getBigDecimal("totalAmount");
                    taxInvItemFact.set("taxDue", totalAmount != null ? totalAmount : null);
                    sequenceId++;
                    taxInvItemFact.set("taxInvItemFactId", sequenceId);

                    taxInvItemFact.create();
                }
            }
            iter2.close();

        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, MODULE);
        }

        return results;
    }

    /**
     * <p>Collect sales taxes on given sales invoice item.<br>
     * Since a few tax items to different authorities are possible this method returns records grouped
     * by tax authority party and geographical unit.</p>
     *
     * @param invoiceId sales invoice id
     * @param invoiceItemSeqId sales invoice item id
     * @param delegator <code>Delegator</code> instance
     * @return
     * Each record is <code>Map</code> with following members:<br>
     * <b>taxAuthPartyId</b> : tax authority party<br>
     * <b>taxAuthGeoId</b> : tax authority geographical unit<br>
     * <b>taxDue</b> : sales tax amount under the taxing authority<br>
     * <b>taxable</b> : taxable amount of specified invoice item
     * @throws GenericEntityException
     */
    private static List<Map<String, Object>> getTaxDueAmount(String invoiceId, String invoiceItemSeqId, Delegator delegator) throws GenericEntityException {
        List<Map<String, Object>> taxes = FastList.newInstance();

        // find sales tax invoice items which have given invoice item as parent
        // and calculate product of quantity and amount for each record
        DynamicViewEntity dv = new DynamicViewEntity();
        ComplexAlias a = new ComplexAlias("*");
        a.addComplexAliasMember(new ComplexAliasField("II", "quantity", "1.0", null));
        a.addComplexAliasMember(new ComplexAliasField("II", "amount", "0.0", null));
        dv.addMemberEntity("II", "InvoiceItem");
        dv.addAlias("II", "taxAuthPartyId", null, null, null, Boolean.TRUE, null);
        dv.addAlias("II", "taxAuthGeoId", null, null, null, Boolean.TRUE, null);
        dv.addAlias("II", "parentInvoiceId");
        dv.addAlias("II", "parentInvoiceItemSeqId");
        dv.addAlias("II", "quantity");
        dv.addAlias("II", "amount");
        dv.addAlias("II", "totalTaxDue", null, null, null, null, "sum", a);
        dv.addAlias("II", "invoiceId", null, null, null, Boolean.TRUE, null);
        dv.addAlias("II", "invoiceItemSeqId", null, null, null, Boolean.TRUE, null);
        dv.addAlias("II", "invoiceItemTypeId");

        EntityCondition conditionList = EntityCondition.makeCondition(EntityOperator.AND,
                        EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "ITM_SALES_TAX"),
                        EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, invoiceId),
                        EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS, invoiceItemSeqId));

        List<String> selectList = UtilMisc.toList("totalTaxDue", "taxAuthPartyId", "taxAuthGeoId");

        // retrieve complete list because only small number of sales tax items might be related to an invoice item
        EntityListIterator iter = delegator.findListIteratorByCondition(dv, conditionList, null, selectList, null, null);
        List<GenericValue> taxItems = iter.getCompleteList();
        iter.close();

        for (GenericValue taxItem : taxItems) {
            BigDecimal taxDue = taxItem.getBigDecimal("totalTaxDue");
            BigDecimal taxAdjAmount = BigDecimal.ZERO;

            String taxAuthPartyId = taxItem.getString("taxAuthPartyId");
            String taxAuthGeoId = taxItem.getString("taxAuthGeoId");

            // calculate taxable amount for current invoiceId, invoiceItemSeqId and authority data
            BigDecimal taxable = BigDecimal.ZERO;
            GenericValue originalInvoiceItem = delegator.findByPrimaryKey("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", invoiceItemSeqId));
            if (originalInvoiceItem != null) {
                BigDecimal taxableQuantity = originalInvoiceItem.getBigDecimal("quantity");
                BigDecimal taxableAmount = originalInvoiceItem.getBigDecimal("amount");
                taxable = (taxableQuantity == null ? BigDecimal.ONE : taxableQuantity).multiply((taxableAmount == null ? BigDecimal.ZERO : taxableAmount));
            }

            // track relation to order and further to return invoice
            // OrderItemBilling -> ReturnItem -> ReturnItemBilling -> InvoiceItem
            List<GenericValue> orderItemBillings = delegator.findByAnd("OrderItemBilling", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", invoiceItemSeqId));
            for (GenericValue orderItemBilling : orderItemBillings) {
                List<GenericValue> returnItems = delegator.findByAnd("ReturnItem", UtilMisc.toMap("orderId", orderItemBilling.getString("orderId"), "orderItemSeqId", orderItemBilling.getString("orderItemSeqId")));
                for (GenericValue returnItem : returnItems) {
                    List<GenericValue> returnBillings = returnItem.getRelated("ReturnItemBilling");
                    for (GenericValue returnItemBilling : returnBillings) {
                        String ribInvoiceId = returnItemBilling.getString("invoiceId");
                        String ribInvoiceItemSeqId = returnItemBilling.getString("invoiceItemSeqId");

                        // retrieve return invoice items as join of Invoice and InvoiceItem
                        // and calculate product of quantity and amount for each record
                        DynamicViewEntity rdv = new DynamicViewEntity();
                        rdv.addMemberEntity("RI", "Invoice");
                        rdv.addMemberEntity("RII", "InvoiceItem");
                        rdv.addAlias("RI", "invoiceId");
                        rdv.addAlias("RI", "invoiceTypeId");
                        rdv.addAlias("RII", "parentInvoiceId");
                        rdv.addAlias("RII", "parentInvoiceItemSeqId");
                        rdv.addAlias("RII", "invoiceItemTypeId");
                        rdv.addAlias("RII", "taxAuthPartyId");
                        rdv.addAlias("RII", "taxAuthGeoId");
                        rdv.addAlias("RII", "quantity");
                        rdv.addAlias("RII", "amount");
                        ComplexAlias r = new ComplexAlias("*");
                        r.addComplexAliasMember(new ComplexAliasField("RII", "quantity", "1.0", null));
                        r.addComplexAliasMember(new ComplexAliasField("RII", "amount", "0.0", null));
                        rdv.addAlias("RII", "totalTaxRefundAdj", null, null, null, null, "sum", r);

                        EntityCondition conditionList1 = EntityCondition.makeCondition(EntityOperator.AND,
                                        EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, ribInvoiceId),
                                        EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "CUST_RTN_INVOICE"),
                                        EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, ribInvoiceId),
                                        EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS, ribInvoiceItemSeqId),
                                        EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "CRT_SALES_TAX_ADJ"),
                                        EntityCondition.makeCondition("taxAuthPartyId", EntityOperator.EQUALS, taxAuthPartyId),
                                        EntityCondition.makeCondition("taxAuthGeoId", EntityOperator.EQUALS, taxAuthGeoId));

                        EntityListIterator iter1 = delegator.findListIteratorByCondition(rdv, conditionList1, null, Arrays.asList("totalTaxRefundAdj"), null, null);
                        List<GenericValue> taxAdjs = iter1.getCompleteList();
                        iter1.close();

                        // add up sales tax adjustments
                        for (GenericValue taxAdj : taxAdjs) {
                            BigDecimal totalTaxRefundAdj = taxAdj.getBigDecimal("totalTaxRefundAdj");
                            if (totalTaxRefundAdj != null) {
                                taxAdjAmount = taxAdjAmount.add(totalTaxRefundAdj);
                            }
                        }
                    }
                }
            }

            // tad due w/o tax adjustments
            taxDue = taxDue.subtract(taxAdjAmount);

            // add all significant things into return value
            taxes.add(UtilMisc.<String, Object>toMap(
                    "taxDue", taxDue,
                    "taxAuthPartyId", taxAuthPartyId,
                    "taxAuthGeoId", taxAuthGeoId,
                    "taxable", taxable
            ));
        }

        return taxes;
    }

    /**
     * Returns amount that corresponds to sales subject returned by customer.
     *
     * @param invoiceId sales invoice id
     * @param invoiceItemSeqId sales invoice item id
     * @param delegator <code>Delegator</code> instance.
     * @return refund amount for given sales invoice item.
     * @throws GenericEntityException
     */
    private static BigDecimal getTotalRefundAmount(String invoiceId, String invoiceItemSeqId, Delegator delegator) throws GenericEntityException {
        BigDecimal totalRefunds = BigDecimal.ZERO;
        long totalRefundsIiNum = 0;

        // find order item corresponding to given sales invoice item, available in OrderItemBilling
        List<GenericValue> orderItemBillings = delegator.findByAnd("OrderItemBilling", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", invoiceItemSeqId));
        if (UtilValidate.isEmpty(orderItemBillings)) {
            // not found
            return BigDecimal.ZERO;
        }

        // find all return invoice items related to given invoice item looking through
        // OrderItemBilling -> ReturnItem -> ReturnItemBilling
        // add up product of quantity and amount of each item to calculate returns amount
        for (GenericValue orderBillingItem : orderItemBillings) {
            List<GenericValue> returnItems = delegator.findByAnd("ReturnItem", UtilMisc.toMap("orderId", orderBillingItem.getString("orderId"), "orderItemSeqId", orderBillingItem.getString("orderItemSeqId")));
            for (GenericValue returnItem : returnItems) {
                List<GenericValue> returnItemBillings = returnItem.getRelated("ReturnItemBilling");
                for (GenericValue returnItemBilling : returnItemBillings) {
                    GenericValue returnInvoice = returnItemBilling.getRelatedOne("Invoice");
                    if ("CUST_RTN_INVOICE".equals(returnInvoice.getString("invoiceTypeId"))) {
                        GenericValue returnInvoiceItem = returnItemBilling.getRelatedOne("InvoiceItem");
                        if ("CRT_PROD_ITEM".equals(returnInvoiceItem.getString("invoiceItemTypeId"))
                         || "CRT_DPROD_ITEM".equals(returnInvoiceItem.getString("invoiceItemTypeId"))
                         || "CRT_FDPROD_ITEM".equals(returnInvoiceItem.getString("invoiceItemTypeId"))
                         || "CRT_PROD_FEATR_ITEM".equals(returnInvoiceItem.getString("invoiceItemTypeId"))
                         || "CRT_SPROD_ITEM".equals(returnInvoiceItem.getString("invoiceItemTypeId"))
                        ) {
                            BigDecimal quantity = returnInvoiceItem.getBigDecimal("quantity");
                            BigDecimal amount = returnInvoiceItem.getBigDecimal("amount");
                            totalRefunds = totalRefunds.add((quantity == null ? BigDecimal.ONE : quantity).multiply((amount == null ? BigDecimal.ZERO : amount)));
                            totalRefundsIiNum++;
                        }
                    }
                }
            }
        }

        return totalRefunds;
    }

    /**
     * Returns total amount of discounts and promotions on given sales invoice item.
     *
     * @param invoiceId sales invoice id
     * @param invoiceItemSeqId sales invoice item id
     * @param delegator <code>Delegator</code> instance
     * @return sales invoice item adjustments.
     * @throws GenericEntityException
     */
    private static BigDecimal getTotalPromoAmount(String invoiceId, String invoiceItemSeqId, Delegator delegator) throws GenericEntityException {
        BigDecimal totalPromotions = BigDecimal.ZERO;

        // find promotion/discount invoice items which have given invoice item as a parent
        EntityCondition promoConditions = EntityCondition.makeCondition(EntityOperator.AND,
                EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, invoiceId),
                EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS, invoiceItemSeqId),
                EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN, Arrays.asList("ITM_PROMOTION_ADJ", "ITM_DISCOUNT_ADJ")));
        List<GenericValue> promoItems = delegator.findByCondition("InvoiceItem", promoConditions, UtilMisc.toSet("quantity", "amount"), null);

        // add up each product of quantity and amount to get total promotion amount related to specified invoice item
        // default value for quantity is 1 and for amount is 0
        if (UtilValidate.isNotEmpty(promoItems)) {
            for (GenericValue promoItem : promoItems) {
                BigDecimal quantity = promoItem.getBigDecimal("quantity");
                BigDecimal amount = promoItem.getBigDecimal("amount");
                totalPromotions = totalPromotions.add((quantity == null ? BigDecimal.ONE : quantity).multiply((amount == null ? BigDecimal.ZERO : amount)));
            }
        }

        return totalPromotions;
    }

    /**
     * Lookup store dimension key.
     *
     * @param invoiceId invoice identifier
     * @param invoiceItemSeqId invoice item sequence number
     * @param delegator <code>Delegator</code> instance
     * @return
     *   Store dimension key.
     * @throws GenericEntityException
     */
    private static Long lookupStoreDim(String invoiceId, String invoiceItemSeqId, Delegator delegator) throws GenericEntityException {
        // store dim has a special record with key 0 with meaning "no product store"
        Long notfound = 0L;
        if (invoiceId == null && invoiceItemSeqId == null) {
            return notfound;
        }

        // in order to get product store id for an invoice item we have to find OrderItemBilling
        // entity for the invoice item and further corresponding OrderHeader.productStoreId
        DynamicViewEntity dv = new DynamicViewEntity();
        dv.addMemberEntity("OIB", "OrderItemBilling");
        dv.addMemberEntity("OH", "OrderHeader");
        dv.addAlias("OIB", "invoiceId");
        dv.addAlias("OIB", "invoiceItemSeqId");
        dv.addAlias("OIB", "orderId");
        dv.addAlias("OH", "orderId");
        dv.addAlias("OH", "productStoreId", null, null, null, null, "max");
        dv.addViewLink("OIB", "OH", false, ModelKeyMap.makeKeyMapList("orderId"));

        EntityCondition conditionList = EntityCondition.makeCondition(EntityOperator.AND,
                EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId),
                EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, invoiceItemSeqId));

        // retrieve first record
        EntityListIterator iter = delegator.findListIteratorByCondition(dv, conditionList, null, UtilMisc.toList("productStoreId"), null, null);
        GenericValue orderStore = EntityUtil.getFirst(iter.getCompleteList());
        iter.close();

        // it isn't necessary order have to be found
        if (orderStore == null) {
            return notfound;
        }

        String productStoreId = orderStore.getString("productStoreId");
        if (UtilValidate.isEmpty(productStoreId)) {
            return notfound;
        }

        // find store dimension key for given productStoreId
        GenericValue storeDim = EntityUtil.getFirst(
                delegator.findByCondition("StoreDim", EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId), Arrays.asList("storeDimId"), null)
        );
        if (storeDim == null) {
            return notfound;
        }

        // return store dimension key
        Long storeDimId = storeDim.getLong("storeDimId");
        return storeDimId == null ? notfound : storeDimId;
    }

    /**
     * <p>Look over invoice adjustments and transform  them into into sales and tax invoice item facts.
     * Thus an adjustment amount is added into discount column of the fact table and this is only
     * currency column affected.</p>
     *
     * @param session Hibernate session
     * @throws GenericEntityException
     */
    public static void loadInvoiceAdjustments(Session session, Delegator delegator) throws GenericEntityException {

        Transaction tx = session.beginTransaction();

        // retrieve data as scrollable result set.
        // this is join of InvoiceAdjustment and Invoice entities and each record has all required data
        // to create new fact row
        Query invAdjQry = session.createQuery("select IA.invoiceAdjustmentId, IA.invoiceId, IA.amount, I.partyIdFrom, I.invoiceDate, I.currencyUomId from InvoiceAdjustment IA, Invoice I where IA.invoiceId = I.invoiceId and I.invoiceTypeId = 'SALES_INVOICE' and I.statusId not in ('INVOICE_IN_PROCESS', 'INVOICE_CANCELLED', 'INVOICE_VOIDED', 'INVOICE_WRITEOFF')");
        ScrollableResults adjustments = invAdjQry.scroll();

        // iterate over record set
        while (adjustments.next()) {

            // keep result fields in variables as a matter of convenience
            String invoiceId = adjustments.getString(1);
            String invoiceAdjustmentId = adjustments.getString(0);
            BigDecimal amount = adjustments.getBigDecimal(2);
            String organizationPartyId = adjustments.getString(3);
            Timestamp invoiceDate = (Timestamp) adjustments.get(4);
            String currencyUomId = adjustments.getString(5);

            // lookup date dimension
            DateFormat dayOfMonthFmt = new SimpleDateFormat("dd");
            DateFormat monthOfYearFmt = new SimpleDateFormat("MM");
            DateFormat yearNumberFmt = new SimpleDateFormat("yyyy");

            String dayOfMonth = dayOfMonthFmt.format(invoiceDate);
            String monthOfYear = monthOfYearFmt.format(invoiceDate);
            String yearNumber = yearNumberFmt.format(invoiceDate);

            EntityCondition dateDimConditions = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("dayOfMonth", dayOfMonth),
                    EntityCondition.makeCondition("monthOfYear", monthOfYear),
                    EntityCondition.makeCondition("yearNumber", yearNumber));
            Long dateDimId = UtilEtl.lookupDimension("DateDim", "dateDimId", dateDimConditions, delegator);

            // lookup currency dimension
            Long currencyDimId = UtilEtl.lookupDimension("CurrencyDim", "currencyDimId", EntityCondition.makeCondition("uomId", currencyUomId), delegator);

            // lookup organization dimension
            Long organizationDimId = UtilEtl.lookupDimension("OrganizationDim", "organizationDimId", EntityCondition.makeCondition("organizationPartyId", organizationPartyId), delegator);

            // creates rows for both fact tables
            TaxInvoiceItemFact taxFact = new TaxInvoiceItemFact();
            taxFact.setDateDimId(dateDimId);
            taxFact.setStoreDimId(0L);
            taxFact.setTaxAuthorityDimId(0L);
            taxFact.setCurrencyDimId(currencyDimId);
            taxFact.setOrganizationDimId(organizationDimId);
            taxFact.setInvoiceId(invoiceId);
            taxFact.setInvoiceAdjustmentId(invoiceAdjustmentId);
            taxFact.setGrossAmount(BigDecimal.ZERO);
            taxFact.setDiscounts(amount);
            taxFact.setRefunds(BigDecimal.ZERO);
            taxFact.setNetAmount(BigDecimal.ZERO);
            taxFact.setTaxable(BigDecimal.ZERO);
            taxFact.setTaxDue(BigDecimal.ZERO);
            session.save(taxFact);

            SalesInvoiceItemFact salesFact = new SalesInvoiceItemFact();
            salesFact.setDateDimId(dateDimId);
            salesFact.setStoreDimId(0L);
            salesFact.setCurrencyDimId(currencyDimId);
            salesFact.setOrganizationDimId(organizationDimId);
            salesFact.setInvoiceId(invoiceId);
            salesFact.setInvoiceAdjustmentId(invoiceAdjustmentId);
            salesFact.setGrossAmount(BigDecimal.ZERO);
            salesFact.setDiscounts(amount);
            salesFact.setRefunds(BigDecimal.ZERO);
            salesFact.setNetAmount(BigDecimal.ZERO);
            session.save(salesFact);

        }
        adjustments.close();
        tx.commit(); // persist result, don't move this statement upper
    }

    /**
     * Wrapper service that prepare fact tables on behalf of sales tax report
     * running Kettle sales tax transformations and loadTaxInvoiceItemFact service.
     *
     * @param dctx a <code>DispatchContext</code> instance
     * @param context the service context <code>Map</code>
     * @return the service response <code>Map</code>
     */
    public static Map<String, Object> loadSalesTaxData(DispatchContext dctx, Map<String, Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        if (locale == null) {
            locale = Locale.getDefault();
        }
        TimeZone timeZone = (TimeZone) context.get("timeZone");
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }

        String organizationPartyId = (String) context.get("organizationPartyId");
        // since organization party id attribute is optional we use special value
        // in case no id is provided, in order to keep this fact in runtime data
        if (UtilValidate.isEmpty(organizationPartyId)) {
            organizationPartyId = "ALL";
        }

        Map<String, Object> results = ServiceUtil.returnSuccess();
        Timestamp startedAt = UtilDateTime.nowTimestamp();

        try {
            long rowCount = delegator.findCountByCondition("DateDim", null, null);
            if (rowCount < 2) {
                Debug.logInfo("Creating date dimension ...", MODULE);
                UtilEtl.setupDateDimension(delegator, timeZone, locale);
            }

            // clean up datamart data
            Debug.logInfo("Clean up dimension and fact tables", MODULE);
            delegator.removeByCondition("StoreDim", EntityCondition.makeCondition("storeDimId", EntityOperator.NOT_EQUAL, null));
            delegator.removeByCondition("TaxAuthorityDim", EntityCondition.makeCondition("taxAuthorityDimId", EntityOperator.NOT_EQUAL, null));
            delegator.removeByCondition("CurrencyDim", EntityCondition.makeCondition("currencyDimId", EntityOperator.NOT_EQUAL, null));
            delegator.removeByCondition("OrganizationDim", EntityCondition.makeCondition("organizationDimId", EntityOperator.NOT_EQUAL, null));
            delegator.removeByCondition("SalesInvoiceItemFact", EntityCondition.makeCondition("dateDimId", EntityOperator.NOT_EQUAL, null));
            delegator.removeByCondition("TaxInvoiceItemFact", EntityCondition.makeCondition("dateDimId", EntityOperator.NOT_EQUAL, null));

            // ETL transformations use JNDI to obtain database connection parameters.
            // We have to put proper data source into naming context before run transformations.
            String dataSourceName = delegator.getGroupHelperName("org.ofbiz");
            DataSourceImpl datasource = new DataSourceImpl(dataSourceName);
            new InitialContext().rebind("java:comp/env/jdbc/default_delegator", datasource);

            // run the ETL transformations to load the datamarts
            UtilEtl.runTrans("component://financials/script/etl/load_product_store_dimension.ktr", null);
            UtilEtl.runTrans("component://financials/script/etl/load_tax_authority_dimension.ktr", null);
            UtilEtl.runTrans("component://financials/script/etl/load_organization_dimension.ktr", null);
            UtilEtl.runTrans("component://financials/script/etl/load_sales_invoice_item_fact.ktr", null);
            UtilEtl.runTrans("component://financials/script/etl/load_invoice_level_promotions.ktr", null);
            // ... and invoke other required services and methods
            dispatcher.runSync("financials.loadTaxInvoiceItemFact", UtilMisc.toMap("userLogin", userLogin, "locale", locale));
            Session session = new Infrastructure(dispatcher).getSession();
            loadInvoiceAdjustments(session, delegator);
            session.close();

            // unregister data source
            new InitialContext().unbind("java:comp/env/jdbc/default_delegator");

            // keep runtime info
            GenericValue transform = delegator.makeValue("DataWarehouseTransform");
            transform.set("transformId", delegator.getNextSeqId("DataWarehouseTransform"));
            transform.set("organizationPartyId", organizationPartyId);
            transform.set("transformEnumId", "SALES_TAX_FACT");
            transform.set("transformTimestamp", startedAt);
            transform.set("userLoginId", userLogin.get("userLoginId"));
            transform.create();

        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, MODULE);
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, MODULE);
        } catch (NamingException e) {
            return UtilMessage.createAndLogServiceError(e, MODULE);
        } catch (InfrastructureException e) {
            return UtilMessage.createAndLogServiceError(e, MODULE);
        }

        return results;
    }

    
}
