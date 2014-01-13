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
package org.opentaps.common.domain.organization;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.opentaps.common.domain.party.PartyRepository;
import org.opentaps.common.util.UtilAccountingTags;
import org.opentaps.common.util.UtilClassification;
import org.opentaps.common.util.UtilDate;
import org.opentaps.base.constants.GlAccountTypeConstants;
import org.opentaps.base.constants.PeriodTypeConstants;
import org.opentaps.base.constants.RoleTypeConstants;
import org.opentaps.base.entities.AcctgTagEnumType;
import org.opentaps.base.entities.AcctgTagInvoiceType;
import org.opentaps.base.entities.AgreementTermTypesByDocumentType;
import org.opentaps.base.entities.ClasifPresupuestal;
import org.opentaps.base.entities.CustomTimePeriod;
import org.opentaps.base.entities.Enumeration;
import org.opentaps.base.entities.EnumerationType;
import org.opentaps.base.entities.EstructuraClave;
import org.opentaps.base.entities.Geo;
import org.opentaps.base.entities.GeoType;
import org.opentaps.base.entities.GlAccountTypeDefault;
import org.opentaps.base.entities.InvoiceItem;
import org.opentaps.base.entities.NivelPresupuestal;
import org.opentaps.base.entities.Party;
import org.opentaps.base.entities.PartyAcctgPreference;
import org.opentaps.base.entities.PartyGroup;
import org.opentaps.base.entities.PartyRole;
import org.opentaps.base.entities.PaymentMethod;
import org.opentaps.base.entities.ProductCategoryType;
import org.opentaps.base.entities.TermType;
import org.opentaps.base.entities.WorkEffort;
import org.opentaps.base.services.ConvertUomService;
import org.opentaps.domain.DomainsDirectory;
import org.opentaps.domain.ledger.LedgerRepositoryInterface;
import org.opentaps.domain.organization.AccountingTagConfigurationForOrganizationAndUsage;
import org.opentaps.domain.organization.ClassificationConfigurationForOrganization;
import org.opentaps.domain.organization.Organization;
import org.opentaps.domain.organization.OrganizationRepositoryInterface;
import org.opentaps.domain.organization.ReadConfigurationForOrganization;
import org.opentaps.domain.party.PartyRepositoryInterface;
import org.opentaps.foundation.entity.Entity;
import org.opentaps.foundation.entity.EntityInterface;
import org.opentaps.foundation.entity.EntityNotFoundException;
import org.opentaps.foundation.entity.hibernate.Query;
import org.opentaps.foundation.entity.hibernate.Session;
import org.opentaps.foundation.infrastructure.InfrastructureException;
import org.opentaps.foundation.repository.RepositoryException;
import org.opentaps.foundation.service.ServiceException;

/**
 * {@inheritDoc}
 */
public class OrganizationRepository extends PartyRepository implements
		OrganizationRepositoryInterface {

	private static final String MODULE = OrganizationRepositoryInterface.class
			.getName();
	private Session session;

	/** List the available fiscal period types. */
	public static List<String> FISCAL_PERIOD_TYPES = Arrays.asList(
			PeriodTypeConstants.FISCAL_YEAR,
			PeriodTypeConstants.FISCAL_QUARTER,
			PeriodTypeConstants.FISCAL_MONTH, PeriodTypeConstants.FISCAL_WEEK,
			PeriodTypeConstants.FISCAL_BIWEEK);

	/**
	 * Default constructor.
	 */
	public OrganizationRepository() {
		super();
	}

	/**
	 * Constructor with delegator.
	 * 
	 * @param delegator
	 *            a <code>Delegator</code> value
	 * @deprecated for legacy support only
	 */
	@Deprecated
	public OrganizationRepository(Delegator delegator) {
		super(delegator);
	}

	/** {@inheritDoc} */
	public Organization getOrganizationById(String organizationPartyId)
			throws RepositoryException, EntityNotFoundException {
		if (UtilValidate.isEmpty(organizationPartyId)) {
			return null;
		}

		PartyRole role = findOneNotNull(
				PartyRole.class,
				map(PartyRole.Fields.partyId, organizationPartyId,
						PartyRole.Fields.roleTypeId,
						RoleTypeConstants.INTERNAL_ORGANIZATIO),
				"Organization [" + organizationPartyId
						+ "] not found with role INTERNAL_ORGANIZATIO");
		return role.getRelatedOne(Organization.class, "Party");
	}

	/** {@inheritDoc} */
	public List<CustomTimePeriod> getAllFiscalTimePeriods(
			String organizationPartyId) throws RepositoryException {
		return findList(
				CustomTimePeriod.class,
				map(CustomTimePeriod.Fields.organizationPartyId,
						organizationPartyId));
	}

	/** {@inheritDoc} */
	public List<CustomTimePeriod> getOpenFiscalTimePeriods(
			String organizationPartyId) throws RepositoryException {
		return getOpenFiscalTimePeriods(organizationPartyId,
				UtilDateTime.nowTimestamp());
	}

	/** {@inheritDoc} */
	public List<CustomTimePeriod> getOpenFiscalTimePeriods(
			String organizationPartyId, Timestamp asOfDate)
			throws RepositoryException {
		return getOpenFiscalTimePeriods(organizationPartyId,
				FISCAL_PERIOD_TYPES, asOfDate);
	}

	/** {@inheritDoc} */
	public List<CustomTimePeriod> getOpenFiscalTimePeriods(
			String organizationPartyId, List<String> fiscalPeriodTypes,
			Timestamp asOfDate) throws RepositoryException {
		// isClosed must either be null or N. This or conditions prevents an
		// issue where rows with null values are not selected
		EntityConditionList<EntityCondition> conditions = EntityCondition
				.makeCondition(
						EntityOperator.AND,
						EntityCondition.makeCondition(
								CustomTimePeriod.Fields.organizationPartyId
										.name(), organizationPartyId),
						EntityCondition.makeCondition(
								CustomTimePeriod.Fields.periodTypeId.name(),
								EntityOperator.IN, fiscalPeriodTypes),
						EntityUtil.getFilterByDateExpr(
								UtilDate.timestampToSqlDate(asOfDate),
								CustomTimePeriod.Fields.fromDate.name(),
								CustomTimePeriod.Fields.thruDate.name()),
						EntityCondition
								.makeCondition(
										EntityOperator.OR,
										EntityCondition
												.makeCondition(
														CustomTimePeriod.Fields.isClosed
																.name(), null),
										EntityCondition
												.makeCondition(
														CustomTimePeriod.Fields.isClosed
																.name(), "N")));
		return findList(CustomTimePeriod.class, conditions);
	}

	/** {@inheritDoc} */
	public PaymentMethod getDefaultPaymentMethod(String organizationPartyId)
			throws RepositoryException {
		PaymentMethod defaultPaymentMethod = null;
		GlAccountTypeDefault glAccountTypeDefault = findOne(
				GlAccountTypeDefault.class,
				map(GlAccountTypeDefault.Fields.organizationPartyId,
						organizationPartyId,
						GlAccountTypeDefault.Fields.glAccountTypeId,
						GlAccountTypeConstants.BANK_STLMNT_ACCOUNT));
		if (glAccountTypeDefault != null) {
			defaultPaymentMethod = getFirst(findList(
					PaymentMethod.class,
					map(PaymentMethod.Fields.partyId, organizationPartyId,
							PaymentMethod.Fields.glAccountId,
							glAccountTypeDefault.getGlAccountId())));
		}
		return defaultPaymentMethod;
	}

	/** {@inheritDoc} */
	public BigDecimal determineUomConversionFactor(String organizationPartyId,
			String currencyUomId) throws RepositoryException {
		return determineUomConversionFactor(organizationPartyId, currencyUomId,
				UtilDateTime.nowTimestamp());
	}

	/** {@inheritDoc} */
	public BigDecimal determineUomConversionFactor(String organizationPartyId,
			String currencyUomId, Timestamp asOfDate)
			throws RepositoryException {
		try {
			Organization organization = getOrganizationById(organizationPartyId);
			// default conversion factor
			BigDecimal conversionFactor = BigDecimal.ONE;
			// if currencyUomId is null, return default
			if (currencyUomId == null) {
				return conversionFactor;
			}

			// get our organization's accounting preference
			PartyAcctgPreference accountingPreference = organization
					.getPartyAcctgPreference();
			if (accountingPreference == null) {
				throw new RepositoryException(
						"Currency conversion failed: No PartyAcctgPreference entity data for organizationPartyId "
								+ organization.getPartyId());
			}

			// if the currencies are equal, return default
			if (currencyUomId.equals(accountingPreference
					.getBaseCurrencyUomId())) {
				return conversionFactor;
			}

			// this does a currency conversion, based on currencyUomId and the
			// party's accounting preferences. conversionFactor will be used for
			// postings
			ConvertUomService service = new ConvertUomService();
			service.setInOriginalValue(conversionFactor);
			service.setInUomId(currencyUomId);
			service.setInUomIdTo(accountingPreference.getBaseCurrencyUomId());
			service.setInAsOfDate(asOfDate);
			service.runSyncNoNewTransaction(getInfrastructure());

			if (service.isSuccess()) {
				conversionFactor = service.getOutConvertedValue();
			} else {
				throw new RepositoryException(
						"Currency conversion failed: No currencyUomId defined in PartyAcctgPreference entity for organizationPartyId "
								+ organization.getPartyId());
			}

			return conversionFactor;

		} catch (ServiceException e) {
			throw new RepositoryException(e);
		} catch (EntityNotFoundException e) {
			throw new RepositoryException(e);
		}
	}

	/** {@inheritDoc} */
	public Map<Integer, String> getAccountingTagTypes(
			String organizationPartyId, String accountingTagUsageTypeId)
			throws RepositoryException {
		Map<Integer, String> tagTypes = new TreeMap<Integer, String>();
		AcctgTagEnumType conf = findOneCache(
				AcctgTagEnumType.class,
				map(AcctgTagEnumType.Fields.organizationPartyId,
						organizationPartyId,
						AcctgTagEnumType.Fields.acctgTagUsageTypeId,
						accountingTagUsageTypeId));
		if (conf == null) {
			Debug.logInfo("No tag configuration found for organization ["
					+ organizationPartyId + "]", MODULE);
			return tagTypes;
		}

		// find each non null configured tag type
		for (int i = 1; i <= UtilAccountingTags.TAG_COUNT; i++) {
			String type = conf.getString("enumTypeId" + i);
			if (type != null) {
				tagTypes.put(new Integer(i), type);
			}
		}
		return tagTypes;
	}

	/** {@inheritDoc} */
	public List<AccountingTagConfigurationForOrganizationAndUsage> getAccountingTagConfiguration(
			String organizationPartyId, String accountingTagUsageTypeId)
			throws RepositoryException {

		Map<Integer, String> tagTypes = getAccountingTagTypes(
				organizationPartyId, accountingTagUsageTypeId);
		AcctgTagEnumType acctgTagEnumType = findOneCache(
				AcctgTagEnumType.class,
				map(AcctgTagEnumType.Fields.organizationPartyId,
						organizationPartyId,
						AcctgTagEnumType.Fields.acctgTagUsageTypeId,
						accountingTagUsageTypeId));
		List<AccountingTagConfigurationForOrganizationAndUsage> tagTypesAndValues = new ArrayList<AccountingTagConfigurationForOrganizationAndUsage>();

		for (Integer index : tagTypes.keySet()) {
			String type = tagTypes.get(index);
			// get if required field value, default is N
			String isRequired = acctgTagEnumType.getString("isTagEnum" + index
					+ "Required") == null ? "N" : acctgTagEnumType
					.getString("isTagEnum" + index + "Required");
			String defaultValue = acctgTagEnumType.getString("defaultTagEnumId"
					+ index);
			AccountingTagConfigurationForOrganizationAndUsage tag = new AccountingTagConfigurationForOrganizationAndUsage(
					this);
			tag.setIndex(index);
			tag.setType(type);
			tag.setDescription(findOneCache(EnumerationType.class,
					map(EnumerationType.Fields.enumTypeId, type))
					.getDescription());
			tag.setTagValues(findListCache(Enumeration.class, Arrays
					.asList(EntityCondition.makeCondition(
							Enumeration.Fields.enumTypeId.name(), type)),
					Arrays.asList(Enumeration.Fields.sequenceId.asc())));
			// filter out disabled tags
			tag.setActiveTagValues(findList(Enumeration.class,
					Arrays.asList(EntityCondition.makeCondition(
							Enumeration.Fields.enumTypeId.name(), type),
							EntityCondition.makeCondition(EntityOperator.OR,
									EntityCondition.makeCondition(
											Enumeration.Fields.disabled.name(),
											"N"), EntityCondition
											.makeCondition(
													Enumeration.Fields.disabled
															.name(), null))),
					Arrays.asList(Enumeration.Fields.sequenceId.asc())));
			// add if required property for tag
			tag.setIsRequired(isRequired);
			// add its default value
			tag.setDefaultValue(defaultValue);
			if (UtilValidate.isNotEmpty(defaultValue)) {
				tag.setDefaultValueTag(findOneCache(Enumeration.class,
						map(Enumeration.Fields.enumId, defaultValue)));
			}

			tagTypesAndValues.add(tag);
		}

		return tagTypesAndValues;
	}

	/** {@inheritDoc} */
	public List<String> getValidTermTypeIds(String documentTypeId)
			throws RepositoryException {
		List<AgreementTermTypesByDocumentType> types = findListCache(
				AgreementTermTypesByDocumentType.class,
				map(AgreementTermTypesByDocumentType.Fields.documentTypeId,
						documentTypeId));
		return Entity.getFieldValues(String.class, types,
				AgreementTermTypesByDocumentType.Fields.termTypeId);
	}

	/** {@inheritDoc} */
	public List<TermType> getValidTermTypes(String documentTypeId)
			throws RepositoryException {
		return findListCache(TermType.class, EntityCondition.makeCondition(
				TermType.Fields.termTypeId.name(), EntityOperator.IN,
				getValidTermTypeIds(documentTypeId)));
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public List<Organization> getAllValidOrganizations()
			throws RepositoryException {
		String hql = "select eo.party from PartyRole eo where eo.id.roleTypeId = 'INTERNAL_ORGANIZATIO'";
		Session session = null;
		try {
			session = getInfrastructure().getSession();
			Query query = session.createQuery(hql);
			List<Party> parties = query.list();
			return findList(Organization.class, Arrays.asList(EntityCondition
					.makeCondition(Party.Fields.partyId.name(),
							EntityOperator.IN, Entity.getDistinctFieldValues(
									parties, Party.Fields.partyId))));
		} catch (InfrastructureException e) {
			throw new RepositoryException(e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public List<AccountingTagConfigurationForOrganizationAndUsage> validateTagParameters(
			Map tags, String organizationPartyId,
			String accountingTagUsageTypeId, String prefix)
			throws RepositoryException {
		Debug.logInfo("validateTagParameters: for organization ["
				+ organizationPartyId + "] and usage ["
				+ accountingTagUsageTypeId + "]", MODULE);
		List<AccountingTagConfigurationForOrganizationAndUsage> missings = new ArrayList<AccountingTagConfigurationForOrganizationAndUsage>();
		for (AccountingTagConfigurationForOrganizationAndUsage tag : getAccountingTagConfiguration(
				organizationPartyId, accountingTagUsageTypeId)) {
			// if the tag is forced set the value, else if it is required, then
			// validate its input
			String tagName = prefix + tag.getIndex();
			Debug.logInfo(
					"validateTagParameters: tag current value = "
							+ tags.get(tagName) + ", is required ? "
							+ tag.isRequired() + ", has forced value ? "
							+ tag.hasDefaultValue() + " = "
							+ tag.getDefaultValue(), MODULE);
			if (tag.isRequired()) {
				String tagValue = (String) tags.get(tagName);
				if (UtilValidate.isEmpty(tagValue)) {
					if (tag.hasDefaultValue()) {
						tags.put(tagName, tag.getDefaultValue());
					} else {
						missings.add(tag);
					}
				}
			}
		}
		return missings;
	}

	/** {@inheritDoc} */
	public List<AccountingTagConfigurationForOrganizationAndUsage> validateTagParameters(
			EntityInterface entity, String organizationPartyId,
			String accountingTagUsageTypeId) throws RepositoryException {
		Debug.logInfo("validateTagParameters: for organization ["
				+ organizationPartyId + "] and usage ["
				+ accountingTagUsageTypeId + "]", MODULE);
		List<AccountingTagConfigurationForOrganizationAndUsage> missings = new ArrayList<AccountingTagConfigurationForOrganizationAndUsage>();
		for (AccountingTagConfigurationForOrganizationAndUsage tag : getAccountingTagConfiguration(
				organizationPartyId, accountingTagUsageTypeId)) {
			// if the tag is forced set the value, else if it is required, then
			// validate its input
			String tagName = tag.getEntityFieldName();
			Debug.logInfo(
					"validateTagParameters: tag current value = "
							+ entity.getString(tagName) + ", is required ? "
							+ tag.isRequired() + ", has forced value ? "
							+ tag.hasDefaultValue() + " = "
							+ tag.getDefaultValue(), MODULE);
			if (tag.isRequired()) {
				String tagValue = entity.getString(tagName);
				if (UtilValidate.isEmpty(tagValue)) {
					if (tag.hasDefaultValue()) {
						entity.set(tagName, tag.getDefaultValue());
					} else {
						missings.add(tag);
					}
				}
			}
		}
		return missings;
	}

	/** {@inheritDoc} */
	public List<PartyGroup> getOrganizationTemplates()
			throws RepositoryException {
		String hql = "select eo.party.partyGroup from PartyRole eo where eo.id.roleTypeId = 'ORGANIZATION_TEMPL'";
		Session session = null;
		try {
			session = getInfrastructure().getSession();
			Query query = session.createQuery(hql);
			List<PartyGroup> partyGroups = query.list();
			return partyGroups;
		} catch (InfrastructureException e) {
			throw new RepositoryException(e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	/** {@inheritDoc} */
	public List<PartyGroup> getOrganizationWithoutLedgerSetup()
			throws RepositoryException {
		String hql = "select eo.party.partyGroup from PartyRole eo where eo.id.roleTypeId='INTERNAL_ORGANIZATIO'";
		Session session = null;
		try {
			session = getInfrastructure().getSession();
			Query query = session.createQuery(hql);
			List<PartyGroup> partyGroups1 = query.list();
			hql = "select eo.party.partyGroup from PartyAcctgPreference eo";
			query = session.createQuery(hql);
			List<PartyGroup> partyGroups2 = query.list();
			List<PartyGroup> partyGroups = new ArrayList<PartyGroup>();
			for (PartyGroup partyGroup : partyGroups1) {
				// filter the party group with role type INTERNAL_ORGANIZATIO
				// and not have relate PartyAcctgPreference
				if (!partyGroups2.contains(partyGroup)) {
					partyGroups.add(partyGroup);
				}
			}
			return partyGroups;
		} catch (InfrastructureException e) {
			throw new RepositoryException(e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	/*
	 * Metodos para mostrar la estructura de las clasificaciones en pantallas
	 * (Transacciones)
	 */

	/** {@inheritDoc} */
	public Map<Integer, String> getClassificationTagTypes(
			String organizationPartyId, String accountingTagUsageTypeId)
			throws RepositoryException {
		Date date = new Date();
		Map<Integer, String> tagTypes = new TreeMap<Integer, String>();
		List<EstructuraClave> conf = findList(
				EstructuraClave.class,
				map(EstructuraClave.Fields.organizationPartyId,
						organizationPartyId,
						EstructuraClave.Fields.acctgTagUsageTypeId,
						accountingTagUsageTypeId, EstructuraClave.Fields.ciclo,
						date.getYear() + 1900));
		if (conf.isEmpty()) {
			Debug.logInfo(
					"No se encontro la configuracion de la clasificacion para la organizacion ["
							+ organizationPartyId + "]", MODULE);
			return tagTypes;
		}
		
		// find each non null configured tag type
		for (EstructuraClave estructuraClave : conf) {
			for (int i = 1; i <= UtilAccountingTags.TAG_COUNT_CLASSI; i++) {

				String type = estructuraClave.getString("clasificacion" + i);
				if (type != null) {
					tagTypes.put(new Integer(i), type);
				}
			}
		}
		Debug.log("getClassificationTagTypes tagTypes size ( "
				+ tagTypes.size());
		return tagTypes;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws InfrastructureException
	 */
	public List<ClassificationConfigurationForOrganization> getClassificationTagConfiguration(
			String organizationPartyId, String accountingTagUsageTypeId)
			throws RepositoryException {

		
		Delegator delegator = getDelegator();
		
		Map<Integer, String> tagTypes = getClassificationTagTypes(
				organizationPartyId, accountingTagUsageTypeId);
		
		List<ClassificationConfigurationForOrganization> tagTypesAndValues = new ArrayList<ClassificationConfigurationForOrganization>();

		Debug.log("getClassificationTagConfiguration tagTypes size"
				+ tagTypes.size());

		for (Integer index : tagTypes.keySet()) {
			

			String type = tagTypes.get(index);
			

			ClassificationConfigurationForOrganization tag = null;
			List<ClasifPresupuestal> listclas = findListCache(
					ClasifPresupuestal.class,
					map(ClasifPresupuestal.Fields.clasificacionId, type));
			Debug.log("getClassificationTagConfiguration list size"
					+ listclas.size());

			if (!listclas.isEmpty()) {
				for (ClasifPresupuestal clasifPresupuestal : listclas) {

					tag = new ClassificationConfigurationForOrganization(this);
					tag.setIndex(index);
					tag.setType(type);
					tag.setDescription(clasifPresupuestal.getDescripcion());
					tag.setTagValues(findListCache(
							ClasifPresupuestal.class,
							Arrays.asList(EntityCondition.makeCondition(
									ClasifPresupuestal.Fields.clasificacionId
											.name(), type)),
							Arrays.asList(ClasifPresupuestal.Fields.clasificacionId
									.asc())));
					// buscar ultimo nivel
					
					String ultimoNivel = buscaUltimoNivel(clasifPresupuestal.getClasificacionId());					
					if(ultimoNivel.isEmpty())
					{
						Debug.log("getClassificationTagConfiguration - No se encontro el ultimo nivel");
						break;
					}
					if(clasifPresupuestal.getTablaRelacion().isEmpty() || clasifPresupuestal.getTablaRelacion()==null)
					{Debug.log("getClassificationTagConfiguration - No se encontro tabla relacion");
					break;}
					
					// Metodo para obtener lista<genericValue> parametros ultimonivel, tablarelacion
					

					Debug.log("getClassificationTagConfiguration clasifPresupuestal.getTablaRelacion()"
							+ clasifPresupuestal.getTablaRelacion());

					// filter out disabled tags
					
					tag.setActiveTagValues(UtilClassification.getListaNiveles(
							clasifPresupuestal.getTablaRelacion(), ultimoNivel,
							delegator));
					
			
					// add if required property for tag
					tag.setIsRequired("Y");
					// add its default value
					tag.setDefaultValue(String.valueOf(index));
					if (UtilValidate.isNotEmpty(String.valueOf(index))) {
						tag.setDefaultValueTag(clasifPresupuestal);
					}

					tagTypesAndValues.add(tag);
				}
			}
		}
		Debug.log("getClassificationTagConfiguration tagTypesAndValues size"
				+ tagTypesAndValues.size());
		return tagTypesAndValues;
	}

	private String buscaUltimoNivel(String tipoClasificacion) {
		
		String nivel = null;		
		try {
			if(tipoClasificacion.contains("CL_GEOGRAFICA"))
				nivel = buscaHojaGeo();
			else if(tipoClasificacion.contains("CL_CRI"))
				nivel = buscaHojaCri();
			else if(tipoClasificacion.contains("CL_COG"))
				nivel = buscaHojaCog();
			else			
				nivel = buscaHojaNivelPresupuestal(tipoClasificacion);
				
			
			Debug.log("buscaUltimoNivel " + nivel);
			
		} catch (Exception e) {
			Debug.log("buscaUltimoNivel error " + e.getMessage());
		}
		return nivel;
	}
	
	
	public String buscaHojaNivelPresupuestal(String tipo)
			throws RepositoryException {
		String ultimoNivel = null;
		try{  
			List<NivelPresupuestal> niveles = findList(NivelPresupuestal.class,
					map(NivelPresupuestal.Fields.clasificacionId, tipo));
			
			if(!niveles.isEmpty())
			{
				for (NivelPresupuestal nivelPresupuestal : niveles) {
					
					Debug.log("buscaHojaNivelPresupuestal nivelPresupuestal " + nivelPresupuestal.getNivelPadreId());
					
					List<NivelPresupuestal> listUltimoNivel = findList(
							NivelPresupuestal.class,
							map(NivelPresupuestal.Fields.nivelPadreId,
									nivelPresupuestal.getNivelId()));
					
					if(listUltimoNivel.isEmpty())
					{
						ultimoNivel = nivelPresupuestal.getNivelId();
						Debug.log("buscaHojaNivelPresupuestal ultimoNivel " + ultimoNivel);
						break;
					}	
				}
			}
		}
		catch (Exception e) {
			Debug.log("buscaHojaNivelPresupuestal error " +  e.getMessage());
		}
		return ultimoNivel;
	}
	
	public String buscaHojaGeo() throws RepositoryException
	{
		boolean rama = true;
		String tipo = "COUNTRY";

		do {
			List<GeoType> geoTypes = findList(GeoType.class,
					map(GeoType.Fields.parentTypeId, tipo));
			if(!geoTypes.isEmpty()){
				tipo = geoTypes.get(0).getGeoTypeId(); 
			}else{
				rama = false;
			}
		} while (rama);
		
		Debug.log("buscaHojaGeo ULTIMONIVEL " +  tipo);
		return tipo;
	}
	
	public String buscaHojaCri()
			throws RepositoryException {
		boolean rama = true;
		String tipo = "CRI";

		do {
			List<ProductCategoryType> prodCatTypes = findList(ProductCategoryType.class,
					map(ProductCategoryType.Fields.parentTypeId, tipo));
			if(!prodCatTypes.isEmpty()){
				tipo = prodCatTypes.get(0).getProductCategoryTypeId(); 
			}else{
				rama = false;
			}
		} while (rama);
		Debug.log("buscaHojaCri ULTIMONIVEL " +  tipo);
		return tipo;
	}
	
	public String buscaHojaCog()
			throws RepositoryException {
		boolean rama = true;
		String tipo = "COG";

		do {
			List<ProductCategoryType> prodCatTypes = findList(ProductCategoryType.class,
					map(ProductCategoryType.Fields.parentTypeId, tipo));
			if(!prodCatTypes.isEmpty()){
				tipo = prodCatTypes.get(0).getProductCategoryTypeId(); 
			}else{
				rama = false;
			}
		} while (rama);
		return tipo;
	}
	
	
	/*
	 * 
	 * */
	
	/** {@inheritDoc} */
	public List<ClassificationConfigurationForOrganization> getAccountingTagConfigurationCustom(
			String organizationPartyId, String accountingTagUsageTypeId)
			throws RepositoryException {
		
		Debug.log("Esme - - getAccountingTagConfigurationCustom si entro");
		
		Delegator delegator = getDelegator();

		Map<Integer, String> tagTypes = getAccountingTagTypesPurchase(
				organizationPartyId, accountingTagUsageTypeId);
		AcctgTagInvoiceType acctgTagInvoiceType = findOneCache(
				AcctgTagInvoiceType.class,
				map(AcctgTagInvoiceType.Fields.organizationPartyId,
						organizationPartyId,
						AcctgTagInvoiceType.Fields.acctgTagUsageTypeId,
						accountingTagUsageTypeId));
		List<ClassificationConfigurationForOrganization> tagTypesAndValues = new ArrayList<ClassificationConfigurationForOrganization>();

		Debug.log("getAccountingTagConfigurationCustom tagTypes" + tagTypes);
		
		for (Integer index : tagTypes.keySet()) {
			String type = tagTypes.get(index);
			
			ClassificationConfigurationForOrganization tag = null;
			List<ClasifPresupuestal> listclas = findListCache(
					ClasifPresupuestal.class,
					map(ClasifPresupuestal.Fields.clasificacionId, type));
			Debug.log("getAccountingTagConfigurationCustom list "
					+ listclas);

			if (!listclas.isEmpty()) {
				for (ClasifPresupuestal clasifPresupuestal : listclas) {
					Debug.log("getAccountingTagConfigurationCustom type" + type);
					tag = new ClassificationConfigurationForOrganization(this);
					tag.setIndex(index);
					tag.setType(type);
					tag.setDescription(clasifPresupuestal.getDescripcion());
					tag.setTagValues(findListCache(
							ClasifPresupuestal.class,
							Arrays.asList(EntityCondition.makeCondition(
									ClasifPresupuestal.Fields.clasificacionId
											.name(), type)),
							Arrays.asList(ClasifPresupuestal.Fields.clasificacionId
									.asc())));
					// buscar ultimo nivel
					
					String ultimoNivel = buscaUltimoNivel(clasifPresupuestal.getClasificacionId());					
					if(ultimoNivel.isEmpty())
					{
						Debug.log("getAccountingTagConfigurationCustom - No se encontro el ultimo nivel");
						break;
					}
					if(clasifPresupuestal.getTablaRelacion().isEmpty() || clasifPresupuestal.getTablaRelacion()==null)
					{Debug.log("getAccountingTagConfigurationCustom - No se encontro tabla relacion");
					break;}
					
					// Metodo para obtener lista<genericValue> parametros ultimonivel, tablarelacion
					

					Debug.log("getClassificationTagConfiguration clasifPresupuestal.getTablaRelacion()"
							+ clasifPresupuestal.getTablaRelacion());

					// filter out disabled tags
					
					tag.setActiveTagValues(UtilClassification.getListaNiveles(
							clasifPresupuestal.getTablaRelacion(), ultimoNivel,
							delegator));
					
			
					// add if required property for tag
					tag.setIsRequired("Y");
					// add its default value
					tag.setDefaultValue(String.valueOf(index));
					Debug.log("getAccountingTagConfigurationCustom String.valueOf(index) " + String.valueOf(index));
					
					if (UtilValidate.isNotEmpty(String.valueOf(index))) {
						tag.setDefaultValueTag(clasifPresupuestal);
					}

					tagTypesAndValues.add(tag);
					
					Debug.log("tag " + tag);
				}
			}
		}
		Debug.log("getClassificationTagConfiguration tagTypesAndValues size"
				+ tagTypesAndValues.size());
		return tagTypesAndValues;
	}
	
	
	/** {@inheritDoc} */
	public Map<Integer, String> getAccountingTagTypesPurchase(
			String organizationPartyId, String accountingTagUsageTypeId)
			throws RepositoryException {
		Map<Integer, String> tagTypes = new TreeMap<Integer, String>();
		AcctgTagInvoiceType conf = findOneCache(
				AcctgTagInvoiceType.class,
				map(AcctgTagInvoiceType.Fields.organizationPartyId,
						organizationPartyId,
						AcctgTagInvoiceType.Fields.acctgTagUsageTypeId,
						accountingTagUsageTypeId));
		if (conf == null) {
			Debug.logInfo("No se encontro la configuracion de tag con la organizacion ["
					+ organizationPartyId + "]", MODULE);
			return tagTypes;
		}

		// find each non null configured tag type
		for (int i = 1; i <= UtilAccountingTags.TAG_COUNT; i++) {
			String type = conf.getString("clasifTypeId" + i);
			if (type != null) {
				tagTypes.put(new Integer(i), type);
			}
		}
		return tagTypes;
	}
	
	
	public List<ReadConfigurationForOrganization> getItemRead(String organizationPartyId,
			String TagTypeId, List<? extends InvoiceItem> invoiceItems) throws RepositoryException {
		
		
		Debug.log("Entro getItemRead invoiceItems.size() " + invoiceItems.size());
		Delegator delegator = getDelegator();
		String descripcion = null;
		//Regresa mapa de la estructura de las clasificaciones para el Item	
		Map<Integer, String> tagTypes = getAccountingTagTypesPurchase(
				organizationPartyId, TagTypeId);
		
		
		Debug.log("getItemRead tagTypes.size() " + tagTypes.size());
		
		List<ReadConfigurationForOrganization> tagTypesAndValues = new ArrayList<ReadConfigurationForOrganization>();	
		
		if (invoiceItems.size()!=0 && tagTypes.size()!=0)
		{	
			
			for (InvoiceItem elementInvoice : invoiceItems) {
				Debug.log("getItemRead elementInvoice " + elementInvoice.getInvoiceItemSeqId());
				int i= 1;
			if(elementInvoice.get("clasifTypeId" +i) != null)
			{
				for (Integer index : tagTypes.keySet()) {
					String type = tagTypes.get(index);
					
					ReadConfigurationForOrganization tag = null;
					List<ClasifPresupuestal> listclas = findListCache(
							ClasifPresupuestal.class,
							map(ClasifPresupuestal.Fields.clasificacionId, type));
					Debug.log("getItemRead listclas "
							+ listclas);			
					
						if (!listclas.isEmpty()) {
							for (ClasifPresupuestal clasifPresupuestal : listclas) {
								descripcion = "";
								
								tag = new ReadConfigurationForOrganization(this);
								tag.setIndex(index);
								tag.setType(clasifPresupuestal.getDescripcion());
								tag.setInvoiceItem(elementInvoice.getInvoiceItemSeqId());
								
								
								Debug.log("elementInvoice.get(clasifTypeId +i) " + elementInvoice.get("clasifTypeId" +i));
								
								try {
									
									Debug.log("getItemRead elementInvoice " + elementInvoice.getInvoiceItemSeqId() + "; " + clasifPresupuestal.getDescripcion());
									
									if(clasifPresupuestal.getTablaRelacion().contains("Enumeration"))
									{
										Debug.log("Entro a Enumeration ");								
										
										descripcion = findOne(Enumeration.class, map(Enumeration.Fields.enumId, elementInvoice.get("clasifTypeId" +i).toString())).getDescription();
										//getClasifTypeId1
										Debug.log("getItemRead Enumeration descripcion " + descripcion); 
									}
									else if(clasifPresupuestal.getTablaRelacion().contains("Geo"))
									{
										Debug.log("Entro a Geo ");
										descripcion = findOne(Geo.class, map(Geo.Fields.geoId, elementInvoice.get("clasifTypeId" +i).toString())).getGeoName();
										//getClasifTypeId1
										Debug.log("getItemRead Geo descripcion " + descripcion);
									}
									else
									{
										Debug.log("Entro a WorkEffort ");
										
											descripcion = findOne(
													WorkEffort.class,
													map(WorkEffort.Fields.workEffortId,
															elementInvoice.get(
																	"clasifTypeId"
																			+ i)
																	.toString()))
													.getDescription();
										
										
										//getClasifTypeId1
										Debug.log("getItemRead WorkEffort descripcion " + descripcion);
									}
								} catch (Exception e) {
									descripcion = "";
								}
								Debug.log("descripcion " + descripcion);
								
								tag.setDescription(descripcion);
								tagTypesAndValues.add(tag);						
								Debug.log("tag " + tag);
								i=i+1;
								
								
							}
						}
					}
			}
		}
	}
		Debug.log("getItemRead tagTypesAndValues size"
				+ tagTypesAndValues.size());

		return tagTypesAndValues;
	}
		
}
