package org.opentaps.dataimport.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.opentaps.base.entities.AcctgPolizasDetalle;
import org.opentaps.base.entities.AcctgTagPostingCheck;
import org.opentaps.base.entities.AcctgTrans;
import org.opentaps.base.entities.AcctgTransAndEntries;
import org.opentaps.base.entities.AcctgTransEntry;
import org.opentaps.base.entities.AcctgTransPresupuestal;
import org.opentaps.base.entities.AcctgTransPresupuestalEg;
import org.opentaps.base.entities.AcctgTransPresupuestalIng;
import org.opentaps.base.entities.DataImportOperacionDiaria;
import org.opentaps.base.entities.GlAccountClass;
import org.opentaps.base.entities.GlAccountHistory;
import org.opentaps.base.entities.GlAccountOrganization;
import org.opentaps.base.entities.InvoiceAdjustmentGlAccount;
import org.opentaps.base.entities.InvoiceGlAccountType;
import org.opentaps.base.entities.Party;
import org.opentaps.base.entities.TipoDocumento;
import org.opentaps.dataimport.UtilImport;
import org.opentaps.domain.DomainService;
import org.opentaps.domain.dataimport.OperacionDiariaDataImportRepositoryInterface;
import org.opentaps.domain.ledger.AccountingTransaction;
import org.opentaps.domain.ledger.GeneralLedgerAccount;
import org.opentaps.domain.ledger.LedgerException;
import org.opentaps.domain.ledger.LedgerRepositoryInterface;
import org.opentaps.domain.ledger.LedgerSpecificationInterface;
import org.opentaps.domain.organization.AccountingTagConfigurationForOrganizationAndUsage;
import org.opentaps.foundation.entity.EntityFieldInterface;
import org.opentaps.foundation.entity.EntityInterface;
import org.opentaps.foundation.entity.EntityNotFoundException;
import org.opentaps.foundation.entity.hibernate.Session;
import org.opentaps.foundation.entity.hibernate.Transaction;
import org.opentaps.foundation.entity.util.EntityListIterator;
import org.opentaps.foundation.infrastructure.DomainContextInterface;
import org.opentaps.foundation.infrastructure.Infrastructure;
import org.opentaps.foundation.infrastructure.InfrastructureException;
import org.opentaps.foundation.infrastructure.User;
import org.opentaps.foundation.repository.RepositoryException;
import org.opentaps.foundation.service.ServiceException;

import com.ibm.icu.util.Calendar;

public class OperacionDiariaService extends DomainService{
	
	public Map<String, Object> registraDiario(DispatchContext d,Map<String, Object> context) throws ServiceException{
	
		Session session = null;
		Infrastructure i = new Infrastructure(d.getDelegator());
		Map<String,Object> output = null;
		String tTrans_S= (String) context.get("tTrans");
		Date fechaRegistro_S= (Date) context.get("fechaRegistro");
		Date fechaContable_S= (Date) context.get("fechaContable");
		BigDecimal monto_S= (BigDecimal) context.get("monto");
		String organizacionContable_S= (String) context.get("organizacionContable");
		String organizacionEjecutora_S= (String) context.get("organizacionEjecutora");
		String tipoDocumento_S= (String) context.get("tipoDocumento");
		String refDoc_S= (String) context.get("refDoc");
		String secuencia_S= (String) context.get("secuencia");
		String usuario_S= (String) context.get("usuario");
		String lote_S= (String) context.get("lote");
		String concepto_S= (String) context.get("concepto");
		String subConcepto_S= (String) context.get("subConcepto");
		String tipoCatalogoC_S= (String) context.get("tipoCatalogoC");
		String idC_S= (String) context.get("idC");
		String tipoCatalogoD_S= (String) context.get("tipoCatalogoD");
		String idD_S= (String) context.get("idD");
		
		try {
		session = i.getSession();
		LedgerRepositoryInterface ledger_repo = new LedgerRepositoryInterface() {
			
			@Override
			public void setUser(User user) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setInfrastructure(Infrastructure infrastructure) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setDomainContext(Infrastructure infrastructure, User user) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setDomainContext(DomainContextInterface context) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public User getUser() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Infrastructure getInfrastructure() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void update(Collection<? extends EntityInterface> entities)
					throws RepositoryException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void update(EntityInterface entity) throws RepositoryException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void remove(Collection<? extends EntityInterface> entities)
					throws RepositoryException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void remove(EntityInterface entity) throws RepositoryException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public <T extends EntityInterface> Map<? extends EntityFieldInterface<? super T>, Object> map(
					EntityFieldInterface<? super T> key1, Object value1,
					EntityFieldInterface<? super T> key2, Object value2,
					EntityFieldInterface<? super T> key3, Object value3,
					EntityFieldInterface<? super T> key4, Object value4,
					EntityFieldInterface<? super T> key5, Object value5,
					EntityFieldInterface<? super T> key6, Object value6,
					EntityFieldInterface<? super T> key7, Object value7) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> Map<? extends EntityFieldInterface<? super T>, Object> map(
					EntityFieldInterface<? super T> key1, Object value1,
					EntityFieldInterface<? super T> key2, Object value2,
					EntityFieldInterface<? super T> key3, Object value3,
					EntityFieldInterface<? super T> key4, Object value4,
					EntityFieldInterface<? super T> key5, Object value5,
					EntityFieldInterface<? super T> key6, Object value6) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> Map<? extends EntityFieldInterface<? super T>, Object> map(
					EntityFieldInterface<? super T> key1, Object value1,
					EntityFieldInterface<? super T> key2, Object value2,
					EntityFieldInterface<? super T> key3, Object value3,
					EntityFieldInterface<? super T> key4, Object value4,
					EntityFieldInterface<? super T> key5, Object value5) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> Map<? extends EntityFieldInterface<? super T>, Object> map(
					EntityFieldInterface<? super T> key1, Object value1,
					EntityFieldInterface<? super T> key2, Object value2,
					EntityFieldInterface<? super T> key3, Object value3,
					EntityFieldInterface<? super T> key4, Object value4) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> Map<? extends EntityFieldInterface<? super T>, Object> map(
					EntityFieldInterface<? super T> key1, Object value1,
					EntityFieldInterface<? super T> key2, Object value2,
					EntityFieldInterface<? super T> key3, Object value3) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> Map<? extends EntityFieldInterface<? super T>, Object> map(
					EntityFieldInterface<? super T> key1, Object value1,
					EntityFieldInterface<? super T> key2, Object value2) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> Map<? extends EntityFieldInterface<? super T>, Object> map(
					EntityFieldInterface<? super T> key1, Object value1) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface, T2 extends EntityInterface> T getRelatedOneCache(
					Class<T> entityName, String relation, T2 entity)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface, T2 extends EntityInterface> T getRelatedOneCache(
					Class<T> entityName, T2 entity) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> EntityInterface getRelatedOneCache(
					String relation, T entity) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface, T2 extends EntityInterface> T getRelatedOne(
					Class<T> entityName, String relation, T2 entity)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface, T2 extends EntityInterface> T getRelatedOne(
					Class<T> entityName, T2 entity) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> EntityInterface getRelatedOne(
					String relation, T entity) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface, T2 extends EntityInterface> List<T> getRelatedCache(
					Class<T> entityName, String relation, T2 entity)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface, T2 extends EntityInterface> List<T> getRelatedCache(
					Class<T> entityName, T2 entity) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<? extends EntityInterface> getRelatedCache(
					String relation, T entity) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface, T2 extends EntityInterface> List<T> getRelated(
					Class<T> entityName, String relation, T2 entity,
					List<String> orderBy) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface, T2 extends EntityInterface> List<T> getRelated(
					Class<T> entityName, String relation, T2 entity)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface, T2 extends EntityInterface> List<T> getRelated(
					Class<T> entityName, T2 entity, List<String> orderBy)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface, T2 extends EntityInterface> List<T> getRelated(
					Class<T> entityName, T2 entity) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<? extends EntityInterface> getRelated(
					String relation, T entity, List<String> orderBy)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<? extends EntityInterface> getRelated(
					String relation, T entity) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getNextSubSeqId(EntityInterface entity,
					String sequenceFieldName, int numericPadding, int incrementBy)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getNextSeqId(EntityInterface entity) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getNextSeqId(String seqName, long staggerMax) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getNextSeqId(String seqName) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findPage(Class<T> entityName,
					EntityCondition condition, List<String> fields,
					List<String> orderBy, int pageStart, int pageSize)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findPage(Class<T> entityName,
					EntityCondition condition, List<String> orderBy, int pageStart,
					int pageSize) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findPage(Class<T> entityName,
					EntityCondition condition, int pageStart, int pageSize)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findPage(Class<T> entityName,
					List<? extends EntityCondition> conditions, List<String> fields,
					List<String> orderBy, int pageStart, int pageSize)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findPage(Class<T> entityName,
					List<? extends EntityCondition> conditions, List<String> orderBy,
					int pageStart, int pageSize) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findPage(Class<T> entityName,
					List<? extends EntityCondition> conditions, int pageStart,
					int pageSize) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findPage(Class<T> entityName,
					Map<? extends EntityFieldInterface<? super T>, Object> conditions,
					List<String> fields, List<String> orderBy, int pageStart,
					int pageSize) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findPage(Class<T> entityName,
					Map<? extends EntityFieldInterface<? super T>, Object> conditions,
					List<String> orderBy, int pageStart, int pageSize)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findPage(Class<T> entityName,
					Map<? extends EntityFieldInterface<? super T>, Object> conditions,
					int pageStart, int pageSize) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> T findOneNotNullCache(
					Class<T> entityName,
					Map<? extends EntityFieldInterface<? super T>, Object> pk,
					String messageLabel, Map<String, Object> context)
					throws RepositoryException, EntityNotFoundException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> T findOneNotNullCache(
					Class<T> entityName,
					Map<? extends EntityFieldInterface<? super T>, Object> pk,
					String message) throws RepositoryException, EntityNotFoundException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> T findOneNotNullCache(
					Class<T> entityName,
					Map<? extends EntityFieldInterface<? super T>, Object> pk)
					throws RepositoryException, EntityNotFoundException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> T findOneNotNull(Class<T> entityName,
					Map<? extends EntityFieldInterface<? super T>, Object> pk,
					String messageLabel, Map context) throws RepositoryException,
					EntityNotFoundException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> T findOneNotNull(Class<T> entityName,
					Map<? extends EntityFieldInterface<? super T>, Object> pk,
					String message) throws RepositoryException, EntityNotFoundException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> T findOneNotNull(Class<T> entityName,
					Map<? extends EntityFieldInterface<? super T>, Object> pk)
					throws RepositoryException, EntityNotFoundException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> T findOneCache(Class<T> entityName,
					Map<? extends EntityFieldInterface<? super T>, Object> pk)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> T findOne(Class<T> entityName,
					Map<? extends EntityFieldInterface<? super T>, Object> pk)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findListCache(
					Class<T> entityName, EntityCondition condition, List<String> orderBy)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findListCache(
					Class<T> entityName, EntityCondition condition)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findListCache(
					Class<T> entityName, List<? extends EntityCondition> conditions,
					List<String> orderBy) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findListCache(
					Class<T> entityName, List<? extends EntityCondition> conditions)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findListCache(
					Class<T> entityName,
					Map<? extends EntityFieldInterface<? super T>, Object> conditions,
					List<String> orderBy) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findListCache(
					Class<T> entityName,
					Map<? extends EntityFieldInterface<? super T>, Object> conditions)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findList(Class<T> entityName,
					EntityCondition condition, List<String> fields, List<String> orderBy)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findList(Class<T> entityName,
					EntityCondition condition, List<String> orderBy)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findList(Class<T> entityName,
					EntityCondition condition) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findList(Class<T> entityName,
					List<? extends EntityCondition> conditions, List<String> fields,
					List<String> orderBy) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findList(Class<T> entityName,
					List<? extends EntityCondition> conditions, List<String> orderBy)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findList(Class<T> entityName,
					List<? extends EntityCondition> conditions)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findList(Class<T> entityName,
					Map<? extends EntityFieldInterface<? super T>, Object> conditions,
					List<String> fields, List<String> orderBy)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findList(Class<T> entityName,
					Map<? extends EntityFieldInterface<? super T>, Object> conditions,
					List<String> orderBy) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findList(Class<T> entityName,
					Map<? extends EntityFieldInterface<? super T>, Object> conditions)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> EntityListIterator<T> findIterator(
					Class<T> entityName, EntityCondition condition,
					List<String> fields, List<String> orderBy)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> EntityListIterator<T> findIterator(
					Class<T> entityName, EntityCondition condition, List<String> orderBy)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> EntityListIterator<T> findIterator(
					Class<T> entityName, EntityCondition condition)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> EntityListIterator<T> findIterator(
					Class<T> entityName, List<? extends EntityCondition> conditions,
					List<String> fields, List<String> orderBy)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> EntityListIterator<T> findIterator(
					Class<T> entityName, List<? extends EntityCondition> conditions,
					List<String> orderBy) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> EntityListIterator<T> findIterator(
					Class<T> entityName, List<? extends EntityCondition> conditions)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> EntityListIterator<T> findIterator(
					Class<T> entityName,
					Map<? extends EntityFieldInterface<? super T>, Object> conditions,
					List<String> fields, List<String> orderBy)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> EntityListIterator<T> findIterator(
					Class<T> entityName,
					Map<? extends EntityFieldInterface<? super T>, Object> conditions,
					List<String> orderBy) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> EntityListIterator<T> findIterator(
					Class<T> entityName,
					Map<? extends EntityFieldInterface<? super T>, Object> conditions)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findAllCache(
					Class<T> entityName, List<String> orderBy)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findAllCache(Class<T> entityName)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findAll(Class<T> entityName,
					List<String> orderBy) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends EntityInterface> List<T> findAll(Class<T> entityName)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void createOrUpdate(Collection<? extends EntityInterface> entities)
					throws RepositoryException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void createOrUpdate(EntityInterface entity)
					throws RepositoryException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void create(EntityInterface entity) throws RepositoryException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public List<AccountingTagConfigurationForOrganizationAndUsage> validateTagParameters(
					AcctgTransEntry entry) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String storeAcctgTransAndEntries(AccountingTransaction acctgTrans,
					List<AcctgTransEntry> acctgTransEntries)
					throws RepositoryException, ServiceException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void setPosted(AccountingTransaction transaction)
					throws RepositoryException, LedgerException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public AcctgTransEntry getTransactionEntry(String acctgTransId,
					String acctgTransEntrySeqId) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public List<AcctgTransEntry> getTransactionEntries(String acctgTransId)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public LedgerSpecificationInterface getSpecification() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public GeneralLedgerAccount getProductLedgerAccount(String productId,
					String glAccountTypeId, String organizationPartyId)
					throws RepositoryException, EntityNotFoundException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public List<AcctgTransAndEntries> getPostedTransactionsAndEntries(
					String organizationPartyId, List<String> glFiscalTypeId,
					Timestamp fromDate, Timestamp thruDate) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public List<AccountingTransaction> getPostedTransactions(
					String organizationPartyId, String glFiscalTypeId,
					Timestamp fromDate, Timestamp thruDate) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public GlAccountOrganization getOrganizationAccount(String glAccountId,
					String organizationPartyId) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public GeneralLedgerAccount getLedgerAccount(String glAccountId,
					String organizationPartyId) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public InvoiceGlAccountType getInvoiceGlAccountType(
					String organizationPartyId, String invoiceTypeId)
					throws RepositoryException, EntityNotFoundException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public InvoiceAdjustmentGlAccount getInvoiceAdjustmentGlAccount(
					String organizationPartyId, String invoiceTypeId,
					String invoiceAdjustmentTypeId) throws RepositoryException,
					EntityNotFoundException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public GeneralLedgerAccount getDefaultLedgerAccount(String glAccountTypeId,
					String organizationPartyId) throws RepositoryException,
					EntityNotFoundException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public AcctgTransPresupuestalIng getAcctgTransPresupuestalIng(
					String acctgTransId) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public AcctgTransPresupuestalEg getAcctgTransPresupuestalEg(
					String acctgTransId) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public AcctgTagPostingCheck getAcctgTagPostingCheck(
					AccountingTransaction transaction) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public AcctgPolizasDetalle getAcctgPolizasDetalle(String acctgTransId)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public AccountingTransaction getAccountingTransaction(String acctgTransId)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public GlAccountHistory getAccountHistory(String glAccountId,
					String organizationPartyId, String customTimePeriodId)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public List<GlAccountClass> getAccountClassTree(String glAccountClassId)
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public AccountingTransaction createSimpleTransaction(
					AccountingTransaction acctgTrans,
					GeneralLedgerAccount debitAccount,
					GeneralLedgerAccount creditAccount, String organizationPartyId,
					BigDecimal amount, String transactionPartyId)
					throws RepositoryException, ServiceException {
				// TODO Auto-generated method stub
				return null;
			}
		};

		
		Transaction imp_tx1 = null;
		Transaction imp_tx2 = null;
		Transaction imp_tx3 = null;
		Transaction imp_tx4 = null;
		Transaction imp_tx5 = null;
		Transaction imp_tx6 = null;
		Transaction imp_tx7 = null;
		Transaction imp_tx8 = null;
		Transaction imp_tx9 = null;
		Transaction imp_tx10 = null;
		Transaction imp_tx11 = null;
		Transaction imp_tx12 = null;

		
			// Empieza bloque de validaciones
			String mensaje = "";
			Debug.log("Empieza bloque de validaciones");
			mensaje = UtilImport.validaParty(mensaje, ledger_repo,
					organizacionEjecutora_S,
					"Organizacion Ejecutora");
			mensaje = UtilImport.validaTipoDoc(mensaje, ledger_repo,
					tipoDocumento_S);

			if (!mensaje.isEmpty()) {
				String message = "Failed to import Operacion Diaria ["
						+ refDoc_S + secuencia_S
						+ "], Error message : " + mensaje;
				//storeImportOperacionDiariaError(rowdata, message, imp_repo);
				//continue;
			}

			// Creacion de objetos
			Debug.log("Empieza creacion de objetos");
			Party ue = UtilImport.obtenParty(ledger_repo,
					organizacionEjecutora_S);
			TipoDocumento tipoDoc = UtilImport.obtenTipoDocumento(
					ledger_repo, tipoDocumento_S);

			Debug.log("Motor Contable");
			MotorContable motor = new MotorContable(ledger_repo);
			Map<String, String> cuentas = motor.cuentasDiarias(
					tipoDoc.getAcctgTransTypeId(), null, null,
					organizacionContable_S, null, null,
					tipoCatalogoC_S, idC_S,
					tipoCatalogoD_S, idD_S, null,
					false, concepto_S, subConcepto_S,
					null);
			try {

				imp_tx1 = null;
				imp_tx2 = null;
				imp_tx3 = null;
				imp_tx4 = null;
				imp_tx5 = null;
				imp_tx6 = null;
				imp_tx7 = null;
				imp_tx8 = null;
				imp_tx9 = null;
				imp_tx10 = null;
				imp_tx11 = null;
				imp_tx12 = null;

				AcctgTrans OperacionDiaria = new AcctgTrans();

				Calendar cal = Calendar.getInstance();
				cal.setTime(fechaRegistro_S);
				OperacionDiaria.setTransactionDate(new Timestamp(cal
						.getTimeInMillis()));
				OperacionDiaria.setIsPosted("Y");
				cal.setTime(fechaContable_S);
				OperacionDiaria.setPostedDate(new Timestamp(cal
						.getTimeInMillis()));
				OperacionDiaria.setAcctgTransTypeId(tipoDoc
						.getAcctgTransTypeId());
				OperacionDiaria.setLastModifiedByUserLogin(usuario_S);
				OperacionDiaria.setPartyId(ue.getPartyId());
				OperacionDiaria.setPostedAmount(monto_S);

				// ACCTG_TRANS_PRESUPUESTAL
				AcctgTransPresupuestal aux = new AcctgTransPresupuestal();
				aux.setUnidadEjecutora(ue.getPartyId());
				aux.setAgrupador(refDoc_S);
				aux.setIdTipoDoc(tipoDocumento_S);
				aux.setSecuencia(secuencia_S);
				aux.setLote(lote_S);

				if (cuentas.get("Cuenta Cargo Presupuesto") != null) {
					Debug.log("Cuenta Presupuestal");
					OperacionDiaria.setDescription(tipoDoc.getDescripcion()
							+ "-" + refDoc_S + "-P");

					// id Transaccion
					OperacionDiaria.setAcctgTransId(UtilImport
							.getAcctgTransIdDiario(refDoc_S,
									secuencia_S, "P"));

					AcctgTrans trans = ledger_repo.findOne(
							AcctgTrans.class, ledger_repo.map(
									AcctgTrans.Fields.acctgTransId,
									OperacionDiaria.getAcctgTransId()));

					if (trans != null) {
						Debug.log("Trans Modif");
						String message = "La transaccion con id: "
								+ OperacionDiaria.getAcctgTransId()
								+ "ya existe.";
						Debug.log(message);
						//storeImportOperacionDiariaError(rowdata, message,
							//	imp_repo);
						//continue;
					}

					Debug.log("Trans Nueva");
					OperacionDiaria.setCreatedByUserLogin(usuario_S);
					OperacionDiaria.setGlFiscalTypeId(cuentas
							.get("GlFiscalTypePresupuesto"));
					imp_tx1 = session.beginTransaction();
					ledger_repo.createOrUpdate(OperacionDiaria);
					imp_tx1.commit();

					aux.setAcctgTransId(OperacionDiaria.getAcctgTransId());
					imp_tx3 = session.beginTransaction();
					ledger_repo.createOrUpdate(aux);
					imp_tx3.commit();

					AcctgTransEntry acctgentry = UtilImport
							.generaAcctgTransEntry(
									OperacionDiaria,
									organizacionContable_S,
									"00001",
									"D",
									cuentas.get("Cuenta Cargo Presupuesto"),
									null);
					imp_tx5 = session.beginTransaction();
					ledger_repo.createOrUpdate(acctgentry);
					imp_tx5.commit();

					GlAccountOrganization glAccountOrganization = UtilImport
							.actualizaGlAccountOrganization(
									ledger_repo,
									monto_S,
									cuentas.get("Cuenta Cargo Presupuesto"),
									organizacionContable_S);
					imp_tx7 = session.beginTransaction();
					ledger_repo.createOrUpdate(glAccountOrganization);
					imp_tx7.commit();

					acctgentry = UtilImport.generaAcctgTransEntry(
							OperacionDiaria,
							organizacionContable_S, "00002", "C",
							cuentas.get("Cuenta Abono Presupuesto"), null);
					imp_tx9 = session.beginTransaction();
					ledger_repo.createOrUpdate(acctgentry);
					imp_tx9.commit();

					glAccountOrganization = UtilImport
							.actualizaGlAccountOrganization(
									ledger_repo,
									monto_S,
									cuentas.get("Cuenta Abono Presupuesto"),
									organizacionContable_S);
					imp_tx11 = session.beginTransaction();
					ledger_repo.createOrUpdate(glAccountOrganization);
					imp_tx11.commit();

				}

				if (cuentas.get("Cuenta Cargo Contable") != null) {
					Debug.log("Cuenta Contable");
					OperacionDiaria.setDescription(tipoDoc.getDescripcion()
							+ "-" + refDoc_S + "-C");

					// id Transaccion
					OperacionDiaria.setAcctgTransId(UtilImport
							.getAcctgTransIdDiario(refDoc_S,
									secuencia_S, "P"));

					AcctgTrans trans = ledger_repo.findOne(
							AcctgTrans.class, ledger_repo.map(
									AcctgTrans.Fields.acctgTransId,
									OperacionDiaria.getAcctgTransId()));

					if (trans != null) {
						Debug.log("Trans Modif");
						String message = "La transaccion con id: "
								+ OperacionDiaria.getAcctgTransId()
								+ "ya existe.";
						Debug.log(message);
						//storeImportOperacionDiariaError(rowdata, message,
							//	imp_repo);
						//continue;
					}

					Debug.log("Trans Nueva");
					OperacionDiaria.setCreatedByUserLogin(usuario_S);
					OperacionDiaria.setGlFiscalTypeId(cuentas
							.get("GlFiscalTypeContable"));
					imp_tx2 = session.beginTransaction();
					ledger_repo.createOrUpdate(OperacionDiaria);
					imp_tx2.commit();

					aux.setAcctgTransId(OperacionDiaria.getAcctgTransId());
					imp_tx4 = session.beginTransaction();
					ledger_repo.createOrUpdate(aux);
					imp_tx4.commit();

					AcctgTransEntry acctgentry = UtilImport
							.generaAcctgTransEntry(OperacionDiaria,
									organizacionContable_S,
									"00001", "D",
									cuentas.get("Cuenta Cargo Contable"),
									null);
					imp_tx6 = session.beginTransaction();
					ledger_repo.createOrUpdate(acctgentry);
					imp_tx6.commit();

					GlAccountOrganization glAccountOrganization = UtilImport
							.actualizaGlAccountOrganization(ledger_repo,
									monto_S,
									cuentas.get("Cuenta Cargo Contable"),
									organizacionContable_S);
					imp_tx8 = session.beginTransaction();
					ledger_repo.createOrUpdate(glAccountOrganization);
					imp_tx8.commit();

					acctgentry = UtilImport.generaAcctgTransEntry(
							OperacionDiaria,
							organizacionContable_S, "00002", "C",
							cuentas.get("Cuenta Abono Contable"), null);
					imp_tx10 = session.beginTransaction();
					ledger_repo.createOrUpdate(acctgentry);
					imp_tx10.commit();

					glAccountOrganization = UtilImport
							.actualizaGlAccountOrganization(ledger_repo,
									monto_S,
									cuentas.get("Cuenta Abono Contable"),
									organizacionContable_S);
					imp_tx12 = session.beginTransaction();
					ledger_repo.createOrUpdate(glAccountOrganization);
					imp_tx12.commit();
				}

				if (mensaje.isEmpty()) {
					String message = "Successfully imported Operacion Diaria [";
					// + rowdata.getClavePres() + "].";
					//this.storeImportOperacionDiariaSuccess(rowdata,
						//	imp_repo);
					//Debug.logInfo(message, MODULE);
					//imported = imported + 1;
				}
			} catch (Exception ex) {
				String message = "Failed to import Operacion Diaria ["
				// + rowdata.getClavePres() + "], Error message : "
						+ ex.getMessage();
				//storeImportOperacionDiariaError(rowdata, message, imp_repo);

				// rollback all if there was an error when importing item
				if (imp_tx1 != null) {
					imp_tx1.rollback();
				}
				if (imp_tx2 != null) {
					imp_tx2.rollback();
				}
				if (imp_tx3 != null) {
					imp_tx3.rollback();
				}
				if (imp_tx4 != null) {
					imp_tx4.rollback();
				}
				if (imp_tx5 != null) {
					imp_tx5.rollback();
				}
				if (imp_tx6 != null) {
					imp_tx6.rollback();
				}
				if (imp_tx7 != null) {
					imp_tx7.rollback();
				}
				if (imp_tx8 != null) {
					imp_tx8.rollback();
				}
				if (imp_tx9 != null) {
					imp_tx9.rollback();
				}
				if (imp_tx10 != null) {
					imp_tx10.rollback();
				}
				if (imp_tx11 != null) {
					imp_tx11.rollback();
				}
				if (imp_tx12 != null) {
					imp_tx12.rollback();
				}

				//Debug.logError(ex, message, MODULE);
				throw new ServiceException(ex.getMessage());
			}
		
		//this.importedRecords = imported;
			output = ServiceUtil.returnSuccess();
			output.put("messageOut", "Registro Exitoso");

	} catch (InfrastructureException ex) {
		//Debug.logError(ex, MODULE);
		output = ServiceUtil.returnError(ex.getMessage());
		throw new ServiceException(ex.getMessage());
	} catch (RepositoryException ex) {
		//Debug.logError(ex, MODULE);
		output = ServiceUtil.returnError(ex.getMessage());
		throw new ServiceException(ex.getMessage());
	} finally {
		if (session != null) {
			session.close();
		}
	}
		return output;
}

}
