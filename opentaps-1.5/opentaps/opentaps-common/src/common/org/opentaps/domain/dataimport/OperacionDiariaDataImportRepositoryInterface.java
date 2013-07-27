package org.opentaps.domain.dataimport;

import java.util.List;

import org.opentaps.base.entities.DataImportOperacionDiaria;
import org.opentaps.foundation.repository.RepositoryException;
import org.opentaps.foundation.repository.RepositoryInterface;

public interface OperacionDiariaDataImportRepositoryInterface extends RepositoryInterface{
	public List<DataImportOperacionDiaria> findNotProcessesDataImportOperacionDiariaEntries() throws RepositoryException;
}
