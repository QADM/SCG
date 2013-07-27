package org.opentaps.domain.dataimport;

import java.util.List;

import org.opentaps.base.entities.DataImportPresupuestoIngreso;
import org.opentaps.foundation.repository.RepositoryException;
import org.opentaps.foundation.repository.RepositoryInterface;

public interface PresupuestoIngresoDataImportRepositoryInterface  extends RepositoryInterface{
    
    /**
     * Finds not imported presupuestos ingresos.
     * @throws org.opentaps.foundation.repository.RepositoryException
     */
    public List<DataImportPresupuestoIngreso> findNotProcessesDataImportPresupuestoIngresoEntries() throws RepositoryException;

}
