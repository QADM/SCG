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
import org.opentaps.base.entities.AcctgTransOrgPresupIng;
import org.opentaps.base.entities.AcctgTransPresupuestal;
import org.opentaps.base.entities.AcctgTransType;
import org.opentaps.base.entities.Enumeration;
import org.opentaps.base.entities.GlAccount;
import org.opentaps.base.entities.GlAccountOrganization;
import org.opentaps.base.entities.GlFiscalType;
import org.opentaps.base.entities.NivelPresupuestal;
import org.opentaps.base.entities.Party;
import org.opentaps.base.entities.PartyAcctgPreference;
import org.opentaps.base.entities.PartyGroup;
import org.opentaps.base.entities.ProductCategory;
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

		// possible fields we're searching by
		String partyId = ac.getParameter("partyId");
		String acctgTransId = ac.getParameter("findAcctgTransId");
		String glFiscalTypeId = ac.getParameter("glFiscalTypeId");
		String geoIdEntidad = ac.getParameter("geoId");
		// String geoSubFuente = ac.getParameter("geoId");

		if (UtilValidate.isEmpty(glFiscalTypeId)) {
			glFiscalTypeId = "ACTUAL";
		}

		ac.put("glFiscalTypeId", glFiscalTypeId);

		if (!UtilValidate.isEmpty(geoIdEntidad)) {
			Debug.log("geoIdEntidad " + geoIdEntidad);
		}

		DomainsDirectory dd = DomainsDirectory.getDomainsDirectory(ac);
		final LedgerRepositoryInterface ledgerRepository = dd.getLedgerDomain()
				.getLedgerRepository();

		// TODO: Put a currencyUomId on AcctgTrans and modify postAcctgTrans to
		// set that in addition to postedAmount,
		// instead of using the organization's base currency
		OrganizationRepositoryInterface organizationRepository = dd
				.getOrganizationDomain().getOrganizationRepository();
		Organization organization = organizationRepository
				.getOrganizationById(organizationPartyId);
		if (organization != null) {
			ac.put("orgCurrencyUomId", organization.getPartyAcctgPreference()
					.getBaseCurrencyUomId());
		}

		List<Map<String, Object>> categoryEntry = new FastList<Map<String, Object>>();
		List<Map<String, Object>> categoryType = new FastList<Map<String, Object>>();
		List<Map<String, Object>> categoryClass = new FastList<Map<String, Object>>();
		List<Map<String, Object>> categoryConcept = new FastList<Map<String, Object>>();
		List<Map<String, Object>> categoryN5 = new FastList<Map<String, Object>>();
		List<Map<String, Object>> subFuenteE = new FastList<Map<String, Object>>();
		List<Map<String, Object>> unidadE = new FastList<Map<String, Object>>();

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

		// listpartys.get(0).getPartyGroup().getGroupName();

		ac.put("listallcategory", categoryEntry);
		ac.put("listallcategoryType", categoryType);
		ac.put("listallcategoryClass", categoryClass);
		ac.put("listallcategoryConc", categoryConcept);
		ac.put("listallcategoryN", categoryN5);
		ac.put("listSubFuente", subFuenteE);
		ac.put("listUnidadE", unidadE);

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

			Map createAcctgTransCtx = dctx.getModelService("createAcctgTrans")
					.makeValid(context, ModelService.IN_PARAM);
			if (UtilValidate.isEmpty(createAcctgTransCtx
					.get("fechaTransaccion"))) {
				createAcctgTransCtx.put("transactionDate", fechaTrasac);

			}

			Map results = dispatcher.runSync("createAcctgTrans",
					createAcctgTransCtx);

			if (!UtilCommon.isSuccess(results)) {
				Debug.log("results" + results.toString());
				return UtilMessage.createAndLogServiceError(results, MODULE);

			}
			String acctgTransId = (String) results.get("acctgTransId");
			Debug.log("acctgTransId" + acctgTransId);

			// create createAcctgTransPresupuestalManual

			// Map createAcctgTransPresupuestalCtx = dctx.getModelService(
			// "createAcctgTransPresupuestalManual").makeValid(context,
			// ModelService.IN_PARAM);
			//
			// Map acctranspresu = new HashMap(createAcctgTransPresupuestalCtx);
			// acctranspresu.put("acctgTransId", acctgTransId);
			// acctranspresu.put("clavePres", clave);
			// acctranspresu.put("ciclo", "2013");
			// acctranspresu.put("unidadResponsable", organizationPartyId);
			// acctranspresu.put("unidadOrganizacional", organizationPartyId);
			// acctranspresu.put("unidadEjecutora", organizationPartyId);
			// acctranspresu.put("rubro", idRubro);
			// acctranspresu.put("tipo", idTipo);
			// acctranspresu.put("conceptoRub", idConcepto);
			// acctranspresu.put("nivel5", idN5);
			// results =
			// dispatcher.runSync("createAcctgTransPresupuestalManual",
			// createAcctgTransPresupuestalCtx);

			// /

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
			results = dispatcher.runSync("createAcctgTransEntryManual",
					creditCtx);

			results = ServiceUtil.returnSuccess();
			results.put("acctgTransId", acctgTransId);

			postedTransaccion(acctgTransId, fechaConta, dctx, userLogin);
			glOrganizationHistory(debitGlAccountId, creditGlAccountId, dctx,
					organizationPartyId, userLogin, amount);
			return results;

		} catch (GeneralException e) {
			return UtilMessage.createAndLogServiceError(e, MODULE);
		}
	}

	private static void glOrganizationHistory(String debitGlAccountId,
			String creditGlAccountId, DispatchContext dctx,
			String organizationPartyId, GenericValue userLogin, double amount)
			throws RepositoryException {

		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		LedgerRepositoryInterface ledgerRepository = new DomainsLoader(
				new Infrastructure(dispatcher), new User(userLogin))
				.loadDomainsDirectory().getLedgerDomain().getLedgerRepository();

		BigDecimal monto = new BigDecimal(amount);
		String cuenta = debitGlAccountId;
		try {

			for (int i = 0; i < 2; i++) {

				if (i != 0)
					cuenta = creditGlAccountId;

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

			}

		} catch (Exception e) {

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

			String SubFuenteEspecifica = (String) context.get("subFuenteEsp");
			String SubFuente = getParentEnumeration(SubFuenteEspecifica,
					dispatcher);
			String Fuente = getParentEnumeration(SubFuente, dispatcher);

			AcctgTransPresupuestal presupuestal = new AcctgTransPresupuestal();
			presupuestal.initRepository(ledgerRepository);

			presupuestal.setAcctgTransId(acctgTransId);
			presupuestal.setClavePres((String) context.get("clave"));
			presupuestal.setCiclo(String.valueOf((getDateTransaction(
					fechaContable).getYear() + 1900)));
			presupuestal.setUnidadResponsable(UR);
			presupuestal.setUnidadOrganizacional(UO);
			presupuestal.setUnidadEjecutora(UE);
			presupuestal.setRubro((String) context.get("idRubro"));
			presupuestal.setTipo((String) context.get("idTipo"));
			presupuestal.setClase((String) context.get("idClase"));
			presupuestal.setConceptoRub((String) context.get("idConcepto"));
			presupuestal.setNivel5((String) context.get("idN5"));
			presupuestal.setFuente(Fuente);
			presupuestal.setSubFuente(SubFuente);
			presupuestal.setSubFuenteEspecifica(SubFuenteEspecifica);
			presupuestal.setEntidadFederativa((String) context
					.get("EntidadFederativa"));
			presupuestal.setRegion((String) context.get("Region"));
			presupuestal.setMunicipio((String) context.get("Municipio"));
			presupuestal.setLocalidad((String) context.get("Localidad"));
			presupuestal.setSecuencia((String) context.get("referencia"));

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

	private static String getParentEnumeration(String subFuenteEspecificaId,
			LocalDispatcher dispatcher) {
		String parentEnum = "";
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
		String parentParty = "";
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

	public static Map createAcctgTransPresupuestalManual(DispatchContext dctx,
			Map context) throws GenericServiceException {

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

			AcctgTransPresupuestal presupuestal = new AcctgTransPresupuestal();
			presupuestal.initRepository(ledgerRepository);
			presupuestal.setAllFields(context);

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

}
