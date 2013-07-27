package org.opentaps.domain.dataimport;

import org.opentaps.foundation.service.ServiceException;
import org.opentaps.foundation.service.ServiceInterface;

public interface IngresoDiarioImportServiceInterface extends ServiceInterface {
	
	public int getImportedRecords();

	public void importIngresoDiario() throws ServiceException;
}
