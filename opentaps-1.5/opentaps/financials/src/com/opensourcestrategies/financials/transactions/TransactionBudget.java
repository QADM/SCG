/*

 */
package com.opensourcestrategies.financials.transactions;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.net.ntp.TimeStamp;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.opentaps.base.entities.AcctgTrans;
import org.opentaps.base.entities.AcctgTransAndOrg;
import org.opentaps.base.entities.AcctgTransEntry;
import org.opentaps.base.entities.AcctgTransEntry.Fields;
import org.opentaps.base.entities.AcctgTransOrgPresupIng;
import org.opentaps.base.entities.AcctgTransPresupuestal;
import org.opentaps.base.entities.AcctgTransType;
import org.opentaps.base.entities.CustomTimePeriod;
import org.opentaps.base.entities.Enumeration;
import org.opentaps.base.entities.GlAccount;
import org.opentaps.base.entities.GlAccountHistory;
import org.opentaps.base.entities.GlAccountOrganization;
import org.opentaps.base.entities.GlFiscalType;
import org.opentaps.base.entities.MiniGuiaContable;
import org.opentaps.base.entities.NivelPresupuestal;
import org.opentaps.base.entities.Party;
import org.opentaps.base.entities.PartyAcctgPreference;
import org.opentaps.base.entities.PartyGroup;
import org.opentaps.base.entities.ProductCategory;
import org.opentaps.base.entities.WorkEffort;
import org.opentaps.base.entities.bridge.GlAccountTypeDefaultPkBridge;
import org.opentaps.base.services.CreateQuickAcctgTransService;
import org.opentaps.base.services.PostAcctgTransService;
import org.opentaps.common.builder.EntityListBuilder;
import org.opentaps.common.builder.PageBuilder;
import org.opentaps.common.util.UtilAccountingTags;
import org.opentaps.common.util.UtilCommon;
import org.opentaps.common.util.UtilMessage;
import org.opentaps.domain.DomainsDirectory;
import org.opentaps.domain.DomainsLoader;
import org.opentaps.domain.ledger.LedgerRepositoryInterface;
import org.opentaps.domain.organization.AccountingTagConfigurationForOrganizationAndUsage;
import org.opentaps.domain.organization.Organization;
import org.opentaps.domain.organization.OrganizationRepositoryInterface;
import org.opentaps.foundation.action.ActionContext;
import org.opentaps.foundation.infrastructure.Infrastructure;
import org.opentaps.foundation.infrastructure.User;
import org.opentaps.foundation.repository.RepositoryException;


/**
 * TransactionActions - Java Actions for Transactions.
 */
public class TransactionBudget {

	private static final String MODULE = TransactionBudget.class.getName();

	/**
	 * Action for the find / list transactions screen.
	 * 
	 * @param context
	 *            the screen context
	 * @throws GeneralException
	 *             if an error occurs
	 * @throws ParseException
	 *             if an error occurs
	 */
	public static void buscarCatalogos(Map<String, Object> context)
			throws GeneralException, ParseException {

		final ActionContext ac = new ActionContext(context);
		final Locale locale = ac.getLocale();
		String organizationPartyId = UtilCommon.getOrganizationPartyId(ac
				.getRequest());

		

		DomainsDirectory dd = DomainsDirectory.getDomainsDirectory(ac);
		final LedgerRepositoryInterface ledgerRepository = dd.getLedgerDomain()
				.getLedgerRepository();		

		List<Map<String, Object>> categoryEntry = new FastList<Map<String, Object>>();
		List<Map<String, Object>> categoryType = new FastList<Map<String, Object>>();
		List<Map<String, Object>> categoryClass = new FastList<Map<String, Object>>();
		List<Map<String, Object>> categoryConcept = new FastList<Map<String, Object>>();
		List<Map<String, Object>> categoryN5 = new FastList<Map<String, Object>>();
		List<Map<String, Object>> subFuenteE = new FastList<Map<String, Object>>();
		List<Map<String, Object>> unidadE = new FastList<Map<String, Object>>();
		List<Map<String, Object>> subfuncion = new FastList<Map<String, Object>>();
		List<Map<String, Object>> actividad = new FastList<Map<String, Object>>();
		List<Map<String, Object>> tipoGasto = new FastList<Map<String, Object>>();
		List<Map<String, Object>> partidaEspecifica = new FastList<Map<String, Object>>();
		List<Map<String, Object>> area = new FastList<Map<String, Object>>();

		/*
		 * Lista para visualizar catalogos PRODUCTCATEGORY
		 */
		List<ProductCategory> listallcategory = ledgerRepository
				.findAll(ProductCategory.class);

		for (ProductCategory ss : listallcategory) {

			if (ss.getProductCategoryTypeId().toString().equals("RU")) {
				Map<String, Object> map = ss.toMap();
				categoryEntry.add(map);
			} else if (ss.getProductCategoryTypeId().toString().equals("TI")) {
				Map<String, Object> map = ss.toMap();
				categoryType.add(map);
			} else if (ss.getProductCategoryTypeId().toString().equals("CL")) {
				Map<String, Object> map = ss.toMap();
				categoryClass.add(map);
			} else if (ss.getProductCategoryTypeId().toString().equals("CO")) {
				Map<String, Object> map = ss.toMap();
				categoryConcept.add(map);

			} else if (ss.getProductCategoryTypeId().toString().equals("N5")) {
				Map<String, Object> map = ss.toMap();
				categoryN5.add(map);
			}
		}

		/*
		 * Obtener lista Subfuente Especifica
		 */
		List<NivelPresupuestal> nivel = ledgerRepository.findList(
				NivelPresupuestal.class, ledgerRepository.map(
						NivelPresupuestal.Fields.descripcion,
						"Subfuente especifica"));
		
		List<Enumeration> listSubFuente = ledgerRepository.findList(
				Enumeration.class, ledgerRepository.map(
						Enumeration.Fields.enumTypeId, "CL_FUENTE_RECURSOS",
						Enumeration.Fields.nivelId, "SUBFUENTE_ESPECIFICA"));

		for (Enumeration enumeration : listSubFuente) {
			Map<String, Object> map = enumeration.toMap();
			subFuenteE.add(map);
		}

		/*
		 * Obtener Lista Unidad Ejecutora
		 */

		List<NivelPresupuestal> nivelUE = ledgerRepository.findList(
				NivelPresupuestal.class, ledgerRepository.map(
						NivelPresupuestal.Fields.descripcion,
						"Unidad Ejecutora"));

		List<Party> listpartys = ledgerRepository.findList(Party.class,
				ledgerRepository.map(Party.Fields.Nivel_id, "UNIDAD_EJECUTORA"));		
		
		for (Party party : listpartys) {
			Debug.log("Party " + party.getPartyId() + " "
					+ party.getPartyGroup().getGroupName());
			PartyGroup pg = party.getPartyGroup();
			// listpg.add(pg);
			Debug.log("PartyGroup " + pg.getPartyId() + " " + pg.getGroupName());
			Map<String, Object> map = pg.toMap();
			unidadE.add(map);
			// party.getPartyGroup().getGroupName();

		}
		
		
		/*
		 * Obtener Subfuncion*/
		
		List<Enumeration> listsubfuncion = ledgerRepository.findList(
				Enumeration.class, ledgerRepository.map(
						Enumeration.Fields.enumTypeId, "CL_FUNCIONAL",
						Enumeration.Fields.nivelId, "SUBFUNCION"));
		
		for (Enumeration enumeration : listsubfuncion) {
			Map<String, Object> map = enumeration.toMap();
			subfuncion.add(map);
		}
		
		/*
		 * Obtener Tipo Gasto*/
		List<Enumeration> listTipoGasto = ledgerRepository.findList(
				Enumeration.class, ledgerRepository.map(
						Enumeration.Fields.enumTypeId, "TIPO_GASTO",
						Enumeration.Fields.nivelId, "TIPO_DE_GASTO"));
		
		for (Enumeration enumeration : listTipoGasto) {
			Map<String, Object> map = enumeration.toMap();
			tipoGasto.add(map);
		}
		
		/*
		 * Obtener Area*/		
		
		List<Enumeration> listarea = ledgerRepository.findList(
		Enumeration.class, ledgerRepository.map(
				Enumeration.Fields.enumTypeId, "CL_SECTORIAL",
				Enumeration.Fields.nivelId, "AREA"));
		
		for (Enumeration enumeration : listarea) {
			Map<String, Object> map = enumeration.toMap();
			area.add(map);
		}
		
		/*
		 * Obtener Actividades
		 */
		List<WorkEffort> listactividades = ledgerRepository.findList(
				WorkEffort.class, ledgerRepository.map(
						WorkEffort.Fields.workEffortTypeId, "PHASE",
						WorkEffort.Fields.nivelId, "ACTIVIDAD_INSTITUCIO"));
		
		for (WorkEffort workEffort : listactividades) {
			Map<String, Object> map = workEffort.toMap();
			actividad.add(map);
		}
		
		/*
		 * Obtener Partida Especifica
		 * select * from PRODUCT_CATEGORY where PRODUCT_CATEGORY_TYPE_ID = 'PARTIDA ESPECIFICA'*/

		List<ProductCategory> listPE = ledgerRepository.findList(
				ProductCategory.class, ledgerRepository.map(
						ProductCategory.Fields.productCategoryTypeId,
						"PARTIDA ESPECIFICA"));
		
		for (ProductCategory productCategory : listPE) {
			Map<String, Object> map = productCategory.toMap();
			partidaEspecifica.add(map);
		}

		ac.put("listallcategory", categoryEntry);
		ac.put("listallcategoryType", categoryType);
		ac.put("listallcategoryClass", categoryClass);
		ac.put("listallcategoryConc", categoryConcept);
		ac.put("listallcategoryN", categoryN5);
		ac.put("listSubFuente", subFuenteE);
		ac.put("listUnidadE", unidadE);
		ac.put("listsubfuncion", subfuncion);
		ac.put("listtipoGasto", tipoGasto);
		ac.put("listarea", area);
		ac.put("listactividad", actividad);
		ac.put("listpartidaEspecifica", partidaEspecifica);


	}

	/**
	 * Create a Quick <code>AcctgTrans</code> record. IsPosted is forced to "N".
	 * Creates an Quick AcctgTrans and two offsetting AcctgTransEntry records.
	 * 
	 * @param dctx
	 *            a <code>DispatchContext</code> value
	 * @param context
	 *            a <code>Map</code> value
	 * @return a <code>Map</code> value
	 */
	@SuppressWarnings("unchecked")
	public static Map createAcctgTransPresupuestoIngreso(DispatchContext dctx,
			Map context) {
		Debug.log("Si entro al servicio createAcctgTransPresupuestoIngreso");

		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		try {
			DomainsLoader dl = new DomainsLoader(
					new Infrastructure(dispatcher), new User(userLogin));
			OrganizationRepositoryInterface organizationRepository = dl
					.loadDomainsDirectory().getOrganizationDomain()
					.getOrganizationRepository();

			String organizationPartyId = (String) context
					.get("organizationPartyId");

			String glFiscalTypeId = (String) context.get("glFiscalTypeId");
			String acctgTransTypeId = (String) context.get("acctgTransTypeId");
			String idRubro = (String) context.get("idRubro");
			String idTipo = (String) context.get("idTipo");
			String idClase = (String) context.get("idClase");
			String idConcepto = (String) context.get("idConcepto");
			String idN5 = (String) context.get("idN5");
			String EntidadFederativa = (String) context
					.get("EntidadFederativa");
			String Region = (String) context.get("Region");
			String Municipio = (String) context.get("Municipio");
			String Localidad = (String) context.get("Localidad");
			String fechaTransaccion = (String) context.get("fechaTransaccion");
			String fechaContable = (String) context.get("fechaContable");
			String clave = (String) context.get("clave");
			String monto = (String) context.get("monto");
			String referencia = (String) context.get("referencia");
			String descripcion = (String) context.get("description");
			String unidadEjecutora = (String) context.get("unidadEjecutora");
			Double amount = (Double) context.get("amount");
			Debug.log("organizacion" + organizationPartyId);
			Debug.log("glFiscalTypeId" + glFiscalTypeId);
			Debug.log("acctgTransTypeId" + acctgTransTypeId);
			Debug.log("idRubro" + idRubro);
			Debug.log("idTipo" + idTipo);
			Debug.log("idClase" + idClase);
			Debug.log("idConcepto" + idConcepto);
			Debug.log("idN5" + idN5);
			Debug.log("EntidadFederativa" + EntidadFederativa);
			Debug.log("Region" + Region);
			Debug.log("Municipio" + Municipio);
			Debug.log("Localidad" + Localidad);
			Debug.log("fechaTransaccion" + fechaTransaccion);
			Debug.log("fechaContable" + fechaContable);
			Debug.log("clave" + clave);
			Debug.log("monto" + monto);
			Debug.log("referencia" + referencia);
			Debug.log("descripcion" + descripcion);
			Debug.log("amount" + amount);

			Organization organization = organizationRepository
					.getOrganizationById(organizationPartyId);

			Date fechaTrasac = getDateTransaction(fechaTransaccion);
			Date fechaConta = getDateTransaction(fechaContable);
			
			// create the accounting transaction
			String TypeTransId = getAcctgTransTypeId("TINGRESOESTIMADO", dispatcher);
			//AcctgTrans.Fields.postedAmount

			Map createAcctgTransCtx = dctx.getModelService("createAcctgTransBugetManual")
					.makeValid(context, ModelService.IN_PARAM);
			if (UtilValidate.isEmpty(createAcctgTransCtx
					.get("fechaTransaccion"))) {
				
				createAcctgTransCtx.put("transactionDate", fechaTransaccion);
				createAcctgTransCtx.put("description", clave + "-" +  getFormatMes (fechaConta.getMonth()));
				createAcctgTransCtx.put("acctgTransTypeId", "TINGRESOESTIMADO");
				createAcctgTransCtx.put("glFiscalTypeId" ,TypeTransId);
				createAcctgTransCtx.put("partyId" ,unidadEjecutora);
				//createAcctgTransCtx.put("createdByUserLogin" ,"admin");
				createAcctgTransCtx.put("postedAmount", amount);
				//createdByUserLogin
				//lastModifiedByUserLogin
				//postedAmount
				

			}

			Map results = dispatcher.runSync("createAcctgTransBugetManual",
					createAcctgTransCtx);

			if (!UtilCommon.isSuccess(results)) {
				Debug.log("results" + results.toString());
				return UtilMessage.createAndLogServiceError(results, MODULE);

			}
			String acctgTransId = (String) results.get("acctgTransId");
			Debug.log("acctgTransId" + acctgTransId);

			UtilCommon.isSuccess(createAcctgPresupuestal(context, acctgTransId,
					dctx));

			// create both debit and credit entries
			String currencyUomId = (String) context.get("currencyUomId");
			if (UtilValidate.isEmpty(currencyUomId)) {
				PartyAcctgPreference partyAcctgPref = organization
						.getPartyAcctgPreference();
				if (partyAcctgPref != null) {
					currencyUomId = partyAcctgPref.getBaseCurrencyUomId();
				} else {
					Debug.logWarning(
							"No accounting preference found for organization: "
									+ organizationPartyId, MODULE);
				}
			}

			EntityCondition condicion = EntityCondition.makeCondition(
					"acctgTransTypeId", acctgTransTypeId);
			List<GenericValue> cuentas = dispatcher
					.getDelegator()
					.findByCondition("MiniGuiaContable", condicion,
							UtilMisc.toList("cuentaCargo", "cuentaAbono"), null);
			//AcctgTransEntry.Fields.acctgTagEnumId3
			
			String debitGlAccountId = "";
			String creditGlAccountId = "";

			for (GenericValue genericValue : cuentas) {
				Debug.log("cuentaCargo"
						+ genericValue.get("cuentaCargo").toString());
				Debug.log("cuentaAbono"
						+ genericValue.get("cuentaAbono").toString());
				debitGlAccountId = genericValue.get("cuentaCargo").toString();
				creditGlAccountId = genericValue.get("cuentaAbono").toString();
			}

			String rubro = (String) context.get("idRubro");
			Debug.log("idRubro" + rubro);
			// debit entry, using createAcctgTransEntryManual which validate the
			// accounting tags, the tags for are prefixed by "debitTagEnumId"
			Map createAcctgTransEntryCtx = dctx.getModelService(
					"createAcctgTransEntryManual").makeValid(context,
					ModelService.IN_PARAM);

			Map debitCtx = new HashMap(createAcctgTransEntryCtx);
			UtilAccountingTags.addTagParameters(context, debitCtx,
					"debitTagEnumId", UtilAccountingTags.ENTITY_TAG_PREFIX);
			debitCtx.put("acctgTransId", acctgTransId);
			debitCtx.put("glAccountId", debitGlAccountId);
			debitCtx.put("debitCreditFlag", "D");
			debitCtx.put("acctgTransEntryTypeId", "_NA_");
			debitCtx.put("currencyUomId", currencyUomId);
			debitCtx.put("description", clave + "-" +  getFormatMes (fechaConta.getMonth()));
			debitCtx.put("acctgTagEnumId3", (String) context.get("subFuenteEsp"));
			results = dispatcher.runSync("createAcctgTransEntryManual",
					debitCtx);
			
			// credit entry, the tags for are prefixed by "creditTagEnumId"
			Map creditCtx = new HashMap(createAcctgTransEntryCtx);
			UtilAccountingTags.addTagParameters(context, creditCtx,
					"creditTagEnumId", UtilAccountingTags.ENTITY_TAG_PREFIX);
			creditCtx.put("acctgTransId", acctgTransId);
			creditCtx.put("glAccountId", creditGlAccountId);
			creditCtx.put("debitCreditFlag", "C");
			creditCtx.put("acctgTransEntryTypeId", "_NA_");
			creditCtx.put("currencyUomId", currencyUomId);
			creditCtx.put("description", clave + "-" +  getFormatMes (fechaConta.getMonth()));
			creditCtx.put("acctgTagEnumId3", (String) context.get("subFuenteEsp"));
			results = dispatcher.runSync("createAcctgTransEntryManual",
					creditCtx);

			results = ServiceUtil.returnSuccess();
			results.put("acctgTransId", acctgTransId);

			postedTransaccion(acctgTransId, fechaConta, dctx, userLogin);
			glOrganizationHistory(debitGlAccountId, creditGlAccountId, dctx,
					organizationPartyId, userLogin, amount, fechaConta);
			return results;

		} catch (GeneralException e) {
			return UtilMessage.createAndLogServiceError(e, MODULE);
		}
	}

	private static void glOrganizationHistory(String debitGlAccountId,
			String creditGlAccountId, DispatchContext dctx,
			String organizationPartyId, GenericValue userLogin, double amount, Date fechaConta)
			throws RepositoryException {

		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		LedgerRepositoryInterface ledgerRepository = new DomainsLoader(
				new Infrastructure(dispatcher), new User(userLogin))
				.loadDomainsDirectory().getLedgerDomain().getLedgerRepository();

		BigDecimal monto = new BigDecimal(amount);
		String cuenta = debitGlAccountId;
		String tipo = "D";
		try {
			
			List<CustomTimePeriod> periodos = getPeriodos(organizationPartyId,
					fechaConta, ledgerRepository);
			
			for (int i = 0; i < 2; i++) {

				if (i != 0)
				{
					cuenta = creditGlAccountId;
					tipo = "C";
				}
					

				GlAccountOrganization glorganization = ledgerRepository
						.findOne(
								GlAccountOrganization.class,
								ledgerRepository
										.map(GlAccountOrganization.Fields.glAccountId,
												cuenta,
												GlAccountOrganization.Fields.organizationPartyId,
												organizationPartyId));

				if (glorganization.getPostedBalance() == null) {
					glorganization.setPostedBalance(monto);
				} else {
					glorganization.setPostedBalance(glorganization
							.getPostedBalance().add(monto));
				}

				ledgerRepository.createOrUpdate(glorganization);
				
				
				
				for (CustomTimePeriod customTimePeriod : periodos) {
					
					GlAccountHistory glhistory = ledgerRepository
							.findOne(
									GlAccountHistory.class,
									ledgerRepository
											.map(GlAccountHistory.Fields.glAccountId,
													cuenta,
													GlAccountHistory.Fields.organizationPartyId,
													organizationPartyId,
													GlAccountHistory.Fields.customTimePeriodId,
													customTimePeriod
															.getCustomTimePeriodId()));
					BigDecimal saldoD = BigDecimal.ZERO;
					BigDecimal saldoC = BigDecimal.ZERO;
					BigDecimal amouts = BigDecimal.valueOf(amount);
					if(glhistory!= null)
					{
						if(tipo == "D")
						{
							if(glhistory.getPostedDebits()==null)
							{
								saldoD =  amouts;
							}
							else
							{
								saldoD = glhistory.getPostedDebits().add(amouts);
							}
						}
						else
						{
							if(glhistory.getPostedCredits()==null)
							{
								saldoC =  amouts;
							}
							else
							{
								saldoC = glhistory.getPostedCredits().add(amouts);
							}
						}
						glhistory.setPostedDebits(saldoD);
						glhistory.setPostedCredits(saldoC);
						ledgerRepository.createOrUpdate(glhistory);
						
					}
					else
					{
						GlAccountHistory glaccounthistory = new GlAccountHistory();
						glaccounthistory.setGlAccountId(cuenta);
						glaccounthistory.setOrganizationPartyId(organizationPartyId);
						glaccounthistory.setCustomTimePeriodId(customTimePeriod.getCustomTimePeriodId());
						
						if(tipo == "D")
						{
							
							glaccounthistory.setPostedDebits(amouts);
							
						}
						else
						{
							glaccounthistory.setPostedCredits(amouts);
						}
						
						ledgerRepository.createOrUpdate(glaccounthistory);
						
					}
					
				}

			}
			
			

		} catch (Exception e) {

		}

	}

	/**
	 * @param organizationPartyId
	 * @param fechaConta
	 * @param ledgerRepository
	 * @return
	 * @throws RepositoryException
	 */
	private static List<CustomTimePeriod> getPeriodos(
			String organizationPartyId, Date fechaConta,
			LedgerRepositoryInterface ledgerRepository)
			throws RepositoryException {
		EntityCondition condicion = EntityCondition.makeCondition(EntityOperator.AND,
		        EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS,organizationPartyId),
		        EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS,"N"),
		        EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,fechaConta),
		        EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO,fechaConta));

		List<CustomTimePeriod> periodos = ledgerRepository.findList(CustomTimePeriod.class, condicion);
		return periodos;
	}

	private static void getPeriodos() {
		try {
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	private static void postedTransaccion(String acctgTransId, Date fechaConta,
			DispatchContext dctx, GenericValue userLogin)
			throws RepositoryException {

		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		LedgerRepositoryInterface ledgerRepository = new DomainsLoader(
				new Infrastructure(dispatcher), new User(userLogin))
				.loadDomainsDirectory().getLedgerDomain().getLedgerRepository();
		try {

			// Fin
			AcctgTrans acctgtrans = ledgerRepository.findOne(AcctgTrans.class,
					ledgerRepository.map(AcctgTrans.Fields.acctgTransId,
							acctgTransId));

			if (acctgtrans != null) {
				Timestamp timestamp = new Timestamp(fechaConta.getTime());
				acctgtrans.setIsPosted("Y");
				acctgtrans.setPostedDate(timestamp);
				
			

				ledgerRepository.createOrUpdate(acctgtrans);
			}

		} catch (Exception e) {
			Debug.log("Error al publicar Transaccion: acctgTransId[" + acctgTransId + "] " + e);
		}

	}

	private static Date getDateTransaction(String Fecha) {

		// 21/08/13 12:49:00
		Debug.log(Fecha);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

		try {
			return sdf.parse(Fecha);
		} catch (ParseException e) {
			String msg = "Error al hacer el parse en la fecha " + e;
			Debug.log(msg);
			return null;
		}
	}

	private static Map<String, Object> createAcctgPresupuestal(Map context,
			String acctgTransId, DispatchContext dctx) {

		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = UtilCommon.getLocale(context);
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Debug.log("Si entro al createAcctgTransPresupuestalManual, acctgTransId "
				+ context.get("acctgTransId"));

		try {
			LedgerRepositoryInterface ledgerRepository = new DomainsLoader(
					new Infrastructure(dispatcher), new User(userLogin))
					.loadDomainsDirectory().getLedgerDomain()
					.getLedgerRepository();

			String fechaContable = (String) context.get("fechaContable");
			String UE = (String) context.get("unidadEjecutora");
			String UO = getParentParty(UE, dispatcher);
			String UR = getParentParty(UO, dispatcher);
			
			String nivel5 = (String) context.get("idN5");
			String conceptoRUB = getParentProductCategory(nivel5, dispatcher);
			String clase = getParentProductCategory(conceptoRUB, dispatcher);
			String tipo = getParentProductCategory(clase, dispatcher);
			String rubro = getParentProductCategory(tipo, dispatcher);

			String SubFuenteEspecifica = (String) context.get("subFuenteEsp");
			String SubFuente = getParentEnumeration(SubFuenteEspecifica,
					dispatcher);
			String Fuente = getParentEnumeration(SubFuente, dispatcher);
			
			String subFuncion = (String) context.get("subfuncion");
			String funcion = getParentEnumeration(subFuncion, dispatcher);
			String finalidad = getParentEnumeration(funcion, dispatcher);
			
			String actividad = (String) context.get("actividad");
			String subprogramap = getParentWorkEffort(actividad, dispatcher);
			String programa = getParentWorkEffort(subprogramap, dispatcher);
			String plan = getParentWorkEffort(programa, dispatcher);
			
			String PE = (String) context.get("partidaEspecifica");
			String PG = getParentProductCategory(PE, dispatcher);
			String conceptoPG = getParentProductCategory(PG, dispatcher);
			String capitulo = getParentProductCategory(conceptoPG, dispatcher);
			
			String area = ((String) context.get("area"));
			String subSector = getParentEnumeration(area, dispatcher);
			String sector = getParentEnumeration(subSector, dispatcher);
			

			AcctgTransPresupuestal presupuestal = new AcctgTransPresupuestal();
			presupuestal.initRepository(ledgerRepository);

			presupuestal.setAcctgTransId(acctgTransId);
			presupuestal.setClavePres((String) context.get("clave"));
			presupuestal.setCiclo(String.valueOf((getDateTransaction(
					fechaContable).getYear() + 1900)));
			presupuestal.setUnidadResponsable(UR);
			presupuestal.setUnidadOrganizacional(UO);
			presupuestal.setUnidadEjecutora(UE);
			
			presupuestal.setRubro(rubro);
			presupuestal.setTipo(tipo);
			presupuestal.setClase(clase);
			presupuestal.setConceptoRub(conceptoRUB);
			presupuestal.setNivel5(nivel5);
			
			presupuestal.setFuente(Fuente);
			presupuestal.setSubFuente(SubFuente);
			presupuestal.setSubFuenteEspecifica(SubFuenteEspecifica);
			
			presupuestal.setEntidadFederativa((String) context
					.get("EntidadFederativa"));
			presupuestal.setRegion((String) context.get("Region"));
			presupuestal.setMunicipio((String) context.get("Municipio"));
			presupuestal.setLocalidad((String) context.get("Localidad"));
			presupuestal.setAgrupador((String) context.get("referencia"));
			
			presupuestal.setFinalidad(finalidad);
			presupuestal.setFuncion(funcion);
			presupuestal.setSubFuncion(subFuncion);
			
			presupuestal.setProgramaPlan(plan);
			presupuestal.setProgramaPresupuestario(programa);
			presupuestal.setSubProgramaPresupuestario(subprogramap);			
			presupuestal.setActividad(actividad);	
			
			presupuestal.setTipoGasto((String) context.get("tipoGasto"));
			
			presupuestal.setCapitulo(capitulo);
			presupuestal.setConcepto(conceptoPG);
			presupuestal.setPartidaGenerica(PG);
			presupuestal.setPartidaEspecifica((String) context.get("partidaEspecifica"));
			
			presupuestal.setSector(sector);
			presupuestal.setSubSector(subSector);
			presupuestal.setArea(area);
			
			presupuestal.setAgrupador((String) context.get("referencia"));
			// create
			// presupuestal.setNextSubSeqId(AcctgTransEntry.Fields.acctgTransEntrySeqId.name());
			ledgerRepository.createOrUpdate(presupuestal);

			Map results = ServiceUtil.returnSuccess();
			results.put("acctgTransId", presupuestal.getAcctgTransId());
			return results;
		} catch (GeneralException e) {
			return UtilMessage.createAndLogServiceError(e, MODULE);
		}
	}

	private static String getParentProductCategory(String id,
			LocalDispatcher dispatcher) {
		String parentProduct = null;
		try {
			
			EntityCondition condicion = EntityCondition.makeCondition("productCategoryId",
					id);
			List<GenericValue> workeffort = dispatcher.getDelegator()
					.findByCondition("ProductCategory", condicion,
							UtilMisc.toList("productCategoryId", "primaryParentCategoryId"), null);

			for (GenericValue genericValue : workeffort) {
				if(genericValue.get("primaryParentCategoryId") != null || !genericValue.get("primaryParentCategoryId").toString().equals(""))
				{
					Debug.log("productCategoryId" + genericValue.get("productCategoryId").toString());
					Debug.log("primaryParentCategoryId"
							+ genericValue.get("primaryParentCategoryId").toString());
					
					parentProduct = genericValue.get("primaryParentCategoryId").toString();
				}
				
			}
			
		} catch (Exception e) {
			Debug.log("Error al obtener Parent de ProductCategory Id ["
					+ parentProduct + "] " + e);
		}
		return parentProduct;
	}

	private static String getParentWorkEffort(String id,
			LocalDispatcher dispatcher) {
		
		String parentwork = null;
		try {
			
			EntityCondition condicion = EntityCondition.makeCondition("workEffortId",
					id);
			List<GenericValue> workeffort = dispatcher.getDelegator()
					.findByCondition("WorkEffort", condicion,
							UtilMisc.toList("workEffortId", "workEffortParentId"), null);

			for (GenericValue genericValue : workeffort) {
				if(genericValue.get("workEffortParentId") != null || !genericValue.get("workEffortParentId").toString().equals(""))
				{
					Debug.log("workEffortId" + genericValue.get("workEffortId").toString());
					Debug.log("workEffortParentId"
							+ genericValue.get("workEffortParentId").toString());
					
					parentwork = genericValue.get("workEffortParentId").toString();
				}
				
			}
			
		} catch (Exception e) {
			Debug.log("Error al obtener Parent de WorkEffort Id ["
					+ parentwork + "] " + e);
		}
		return parentwork;

	}

	private static String getParentEnumeration(String subFuenteEspecificaId,
			LocalDispatcher dispatcher) {
		String parentEnum = null;
		try {

			EntityCondition condicion = EntityCondition.makeCondition("enumId",
					subFuenteEspecificaId);
			List<GenericValue> enumeration = dispatcher.getDelegator()
					.findByCondition("Enumeration", condicion,
							UtilMisc.toList("enumId", "parentEnumId"), null);

			for (GenericValue genericValue : enumeration) {
				if(genericValue.get("parentEnumId") != null || !genericValue.get("parentEnumId").toString().equals(""))
				{
					Debug.log("enumId" + genericValue.get("enumId").toString());
					Debug.log("parentEnumId"
							+ genericValue.get("parentEnumId").toString());
					
					parentEnum = genericValue.get("parentEnumId").toString();
				}
				
			}
			
		} catch (Exception e) {
			Debug.log("Error al obtener Parent de Enumeration Id ["
					+ subFuenteEspecificaId + "] " + e);
		}
		return parentEnum;
	}

	private static String getParentParty(String partyId,
			LocalDispatcher dispatcher) {
		String parentParty = null;
		try {

			EntityCondition condicion = EntityCondition.makeCondition(
					"partyId", partyId);
			List<GenericValue> partys = dispatcher.getDelegator()
					.findByCondition("PartyGroup", condicion,
							UtilMisc.toList("partyId", "Parent_id"), null);

			for (GenericValue genericValue : partys) {
				Debug.log("partyId" + genericValue.get("partyId").toString());
				Debug.log("parentId" + genericValue.get("Parent_id").toString());
				parentParty = genericValue.get("Parent_id").toString();
			}

		} catch (Exception e) {
			Debug.log("Error al obtener Parent de Party Id [" + partyId + "] "
					+ e);
		}
		return parentParty;
	}

	public static Map createAcctgTransBugetManual(DispatchContext dctx,
			Map context) throws GenericServiceException {

		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = UtilCommon.getLocale(context);
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Debug.log("Si entro al createAcctgTransBugetManual");

		try {
			LedgerRepositoryInterface ledgerRepository = new DomainsLoader(
					new Infrastructure(dispatcher), new User(userLogin))
					.loadDomainsDirectory().getLedgerDomain()
					.getLedgerRepository();
			String acctgTransId = ledgerRepository.getNextSeqId("AcctgTrans");
			Debug.log("acctgTransId " + acctgTransId);
			
			AcctgTrans acctgTrans = new AcctgTrans();
			acctgTrans.initRepository(ledgerRepository);
			//acctgTrans.setAllFields(context);
			Date fecha = getDateTransaction((String) context.get("transactionDate"));
			Timestamp timestamp = new Timestamp(fecha.getTime());
			String acctgTransTypeId = (String) context.get("acctgTransTypeId");
			BigDecimal postedAmount = new BigDecimal((Double) context.get("postedAmount"));
			
			acctgTrans.setPartyId((String) context.get("partyId"));			
			acctgTrans.setDescription((String) context.get("description"));			
			acctgTrans.setAcctgTransTypeId(acctgTransTypeId);
			acctgTrans.setPostedAmount(postedAmount);
			
			acctgTrans.setCreatedByUserLogin(userLogin.getString("userLoginId"));
			acctgTrans.setLastModifiedByUserLogin(userLogin.getString("userLoginId"));
			
			acctgTrans.setTransactionDate(timestamp);
			
			String mes =  getFormatMes(fecha.getMonth());
			String annio = String.valueOf(fecha.getYear() + 1900).substring(2);
			if(acctgTransTypeId.equals("TINGRESOESTIMADO"))
			{
				acctgTransId = acctgTransId + " I" + annio + "-" + mes; 
			}
			else if(acctgTransTypeId.equals("TPRESUPAPROBADO"))
			{
				acctgTransId = acctgTransId + " E" + annio + "-" + mes; 
			}
			//acctgTrans.setDescription(description);
			acctgTrans.setAcctgTransId(acctgTransId);
			// create
			// presupuestal.setNextSubSeqId(AcctgTransEntry.Fields.acctgTransEntrySeqId.name());
			ledgerRepository.createOrUpdate(acctgTrans);

			Map results = ServiceUtil.returnSuccess();
			results.put("acctgTransId", acctgTrans.getAcctgTransId());
			return results;
		} catch (GeneralException e) {
			return UtilMessage.createAndLogServiceError(e, MODULE);
		}
	}
	
	
	private static String getFormatMes(int month) {
		
		String Mes = "";
		try {
			
			switch (month) {
			case 0:	
				Mes = "01";
				break;
			case 1:
				Mes = "02";
				break;
			case 2:	
				Mes = "03";
				break;
			case 3:	
				Mes = "04";
				break;
			case 4:		
				Mes = "05";
				break;
			case 5:			
				Mes = "06";
				break;
			case 6:		
				Mes = "07";
				break;
			case 7:		
				Mes = "08";
				break;
			case 8:		
				Mes = "09";
				break;
			case 9:		
				Mes = "10";
				break;
			case 10:	
				Mes = "11";
				break;
			case 11:	
				Mes = "12";
				break;

			default:
				break;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return Mes;
		
	}

	/**
	 * Create a Quick <code>AcctgTrans</code> record. IsPosted is forced to "N".
	 * Creates an Quick AcctgTrans and two offsetting AcctgTransEntry records.
	 * 
	 * @param dctx
	 *            a <code>DispatchContext</code> value
	 * @param context
	 *            a <code>Map</code> value
	 * @return a <code>Map</code> value
	 */
	@SuppressWarnings("unchecked")
	public static Map createAcctgTransPresupuestoEgreso(DispatchContext dctx,
			Map context) {
		Debug.log("Si entro al servicio createAcctgTransPresupuestoEgreso");

		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		try {
			DomainsLoader dl = new DomainsLoader(
					new Infrastructure(dispatcher), new User(userLogin));
			OrganizationRepositoryInterface organizationRepository = dl
					.loadDomainsDirectory().getOrganizationDomain()
					.getOrganizationRepository();

			String organizationPartyId = (String) context
					.get("organizationPartyId");

			//String glFiscalTypeId = (String) context.get("glFiscalTypeId");
			String acctgTransTypeId = (String) context.get("acctgTransTypeId");
			String subfuncion = (String) context.get("subfuncion");
			String actividad = (String) context.get("actividad");
			String tipoGasto = (String) context.get("tipoGasto");
			String partidaEspecifica = (String) context.get("partidaEspecifica");
			String area = (String) context.get("area");
			String EntidadFederativa = (String) context
					.get("EntidadFederativa");
			String Region = (String) context.get("Region");
			String Municipio = (String) context.get("Municipio");
			String Localidad = (String) context.get("Localidad");
			String fechaTransaccion = (String) context.get("fechaTransaccion");
			String fechaContable = (String) context.get("fechaContable");
			String clave = (String) context.get("clave");
			//String monto = (String) context.get("amount");
			String referencia = (String) context.get("referencia");
			String descripcion = (String) context.get("description");
			String unidadEjecutora = (String) context.get("unidadEjecutora");
			Double amount = (Double) context.get("amount");
			Debug.log("organizacion" + organizationPartyId);
			//Debug.log("glFiscalTypeId" + glFiscalTypeId);
			Debug.log("acctgTransTypeId" + acctgTransTypeId);
			Debug.log("area" + subfuncion);
			Debug.log("actividad" + actividad);
			Debug.log("tipoGasto" + tipoGasto);
			Debug.log("partidaEspecifica" + partidaEspecifica);
			Debug.log("area" + area);
			Debug.log("EntidadFederativa" + EntidadFederativa);
			Debug.log("Region" + Region);
			Debug.log("Municipio" + Municipio);
			Debug.log("Localidad" + Localidad);
			Debug.log("fechaTransaccion" + fechaTransaccion);
			Debug.log("fechaContable" + fechaContable);
			Debug.log("clave" + clave);
			//Debug.log("monto" + monto);
			Debug.log("referencia" + referencia);
			Debug.log("descripcion" + descripcion);
			Debug.log("amount" + amount);

			Organization organization = organizationRepository
					.getOrganizationById(organizationPartyId);

			Date fechaTrasac = getDateTransaction(fechaTransaccion);
			Date fechaConta = getDateTransaction(fechaContable);
			// create the accounting transaction
			
			String TypeTransId = getAcctgTransTypeId("TPRESUPAPROBADO", dispatcher);

			Map createAcctgTransCtx = dctx.getModelService("createAcctgTransBugetManual")
					.makeValid(context, ModelService.IN_PARAM);
			if (UtilValidate.isEmpty(createAcctgTransCtx
					.get("fechaTransaccion"))) {				
				
				
				createAcctgTransCtx.put("transactionDate", fechaTransaccion);
				createAcctgTransCtx.put("description", clave + "-" +  getFormatMes (fechaConta.getMonth()));
				createAcctgTransCtx.put("acctgTransTypeId", "TPRESUPAPROBADO");
				createAcctgTransCtx.put("glFiscalTypeId" ,TypeTransId);
				createAcctgTransCtx.put("partyId" ,unidadEjecutora);
				//createAcctgTransCtx.put("createdByUserLogin" ,"admin");
				createAcctgTransCtx.put("postedAmount", amount);
			}

			Map results = dispatcher.runSync("createAcctgTransBugetManual",
					createAcctgTransCtx);

			if (!UtilCommon.isSuccess(results)) {
				Debug.log("results" + results.toString());
				return UtilMessage.createAndLogServiceError(results, MODULE);

			}
			String acctgTransId = (String) results.get("acctgTransId");
			Debug.log("acctgTransId" + acctgTransId);

			UtilCommon.isSuccess(createAcctgPresupuestal(context, acctgTransId,
					dctx));

			// create both debit and credit entries
			String currencyUomId = (String) context.get("currencyUomId");
			if (UtilValidate.isEmpty(currencyUomId)) {
				PartyAcctgPreference partyAcctgPref = organization
						.getPartyAcctgPreference();
				if (partyAcctgPref != null) {
					currencyUomId = partyAcctgPref.getBaseCurrencyUomId();
				} else {
					Debug.logWarning(
							"No accounting preference found for organization: "
									+ organizationPartyId, MODULE);
				}
			}

			EntityCondition condicion = EntityCondition.makeCondition(
					"acctgTransTypeId", acctgTransTypeId);
			List<GenericValue> cuentas = dispatcher
					.getDelegator()
					.findByCondition("MiniGuiaContable", condicion,
							UtilMisc.toList("cuentaCargo", "cuentaAbono"), null);

			String debitGlAccountId = "";
			String creditGlAccountId = "";

			for (GenericValue genericValue : cuentas) {
				Debug.log("cuentaCargo"
						+ genericValue.get("cuentaCargo").toString());
				Debug.log("cuentaAbono"
						+ genericValue.get("cuentaAbono").toString());
				debitGlAccountId = genericValue.get("cuentaCargo").toString();
				creditGlAccountId = genericValue.get("cuentaAbono").toString();
			}

			String rubro = (String) context.get("idRubro");
			Debug.log("idRubro" + rubro);
			// debit entry, using createAcctgTransEntryManual which validate the
			// accounting tags, the tags for are prefixed by "debitTagEnumId"
			Map createAcctgTransEntryCtx = dctx.getModelService(
					"createAcctgTransEntryManual").makeValid(context,
					ModelService.IN_PARAM);

			Map debitCtx = new HashMap(createAcctgTransEntryCtx);
			UtilAccountingTags.addTagParameters(context, debitCtx,
					"debitTagEnumId", UtilAccountingTags.ENTITY_TAG_PREFIX);
			debitCtx.put("acctgTransId", acctgTransId);
			debitCtx.put("glAccountId", debitGlAccountId);
			debitCtx.put("debitCreditFlag", "D");
			debitCtx.put("acctgTransEntryTypeId", "_NA_");
			debitCtx.put("currencyUomId", currencyUomId);
			debitCtx.put("acctgTagEnumId1", subfuncion);
			debitCtx.put("acctgTagEnumId2", tipoGasto);
			debitCtx.put("acctgTagEnumId3", (String) context.get("subFuenteEsp"));
			debitCtx.put("acctgTagEnumId4", area);
			debitCtx.put("partyId", organizationPartyId);
			debitCtx.put("organizationPartyId", organizationPartyId);

			results = dispatcher.runSync("createAcctgTransEntryManual",
					debitCtx);

			// credit entry, the tags for are prefixed by "creditTagEnumId"
			Map creditCtx = new HashMap(createAcctgTransEntryCtx);
			UtilAccountingTags.addTagParameters(context, creditCtx,
					"creditTagEnumId", UtilAccountingTags.ENTITY_TAG_PREFIX);
			creditCtx.put("acctgTransId", acctgTransId);
			creditCtx.put("glAccountId", creditGlAccountId);
			creditCtx.put("debitCreditFlag", "C");
			creditCtx.put("acctgTransEntryTypeId", "_NA_");
			creditCtx.put("currencyUomId", currencyUomId);
			creditCtx.put("acctgTagEnumId1", subfuncion);
			creditCtx.put("acctgTagEnumId1", tipoGasto);
			creditCtx.put("acctgTagEnumId3", (String) context.get("subFuenteEsp"));
			creditCtx.put("acctgTagEnumId4", area);
			creditCtx.put("partyId", organizationPartyId);
			creditCtx.put("organizationPartyId", organizationPartyId);
			results = dispatcher.runSync("createAcctgTransEntryManual",
					creditCtx);
			
			//AcctgTransEntry.Fields.acctgTagEnumId1

			results = ServiceUtil.returnSuccess();
			results.put("acctgTransId", acctgTransId);

			postedTransaccion(acctgTransId, fechaConta, dctx, userLogin);
			glOrganizationHistory(debitGlAccountId, creditGlAccountId, dctx,
					organizationPartyId, userLogin, amount, fechaConta);
			return results;

		} catch (GeneralException e) {
			return UtilMessage.createAndLogServiceError(e, MODULE);
		}
	}

	private static String getAcctgTransTypeId(String tipo, LocalDispatcher dispatcher) {
		String AcctgTransTypeId = "";
		try {
			
			
			EntityCondition condicion = EntityCondition.makeCondition(
					"acctgTransTypeId", tipo);
			List<GenericValue> partys = dispatcher.getDelegator()
					.findByCondition("MiniGuiaContable", condicion,
							UtilMisc.toList("acctgTransTypeId", "glFiscalTypeIdPres"), null);

			for (GenericValue genericValue : partys) {
				Debug.log("acctgTransTypeId" + genericValue.get("acctgTransTypeId").toString());
				Debug.log("glFiscalTypeIdPres" + genericValue.get("glFiscalTypeIdPres").toString());
				AcctgTransTypeId = genericValue.get("glFiscalTypeIdPres").toString();
			}

			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return AcctgTransTypeId;
	}

}
