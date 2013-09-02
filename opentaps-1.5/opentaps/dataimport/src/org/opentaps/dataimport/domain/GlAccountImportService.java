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
package org.opentaps.dataimport.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.ofbiz.base.util.Debug;
import java.util.Locale;

import javax.el.ListELResolver;

import org.ofbiz.base.util.UtilDateTime;
import org.opentaps.base.constants.StatusItemConstants;
import org.opentaps.base.entities.AcctgTrans;
import org.opentaps.base.entities.AcctgTransEntry;
import org.opentaps.base.entities.CustomTimePeriod;
import org.opentaps.base.entities.DataImportGlAccount;
import org.opentaps.base.entities.GlAccount;
import org.opentaps.base.entities.GlAccountCategoryRelation;
import org.opentaps.base.entities.GlAccountClass;
import org.opentaps.base.entities.GlAccountClassTypeMap;
import org.opentaps.base.entities.GlAccountOrganization;
import org.opentaps.base.entities.GlAccountTypeDefault;
import org.opentaps.base.services.GetAcctgTransAndEntriesByTypeService;
import org.opentaps.dataimport.ExcelImportServices;
import org.opentaps.domain.DomainService;
import org.opentaps.domain.dataimport.AccountingDataImportRepositoryInterface;
import org.opentaps.domain.dataimport.GlAccountImportServiceInterface;
import org.opentaps.domain.ledger.LedgerRepositoryInterface;
import org.opentaps.foundation.entity.hibernate.Session;
import org.opentaps.foundation.entity.hibernate.Transaction;
import org.opentaps.foundation.infrastructure.Infrastructure;
import org.opentaps.foundation.infrastructure.InfrastructureException;
import org.opentaps.foundation.infrastructure.User;
import org.opentaps.foundation.repository.RepositoryException;
import org.opentaps.foundation.service.ServiceException;

import com.sun.org.apache.bcel.internal.generic.NEW;

/**
 * Import General Ledger accounts via intermediate DataImportGlAccount entity.
 */
public class GlAccountImportService extends DomainService implements GlAccountImportServiceInterface {
    
    private static final String MODULE = GlAccountImportService.class.getName();
    // session object, using to store/search pojos.
    private Session session;
    public String organizationPartyId;
    public int importedRecords;
    private static Logger logger=  Logger.getLogger(ExcelImportServices.class);
    
    public GlAccountImportService() {
        super();
    }

    public GlAccountImportService(Infrastructure infrastructure, User user, Locale locale) throws ServiceException {
        super(infrastructure, user, locale);
    }

    /** {@inheritDoc} */
    public void setOrganizationPartyId(String organizationPartyId) {
        this.organizationPartyId = organizationPartyId;
    }

    /** {@inheritDoc} */
    public int getImportedRecords() {
        return importedRecords;
    }
    
    /** {@inheritDoc} */
    public void importGlAccounts() throws ServiceException{        
        try {
            this.session = this.getInfrastructure().getSession();
            
            AccountingDataImportRepositoryInterface imp_repo = this.getDomainsDirectory().getDataImportDomain().getAccountingDataImportRepository();
            LedgerRepositoryInterface ledger_repo  =this.getDomainsDirectory().getLedgerDomain().getLedgerRepository();
            
            List<DataImportGlAccount> dataforimp = imp_repo.findNotProcessesDataImportGlAccountEntries();
            BigDecimal saldoinicial;
            BigDecimal saldodebit = new BigDecimal(0.00);
            BigDecimal saldoCredit =  new BigDecimal(0.00);
            String TipoParentId = "";
            String glAccountId  = "";
            boolean isDebitOrCredit = false;
            
            int imported = 0;
            Transaction imp_tx1 = null;
            Transaction imp_tx2 = null;
           
            
            List<String> cuentas = new ArrayList<String>();
            //List<String> cuentascredit = new ArrayList<String>();
            
            for(int i = 0; i < dataforimp.size(); i++){
                DataImportGlAccount rawdata = dataforimp.get(i);
                
                //import accounts as many as possible
                try{
                    imp_tx1 = null;
                    imp_tx2 = null;
                    
                    //begin importing raw data item
                    GlAccount glAccount = new GlAccount();
                    GlAccountOrganization glAccountOrganization = new GlAccountOrganization();
                    glAccount.setGlAccountId(rawdata.getGlAccountId());
                    glAccount.setParentGlAccountId(rawdata.getParentGlAccountId());     
                    glAccount.setAccountName(rawdata.getAccountName());
                    glAccount.setAccountCode(rawdata.getGlAccountId());
                    glAccount.setCodificacion(rawdata.getCodificacion());
                    glAccount.setNaturaleza(rawdata.getNaturaleza());
                    glAccount.setTipoCuenta(rawdata.getTipoCuenta());
                    glAccount.setMajorGlAccount(rawdata.getMajorGlAccount());
                    glAccount.setNode(rawdata.getNode());
                    saldoinicial = rawdata.getSaldoinicial(); 
                    
                    if(rawdata.getClassification() != null){
                        //decode account clasificationt to type Id and class Id
                        GlAccountClassTypeMap glAccountClassTypeMap = ledger_repo.findOne(GlAccountClassTypeMap.class, 
                                ledger_repo.map(GlAccountClassTypeMap.Fields.glAccountClassTypeKey, rawdata.getClassification()));
                        glAccount.setGlAccountTypeId(glAccountClassTypeMap.getGlAccountTypeId());
                        glAccount.setGlAccountClassId(glAccountClassTypeMap.getGlAccountClassId());                        
                        

                        GlAccountClass glaccountclass = ledger_repo.findOne(
                        		GlAccountClass.class, ledger_repo.map(
                        				GlAccountClass.Fields.glAccountClassId,
		                                         glAccount.getGlAccountClassId()));
                        
                        if(glaccountclass.getParentClassId()!= null)
                        { 
                        	
                        	do{                          	
                        		
                        		glAccountId = glaccountclass.getParentClassId().toString();
                        		Debug.logInfo("variable1: " + glAccountId, MODULE);
                         		                         		
                         		if (glAccountId.equals("DEBIT") || glAccountId.equals("CREDIT"))
                         		{
                         			Debug.logInfo("BANANA", MODULE);
                         			TipoParentId = glAccountId;
                         			isDebitOrCredit = true;
                         		}
                         		else
                         		{
                         			glaccountclass = ledger_repo.findOne(
                                    		GlAccountClass.class, ledger_repo.map(
                                    				GlAccountClass.Fields.glAccountClassId,
                                    				glAccountId));
                         			Debug.logInfo("variable2: " + glaccountclass, MODULE);
                         			isDebitOrCredit = false;
                         		}                    		
                         		
                         	} while (isDebitOrCredit == false);
                            
                        	Debug.logInfo("variable3: " + TipoParentId, MODULE);
                         	if (TipoParentId.equals("DEBIT") && !rawdata.getSaldoinicial().toString().trim().equals("0.00"))
                         	{                        		
                         		saldodebit = saldodebit.add(rawdata.getSaldoinicial());
                         		cuentas.add(rawdata.getGlAccountId() + ";" +saldoinicial.toString() + ";D" );                        		
                         	}
                         	else if (TipoParentId.equals("CREDIT") && !rawdata.getSaldoinicial().toString().trim().equals("0.00"))
                         	{
                         		saldoCredit = saldoCredit.add(rawdata.getSaldoinicial());
                         		cuentas.add(rawdata.getGlAccountId()+ ";" +saldoinicial.toString()+ ";C");
                         	}
                        }	
                    }
                    
                    Debug.log("Saldo Debit " + saldodebit);
                    Debug.log("Saldo Credit " + saldoCredit);  
                    
                    imp_tx1 = this.session.beginTransaction();
                    ledger_repo.createOrUpdate(glAccount);
                    imp_tx1.commit();

                    if(this.organizationPartyId != null){
                        //map organization party to GL accounts
                        
                        glAccountOrganization.setOrganizationPartyId(this.organizationPartyId);
                        glAccountOrganization.setGlAccountId(rawdata.getGlAccountId());
                        glAccountOrganization.setFromDate(UtilDateTime.nowTimestamp());
                        
                        
                        imp_tx2 = this.session.beginTransaction();
                        ledger_repo.createOrUpdate(glAccountOrganization);
                        imp_tx2.commit();
                    }
                    
                  
                    
                    //impactar Tipo de cuenta                    
                    getType(rawdata, ledger_repo);
                    //impacar Relacion con Categoria                     
                    getRelationCategory(rawdata, ledger_repo);

                    String message = "Successfully imported General Ledger account [" + rawdata.getGlAccountId() + "].";                   
                    this.storeImportGlAccountSuccess(rawdata, imp_repo);
                    Debug.logInfo(message, MODULE);
                    
                    imported = imported + 1;
                     
                }catch(Exception ex){
                    String message = "Failed to import General Ledger account [" + rawdata.getGlAccountId() + "], Error message : " + ex.getMessage();
                    storeImportGlAccountError(rawdata, message, imp_repo);
                    
                    //rollback all if there was an error when importing item
                    if(imp_tx1 != null){
                        imp_tx1.rollback();
                    }
                    if(imp_tx2 != null){
                        imp_tx2.rollback();
                    }
                    Debug.logError(ex, message, MODULE);
                    throw new ServiceException(ex.getMessage());
                }
            }
            
            // si los saldos son iguales crea la transaccion y guarda el saldo inicial.
            
            if(saldodebit.compareTo(saldoCredit)==0)
            {   
            	Debug.log("Saldo debit compare" + saldodebit);
            	if(saldodebit.compareTo(BigDecimal.ZERO) != 0)
            			{
            				GenerarTransaccion(cuentas, saldodebit);
            			}
            		
            }
            
            this.importedRecords = imported;
            
        } catch (InfrastructureException ex) {
            Debug.logError(ex, MODULE);
            throw new ServiceException(ex.getMessage());
        } catch (RepositoryException ex) {
            Debug.logError(ex, MODULE);
            throw new ServiceException(ex.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    /**
     * Relacionar Cuentas con su categoria
     * GlAccountCategoryRelation
     * No se puede repetir el tipo
     * **/
    private void getRelationCategory(DataImportGlAccount rawdata,
			LedgerRepositoryInterface ledger_repo) throws ServiceException {
    	Transaction imp_tx1 = null;
		try 
		{	
			if(rawdata.getCatalog()!=null)
			{	
				GlAccountCategoryRelation glaccountRelation = new GlAccountCategoryRelation();
				glaccountRelation.setGlAccountId(rawdata.getGlAccountId());
				glaccountRelation.setProductCategoryId(rawdata.getCatalog().trim());
				glaccountRelation.setFromDate(UtilDateTime.nowTimestamp());
				
				imp_tx1 = this.session.beginTransaction();
                ledger_repo.createOrUpdate(glaccountRelation);
                imp_tx1.commit();
                
                String message = "Successfully relation GlAccountCategoryRelation [" + rawdata.getGlAccountId() + "].";  
                Debug.logInfo(message, MODULE);
			}
		}
		catch(Exception e)
		{
			String message= "Failed to relation GlAccountCategoryRelation [" + rawdata.getGlAccountId() + "], Error message : " + e.getMessage();
			
			if(imp_tx1 != null){
                imp_tx1.rollback();
            }
			Debug.logError(message, MODULE);
			throw new ServiceException(e.getMessage());
		}
		
		
	}
    
    /**
     * Definir el tipo de la cuenta
     * GlAccountTypeDefault
     * No se puede repetir el tipo
     * **/
    
    private void getType(DataImportGlAccount rawdata, LedgerRepositoryInterface ledger_repo) throws ServiceException {
		Transaction imp_tx1 = null;
		try 
		{
			 
			
			if(rawdata.getType()!=null)
			{
				//Falta validar que exista el type
				GlAccountTypeDefault glTipo = new GlAccountTypeDefault();
				glTipo.setGlAccountId(rawdata.getGlAccountId());
				glTipo.setGlAccountTypeId(rawdata.getType());
				glTipo.setOrganizationPartyId(this.organizationPartyId);
				glTipo.setCreatedStamp(UtilDateTime.nowTimestamp());
				glTipo.setCreatedTxStamp(UtilDateTime.nowTimestamp());
				glTipo.setLastUpdatedStamp(UtilDateTime.nowTimestamp());
				glTipo.setLastUpdatedTxStamp(UtilDateTime.nowTimestamp());
				
				imp_tx1 = this.session.beginTransaction();
                ledger_repo.createOrUpdate(glTipo);
                imp_tx1.commit();
			}
		}
		catch(Exception e)
		{

			if(imp_tx1 != null){
                imp_tx1.rollback();
            }
			Debug.logError(e, MODULE);
			throw new ServiceException(e.getMessage());
		}
		
		
	}
    
    /*Generar Transaccion*/
    private void GenerarTransaccion(List<String> Listacuentas, BigDecimal saldototal) {
		
    	 Transaction imp_tx3 = null;
         Transaction imp_tx4 = null;
         Transaction imp_tx5 = null;
         
         Calendar cale = Calendar.getInstance(); 
         String annioactual = Integer.toString(cale.get(Calendar.YEAR));
         String annioanter = Integer.toString(cale.get(Calendar.YEAR)-1);
         Boolean actual = false;
         Boolean anterior = false;
         
        
     	try {
     		 LedgerRepositoryInterface ledger_repo  =this.getDomainsDirectory().getLedgerDomain().getLedgerRepository();
     		
     		 List<CustomTimePeriod> periodo = ledger_repo.findList(
     				 CustomTimePeriod.class, ledger_repo.map(
     						 CustomTimePeriod.Fields.periodTypeId,
                                 "FISCAL_YEAR")); 
     		 
     		if (!periodo.isEmpty() && periodo.size() > 1) {
     			 
     			 for (CustomTimePeriod customTimePeriod : periodo) {
 					Calendar fechax= Calendar.getInstance();
 					fechax.setTimeInMillis(customTimePeriod.getFromDate().getTime()); 
 					String annio =  Integer.toString( fechax.get(Calendar.YEAR));
     				logger.debug("Annio Lista " + annio);
     				if(annioactual.equals(annio.trim()))    				
     					actual = true;
     				
     				else if(annioanter.equals(annio.trim()))    				
     					anterior = true;     				
 				}
 				
     			 if(actual && anterior)
     			 {
     				 logger.debug("Año actual " + annioactual + "Año anteriror " + annioanter);
     				
     				 Calendar date= Calendar.getInstance();
     				 date.set(Integer.parseInt(annioanter), 11, 31);
     				 Timestamp timespan = new Timestamp(date.getTimeInMillis());
 					
 		    		 int secuencia = 1;    		 
 		    		
 		    		 String id_trans = ledger_repo.getNextSeqId("AcctgTrans"); 		    		 
 		    		 
 		    		 AcctgTrans acctgsaldo = new AcctgTrans();
 		             acctgsaldo.setAcctgTransId(id_trans);	     
 		             acctgsaldo.setAcctgTransTypeId("SALDO INICIAL");
 		             acctgsaldo.setDescription("Carga de Saldo Inicial");
 		             acctgsaldo.setTransactionDate(timespan);
 		             acctgsaldo.setIsPosted("Y");
 		             acctgsaldo.setPostedDate(timespan);
 		             acctgsaldo.setGlFiscalTypeId("ACTUAL");	
 		             acctgsaldo.setCreatedByUserLogin("admin");//Buscar de donde tomarlo
 		             acctgsaldo.setLastModifiedByUserLogin("admin");
 		             acctgsaldo.setPostedAmount(saldototal);
 		             acctgsaldo.setPartyId(organizationPartyId);
 		             
 		             imp_tx3 = this.session.beginTransaction();
 		             ledger_repo.createOrUpdate(acctgsaldo);
 		             imp_tx3.commit();
 		             
 		             String message = "Successfully generate AcctgTrans [GL-" + id_trans + "].";  
 		             Debug.logInfo(message, MODULE);
 		             
 		            
 						for (String  cuenta: Listacuentas) {
 							
 							 String array[] = cuenta.split(";");
 							 BigDecimal saldo = new BigDecimal(array[1]);
 							
 							
 							 AcctgTransEntry acctgentry = new AcctgTransEntry();
 			                 acctgentry.setAcctgTransId(id_trans);
 			                 acctgentry.setAcctgTransEntrySeqId(String.format("%05d",secuencia));
 			                 acctgentry.setAcctgTransEntryTypeId("_NA_");
 			                 acctgentry.setDescription("Carga de Saldo Inicial");
 			                 acctgentry.setGlAccountId(array[0]);
 			                 acctgentry.setOrganizationPartyId(organizationPartyId);
 			                 acctgentry.setAmount(saldo);
 			                 acctgentry.setCurrencyUomId("MXN");		                    
 			                 acctgentry.setDebitCreditFlag(array[2]);
 			                 acctgentry.setReconcileStatusId("AES_NOT_RECONCILED");	
 			                 acctgentry.setPartyId(organizationPartyId);
 			                 
 			                 imp_tx4 = this.session.beginTransaction();
 			                 ledger_repo.createOrUpdate(acctgentry);
 			                 imp_tx4.commit();
 			                 
 			                 String message1 = "Successfully generate AcctgTransEntry [GL-" + id_trans + "].";  
 			                 Debug.logInfo(message1, MODULE);
 			                 
 			                 secuencia = secuencia + 1;
 			                 
 			                 List<GlAccountOrganization> Listorganizacion = ledger_repo.findList(
 			                          GlAccountOrganization.class, ledger_repo.map(
 			                        		  GlAccountOrganization.Fields.glAccountId,
 			                        		  array[0]));
 			                
 			               
 			                if(!Listorganizacion.isEmpty()){
 			                for (GlAccountOrganization glorgan : Listorganizacion) 
 			                {
 			                	BigDecimal saldofinal = BigDecimal.ZERO;
 			                	
 			                	
// 			                	if(array[2].equals("C")){
// 			                		if( glorgan.getPostedBalance()== null)
// 			                		{
// 			                			
// 			                			saldofinal = saldofinal.subtract(saldo);
// 			                		}
// 			                		else
// 			                		{
// 			                			
// 			                			saldofinal =  glorgan.getPostedBalance().subtract(saldo);
// 			                		}
// 			                		
// 			                	}	                	
// 			                	else {saldofinal = saldo;}
 			                	saldofinal = saldo;
 			                	glorgan.setPostedBalance(saldofinal);
 			                	
 			                	imp_tx5 = this.session.beginTransaction();
 			                    ledger_repo.createOrUpdate(glorgan);
 			                    imp_tx5.commit();
 			                    
 			                    
 			                
 			                }
 						}
 				}
 	                
 		    } 
     	}
			
		} catch (Exception e) {
			 String message = "Failed to import transaccion, Error message : " + e.getMessage();
             //storeImportGlAccountError(rawdata, message, imp_repo);
             
             //rollback all if there was an error when importing item
             if(imp_tx3 != null){
                 imp_tx3.rollback();
             }
             if(imp_tx4 != null){
                 imp_tx4.rollback();
             }
             if(imp_tx5 != null){
                 imp_tx5.rollback();
             }
             Debug.logError(e, message, MODULE);
             //throw new ServiceException(e.getMessage());
		}
    	
		
	}

	/**
     * Helper method to store GL account import succes into <code>DataImportGlAccount</code> entity row.
     * @param rawdata item of <code>DataImportGlAccount</code> entity that was successfully imported
     * @param imp_repo repository of accounting
     * @throws org.opentaps.foundation.repository.RepositoryException
     */
    private void storeImportGlAccountSuccess(DataImportGlAccount rawdata, AccountingDataImportRepositoryInterface imp_repo) throws RepositoryException {
        // mark as success
        rawdata.setImportStatusId(StatusItemConstants.Dataimport.DATAIMP_IMPORTED);
        rawdata.setImportError(null);
        rawdata.setProcessedTimestamp(UtilDateTime.nowTimestamp());
        imp_repo.createOrUpdate(rawdata);
    }

    /**
     * Helper method to store GL account import error into <code>DataImportGlAccount</code> entity row.
     * @param rawdata item of <code>DataImportGlAccount</code> entity that was unsuccessfully imported
     * @param message error message
     * @param imp_repo repository of accounting
     * @throws org.opentaps.foundation.repository.RepositoryException
     */
    private void storeImportGlAccountError(DataImportGlAccount rawdata, String message, AccountingDataImportRepositoryInterface imp_repo) throws RepositoryException {
        // store the exception and mark as failed
        rawdata.setImportStatusId(StatusItemConstants.Dataimport.DATAIMP_FAILED);
        rawdata.setImportError(message);
        rawdata.setProcessedTimestamp(UtilDateTime.nowTimestamp());
        imp_repo.createOrUpdate(rawdata);
    }

}
