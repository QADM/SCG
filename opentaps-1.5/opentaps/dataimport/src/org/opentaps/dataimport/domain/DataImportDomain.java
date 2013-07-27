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

import org.opentaps.domain.dataimport.AccountingDataImportRepositoryInterface;
import org.opentaps.domain.dataimport.CategoryDataImportRepositoryInterface;
import org.opentaps.domain.dataimport.DataImportDomainInterface;
import org.opentaps.domain.dataimport.EgresoDiarioDataImportRepositoryInterface;
import org.opentaps.domain.dataimport.GeoDataImportRepositoryInterface;
import org.opentaps.domain.dataimport.GlAccountImportServiceInterface;
import org.opentaps.domain.dataimport.IngresoDiarioDataImportRepositoryInterface;
import org.opentaps.domain.dataimport.OperacionDiariaDataImportRepositoryInterface;
import org.opentaps.domain.dataimport.PartyDataImportRepositoryInterface;
import org.opentaps.domain.dataimport.PresupuestoEgresoDataImportRepositoryInterface;
import org.opentaps.domain.dataimport.PresupuestoIngresoDataImportRepositoryInterface;
import org.opentaps.domain.dataimport.ProductDataImportRepositoryInterface;
import org.opentaps.domain.dataimport.ProjectDataImportRepositoryInterface;
import org.opentaps.domain.dataimport.TagDataImportRepositoryInterface;
import org.opentaps.foundation.domain.Domain;
import org.opentaps.foundation.repository.RepositoryException;
import org.opentaps.foundation.service.ServiceException;

/**
 * This is an implementation of the Data Import domain.
 */
public class DataImportDomain extends Domain implements DataImportDomainInterface{

    /** {@inheritDoc} */
    public AccountingDataImportRepositoryInterface getAccountingDataImportRepository() throws RepositoryException {
        return instantiateRepository(AccountingDataImportRepository.class);
    }

    /** {@inheritDoc} */
    public GlAccountImportServiceInterface getGlAccountImportService() throws ServiceException {
        return this.instantiateService(GlAccountImportService.class);
    }

    /** {@inheritDoc} */
    public ProductDataImportRepositoryInterface getProductDataImportRepository() throws RepositoryException {
        return instantiateRepository(ProductDataImportRepository.class);
    }   
   

    /** {@inheritDoc} */
	public TagDataImportRepositoryInterface getTagDataImportRepository()
			throws RepositoryException { 
		
		return instantiateRepository(TagDataImportRepository.class);
	}
	
	/** {@inheritDoc} */
    public PartyDataImportRepositoryInterface getPartyDataImportRepository() throws RepositoryException {
        return instantiateRepository(PartyDataImportRepository.class);
    }
    
    /** {@inheritDoc} */
    public ProjectDataImportRepositoryInterface getProjectDataImportRepository() throws RepositoryException {
        return instantiateRepository(ProjectDataImportRepository.class);
    }
    
    /** {@inheritDoc} */
    public GeoDataImportRepositoryInterface getGeoDataImportRepository() throws RepositoryException {
        return instantiateRepository(GeoDataImportRepository.class);
    }
    
    /** {@inheritDoc} */
    public CategoryDataImportRepositoryInterface getCategoryDataImportRepository() throws RepositoryException {
        return instantiateRepository(CategoryDataImportRepository.class);
    }
    
    /** {@inheritDoc} */
    public PresupuestoIngresoDataImportRepositoryInterface getPresupuestoIngresoDataImportRepository() throws RepositoryException {
        return instantiateRepository(PresupuestoIngresoDataImportRepository.class);
    }
    
    /** {@inheritDoc} */
    public PresupuestoEgresoDataImportRepositoryInterface getPresupuestoEgresoDataImportRepository() throws RepositoryException {
        return instantiateRepository(PresupuestoEgresoDataImportRepository.class);
    }
    
    /** {@inheritDoc} */
    public IngresoDiarioDataImportRepositoryInterface getIngresoDiarioDataImportRepository() throws RepositoryException {
        return instantiateRepository(IngresoDiarioDataImportRepository.class);
    }
    
    /** {@inheritDoc} */
    public EgresoDiarioDataImportRepositoryInterface getEgresoDiarioDataImportRepository() throws RepositoryException {
        return instantiateRepository(EgresoDiarioDataImportRepository.class);
    }
    
    /** {@inheritDoc} */
    public OperacionDiariaDataImportRepositoryInterface getOperacionDiariaDataImportRepository() throws RepositoryException {
        return instantiateRepository(OperacionDiariaDataImportRepository.class);
    }
    
    

}
