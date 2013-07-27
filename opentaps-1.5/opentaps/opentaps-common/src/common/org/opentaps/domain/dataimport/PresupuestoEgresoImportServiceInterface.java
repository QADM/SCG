package org.opentaps.domain.dataimport;

import org.opentaps.foundation.service.ServiceException;
import org.opentaps.foundation.service.ServiceInterface;

public interface PresupuestoEgresoImportServiceInterface extends
		ServiceInterface {
	/**
	 * Sets the required input parameter for service
	 * {@link #importPresupuestoEgreso}.
	 * 
	 * @param organizationPartyId
	 *            the ID of the organization party
	 */
	public void setOrganizationPartyId(String organizationPartyId);

	/**
	 * Gets imported records count by service {@link #importPresupuestoEgreso}.
	 * 
	 * @return imported records count
	 */
	public int getImportedRecords();

	/**
	 * Import products using <code>DataImportPresupuestoEgreso</code>. Note that
	 * this service is not wrapped in a transaction. Each product record
	 * imported is in its own transaction, so it can store as many good records
	 * as possible.
	 * 
	 * @throws ServiceException
	 *             if an error occurs
	 */
	public void importPresupuestoEgreso() throws ServiceException;
}
