package org.opentaps.domain.dataimport;

import java.util.List;

import org.opentaps.base.entities.DataImportPresupuestoEgreso;
import org.opentaps.foundation.repository.RepositoryException;
import org.opentaps.foundation.repository.RepositoryInterface;


public interface PresupuestoEgresoDataImportRepositoryInterface extends RepositoryInterface{
    
    /**
     * Finds not imported presupuestos egresos.
     * @throws org.opentaps.foundation.repository.RepositoryException
     */
    public List<DataImportPresupuestoEgreso> findNotProcessesDataImportPresupuestoEgresoEntries() throws RepositoryException;

}
