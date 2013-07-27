package org.opentaps.domain.dataimport;

import java.util.List;

import org.opentaps.base.entities.DataImportEgresoDiario;
import org.opentaps.foundation.repository.RepositoryException;
import org.opentaps.foundation.repository.RepositoryInterface;

public interface EgresoDiarioDataImportRepositoryInterface extends
		RepositoryInterface {
	public List<DataImportEgresoDiario> findNotProcessesDataImportEgresoDiarioEntries()
			throws RepositoryException;
}
