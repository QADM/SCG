package org.opentaps.domain.dataimport;

import java.util.List;

import org.opentaps.base.entities.DataImportTag;
import org.opentaps.foundation.repository.RepositoryException;
import org.opentaps.foundation.repository.RepositoryInterface;

public interface TagDataImportRepositoryInterface extends RepositoryInterface{
	  /**
     * Finds not imported General Ledger accounts.
     * @throws org.opentaps.foundation.repository.RepositoryException
     */
    public List<DataImportTag> findNotProcessesDataImportTags() throws RepositoryException;

}
