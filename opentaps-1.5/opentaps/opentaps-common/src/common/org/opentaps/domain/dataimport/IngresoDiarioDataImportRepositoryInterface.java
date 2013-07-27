package org.opentaps.domain.dataimport;

import java.util.List;

import org.opentaps.base.entities.DataImportIngresoDiario;
import org.opentaps.base.entities.DataImportPresupuestoEgreso;
import org.opentaps.foundation.repository.RepositoryException;
import org.opentaps.foundation.repository.RepositoryInterface;

public interface IngresoDiarioDataImportRepositoryInterface extends RepositoryInterface{

	public List<DataImportIngresoDiario> findNotProcessesDataImportIngresoDiarioEntries()
			throws RepositoryException;
		
	
}
