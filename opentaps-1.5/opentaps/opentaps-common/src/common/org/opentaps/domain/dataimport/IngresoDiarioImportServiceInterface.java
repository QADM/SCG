package org.opentaps.domain.dataimport;

import org.opentaps.foundation.service.ServiceException;
import org.opentaps.foundation.service.ServiceInterface;

public interface IngresoDiarioImportServiceInterface extends ServiceInterface {

	/**
	 * Sets the required input parameter for service
	 * {@link #importIngresoDiario}.
	 * 
	 * @param lote
	 *            the ID of the organization party
	 */
	public void setLote(String lote);

	public int getImportedRecords();

	public void importIngresoDiario() throws ServiceException;
}
